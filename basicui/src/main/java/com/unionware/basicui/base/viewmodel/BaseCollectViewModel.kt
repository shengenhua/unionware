package com.unionware.basicui.base.viewmodel

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.api.FileServerApi
import unionware.base.api.basic.BasicApi
import unionware.base.app.event.SingleLiveEvent
import unionware.base.model.bean.barcode.BarCodeBean
import unionware.base.model.req.ReportReq
import javax.inject.Inject

@HiltViewModel
open class BaseCollectViewModel @Inject constructor() : BaseMESViewModel() {

    /**
     * 条码数量
     */
    val barcodeItemCountLiveData: SingleLiveEvent<Int> = SingleLiveEvent(0)

    /**
     * 条码扫描
     */
    val barcodeLiveData: MutableLiveData<BarCodeBean> = MutableLiveData<BarCodeBean>()

    /**
     * 扫描条码
     */
    open fun queryBarcode(
        filtersReq: unionware.base.model.req.FiltersReq,
        scene: String = "MES.Process.Electronic",
        name: String = "66960F0C0DE7EE",
    ) = Unit

    /**
     * 提交
     */
    open fun submitReport(reportReq: ReportReq) = Unit

}