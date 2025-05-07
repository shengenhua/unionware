package com.unionware.emes.view.dialog

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.ConfirmPopupView
import com.unionware.emes.R
import com.unionware.emes.adapter.process.ProcessMultiAdapter
import com.unionware.emes.databinding.LayoutBarCodeNumBinding
import com.unionware.emes.viewmodel.process.ProcessViewModel
import kotlinx.coroutines.launch
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import unionware.base.app.utils.BitmapUtil
import unionware.base.app.utils.GlideEngine
import unionware.base.ext.showToast
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.CollectSelectBean
import unionware.base.model.bean.barcode.MultiBarCodeBean
import unionware.base.model.resp.ChecketReq
import java.io.File
import java.util.stream.Collectors

/**
 * 采集项目
 */
class CollectionPop(
    context: Context,
    val title: String,
    val taskId: String,
    private val gasBarcode: String,
    val bean: MultiBarCodeBean,
    val viewModel: ProcessViewModel,
) : ConfirmPopupView(context, R.layout.collection_pop_emes) {

    override fun onCreate() {
        super.onCreate()
        initView()
        initData()
    }


    var adapter: ProcessMultiAdapter? = null

    private fun initView() {
        adapter = ProcessMultiAdapter()
        val barcodeNumBinding =
            DataBindingUtil.bind<LayoutBarCodeNumBinding>(findViewById(R.id.clFeature))
        barcodeNumBinding?.item = bean
        barcodeNumBinding?.clBarcodeNum?.visibility = GONE

        val recyclerView = findViewById<RecyclerView>(R.id.rv_list)
        findViewById<TextView>(R.id.tvTitle).text = title
        findViewById<TextView>(R.id.tvBarCode).text = "条码: ${bean.code}"
        findViewById<TextView>(R.id.tvGasName).text = "气体: ${bean.gasName}"
        findViewById<TextView>(R.id.tvGasBarcode).text = "气体物料条码: $gasBarcode"
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        findViewById<TextView>(R.id.tv_confirm).setOnClickListener {
            adapter?.items?.forEach {
                if (it.value.isNullOrEmpty()) {
                    "${it.colName}不能为空!".showToast()
                    return@setOnClickListener
                }
            }
            //调用接口


            viewModel.barcodeChecked(ChecketReq().apply {
                data = mutableListOf<Map<String, Any?>>().apply {
                    add(mutableMapOf<String, Any?>(
                        "taskId" to taskId,
                        "gasId" to bean.gasId,
                        "gasBarcode" to gasBarcode,
                        "barcode" to bean.code
                    ).apply {
                        putAll(mapOf(Pair("collects", getItems() as Any)))
                    })
                }
            }, bean)
        }

        adapter?.addOnItemChildClickListener(R.id.btnSelect) { baseQuickAdapter, view, i ->
            //选择弹窗
            val select =
                mutableListOf(CollectSelectBean.Y, CollectSelectBean.N, CollectSelectBean.W)
            XPopup.Builder(context).asCenterList(
                baseQuickAdapter.items[i].colName ?: "请选择一项",
                select.stream().map { it.str }.collect(Collectors.toList()).toTypedArray()
            ) { position: Int, text: String? ->
                baseQuickAdapter.items[i].valueText = text
                baseQuickAdapter.items[i].value = select[position].toString()
                baseQuickAdapter.notifyItemChanged(i)
            }.show()
        }

        adapter?.addOnItemChildClickListener(R.id.acTvFile) { baseQuickAdapter, view, i ->
            val strings = arrayOf("拍照", "从相册中选择")
            XPopup.Builder(context)
                .asBottomList("请选择照片", strings) { position: Int, text: String? ->
                    PictureSelector.create(context)
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

        adapter?.submitList(bean.collects)
    }

    fun getItems(): List<Map<String, String?>>? {
        return adapter?.items?.map {
            mapOf(
                Pair("itemId", it.colId),
                Pair("itemValue", it.value)
            )
        }
    }

    private fun initData() = Unit


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
                    viewModel.mUIChangeLiveData.getShowLoadingViewEvent().postValue("上传中...")
                    viewModel.uploadFileReturn(
                        it.fileName,
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
                            viewModel.mUIChangeLiveData.getShowLoadingViewEvent().postValue(null)
                        }
                    }
                }
            }
        }

        override fun onCancel() = Unit
    }
}