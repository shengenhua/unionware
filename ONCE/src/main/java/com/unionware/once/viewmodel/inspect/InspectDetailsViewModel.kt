package com.unionware.once.viewmodel.inspect

import com.unionware.basicui.base.viewmodel.BaseDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.basic.BasicApi
import unionware.base.api.util.ConvertUtils
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject


@HiltViewModel
class InspectDetailsViewModel @Inject constructor(api: BasicApi) :
    BaseDetailsViewModel(api) {

    /**
     * 巡检，查询电子制造派工单详情
     */
    fun queryJobInfo(
        filtersReq: FiltersReq,
        scene: String = "MES.Process.Electronic",
        name: String = "66960F230DE7F3",
    ) {
        NetHelper.request(
            api.query(
                scene, name, filtersReq
            ), lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
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