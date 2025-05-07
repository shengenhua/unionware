package com.unionware.emes.viewmodel.process

import androidx.lifecycle.MutableLiveData
import com.unionware.lib_base.utils.ext.formatter
import com.unionware.virtual.model.ErrorSponsors
import dagger.hilt.android.lifecycle.HiltViewModel
import unionware.base.app.event.SingleLiveEvent
import unionware.base.model.bean.barcode.BarCodeBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ReportReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import javax.inject.Inject

@HiltViewModel
open class CastingProViewModel @Inject constructor() : ProcessViewModel() {
    /**
     * 保存 头部 view 数据
     */
    val processViewLiveData: MutableLiveData<MutableMap<String?, String?>?> = SingleLiveEvent(
        mutableMapOf()
    )

    /**
     * 固化数据
     */
    val oneBarLiveData: MutableLiveData<List<BarCodeBean>> = SingleLiveEvent()

    /**
     * 时长上报数据
     */
    val twoBarLiveData: MutableLiveData<List<BarCodeBean>> = SingleLiveEvent()

    /**
     * 是否二次汇报
     */
    val quadraticLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)


    /**
     * 保存 二次汇报 交互码
     */
    val twoSponsorsLiveData: MutableLiveData<MutableList<String>> =
        MutableLiveData<MutableList<String>>()

    /**
     * 时长上报
     */
    val timeUpdatedLiveData: MutableLiveData<String?> = MutableLiveData<String?>()

    /**
     * 扫描条码
     */
    override fun queryBarcode(filtersReq: FiltersReq, scene: String, name: String) {
        //防止短时间 请求多个
        if (barcode.isNullOrEmpty()) {
            barcode = filtersReq.filters["primaryCode"]?.toString()?:""
        } else if (barcode == filtersReq.filters["primaryCode"]) {
            return
        }

        mUIChangeLiveData.getShowLoadingViewEvent().postValue("")
        val time = System.currentTimeMillis().formatter()
        if (quadraticLiveData.value == true) {
            //yyyy-MM-dd HH:mm:ss.fff
            filtersReq.filters["feature"] = "TIMEUPDATE"
            twoSponsorsLiveData.value?.also {
                filtersReq.sponsors = it
            }
        } else {
            sponsorsLiveData.value?.also {
                filtersReq.sponsors = it
            }
        }
//        NetHelper.request<CommonListDataResp<BarCodeBean>>(api?.scanBarcode(
//            scene, name, filtersReq
//        ), lifecycle, object : ICallback<CommonListDataResp<BarCodeBean>?> {

        barcodeRequest(filtersReq, scene, name,
            object : ICallback<CommonListDataResp<BarCodeBean>> {
                override fun onSuccess(data: CommonListDataResp<BarCodeBean>?) {
                    barcode = null
                    if (data?.data?.isNotEmpty() == true) {
                        data.data?.get(0)?.also {
                            if (quadraticLiveData.value == true) {
                                it.endTime = time
                            } else {
                                it.startTime = time
                            }
                            barcodeLiveData.value = it
                        }
                    } else {
                        mUIChangeLiveData.getShowToastViewEvent().postValue("无查询数据")
                    }
                    mUIChangeLiveData.getShowLoadingViewEvent().postValue(null)
                }

                override fun onFailure(e: ApiException) {
                    barcodeELiveData.value = filtersReq.filters["primaryCode"]?.toString()?:""
                    barcode = null
                    onVMFailure(e, fFiltersReq = filtersReq)
                }
            })
    }

    /**
     * 时长上报
     */
    fun timeUpdated(reportReq: ReportReq) {
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        twoSponsorsLiveData.value?.also {
            reportReq.sponsors = it
        }
        NetHelper.request(api?.timeUpdated(reportReq),
            lifecycle,
            object : ICallback<String> {
                override fun onSuccess(data: String?) {
                    timeUpdatedLiveData.value = data
                    timeUpdatedLiveData.value?.also {
                        mUIChangeLiveData.getShowToastViewEvent().postValue("提交成功")
                        mUIChangeLiveData.getFinishActivityEvent().postValue(null)
                        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                    }
                }

                override fun onFailure(e: ApiException) {
                    timeUpdatedLiveData.value = null
                    ratifyLiveData.value = null
                    onVMFailure(e, reportReq)
                }
            })
    }

    override fun onVMFailure(e: ApiException, fReportReq: ReportReq?, fFiltersReq: FiltersReq?) {
        failureLiveData.value = e
        val code = e.code.toIntOrNull()
        if (code != null && code >= 90000 && code <= 99999) {
            fReportReq?.also {
                if (it.sponsors == null) {
                    it.sponsors = mutableListOf()
                }
                val codes: MutableList<String> = it.sponsors!!
                codes.add(e.data)
                it.sponsors = codes
                if (e.data == "TIMEUPDATEPASSTIVE") {
                    newSponsorsLiveData.value = e.data
                } else {
                    sponsorsLiveData.value = it.sponsors
                }
            }

            fFiltersReq?.also {
                if (it.sponsors?.isEmpty() == true) {
                    val codes: MutableList<String> = ArrayList(listOf(e.data))
                    codes.add(e.code)
                    it.sponsors = codes
                } else {
                    val codes: MutableList<String> = ArrayList()
                    codes.add(e.data)
                    it.sponsors = codes
                }
                if (e.data == "TIMEUPDATEPASSTIVE") {
                    newSponsorsLiveData.value = e.data
                } else {
                    sponsorsLiveData.value = it.sponsors
                }
            }
            if (e.data != "TIMEUPDATEPASSTIVE") {
                showErrorDialogViewEvent.value = ErrorSponsors(e.errorMsg).also {
                    it.reportReq = fReportReq
                    it.filtersReq = fFiltersReq
                }
            }
        } else {
            mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
        }
        mUIChangeLiveData.getShowLoadingViewEvent().postValue(null)
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
    }
}