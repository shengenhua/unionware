package com.unionware.once.viewmodel

import androidx.lifecycle.MutableLiveData
import unionware.base.model.req.FiltersReq
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.basic.BasicApi
import unionware.base.app.viewmodel.BaseViewModel
import unionware.base.model.req.BarcodePrintExportReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

@HiltViewModel
open class BarcodeReprintingViewModel @Inject constructor() : BaseViewModel() {
    @JvmField
    @Inject
    var api: BasicApi? = null
    var dataLiveData: MutableLiveData<Map<String, Any>> =
        MutableLiveData<Map<String, Any>>()
    var printData: MutableLiveData<String?> = MutableLiveData<String?>()
    open fun getScanBarcode(filtersReq: FiltersReq) {
        NetHelper.request<CommonListDataResp<Map<String, Any>>?>(
            api!!.query(
//            URLPath.BarcodeReprinting.PATH_PACK_SCENE,
//            URLPath.BarcodeReprinting.PATH_BARCODEREPRINTING_FORM_ID,
                "WMS.CodeReprint",
                "UNW_WMS_BARCODEMAIN",
                filtersReq
            ),
            lifecycle,
            object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    if (data != null && data.data != null && data.data.size > 0) {
                        dataLiveData.value = data.data[0]
                    } else {
                        dataLiveData.value = mapOf()
                        mUIChangeLiveData.getShowToastViewEvent().postValue("找不到该条码重新扫描")
                    }

                }

                override fun onFailure(e: ApiException) {
                    dataLiveData.value = mapOf()
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }

    open fun barcodePrintExportReq(scene: String?, req: BarcodePrintExportReq?) {
        mUIChangeLiveData.getShowLoadingViewEvent().postValue("正在打印中...")
        NetHelper.request<String>(
            api!!.barcodePrintExportReq(scene, req),
            lifecycle,
            object : ICallback<String?> {
                override fun onSuccess(bean: String?) {
                    if (bean != null && bean == "!PrinterRemoteService") {
                        mUIChangeLiveData.getShowToastViewEvent().postValue("已发送打印指令")
                    } else {
                        printData.value = bean
                        mUIChangeLiveData.getShowToastViewEvent().postValue("打印成功")
                    }
                    mUIChangeLiveData.getShowLoadingViewEvent().postValue(null)
                }

                override fun onFailure(e: ApiException) {
                    printData.value = ""
                    mUIChangeLiveData.getShowLoadingViewEvent().postValue(null)
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }
}