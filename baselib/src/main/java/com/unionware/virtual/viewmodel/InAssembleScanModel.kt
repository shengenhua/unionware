package com.unionware.virtual.viewmodel

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.model.bean.BillBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.api.util.ConvertUtils
import unionware.base.app.event.SingleLiveEvent
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
open class InAssembleScanModel @Inject constructor() : AssembleModel() {
    /**
     * 已扫描条码数量
     */
    val barcodeItemCountLiveData: SingleLiveEvent<Int> = SingleLiveEvent(0)

    var viewLiveData: MutableLiveData<List<BillBean>> = MutableLiveData<List<BillBean>>()
    var dataLiveData: MutableLiveData<List<Map<String, Any>>> =
        MutableLiveData<List<Map<String, Any>>>()
    var pageIndex: MutableLiveData<Int> = MutableLiveData<Int>(1)

    /**
     *     "configId" : 应用配置Id （可选）
     *         "keyword" : 模糊搜索（可选）
     */
    fun queryList(scene: String, name: String, filtersReq: FiltersReq?) {
        NetHelper.request<CommonListDataResp<Map<String, Any>>>(basicApi?.query(
            scene,
            name,
            filtersReq
        ),
            lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    viewLiveData.value =
                        if (data == null) mutableListOf() else ConvertUtils.convertViewToList(
                            data.view, data.data
                        )
                    data?.data?.also {
                        if (pageIndex.value == 1) {
                            dataLiveData.value = it
                        } else {
                            if (it.size < 20) {
                                pageIndex.value = pageIndex.value?.minus(1)
                            }
                            val dataList = dataLiveData.value
                            dataLiveData.value = dataList?.plus(it) ?: it
                        }

                        haveScanLive.value = !dataLiveData.value.isNullOrEmpty()
                    }
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }
}