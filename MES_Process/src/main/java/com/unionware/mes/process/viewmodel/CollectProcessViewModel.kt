package com.unionware.mes.process.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.basicui.base.viewmodel.BaseCollectViewModel
import com.unionware.mes.process.ui.api.MESProcessApi
import com.unionware.virtual.model.ErrorSponsors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import unionware.base.app.event.SingleLiveEvent
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.UploadReq
import unionware.base.model.bean.barcode.BarCodeBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ReportReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.model.resp.FileResp
import unionware.base.model.resp.ReportResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import java.util.concurrent.CountDownLatch
import javax.inject.Inject


@HiltViewModel
open class CollectProcessViewModel @Inject constructor() : BaseCollectViewModel() {

    @JvmField
    @Inject
    var api: MESProcessApi? = null

    /**
     * 采集项目
     */
    val processCetLvDt: MutableLiveData<List<CollectMultiItem>> =
        MutableLiveData<List<CollectMultiItem>>()

    /**
     * 条码采集项目
     */
    val barcodeCetLvDt: MutableLiveData<List<CollectMultiItem>> =
        MutableLiveData<List<CollectMultiItem>>()

    /**
     * 保存 交互码
     */
    val sponsorsLiveData: MutableLiveData<MutableList<String>> =
        MutableLiveData<MutableList<String>>()

    /**
     * 保存 最新的交互码
     */
    val newSponsorsLiveData: SingleLiveEvent<String> = SingleLiveEvent()

    /**
     * 错误 提示
     */
    val failureLiveData: MutableLiveData<ApiException?> = MutableLiveData<ApiException?>()

    /**
     * 提交
     */
    val submitLiveData: MutableLiveData<ReportResp> = MutableLiveData<ReportResp>()

    /**
     * 条码扫描 失败 保存条码
     */
    val barcodeELiveData: SingleLiveEvent<String> = SingleLiveEvent()

    /**
     * 当前扫描的条码
     */
    var barcode: String? = null

    /**
     * 上传图片
     */
    val fileLiveData: MutableLiveData<FileResp?> = MutableLiveData<FileResp?>()

