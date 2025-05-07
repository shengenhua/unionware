package com.unionware.once.viewmodel.inspect

import com.unionware.basicui.base.viewmodel.BaseQueryListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import unionware.base.api.util.ConvertUtils
import javax.inject.Inject

@HiltViewModel
class InspectQueryViewModel @Inject constructor() : BaseQueryListViewModel() {

    /**
     * 巡检，查询电子制造派工单列表
     */
    fun queryJobList(
        filtersReq: FiltersReq,
        scene: String = "MES.Process.Electronic",
        name: String = "66960F200DE7F2"
    ) {
        filtersReq.apply {
            pageIndex = this@InspectQueryViewModel.pageIndex.value!!
            pageSize = 20
        }
        NetHelper.request<CommonListDataResp<Map<String, Any>>>(api?.query(
            scene, name, filtersReq
        ), lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
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
                        dataLiveData.value = dataList?.plus(it)
                    }
                }
            }

            override fun onFailure(e: ApiException) {
                pageIndex.value = pageIndex.value?.minus(1)
                mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
            }
        })
    }

}