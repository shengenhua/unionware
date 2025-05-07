package com.unionware.emes.viewmodel.process

import com.unionware.basicui.base.viewmodel.BaseDetailsViewModel
import com.unionware.emes.api.EMESApi
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.app.event.SingleLiveEvent
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

@HiltViewModel
class ProcessDetailsVM @Inject constructor(private val emesApi: EMESApi) : BaseDetailsViewModel(emesApi) {
    var updateJobScheduleLive: SingleLiveEvent<Any?> = SingleLiveEvent()

    //固定 name 6617982A9DFDAC
    fun reportProgress(map: Map<String, Any>) {
        NetHelper.request(emesApi.reportProgress(map),
            lifecycle, object : ICallback<Any?> {
                override fun onSuccess(data: Any?) {
                    updateJobScheduleLive.value = data
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }
}