package com.unionware.once.viewmodel

import androidx.lifecycle.MutableLiveData
import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.once.model.FentityView
import com.unionware.once.model.SpecialReportReq
import com.unionware.once.util.DataToViewUtil
import com.unionware.once.viewmodel.process.ProcessViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.model.bean.BarcodeMapBean
import unionware.base.model.bean.BaseInfoBean
import unionware.base.model.bean.CommonListBean
import unionware.base.model.bean.ViewBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.model.resp.ReportResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

@HiltViewModel
open class AgingProViewModel @Inject constructor() : ProcessViewModel() {
    /**
     * 条码扫描
     */
    val viewLiveData: MutableLiveData<List<FentityView>> = MutableLiveData<List<FentityView>>()
    val viewInfoLiveData: MutableLiveData<List<ViewBean>> = MutableLiveData<List<ViewBean>>()
    val dataLiveData: MutableLiveData<List<Map<String, Any>>> =
        MutableLiveData<List<Map<String, Any>>>()

    val snLiveData: MutableLiveData<BarcodeMapBean> = MutableLiveData<BarcodeMapBean>()

    /**
     * 老化架转移，根据条码查询生产订单及老化架号
     *
     */
    fun queryAgingByCode(
        filtersReq: FiltersReq,
        scene: String = "MES.Process.Electronic",
        name: String = "66AB50883F2580",
    ) {
        //MES.Process.Electronic/66960F1B0DE7F1
        NetHelper.request<CommonListDataResp<Map<String, Any>>>(api?.query(
            name, scene, filtersReq
        ), lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
            override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                if (data == null || data.data.isNullOrEmpty()) {
                    postShowToastViewEvent("未查询到数据")
                    dataLiveData.value = mutableListOf()
                    return
                }
                viewInfoLiveData.value = data.view
                dataLiveData.value = data.data
                viewLiveData.value =
                    DataToViewUtil.fentityMapToList(
                        data.view, data.data, "FEntity", "frameName", "moNo"
                    )
            }

            override fun onFailure(e: ApiException) {
                dataLiveData.value = mutableListOf()
                postShowToastViewEvent(e.errorMsg)
            }
        })
    }

    /**
     * 生成老化架转移记
     *
     */
    fun frameTransfer(
        reportReq: SpecialReportReq,
    ) {
        postShowTransLoadingViewEvent(true)
        NetHelper.request(api?.frameTransfer(reportReq),
            lifecycle,
            object : ICallback<ReportResp?> {
                override fun onSuccess(data: ReportResp?) {
                    //
                    data?.also {
                        submitLiveData.value = it
                    }
                    postShowTransLoadingViewEvent(false)
                    postShowToastViewEvent("提交成功")
                    mUIChangeLiveData.getTTSEvent().postValue(SoundType.Default.SUBMIT_SUCCESS)
                }

                override fun onFailure(e: ApiException) {
                    postShowTransLoadingViewEvent(false)
                    postShowToastViewEvent(e.errorMsg)
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }


    fun querySn(
        scene: String,
        name: String = "673721F579E362",
        filtersReq: FiltersReq,
    ) {
        NetHelper.request(api?.query(name, scene, filtersReq),
            lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    if (data == null || data.data.isNullOrEmpty()) {
                        snLiveData.value = BarcodeMapBean("")
                        postShowToastViewEvent("未查询到数据")
                        return
                    }

                    data.data?.get(0)?.also {
                        snLiveData.value = barcodeList("barcode", data.data, data.view)
                    }
                }

                override fun onFailure(e: ApiException) {
                    snLiveData.value = BarcodeMapBean("")
                    postShowToastViewEvent(e.errorMsg)
                }
            })
    }


    private fun barcodeList(
        tagKey: String,
        dataMap: List<Map<String, Any>>?,
        views: List<ViewBean>?,
    ): BarcodeMapBean? {
        var tagName = ""
        val viewMap = views?.filter {
            if (it.key == tagKey) {
                tagName = it.name
            }
            it.isVisible && it.key != tagKey && "billCode" != it.key// packCode 隐藏包装条码
        }?.associate {
            it.key to it.name
        }
        return dataMap?.map { data ->
            BarcodeMapBean(data[tagKey].toString()).apply {
                this.tagName = tagName
                this.value = data
                list = viewMap?.map {
                    CommonListBean(it.value, data[it.key].toString())
                }
            }
        }?.get(0)
    }


    fun query(
        scene: String,
        name: String = "661779469DFB27",
        filtersReq: FiltersReq,
        callback: ICallback<CommonListDataResp<BaseInfoBean>?>,
    ) {
        NetHelper.request(
            api?.getBaseInfoList(
                scene,
                name,
                filtersReq
            ), lifecycle, callback
        )
    }

}