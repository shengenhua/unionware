package com.unionware.virtual.viewmodel

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.basic.BasicApi
import unionware.base.model.bean.CommonListBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.api.util.ConvertUtils
import unionware.base.app.viewmodel.BaseViewModel
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

@HiltViewModel
open class DetailsViewModel @Inject constructor(@JvmField @Inject var api: BasicApi?) :
    BaseViewModel() {

    var viewLiveData: MutableLiveData<List<CommonListBean>> =
        MutableLiveData<List<CommonListBean>>()
    var dataLiveData: MutableLiveData<Map<String, Any>> =
        MutableLiveData<Map<String, Any>>()

    fun query(scene: String, searchId: String, filters: Map<String, String>) {
        val filtersReq = FiltersReq()
        filtersReq.filters = filters
        NetHelper.request<CommonListDataResp<Map<String, Any>>>(api?.query(
            scene,
            searchId,
            filtersReq
        ),
            lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    viewLiveData.value =
                        if (data == null) mutableListOf() else ConvertUtils.convertMapToList(
                            data.view, data.data
                        )
                    dataLiveData.value = data!!.data[0]
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }
}