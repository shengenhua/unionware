package com.unionware.virtual.viewmodel

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ViewReq
import unionware.base.model.resp.AnalysisInfoResp
import unionware.base.model.resp.CommonListDataResp
import unionware.base.app.event.SingleLiveEvent
import unionware.base.ext.bigDecimalToZeros
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

/**
 * 虚拟视图的 viewModel 类
 * 使用 SingleLiveEvent 的liveData 确保 使用最新的数据不会调用多次使用旧数据
 *
 */
@HiltViewModel
open class AssembleModel @Inject constructor() : VirtualViewModel() {
    /**
     * 存在已扫码的数据
     */
    var haveScanLive: SingleLiveEvent<Boolean> = SingleLiveEvent(false)

    /**
     * 关键件信息
     */
    var keyPartsLiveData: SingleLiveEvent<CommonListDataResp<Map<String, Any>>?> = SingleLiveEvent()

    /**
     * 需要扫描的列表
     */
//    var scanListLiveData: SingleLiveEvent<CommonListDataResp<MutableMap<String, Any>>?> = SingleLiveEvent()
    var scanListLiveData: MutableLiveData<CommonListDataResp<Map<String, Any>>?> =
        MutableLiveData()


    /**
     * 扫描监听
     */
    var scanInputData: SingleLiveEvent<String?> = SingleLiveEvent()

    /**
     * 查询关键件信息
     */
    fun queryKeyParts(
        scene: String,
        name: String,
        filters: FiltersReq = FiltersReq(
            mutableMapOf<String?, Any?>().apply {
                put("primaryId", virtualLiveEvent.dataLiveData.value?.primaryId ?: "")
            })
    ) {
        NetHelper.request<CommonListDataResp<Map<String, Any>>>(basicApi?.query(
            scene,
            name,
            filters
        ),
            lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    keyPartsLiveData.value = data
                }

                override fun onFailure(e: ApiException) {
                    keyPartsLiveData.value = null
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }

    /**
     * 查询装配物料数据
     */
    fun queryScanList(
        scene: String,
        name: String,
        filters: FiltersReq = FiltersReq(
            mutableMapOf<String?, Any?>().apply {
                put(
                    "primaryId",
                    virtualLiveEvent.dataLiveData.value?.primaryId?.bigDecimalToZeros() ?: ""
                )
            })
    ) {
        NetHelper.request(basicApi?.query(
            scene,
            name,
            filters
        ),
            lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    scanListLiveData.value = data
                    /*if (data == null) mutableListOf() else ConvertUtils.convertViewToRowsList(
                        data.view, data.data
                    )*/
                }

                override fun onFailure(e: ApiException) {
                    scanListLiveData.value = null
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }

    override fun confirmViewData(command: String) {
        postShowTransLoadingViewEvent(true)
        val viewReq =
            ViewReq(virtualLiveEvent.pageIdLiveData.value)
        viewReq.command = command
        request(viewApi?.commandViewData(viewReq), object : ICallback<AnalysisInfoResp?> {
            override fun onSuccess(data: AnalysisInfoResp?) {
                data?.apply {
                    virtualLiveEvent.dataLiveData.value = this.data
                }
                postShowTransLoadingViewEvent(false)
            }

            override fun onFailure(e: ApiException) {
                viewReq.simulate = "Command"
                onVMFailure(e, viewReq)
                getVirtualViewData()
            }
        })
    }
}