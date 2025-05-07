package com.unionware.once.viewmodel

import com.unionware.basicui.base.viewmodel.BaseCollectViewModel
import com.unionware.once.api.OnceApi
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.basic.BasicApi
import unionware.base.app.event.SingleLiveEvent
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject


@HiltViewModel
open class ElementNoteRecordViewModel @Inject constructor() : BaseCollectViewModel() {

    var elementNoteRecordLive: SingleLiveEvent<Any?> = SingleLiveEvent()


    @JvmField
    @Inject
    var api: OnceApi? = null

    /**
     * 生成工单生产故障记录
     */
    fun faultRecord(map: Map<String, Any>) {
        NetHelper.request(api?.ElementNoteReportAction(map),
            lifecycle, object : ICallback<Any?> {
                override fun onSuccess(data: Any?) {
                    elementNoteRecordLive.value = data
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }
}