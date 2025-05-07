package com.unionware.once.viewmodel.inspect

import com.unionware.once.viewmodel.process.ProcessViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.ChecketReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.model.resp.ReportResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

@HiltViewModel
open class InspectProViewModel @Inject constructor() : ProcessViewModel() {


    /**
     * 巡检，查询工序列表
     */
    fun queryProcessList(
        filtersReq: FiltersReq,
        scene: String = "MES.Process.Electronic",
        name: String = "66960F290DE7F4",
    ) {
        NetHelper.request<CommonListDataResp<Map<String, Any>>>(
            api?.query(
                scene, name, filtersReq
            ), lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {

                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }

    /**
     * 生成产品检验单 巡检
     */
    fun checketReport(checketReq: ChecketReq) {
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        NetHelper.request(
            api?.checket(checketReq),
            lifecycle,
            object : ICallback<List<ReportResp>> {
                override fun onSuccess(data: List<ReportResp>?) {
                    //
                    data?.also {
                        submitLiveData.value = it[0]
                    }
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                    /*mUIChangeLiveData.getShowToastViewEvent().postValue("提交成功")
                    mUIChangeLiveData.getFinishActivityEvent().postValue(null)
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)*/
                }

                override fun onFailure(e: ApiException) {
                    failureLiveData.value = e
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }
            })
    }
}