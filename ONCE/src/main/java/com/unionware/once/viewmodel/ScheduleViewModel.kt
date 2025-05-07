package com.unionware.once.viewmodel

import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.once.viewmodel.process.ProcessViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.app.event.SingleLiveEvent
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject


@HiltViewModel
open class ScheduleViewModel @Inject constructor() : ProcessViewModel() {

    var updateJobScheduleLive: SingleLiveEvent<Any?> = SingleLiveEvent()

    //固定 name 6617982A9DFDAC
    fun updateJobSchedule(map: Map<String, Any>) {
        NetHelper.request(api?.updateJobSchedule(map),
            lifecycle, object : ICallback<Any?> {
                override fun onSuccess(data: Any?) {
                    updateJobScheduleLive.value = data
                    mUIChangeLiveData.getTTSEvent().postValue(SoundType.Default.SUBMIT_SUCCESS)
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }
}