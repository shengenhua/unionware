package com.unionware.once.viewmodel

import androidx.lifecycle.MutableLiveData
import com.unionware.basicui.base.viewmodel.BaseCollectViewModel
import com.unionware.once.api.OnceApi
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.util.ConvertUtils
import unionware.base.app.event.SingleLiveEvent
import unionware.base.model.bean.CommonListBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject


@HiltViewModel
open class FaultRecordViewModel @Inject constructor() : BaseCollectViewModel() {

    var faultRecordLive: SingleLiveEvent<Any?> = SingleLiveEvent()
    var viewLiveData: MutableLiveData<List<CommonListBean>> =
        MutableLiveData<List<CommonListBean>>()
    var dataLiveData: MutableLiveData<Map<String, Any>> =
        MutableLiveData<Map<String, Any>>()


    @JvmField
    @Inject
    var api: OnceApi? = null

    /**
     * 扫描工单号
     *
     */
    fun query(
        scene: String,
        name: String? = "PRD_MO",
        filtersReq: FiltersReq,
    ) {
        NetHelper.request(api?.query(scene, name, filtersReq),
            lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    data?.apply {
                        if (this.data.isNullOrEmpty()) {
                            postShowToastViewEvent("未查询到数据")
                            return
                        }
                        viewLiveData.value = ConvertUtils.convertMapToList(this.view, this.data)
                        this.data?.also { dataLiveData.value = it[0] }
                    }
                }

                override fun onFailure(e: ApiException) {
                    viewLiveData.value = mutableListOf()
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }

    /**
     * 生成工单生产故障记录
     */
    fun faultRecord(map: Map<String, Any>) {
        NetHelper.request(api?.OPFaultRecord(map),
            lifecycle, object : ICallback<Any?> {
                override fun onSuccess(data: Any?) {
                    faultRecordLive.value = data
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)

                }
            })
    }
}