package com.unionware.once.view

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.tamsiree.rxkit.RxFileTool
import com.tencent.mmkv.MMKV
import com.unionware.once.R
import com.unionware.once.app.RouterOncePath
import com.unionware.once.databinding.BarcodeReprintingActivityBinding
import com.unionware.once.viewmodel.BarcodeReprintingViewModel
import com.unionware.printer.FileUtil
import com.unionware.printer.PrintUtils
import com.unionware.printer.print.PermissionUtils
import com.unionware.printer.print.PrinterInterface
import com.unionware.printer.print.ThreadPoolManager
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.app.utils.ToastUtil.showToast
import unionware.base.app.utils.ToastUtil.showToastCenter
import unionware.base.app.view.base.BaseMvvmToolbarActivity
import unionware.base.model.bean.PrintTemplateBean
import unionware.base.model.req.BarcodePrintExportReq
import unionware.base.model.req.FiltersReq
import java.io.File
import java.io.IOException
import java.math.BigDecimal

@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_BARCODE_REPRINTING)
open class BarcodeReprintingActivity :
    BaseMvvmToolbarActivity<BarcodeReprintingActivityBinding, BarcodeReprintingViewModel>() {
    /**
     * 场景码
     */
    @JvmField
    @Autowired(name = "scene")
    var scene: String = ""

    /**
     * 标题
     */
    @JvmField
    @Autowired(name = "title")
    var title: String = ""
    private var kv: MMKV? = null
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var printTemplateBean: PrintTemplateBean? = null
    private var printerInterface: PrinterInterface? = null
    private val callBack = PrinterInterface.PrintCallBack { msg, type ->
        when (type) {
            0 -> setState(msg, Color.YELLOW)
            2 -> setState(msg, Color.GREEN)
            3 -> setState(msg, Color.RED)
        }
    }

    open fun setState(msg: String, color: Int) {
        runOnUiThread {
            binding?.let {
                it.tvPrintState.text = msg
                it.tvPrintState.setBackgroundColor(color)
            }

        }
    }

    open fun connectPrint() {
        try {
            printerInterface = PrintUtils.connectPrint(this, callBack)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onBindVariableId(): MutableList<Pair<Int, Any>> {
        return mutableListOf()
    }

    override fun initViewObservable() {
        mViewModel.dataLiveData.observe(this) {
            if (it.isNotEmpty()) {
                //判断是否选择模版,选了模版自动提交
                if (printTemplateBean != null && printTemplateBean!!.tempId.isNotEmpty()) {
                    barcodePrintExportReq(it)
                }
            } else {
                etRequestFocus()
            }
        }
        mViewModel.printData.observe(this) {
            binding!!.etScanInput.setText("")
            if (!it.isNullOrEmpty()) {
                if (!PermissionUtils.hasPermissions(
                        this@BarcodeReprintingActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    showToast("没有文件读取权限")
                } else {
                    ThreadPoolManager.getInstance().addTask {
                        FileUtil.SaveToPDF(it, this)
                        FileUtil.DeZip(this)
                        printPDF(RxFileTool.listFilesInDir(mContext.filesDir.toString() + "/pdf_android"))
                    }
                }
            }
        }
    }

    open fun barcodePrintExportReq(it: Map<String, Any>) {
        //条码打印处理
        val req = BarcodePrintExportReq().apply {
            items = mutableListOf(mutableMapOf<String?, Any?>().apply {
                put("code", BigDecimal(it["id"].toString()).stripTrailingZeros().toPlainString())
                put("template", printTemplateBean!!.tempId)
            })
            params = mutableMapOf<String?, Any?>().apply {
                put("count", 1)
                put("type", "AABB")
                put("template", "")
            }
        }
        Log.e("测试", "11")
        mViewModel.barcodePrintExportReq(scene, req)
    }

    open fun printPDF(files: List<File?>?) {
        try {
            if (printerInterface != null) {
                printerInterface!!.print(files, 1)
                RxFileTool.delAllFile(mContext.filesDir.toString() + "/pdf_android")
            }
        } catch (e: IOException) {
            showToast("打印报错" + e.message)
        } catch (e: Exception) {
            showToast("打印报错" + e.message)
        }
    }

    override fun onBindLayout(): Int {
        return R.layout.barcode_reprinting_activity
    }

    override fun initView() {
        setTitle(title)
        etRequestFocus()
        kv = MMKV.mmkvWithID("app")
        if (kv!!.getString(scene + "_tempId", "")!!
                .isNotEmpty() && kv!!.getString(scene + "_tempName", "")!!.isNotEmpty()
        ) {
            printTemplateBean = PrintTemplateBean()
            printTemplateBean!!.tempId = kv!!.getString(scene + "_tempId", "")
            printTemplateBean!!.tempName = kv!!.getString(scene + "_tempName", "")
            binding!!.etTemplateInput.setText(printTemplateBean!!.tempName)
        }
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val data = result.data //int code = result.getResultCode();
                if (data != null) {
                    printTemplateBean = PrintTemplateBean()
                    printTemplateBean!!.tempId = data.getStringExtra("tempId")
                    printTemplateBean!!.tempName = data.getStringExtra("tempName")
                    binding!!.etTemplateInput.setText(printTemplateBean!!.tempName)
                    kv!!.putString(scene + "_tempId", printTemplateBean!!.tempId)
                    kv!!.putString(scene + "_tempName", printTemplateBean!!.tempName)
                    if (binding!!.etScanInput.text.toString().isNotEmpty() && mViewModel.dataLiveData.value != null && mViewModel.dataLiveData.value!!.isNotEmpty() && mViewModel.dataLiveData.value!!.containsKey(
                            "id"
                        )
                    ) {
                        barcodePrintExportReq(mViewModel.dataLiveData.value!!)
                    }
                }
            }
        binding?.let {
            it.etScanInput.setOnEditorActionListener { v, actionId, event ->
                if (v.text != null && v.text.toString().isNotEmpty()) {
                    val filtersReq = FiltersReq()
                    val filters: MutableMap<String, Any> = HashMap()
                    filters["primaryCode"] = v.text.toString()
                    filtersReq.filters = filters
                    mViewModel.getScanBarcode(filtersReq)
                } else {
                    showToastCenter("请扫描条码")
                }
                return@setOnEditorActionListener false
            }
            it.ivBaseInfoQuery.setOnClickListener {
                val intent = Intent(this, PrintTemplateListActivity::class.java)
//                intent.putExtra("formId", URLPath.BarcodeReprinting.PATH_BARCODEREPRINTING_FORM_ID)
                intent.putExtra("formId", "UNW_WMS_BARCODEMAIN")
                launcher!!.launch(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        printerInterface?.stopHeartBeat()
    }

    override fun initData() {
        connectPrint()
    }

    open fun etRequestFocus() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding!!.etScanInput.requestFocus()
            binding!!.etScanInput.setSelection(0, binding!!.etScanInput.length())
        }, 50)
    }
}