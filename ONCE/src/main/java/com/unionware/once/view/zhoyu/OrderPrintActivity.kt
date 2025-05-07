package com.unionware.once.view.zhoyu

import android.content.Intent
import android.graphics.Color
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.loadState.LoadState
import com.chad.library.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.google.zxing.integration.android.IntentIntegrator
import com.lxj.xpopup.XPopup
import com.tamsiree.rxkit.RxFileTool
import com.unionware.once.R
import com.unionware.once.adapter.CheckBillListAdapter
import com.unionware.once.app.RouterOncePath
import com.unionware.once.databinding.OrderPrintActivityBinding
import com.unionware.once.viewmodel.zhoyu.ZhoYuViewModel
import com.unionware.printer.FileUtil
import com.unionware.printer.PrintUtils
import com.unionware.printer.print.PrinterInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import unionware.base.app.view.base.BaseMvvmToolbarActivity
import unionware.base.ext.bigDecimalToZeros
import unionware.base.ext.showToast
import unionware.base.model.bean.BillBean
import unionware.base.model.req.FiltersReq


/**
 * 报工单打印
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_ZHOYU_ORDER_PRINT)
class OrderPrintActivity : BaseMvvmToolbarActivity<OrderPrintActivityBinding, ZhoYuViewModel>() {
    override fun onBindVariableId(): MutableList<Pair<Int, Any>> = mutableListOf()
    override fun onBindLayout(): Int = R.layout.order_print_activity
    override fun enableToolBarMenu(): Boolean = true

    override fun showItemMenu(menuItem: MenuItem) {
        when (menuItem.itemId) {
            unionware.base.R.id.action_submit -> {
                menuItem.title = "打印"
            }
        }
    }

    override fun onActionSubmit() {
        checkBillListAdapter.items.firstOrNull { it.isSelect == true }.also {
            if (it == null) {
                "没有勾选需要打印的数据".showToast()
                return@onActionSubmit
            } else {
                XPopup.Builder(this).asConfirm("提示", "是否打印？") {
                    printBillBean(it)
                }.show()
            }
        }
    }

    private fun printBillBean(billBean: BillBean) {
        //打印功能
        try {
            billBean.id?.bigDecimalToZeros()?.also {
                mViewModel.print(scene, it.toInt())
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            "提交异常".showToast()
        }
    }


    fun inputQuery(input: String? = null) {
        mViewModel.getScanBarcode(
            scene, filtersReq = FiltersReq(mutableMapOf<String?, Any?>().apply {
                if (input?.isNotEmpty() == true) {
                    //生产订单号
//                    put("prdSrcNo", input)
                    //零件号
//                    put("mnemonicCode", input)
                    put("keyword", input)
                }
            })
        )
    }

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

    private val checkBillListAdapter = CheckBillListAdapter()
    private var adapterHelper: QuickAdapterHelper? = null
    private var printerInterface: PrinterInterface? = null

    override fun initViewObservable() {
        mViewModel.apply {
            printLiveData.observe(this@OrderPrintActivity) {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (!it.isNullOrEmpty() && printerInterface?.isConnect == true) {
                        val path = mContext.filesDir.toString() + "/pdf_android"
                        try {
                            RxFileTool.delAllFile(path)
                            FileUtil.SaveToPDF(it, this@OrderPrintActivity)
                            FileUtil.DeZip(this@OrderPrintActivity)
                            printerInterface?.print(RxFileTool.listFilesInDir(path), 1)
                            postShowToastViewEvent("打印成功")
                        } catch (e: Throwable) {
                            postShowToastViewEvent("打印失败${e.message}")
                        } finally {
                            RxFileTool.delAllFile(path)//Error writing to connection: Broken pipe
                        }
                    } else {
                        postShowToastViewEvent("未连接打印机")
                    }
                    postShowTransLoadingViewEvent(false)
                }
            }
            dataLiveData.observe(this@OrderPrintActivity) {
                if (mViewModel.pageIndexData.value == 1) {
                    adapterHelper?.trailingLoadState = LoadState.None
                    checkBillListAdapter.submitList(it)
                } else {
                    checkBillListAdapter.addAll(it)
                }
                lifecycleScope.launch {
                    delay(200)
                    if (it.isEmpty()) {
                        adapterHelper?.trailingLoadState = LoadState.NotLoading(true)
                    } else if (it.size < 20) {
                        adapterHelper?.trailingLoadState = LoadState.NotLoading(true)
                    } else {
                        adapterHelper?.trailingLoadState = LoadState.NotLoading(false)
                    }
                    delay(200)
                    binding?.smRefresh?.finishRefresh()
                }
            }
        }
    }


    override fun initView() {
        setTitle(title)
        binding?.apply {
            rvList.layoutManager = LinearLayoutManager(this@OrderPrintActivity)
            rvList.adapter = getListAdapter()
            smRefresh.setOnRefreshListener {
                mViewModel.apply {
                    pageIndexData.value = 1
                    if(queryLayout.etInspectInput.text?.toString().isNullOrEmpty()){
                        "请输入筛选条件查询".showToast()
                        dataLiveData.value = mutableListOf()
                        pageIndexData.value = 1
                        smRefresh.finishRefresh()
                    }else{
                        inputQuery(queryLayout.etInspectInput.text?.toString())
                    }
                }
            }

            queryLayout.ivCommonScan.setOnClickListener {
                val integrator = IntentIntegrator(this@OrderPrintActivity)
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                integrator.setPrompt("请对准二维码进行扫描")
                integrator.setCameraId(0)
                integrator.setOrientationLocked(true)
                integrator.setBeepEnabled(false)
                integrator.setBarcodeImageEnabled(false)
                val zxingIntent = integrator.createScanIntent()
                scanLauncher.launch(zxingIntent)
            }
            queryLayout.etInspectInput.setOnEditorActionListener { v, actionId, event ->
                mViewModel.pageIndexData.value = 1
                if(v.text.toString().isEmpty()){
                    "请输入筛选条件查询".showToast()
                    mViewModel.dataLiveData.value = mutableListOf()
                }else{
                    inputQuery(v.text.toString())
                }
                return@setOnEditorActionListener false
            }
        }
    }

    private val scanLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data ?: return@registerForActivityResult
            val pos = result.resultCode // 更新位置
            val zxingResult = IntentIntegrator.parseActivityResult(pos, data)
            if (zxingResult.contents != null) {
                // 获取扫描结果
                val content = zxingResult.contents.trim { it <= ' ' }
                binding?.queryLayout?.etInspectInput?.setText(content)
                if(content.isNotEmpty()){
                    inputQuery(content)
                }
            }
        }

    private fun getListAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>? {
        return checkBillListAdapter.let { adapter ->
            adapter.setOnItemClickListener { adapter, view, position ->
                adapter.items.withIndex().firstOrNull { it.value.isSelect == true }?.also {
                    it.value.isSelect = false
                    adapter.notifyItemChanged(it.index)
                    if (position == it.index) {
                        return@setOnItemClickListener
                    }
                }
                adapter.items[position].isSelect = !adapter.items[position].isSelect
                adapter.notifyItemChanged(position)
            }
            adapterHelper = QuickAdapterHelper.Builder(adapter)
                .setTrailingLoadStateAdapter(object : TrailingLoadStateAdapter.OnTrailingListener {
                    override fun onFailRetry() = Unit
                    override fun onLoad() {
                        //加页数
                        mViewModel.pageIndexData.value = mViewModel.pageIndexData.value?.plus(1)
                        inputQuery(
                            binding?.queryLayout?.etInspectInput?.text.toString()
                        )
                    }
                })
                .setConfig(ConcatAdapter.Config.DEFAULT)
                .build()
            adapterHelper?.adapter
        }
    }

    override fun initData() {
        printerInterface = PrintUtils.connectPrint(this) { msg, type ->
            binding?.tvPrint?.apply {
                text = msg
                when (type) {
                    0 -> this.setBackgroundColor(Color.YELLOW)
                    2 -> this.setBackgroundColor(Color.GREEN)
                    3 -> this.setBackgroundColor(Color.RED)
                }
            }
        }
//        inputQuery()
    }

    override fun onDestroy() {
        super.onDestroy()
        printerInterface?.stopHeartBeat()
    }
}