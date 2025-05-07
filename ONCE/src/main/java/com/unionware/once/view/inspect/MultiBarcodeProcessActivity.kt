package com.unionware.once.view.inspect

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.chad.library.adapter4.BaseQuickAdapter
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.lxj.xpopup.XPopup
import com.unionware.basicui.base.activity.BaseProcessActivity
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.lib_base.utils.ext.formatter
import com.unionware.once.R
import com.unionware.once.adapter.HeardScanAdapter
import com.unionware.once.adapter.process.BaseMultiAdapter
import com.unionware.once.adapter.process.MultiBarCodeAdapter
import com.unionware.once.viewmodel.process.ProcessViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import unionware.base.app.utils.BitmapUtil
import unionware.base.app.utils.BitmapUtil.compress
import unionware.base.app.utils.GlideEngine
import unionware.base.ext.showToast
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.CollectSelectBean
import unionware.base.model.bean.barcode.MultiBarCodeBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ReportReq
import java.io.File
import java.util.stream.Collectors


/**
 *  采集方案的 工序
 */
open class MultiBarcodeProcessActivity<VM : ProcessViewModel> : BaseProcessActivity<VM>() {

    /**
     * 顶部显示
     */
    protected var topAdapter: ProcessAdapter? = null

    /**
     * 填写资料
     */
    protected var processAdapter: ProcessAdapter? = null

    /**
     * 扫描的条码
     */
    protected open var collectAdapter: BaseMultiAdapter<MultiBarCodeBean, *>? = null

    /**
     * 扫描框
     */
    protected var scanAdapter: HeardScanAdapter? = null

