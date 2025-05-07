package com.unionware.once.viewmodel

import androidx.lifecycle.MutableLiveData
import com.unionware.once.viewmodel.process.ProcessViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.app.event.SingleLiveEvent
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject


@HiltViewModel
open class BadnessJudgeViewModel @Inject constructor() : ProcessViewModel() {

    var repairedJudgeLive: SingleLiveEvent<Any?> = SingleLiveEvent()
    var repairedDataLiveData: MutableLiveData<Map<String, Any>> =
        MutableLiveData<Map<String, Any>>()

    /**
     * 不良品处理单查询
     *
     */
    fun repairedJudgeQuery(
        scene: String,
        name: String? = "67209FE30343FB",
        filtersReq: FiltersReq,
    ) {
        NetHelper.request(api?.query(scene, name, filtersReq),
            lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    data?.data?.also { repairedDataLiveData.value = it[0] }
                }

                override fun onFailure(e: ApiException) {
                    repairedDataLiveData.value = mutableMapOf()
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }

    /**
     * 不良检修判定
     */
    fun repairedJudge(map: Map<String, Any>) {
        NetHelper.request(api?.repairedJudge(map),
            lifecycle, object : ICallback<Any?> {
                override fun onSuccess(data: Any?) {
                    repairedJudgeLive.value = data
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }
}