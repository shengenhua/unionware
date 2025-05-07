package com.unionware.basicui.base.viewmodel

import com.unionware.virtual.model.ErrorSponsors
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.app.event.SingleLiveEvent
import unionware.base.app.viewmodel.BaseViewModel
import javax.inject.Inject

@HiltViewModel
open class BaseMESViewModel @Inject constructor() : BaseViewModel() {
    var showErrorDialogViewEvent: SingleLiveEvent<ErrorSponsors> = SingleLiveEvent()


    open fun postFinishShowToast(toast: String) {
        postShowToastViewEvent(toast)
        postFinishActivityEvent()
        postShowTransLoadingViewEvent(false)
    }
}