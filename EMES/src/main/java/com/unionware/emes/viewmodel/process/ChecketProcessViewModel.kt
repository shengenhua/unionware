package com.unionware.emes.viewmodel.process

import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.model.req.ReportReq
import javax.inject.Inject

@HiltViewModel
open class ChecketProcessViewModel @Inject constructor() : ProcessViewModel() {

    /**
     * 生成产品检验单
     * type : 1 首检，2 巡检，3终检
     */
    fun checket(
        req: ReportReq,
        type: String,
        name: String = "66960F0C0DE7EE"
    ) {

        val items = ReportReq.DataReq()
//        req.data = items

        /*NetHelper.request(api?.checket(
            scene, name, req
        ), lifecycle, object : ICallback<Any?> {
            override fun onSuccess(data: Any?) {

            }

            override fun onFailure(e: ApiException) {
                mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
            }
        })*/
    }
}