    protected open var cameraPictureFlow = MutableStateFlow<ActivityResult?>(null)
    protected open val cameraLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            cameraPictureFlow.value = result
        }

    override fun initViewObservable() {
        mViewModel.apply {
            barcodeObserve()
            submitObserve()
        }
    }

    private fun VM.submitObserve() {
        submitLiveData.observe(this@MultiBarcodeProcessActivity) {
            if (isPdaContinuousReport) {
                //连续上报
                collectAdapter?.submitList(null)
                barcodeItemCountLiveData.value = 0
                processAdapter?.clearData()
//                barcodeLiveData.value = null
            } else {
                postFinishShowToast("提交成功")
            }
        }
    }

    protected open fun VM.barcodeObserve() {
        barcodeLiveData.observe(this@MultiBarcodeProcessActivity) { it ->
            val bean = MultiBarCodeBean(it.code).apply {
                materialId = it.materialId
                materialCode = it.materialCode
                materialName = it.materialName
                materialSpec = it.materialSpec
                qty = it.qty
            }
            bean.collects = mutableListOf<CollectMultiItem>().apply {
                collectionLiveData.value?.forEach {
                    val item = CollectMultiItem()
                    item.code = it.code
                    item.value = it.value
                    item.name = it.name
                    item.id = it.id
                    item.colName = it.colName
                    item.colId = it.colId
                    item.colSeq = it.colSeq
                    item.colMethod = it.colMethod
                    item.colNumber = it.colNumber
                    item.tag = it.tag
                    item.stdValue = it.stdValue
                    add(item)
                }
            }
            collectAdapter?.add(0, bean)
            scanAdapter?.notifyItemChanged(0)
        }
    }

    override fun initData() {
        mViewModel.getCollectOption(FiltersReq(mapOf(Pair("jobId", jobId))), scene)
    }

    override fun initView() {
        super.initView()
        collectAdapter?.addOnItemOnItemClickListener(R.id.btnSelect) { baseQuickAdapter, view, i ->
            //选择弹窗
            val select = mutableListOf(CollectSelectBean.Y, CollectSelectBean.N)
            XPopup.Builder(this).asCenterList(
                baseQuickAdapter.items[i].colName ?: "请选择一项",
                select.stream().map { it.str }.collect(Collectors.toList()).toTypedArray()
            ) { position: Int, text: String? ->
                baseQuickAdapter.items[i].valueText = text
                baseQuickAdapter.items[i].value = select[position].toString()
                baseQuickAdapter.notifyItemChanged(i)
            }.show()
        }
        collectAdapter?.addOnItemOnItemClickListener(R.id.acTvFile) { baseQuickAdapter, view, i ->
            val strings = arrayOf("拍照", "从相册中选择")
            XPopup.Builder(this)
                .asBottomList("请选择照片", strings) { position: Int, text: String? ->
                    /*if (position == 0) {
                        PictureSelector.create(this@MultiBarcodeProcessActivity)
                            .openCamera(SelectMimeType.ofImage())
                            .setCompressEngine(compressFileEngine)
                            .forResult(onResultCallbackListener(baseQuickAdapter, i))
                    } else {
                        PictureSelector.create(this@MultiBarcodeProcessActivity)
                            .openGallery(SelectMimeType.ofImage())
                            .setImageEngine(GlideEngine.InstanceHolder.instance)
                            .setCompressEngine(compressFileEngine)
//                            .setMaxSelectNum(9)
                            .setSelectionMode(SelectModeConfig.SINGLE)
                            .forResult(onResultCallbackListener(baseQuickAdapter, i))
                    }*/
                    PictureSelector.create(this@MultiBarcodeProcessActivity)
                        .apply {
                            if (position == 0) {
                                openCamera(SelectMimeType.ofImage()).apply {
                                    setCompressEngine(compressFileEngine)
                                    forResult(onResultCallbackListener(baseQuickAdapter, i))
                                }
                            } else {
                                openGallery(SelectMimeType.ofImage()).apply {
                                    setImageEngine(GlideEngine.InstanceHolder.instance)
                                    setSelectionMode(SelectModeConfig.SINGLE)
                                    setCompressEngine(compressFileEngine)
                                    forResult(onResultCallbackListener(baseQuickAdapter, i))
                                }
                            }
                        }

                }.show()
        }
        scanAdapter?.setOnEditorActionListener {
            scanQuery(it)
        }
        collectAdapter?.addOnItemChildClickListener(R.id.tbDelete) { baseQuickAdapter, view, i ->
            baseQuickAdapter.removeAt(i)
            mViewModel.barcodeItemCountLiveData.value = binding?.rvTail?.adapter?.itemCount ?: 0
        }
    }

    protected open fun scanQuery(it: String) {
        if (collectAdapter?.items?.firstOrNull { item -> item.code == it } != null) {
            "当前条码已扫描,请勿重复扫描".showToast()
            scanAdapter?.notifyItemChanged(0)
            return
        }
        mViewModel.queryBarcode(queryFilters(it), scene)
    }

    private var compressFileEngine = CompressFileEngine { context, source, call ->
        Luban.with(context).load(source).ignoreBy(100)
            .setCompressListener(object : OnNewCompressListener {
                override fun onStart() = Unit

                override fun onSuccess(source: String, compressFile: File) {
                    call?.onCallback(source, compressFile.absolutePath)
                }

                override fun onError(source: String, e: Throwable) {
                    call?.onCallback(source, null)
                }
            }).launch()
    }

    /**
     * 拍照返回
     */
    private fun onResultCallbackListener(
        baseQuickAdapter: BaseQuickAdapter<CollectMultiItem, *>,
        position: Int,
    ) = object : OnResultCallbackListener<LocalMedia> {
        override fun onResult(result: ArrayList<LocalMedia>?) {
            result?.forEach {
                val path: String =
                    if (it.compressPath.isNullOrEmpty()) it.realPath else it.compressPath
                lifecycleScope.launch {
                    mViewModel.postShowTransLoadingViewEvent(true)
                    mViewModel.uploadFileReturn(
                        it.fileName,
//                        "JPEG${System.currentTimeMillis().formatter("yyyyMMddHHmmss")}.png",
                        BitmapUtil.bitmapToBase64(BitmapFactory.decodeFile(path))
                    )?.apply {
                        lifecycleScope.launch {
                            if (id.isNullOrEmpty()) {
                                message?.showToast()
                            } else {
                                baseQuickAdapter.items[position].value = id
                                baseQuickAdapter.items[position].valueText = "已上传附件"
                                baseQuickAdapter.notifyItemChanged(position)
                            }
                            mViewModel.postShowTransLoadingViewEvent(false)
                        }
                    }
                }
            }
        }

        override fun onCancel() = Unit
    }

    private fun updateImage(
        intent: Intent, baseQuickAdapter: BaseQuickAdapter<CollectMultiItem, *>, i: Int, uri: Uri?,
    ) {
        cameraPictureFlow.value = null
        cameraLauncher.launch(intent)
        lifecycleScope.launch {
            cameraPictureFlow.collectLatest {
                it?.also {
                    withContext(Dispatchers.IO) {
                        //切换 io线程 转换 base64 比较久
                        val imageUri: Uri = uri ?: it.data?.data ?: return@withContext
                        mViewModel.mUIChangeLiveData.getShowTransLoadingViewEvent().postValue(true)
                        // 使用Uri处理图片，例如显示在ImageView中
                        val bitmap =
                            BitmapUtil.getImageFromUri(this@MultiBarcodeProcessActivity, imageUri)
                        val bitmapName = BitmapUtil.getFileNameFromUri(imageUri)
                        mViewModel.uploadFileReturn(
                            "JPEG${System.currentTimeMillis().formatter("yyyyMMddHHmmss")}.png",
                            BitmapUtil.bitmapToBase64(compress(bitmap))
                        )?.apply {
                            withContext(Dispatchers.Main) {
                                if (id.isNullOrEmpty()) {
                                    message?.showToast()
                                } else {
                                    baseQuickAdapter.items[i].value = id
                                    baseQuickAdapter.items[i].valueText = "已上传附件"
                                    baseQuickAdapter.notifyItemChanged(i)
                                }
                            }
                        }
                    }
                    cancel()
                }
            }
        }
    }

    override fun onActionSubmitConfirm() {
        if (collectAdapter?.items.isNullOrEmpty()) {
            ("无提交的条码数据，请检查！").showToast()
            return
        }
        processAdapter?.items?.forEach {
            if (it.isRequired && (it.value == null || it.value!!.isEmpty())) {
                "${it.title}不允许为空!".showToast()
                return@onActionSubmitConfirm
            }
        }
        submitReport()
    }

    protected open fun submitReport() {
        mViewModel.submitReport(ReportReq().apply {
            data = mutableListOf(ReportReq.DataReq().apply {
                jobId = this@MultiBarcodeProcessActivity.jobId
                taskId = this@MultiBarcodeProcessActivity.taskId
                params = HashMap<String, Any>().apply {
                    putAll(mapOf(Pair("items", getItems() as Any)))
                    processAdapter?.items?.forEach {
                        if (it.key?.isNotEmpty() == true && it.value?.isNotEmpty() == true) {
                            put(it.key ?: "", it.id ?: it.value as Any)
                        }
                    }
                }
            })
        })
    }

    /**
     * 扫描条码 上报的数据
     */
    protected open fun queryFilters(barcode: String): FiltersReq = FiltersReq(
        mapOf(
            Pair("primaryCode", barcode), Pair("taskId", taskId)
        )
    )

    /**
     * 获取条码列表中的 数据
     */
    protected open fun getItems(): List<Map<String, Any?>>? {//inspects
        return collectAdapter?.items?.map {
            mapOf(
                Pair("code", it.code),
                Pair("qty", it.qty ?: "0"),
                Pair("collects", it.collects?.stream()?.map { collect ->
                    mapOf(
                        Pair("itemId", collect.colId),
                        Pair("itemValue", collect.value)
                    )
                }?.collect(Collectors.toList()))
            )
        }
    }

    override fun initBaseViewObservable() {
        super.initBaseViewObservable()
        mViewModel.barcodeELiveData.observe(this) {
            scanAdapter?.notifyItemChanged(0)
        }
    }

    override fun heardProcessAdapter(): ProcessAdapter {
        return super.heardProcessAdapter().apply { processAdapter = this }
    }

    override fun middleAdapter(): RecyclerView.Adapter<ViewHolder>? {
        scanAdapter = HeardScanAdapter()
        return scanAdapter
    }

    override fun tailAdapter(): RecyclerView.Adapter<ViewHolder> {/* collectAdapter = collectAdapter ?: MultiBarCodeAdapter()
        return collectAdapter?.let {
            scanAdapter = HeardScanAdapter()
            val helper = QuickAdapterHelper.Builder(it).build()
            helper.addAfterAdapter(scanAdapter!!)

            return helper.adapter
        }!!*/
        collectAdapter = collectAdapter ?: MultiBarCodeAdapter()
        return collectAdapter as MultiBarCodeAdapter
    }
}