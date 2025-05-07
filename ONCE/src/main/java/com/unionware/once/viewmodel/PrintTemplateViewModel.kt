package com.unionware.once.viewmodel

import androidx.lifecycle.MutableLiveData
import com.unionware.basicui.base.viewmodel.BaseCollectViewModel
import com.unionware.once.api.OnceApi
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.app.event.SingleLiveEvent
import unionware.base.model.bean.PrintTemplateBean
import unionware.base.model.req.PrintTemplateReq
import unionware.base.network.request
import javax.inject.Inject

/**
 * Author: sheng
 * Date:2025/3/12
 */
@HiltViewModel
open class PrintTemplateViewModel @Inject constructor() : BaseCollectViewModel() {

    /**
     * 扫描箱码,返回需要扫描的数据
     */
    val printTemplateLiveData: SingleLiveEvent<List<PrintTemplateBean>?> = SingleLiveEvent()
    val failureLiveData: SingleLiveEvent<String?> = SingleLiveEvent()

    @JvmField
    @Inject
    var api: OnceApi? = null

    fun getPrintTemplate(formId: String?) {
        api?.getPrintTemplate(PrintTemplateReq(formId))?.request(lifecycle) {
            success {
                printTemplateLiveData.value = it
            }
            failure {
                failureLiveData.value = it.errorMsg
            }
        }
    }
}