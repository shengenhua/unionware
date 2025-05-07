package com.unionware.once.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.basicui.base.viewmodel.BaseMESViewModel
import com.unionware.once.api.OnceApi
import com.unionware.virtual.model.ErrorSponsors
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.app.event.SingleLiveEvent
import unionware.base.model.bean.BarcodeMapBean
import unionware.base.model.bean.CommonListBean
import unionware.base.model.bean.ViewBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ReportReq
import unionware.base.model.resp.ChecketReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.model.resp.ReportResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject


@HiltViewModel
open class ProductTransferViewModel @Inject constructor() : BaseMESViewModel() {
    /**
     * 已扫描条码数量
     */
    val barcodeItemCountLiveData: SingleLiveEvent<Int> = SingleLiveEvent<Int>(0)

    /**
     * 条码扫描
     */
    val barcodeLiveData: MutableLiveData<BarcodeMapBean> = MutableLiveData<BarcodeMapBean>()

    /**
     * 条码扫描 失败
     */
    val barcodeELiveData: SingleLiveEvent<ApiException> = SingleLiveEvent()

    /**
     * 提交
     */
    val submitLiveData: MutableLiveData<ReportResp> = MutableLiveData<ReportResp>()

    /**
     * 保存 交互码
     */
    val sponsorsLiveData: MutableLiveData<MutableList<String>> =
        MutableLiveData<MutableList<String>>()

    /**
     * 当前扫描的条码
     */
    var barcode: String? = null

    @JvmField
    @Inject
    var api: OnceApi? = null

    /**
     * 扫描条码
     */
    open fun queryBarcode(
        filtersReq: FiltersReq,
        scene: String = "UserDefine.OrderTransfer",
        name: String = "66F3C6DD487178",
    ) {
        //防止短时间 请求多个
        if (barcode.isNullOrEmpty()) {
            barcode = filtersReq.filters["primaryCode"]?.toString()?:""
        } else if (barcode == filtersReq.filters["primaryCode"]) {
            return
        }
        sponsorsLiveData.value?.also {
            filtersReq.sponsors = it
        }
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        barcodeRequest(
            filtersReq,
            scene,
            name,
            object : ICallback<CommonListDataResp<Map<String, Any>>> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    if (data?.data?.isNotEmpty() == true) {
                        data.data?.get(0)?.also {
                            barcodeLiveData.value = barcodeList("barCode", data.data, data.view)
                        }
                    } else {
                        barcodeELiveData.value = ApiException("-1", "当前转入工单未查询到此条码")
                        mUIChangeLiveData.getShowToastViewEvent().postValue("当前转入工单未查询到此条码")
                    }
                    barcode = null
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }

                override fun onFailure(e: ApiException) {
                    barcodeELiveData.value = e
                    barcode = null
                    onVMFailure(e, fFiltersReq = filtersReq)
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

    open fun barcodeRequest(
        filtersReq: FiltersReq,
        scene: String = "MES.Process.Electronic",
        name: String = "66960F0C0DE7EE",
        callback: ICallback<CommonListDataResp<Map<String, Any>>>,
    ) {
        NetHelper.request(api?.queryToAny(
            scene, name, filtersReq
        ), lifecycle, object : ICallback<Any?> {
            override fun onSuccess(data: Any?) {
                val dataResp = Gson().fromJson<CommonListDataResp<Map<String, Any>>>(
                    Gson().toJson(data),
                    (object : TypeToken<CommonListDataResp<Map<String, Any>>?>() {}).type
                )
                callback.onSuccess(dataResp)
            }

            override fun onFailure(e: ApiException) {
                callback.onFailure(e)
            }
        })
    }

    /**
     * 提交
     */
    fun submitReport(checketReq: ChecketReq) {
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        sponsorsLiveData.value?.also {
            checketReq.sponsors = it
        }
        NetHelper.request(
            api?.orderTransfer(checketReq),
            lifecycle,
            object : ICallback<List<ReportResp>> {
                override fun onSuccess(data: List<ReportResp>?) {
                    //
                    data?.also {
                        submitLiveData.value = it[0]
                        mUIChangeLiveData.getTTSSucOrFailEvent().postValue(true)
                    }
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                    mUIChangeLiveData.getTTSEvent().postValue(SoundType.Default.SUBMIT_SUCCESS)
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getTTSSucOrFailEvent().postValue(false)
                    onVMFailure(e)//checketReq
                }
            })
    }


    protected open fun onVMFailure(
        e: ApiException, fReportReq: ReportReq? = null, fFiltersReq: FiltersReq? = null,
    ) {
        val code = e.code.toIntOrNull()
        if (code != null && code >= 90000 && code <= 99999) {
            fReportReq?.also {
                if (it.sponsors == null) {
                    it.sponsors = mutableListOf()
                }
                val codes: MutableList<String> = it.sponsors!!
                codes.add(e.data)
                it.sponsors = codes
                sponsorsLiveData.value = it.sponsors
            }

            fFiltersReq?.also {
                if (it.sponsors?.isEmpty() == true) {
                    val codes: MutableList<String> = ArrayList(listOf(e.data))
                    codes.add(e.code)
                    it.sponsors = codes
                } else {
                    val codes: MutableList<String> = ArrayList()
                    codes.add(e.data)
                    it.sponsors = codes
                }
                sponsorsLiveData.value = it.sponsors
            }
            if (e.data != "TIMEUPDATEPASSTIVE") {
                //弹出对话框
                showErrorDialogViewEvent.value = ErrorSponsors(e.errorMsg).also {
                    it.reportReq = fReportReq
                    it.filtersReq = fFiltersReq
                }
            }
        } else {
            mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
        }
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
    }
}