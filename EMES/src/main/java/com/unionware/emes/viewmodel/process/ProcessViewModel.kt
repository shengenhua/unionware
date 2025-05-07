package com.unionware.emes.viewmodel.process

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.basicui.base.viewmodel.BaseCollectViewModel
import com.unionware.emes.api.EMESApi
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
import unionware.base.model.resp.ChecketReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.model.resp.FileResp
import unionware.base.model.resp.ReportResp
import unionware.base.network.NetHelper
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import unionware.base.network.request
import java.util.concurrent.CountDownLatch
import javax.inject.Inject


@HiltViewModel
open class ProcessViewModel @Inject constructor() : BaseCollectViewModel() {

    /**
     * 条码扫描 失败 保存条码
     */
    val barcodeELiveData: SingleLiveEvent<String> = SingleLiveEvent()

    /**
     * 采集项目
     */
    val collectionLiveData: MutableLiveData<List<CollectMultiItem>> =
        MutableLiveData<List<CollectMultiItem>>()

    /**
     * 提交
     */
    val submitLiveData: MutableLiveData<ReportResp> = MutableLiveData<ReportResp>()

    /**
     * 标定条码检测
     */
    val barcodeCheckLive: SingleLiveEvent<BarCodeBean?> = SingleLiveEvent()

    /**
     * 特批人校验
     */
    val ratifyLiveData: MutableLiveData<String?> = MutableLiveData<String?>()

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
     * 上传图片
     */
    val fileLiveData: MutableLiveData<FileResp?> = MutableLiveData<FileResp?>()


    /**
     * 打印 PDF 套打模板文件
     */
    val filePrintLiveData: SingleLiveEvent<List<MutableMap<String, String>>?> = SingleLiveEvent()

    /**
     * 当前扫描的条码
     */
    var barcode: String? = null

    @JvmField
    @Inject
    var api: EMESApi? = null

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

    open fun barcodeRequestAny(
        filtersReq: FiltersReq,
        scene: String = "MES.Process.Electronic",
        name: String = "66960F0C0DE7EE",
        callback: ICallback<Any>,
    ) {
        NetHelper.request(
            api?.scanBarcodeAny(
                scene, name, filtersReq
            ), lifecycle, object : ICallback<Any?> {
                override fun onSuccess(data: Any?) {
                    callback.onSuccess(data)
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
        if (mUIChangeLiveData.getShowTransLoadingViewEvent().value == true) {
            return
        }
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


    /**
     * 获取采集方案
     */
    fun getCollectOption(
        filtersReq: FiltersReq, scene: String = "MES.Process", name: String = "6592C0EA0F63E5",
    ) {
        NetHelper.request<CommonListDataResp<Map<String, Any>>>(
            api?.queryCollect(
                scene, name, filtersReq
            ), lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    if (data?.data?.isEmpty() == false) {
                        Gson().toJson(data.data[0]["FCollectItems"]).apply {
                            collectionLiveData.value = Gson().fromJson(
                                this, object : TypeToken<List<CollectMultiItem?>?>() {}.type
                            )
                        }
//                    collectionLiveData.value = data.data
                    } else {
                        mUIChangeLiveData.getShowToastViewEvent().postValue("无采集方案")
                    }
                }

                override fun onFailure(e: ApiException) {
                    failureLiveData.value = e
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                }
            })
    }

    /**
     * 获取检验方案
     */
    fun getInspectOption(
        filtersReq: FiltersReq, scene: String = "MES.Process", name: String = "6592C0EA0F63E4",
    ) {
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        NetHelper.request<CommonListDataResp<Map<String, Any>>>(
            api?.queryCollect(
                scene, name, filtersReq
            ), lifecycle, object : ICallback<CommonListDataResp<Map<String, Any>>?> {
                override fun onSuccess(data: CommonListDataResp<Map<String, Any>>?) {
                    if (data?.data?.isEmpty() == false) {
                        Gson().toJson(data.data[0]["FCheckItems"]).apply {
                            collectionLiveData.value = Gson().fromJson(
                                this, object : TypeToken<List<CollectMultiItem?>?>() {}.type
                            )
                        }
//                    collectionLiveData.value = data.data
                    } else {
                        barcodeELiveData.value = ""
                        mUIChangeLiveData.getShowToastViewEvent().postValue("无检验方案")
                    }
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }

                override fun onFailure(e: ApiException) {
                    barcodeELiveData.value = ""
                    failureLiveData.value = e
                    mUIChangeLiveData.getShowToastViewEvent().postValue(e.errorMsg)
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }
            })
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

    /**
     * 套打模板
     */
    fun getPrint(map: MutableMap<String, String>) {
        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        api?.getPrintTemplate(map)?.request(lifecycle) {
            success {
                filePrintLiveData.value = it
                mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
            }
            failure {
                filePrintLiveData.value = null
                onVMFailure(it, null)
            }
        }
        /*NetHelper.request(fileServerApi?.getPrintTemplate(map),
            lifecycle,
            object : ICallback<List<MutableMap<String, String>>?> {
                override fun onSuccess(data: List<MutableMap<String, String>>?) {
                    filePrintLiveData.value = data
                    mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(false)
                }

                override fun onFailure(e: ApiException) {

                }
            })*/
    }

    /**
     * 标定条码检测
     */
    fun barcodeChecked(checketReq: ChecketReq, barCodeBean: BarCodeBean) {
        mUIChangeLiveData.getShowLoadingViewEvent().postValue("校验中...")
//        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        NetHelper.request(
            api?.barcodeCheck(checketReq),
            lifecycle,
            object : ICallback<List<ReportResp>> {
                override fun onSuccess(data: List<ReportResp>?) {
                    barcodeCheckLive.value = barCodeBean
                    mUIChangeLiveData.getShowLoadingViewEvent().postValue(null)
                    mUIChangeLiveData.getTTSEvent().postValue(SoundType.Default.SUCCESS)
                }

                override fun onFailure(e: ApiException) {
                    barcodeCheckLive.value = null
                    mUIChangeLiveData.getShowLoadingViewEvent().postValue(null)
                    onVMFailure(e, null)
                }
            })
    }

    /**
     * 特批人校验
     */
    fun ratifyChecked(ratifyId: String, ratifyPassword: String) {
        ratifyChecked(
            mapOf(
                Pair("ratifyId", ratifyId), Pair("ratifyPassword", ratifyPassword)
            )
        )
    }

    fun ratifyChecked(filters: Map<String, String>) {
        mUIChangeLiveData.getShowLoadingViewEvent().postValue("校验中...")
//        mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
        NetHelper.request(api?.ratifyChecked(filters), lifecycle, object : ICallback<String> {
            override fun onSuccess(data: String?) {
                ratifyLiveData.value = filters.get("ratifyId")
                mUIChangeLiveData.getShowLoadingViewEvent().postValue(null)
            }

            override fun onFailure(e: ApiException) {
                ratifyLiveData.value = null
                mUIChangeLiveData.getShowLoadingViewEvent().postValue(null)
                onVMFailure(e, null)
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
}