    /**
     * 获取采集方案
     */
    fun getCollectOption(
        filtersReq: FiltersReq,
        typeId: String,
        scene: String = "MES.Process",
        name: String = "6592C0EA0F63E5",
    ) {
        filtersReq.filters["typeId"] = typeId

        NetHelper.request<CommonListDataResp<Map<String, Any>>>(
            api?.queryCollect(
                scene, name, filtersReq
            ), lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    if (data?.data?.isEmpty() == false) {
                        data.data?.apply {
                            Gson().toJson(this[0]["FCollectItems"]).also {
                                if (it.isNullOrEmpty()) {
                                    setCollect(typeId, mutableListOf())
                                } else {
                                    setCollect(
                                        typeId, Gson().fromJson(
                                            it,
                                            object : TypeToken<List<CollectMultiItem?>?>() {}.type
                                        )
                                    )
                                }
                            }
                        }
                    } else {
                        setCollect(typeId, mutableListOf())
                    }
                }

                override fun onFailure(e: ApiException) {
                    setCollect(typeId, mutableListOf())
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }

    fun setCollect(typeId: String, collections: List<CollectMultiItem>) {
        when (typeId) {
            "1" -> {
                processCetLvDt.value = collections
            }

            "2" -> {
                barcodeCetLvDt.value = collections
            }
        }
    }


    /**
     * 扫描条码
     */
    override fun queryBarcode(
        filtersReq: FiltersReq,
        scene: String,
        name: String,
    ) {
        //防止短时间 请求多个
        if (barcode.isNullOrEmpty()) {
            barcode = filtersReq.filters["primaryCode"]?.toString() ?: ""
        } else if (barcode == filtersReq.filters["primaryCode"]) {
            return
        }
        sponsorsLiveData.value?.also {
            filtersReq.sponsors = it
        }
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        barcodeRequest(
            filtersReq,
            scene,
            name,
            object : ICallback<CommonListDataResp<BarCodeBean>> {
                override fun onSuccess(data: CommonListDataResp<BarCodeBean>?) {
                    if (data?.data?.isNotEmpty() == true) {
                        data.data?.get(0)?.also {
                            barcodeLiveData.value = it
                        }
                    } else {
                        barcodeELiveData.value = filtersReq.filters["primaryCode"]?.toString() ?: ""
                        mUIChangeLiveData.getShowToastViewEvent().postValue("无查询数据")
                    }
                    barcode = null
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }

                override fun onFailure(e: ApiException) {
                    barcodeELiveData.value = filtersReq.filters["primaryCode"]?.toString() ?: ""
                    barcode = null
                    onVMFailure(e, fFiltersReq = filtersReq)
                }
            })
    }

    open fun barcodeRequest(
        filtersReq: FiltersReq,
        scene: String = "MES.Process.Electronic",
        name: String = "66960F0C0DE7EE",
        callback: ICallback<CommonListDataResp<BarCodeBean>>,
    ) {
        NetHelper.request(
            api?.scanBarcodeAny(
                scene, name, filtersReq
            ), lifecycle, object : ICallback<Any?> {
                override fun onSuccess(data: Any?) {
                    val dataResp = Gson().fromJson<CommonListDataResp<BarCodeBean>>(
                        Gson().toJson(data),
                        (object : TypeToken<CommonListDataResp<BarCodeBean>?>() {}).type
                    )
                    callback.onSuccess(dataResp)
                }

                override fun onFailure(e: ApiException) {
                    callback.onFailure(e)
                }
            })
    }

    /**
     * 提交
     */
    override fun submitReport(reportReq: ReportReq) {
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        sponsorsLiveData.value?.also {
            reportReq.sponsors = it
        }
        NetHelper.request(
            api?.submitReport(reportReq),
            lifecycle,
            object : ICallback<List<ReportResp>> {
                override fun onSuccess(data: List<ReportResp>?) {
                    //
                    data?.also {
                        submitLiveData.value = it[0]
                    }
                    mUIChangeLiveData.getTTSEvent().postValue(SoundType.Default.SUBMIT_SUCCESS)
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }

                override fun onFailure(e: ApiException) {
                    mUIChangeLiveData.getTTSSucOrFailEvent().postValue(false)
                    failureLiveData.value = e
                    onVMFailure(e, reportReq)
                }
            })
    }


    protected open fun onVMFailure(
        e: ApiException, fReportReq: ReportReq? = null, fFiltersReq: FiltersReq? = null,
    ) {
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
                //弹出对话框
                showErrorDialogViewEvent.value = ErrorSponsors(e.errorMsg).also {
                    it.reportReq = fReportReq
                    it.filtersReq = fFiltersReq
                }
            }
        } else {
            mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
        }
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)

        failureLiveData.value = e
    }

    /**
     * 上传文件
     */
    suspend fun uploadFileReturn(name: String?, base64: String): FileResp? {
        val latch = CountDownLatch(1)
        withContext(context = Dispatchers.IO) {
            withContext(context = Dispatchers.Main) {
                NetHelper.request(api?.uploadFile(UploadReq().apply {
                    data = UploadReq.DataBean(name, base64, true)
                }), lifecycle, object : ICallback<FileResp> {
                    override fun onSuccess(data: FileResp?) {
                        fileLiveData.value = data
                        latch.countDown()
                        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                    }

                    override fun onFailure(e: ApiException) {
                        fileLiveData.value = FileResp().apply {
                            message = e.errorMsg
                        }
                        latch.countDown()
                        onVMFailure(e, null)
                    }
                })
            }
            latch.await()
        }
        return fileLiveData.value
    }

    suspend fun uploadFile(name: String?, base64: String) {
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        NetHelper.request(api?.uploadFile(UploadReq().apply {
            data = UploadReq.DataBean(name, base64, true)
        }), lifecycle, object : ICallback<FileResp> {
            override fun onSuccess(data: FileResp?) {
                fileLiveData.value = data
                mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
            }

            override fun onFailure(e: ApiException) {
                fileLiveData.value = null
                onVMFailure(e, null)
            }
        })
    }


}