package com.unionware.emes.viewmodel.process

import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.emes.bean.SpecialReportReq
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

@HiltViewModel
open class BadnessProViewModel @Inject constructor() : ProcessViewModel() {

    /**
     * 生产不良
     */
    fun repaired(reportReq: SpecialReportReq) {
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        NetHelper.request(
            api?.repaired(reportReq),
            lifecycle,
            object : ICallback<Any> {
                override fun onSuccess(data: Any?) {
                    mUIChangeLiveData.getTTSEvent().postValue(SoundType.Default.SUBMIT_SUCCESS)
                    mUIChangeLiveData.getShowToastViewEvent().postValue("提交成功")
                    mUIChangeLiveData.getFinishActivityEvent().postValue(null)
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }

                override fun onFailure(e: ApiException) {
                    ratifyLiveData.value = null
                    onVMFailure(e, null)
                }
            })
    }
}