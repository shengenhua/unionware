package com.unionware.emes.view.process.base

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
import com.unionware.emes.R
import com.unionware.emes.adapter.process.ProcessMultiAdapter
import unionware.base.model.bean.CollectMultiItem
import com.unionware.emes.viewmodel.process.ProcessViewModel
import com.unionware.lib_base.utils.ext.formatter
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
import unionware.base.model.bean.CollectSelectBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ReportReq
import java.io.File
import java.util.stream.Collectors

open class OptionProcessActivity<VM : ProcessViewModel> : BaseProcessActivity<VM>() {
    /**
     * 顶部显示
     */
    protected var topAdapter: ProcessAdapter? = null

    /**
     * 采集的项目
     */
    protected var optionAdapter: ProcessMultiAdapter? = null

    protected open var cameraPictureFlow = MutableStateFlow<ActivityResult?>(null)

    //    protected open var cameraPictureFlow = flow<ActivityResult> {  }
    protected open val cameraLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            cameraPictureFlow.value = result
        }

    override fun initViewObservable() {
        mViewModel.apply {
            collectionLiveData.observe(this@OptionProcessActivity) {
                if (optionAdapter?.items.isNullOrEmpty()) {
                    optionAdapter?.submitList(collectionLiveData.value ?: emptyList())
                }
            }
            submitLiveData.observe(this@OptionProcessActivity) {
//                postFinishShowToast("提交成功")
                if (isPdaContinuousReport) {
                    //连续上报
                    optionAdapter?.submitList(null)
                    barcodeLiveData.value = null
                    barcodeItemCountLiveData.value = 0
                } else {
                    postFinishShowToast("提交成功")
                }
            }
            fileLiveData.observe(this@OptionProcessActivity) {

            }
        }
    }

    override fun initData() {
        mViewModel.getCollectOption(FiltersReq(mapOf(Pair("jobId", jobId))), scene)
    }

    override fun initView() {
        super.initView()
        optionAdapter?.addOnItemChildClickListener(R.id.btnSelect) { baseQuickAdapter, view, i ->
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
        optionAdapter?.addOnItemChildClickListener(R.id.acTvFile) { baseQuickAdapter, view, i ->
            val strings = arrayOf("拍照", "从相册中选择")
            XPopup.Builder(this)
                .asBottomList("请选择照片", strings) { position: Int, text: String? ->
                    /*if (position == 0) {
                        //拍照获取文件上传
                        TakePictureUtil.dispatchTakePictureIntent(this) { intent, uri ->
                            updateImage(intent, baseQuickAdapter, i, uri)
                        }
                    } else {
                        TakePictureUtil.openGallery { intent, uri ->
                            updateImage(intent, baseQuickAdapter, i, null)
                        }
                    }*/
                    if (position == 0) {
                        PictureSelector.create(this@OptionProcessActivity)
                            .openCamera(SelectMimeType.ofImage())
                            .setCompressEngine(compressFileEngine)
                            .forResult(onResultCallbackListener(baseQuickAdapter, i))
                    } else {
                        PictureSelector.create(this@OptionProcessActivity)
                            .openGallery(SelectMimeType.ofImage())
                            .setImageEngine(GlideEngine.InstanceHolder.instance)
                            .setCompressEngine(compressFileEngine)
//                            .setMaxSelectNum(9)
                            .setSelectionMode(SelectModeConfig.SINGLE)
                            .forResult(onResultCallbackListener(baseQuickAdapter, i))
                    }
                }.show()
        }
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
        position: Int
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
        intent: Intent, baseQuickAdapter: BaseQuickAdapter<CollectMultiItem, *>, i: Int, uri: Uri?
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
                            BitmapUtil.getImageFromUri(this@OptionProcessActivity, imageUri)
//                        val bitmapName = BitmapUtil.getFileNameFromUri(imageUri)
                        mViewModel.uploadFileReturn(
                            "JPEG${System.currentTimeMillis().formatter("yyyyMMddHHmmss")}.png",
                            BitmapUtil.bitmapToBase64(
                                compress(bitmap)
                            )
                        )?.apply {
                            withContext(Dispatchers.Main) {
                                baseQuickAdapter.items[i].value = id
                                baseQuickAdapter.items[i].valueText = "已上传文件"
                                baseQuickAdapter.notifyItemChanged(i)
                            }
                        }
                    }
                    cancel()
                }
            }
        }
    }


    override fun onActionSubmitConfirm() {
        headAdapter?.items?.forEach {
            if (it.isRequired && it.value.isNullOrEmpty()) {
                if (it.type == 2) {
                    "请选择${it.title}".showToast()
                } else {
                    "${it.title}不允许为空!".showToast()
                }
                return@onActionSubmitConfirm
            }
        }
        optionAdapter?.items?.forEach {
            if (it.value.isNullOrEmpty()) {
                "${it.colName} 不允许为空!".showToast()
                return@onActionSubmitConfirm
            }
        }
        submitReport()
    }

    /**
     * 提交数据
     */
    protected open fun submitReport() {
        mViewModel.submitReport(ReportReq().apply {
            data = mutableListOf(ReportReq.DataReq().apply {
                jobId = this@OptionProcessActivity.jobId
                taskId = this@OptionProcessActivity.taskId
                params = HashMap<String, Any>().apply {
                    putAll(mapOf(Pair("collects", getCollects() as Any)))
                    headAdapter?.items?.forEach {
                        if (it.key?.isNotEmpty() == true && it.value?.isNotEmpty() == true) {
                            put(it.key ?: "", it.id ?: it.value as Any)
                        }
                    }
                }
            })
        })
    }

    /**
     * 获取条码列表中的 数据
     */
    protected open fun getCollects(): List<Map<String, String?>>? {
        return optionAdapter?.items?.map {
            mapOf(
                Pair("itemId", it.colId),
                Pair("itemValue", it.value)
            )
        }
    }

    override fun tailAdapter(): RecyclerView.Adapter<ViewHolder> {
        optionAdapter = optionAdapter ?: ProcessMultiAdapter()
        return optionAdapter as ProcessMultiAdapter
    }
}