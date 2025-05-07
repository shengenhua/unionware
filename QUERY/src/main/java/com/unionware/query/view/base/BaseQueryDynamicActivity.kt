package com.unionware.query.view.base

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.QuickAdapterHelper
import com.google.zxing.integration.android.IntentIntegrator
import com.lxj.xpopup.XPopup
import com.unionware.lib_base.utils.ext.expandAnimate
import com.unionware.query.R
import com.unionware.query.adapter.QueryTopAdapter
import com.unionware.query.databinding.ActivityDynamicQueryBinding
import com.unionware.query.viewmodel.DynamicQueryViewModel
import com.unionware.virtual.view.basics.BasicVirProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import unionware.base.app.utils.ReflectUtils
import unionware.base.model.bean.BaseInfoBean
import unionware.base.model.bean.PropertyBean

/**
 * 动态查询报表
 * Author: sheng
 * Date:2025/03/06
 */
open class BaseQueryDynamicActivity<VM : DynamicQueryViewModel> :
    QueryBaseActivity<ActivityDynamicQueryBinding, VM>() {

    override fun onBindVariableId(): MutableList<Pair<Int, Any>> = mutableListOf()
    override fun onBindLayout(): Int = R.layout.activity_dynamic_query

    override fun initData() = Unit
    override fun onBindViewModel(): Class<VM> {
        @Suppress("UNCHECKED_CAST")
        return ReflectUtils.getActualTypeArgument(0, this.javaClass) as? Class<VM>
            ?: throw IllegalArgumentException("找不到 ViewModelClass 实例，建议重写该方法")
    }

    open var queryTopAdapter: QueryTopAdapter = QueryTopAdapter()

    @JvmField
    @Autowired(name = "primaryId")
    var primaryId: String = "67185f13c5221e"

    /**
     * 执行单号 code=TASK100042
     */
    @JvmField
    @Autowired(name = "code")
    var code: String = ""

    /**
     * 表单标识
     */
    @JvmField
    @Autowired(name = "reportFormId")
    var reportFormId: String = "UNW_WMS_MOBI_INVERTOY_REPORT"

    /**
     * 执行单号 code=TASK100042
     */
    @JvmField
    @Autowired(name = "filter")
    var filter: Any? = null

    override fun initBaseViewObservable() {
        super.initBaseViewObservable()
        mViewModel.showErrorDialogViewEvent.observe(this) {
            XPopup.Builder(this).asConfirm("提示", it.errorMag) {
                it.viewReq?.apply {
                    when (simulate) {
                        "Command" -> {
                            mViewModel.virtualViewRequest.commandViewData(this)
                        }

                        "UpdateValue" -> {
                            mViewModel.virtualViewRequest.updateVirtualView(this)
                        }
                    }
                }
            }.show()
        }
    }

    override fun initView() {
        setTitle(title)
        binding!!.run {
            topAdapter().also {
                rvTop.adapter = it
                rvTop.layoutManager = topLayoutManager()
            }
            heardAdapter().also {
                rvHeard.adapter = it
                rvHeard.layoutManager = heardLayoutManager()
            }
            tailAdapter().also {
//                rvTail.setItemViewCacheSize(20)
                rvTail.adapter = it
                rvTail.layoutManager = tailLayoutManager()
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mViewModel.apply {
                    virtualView.pageIdLiveData.value.let {
                        if (it.isNullOrEmpty()) {
                            postFinishActivityEvent()
                            return
                        }
                        it
                    }.apply {
                        virtualViewRequest.closeView(this)
                    }
                }
            }
        })
    }

    protected open fun heardLayoutManager(): RecyclerView.LayoutManager = if (isLandscape()) {
        GridLayoutManager(this, 2).apply {
        }
    } else {
        LinearLayoutManager(this)
    }

    protected open fun topLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(this)

    protected open fun middleLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(this)

    protected open fun tailLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(this)
    /*if (isLandscape()) {
        GridLayoutManager(this, 2)
    } else {
        object : LinearLayoutManager(this) {
//           override fun canScrollHorizontally() =false
//           override fun canScrollVertically() =false
        }
    }*/

    protected open fun bottomLayoutManager(): RecyclerView.LayoutManager =
        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    /**
     * 上部分适配器 最终
     */
    protected open fun heardAdapter(): RecyclerView.Adapter<ViewHolder> {
        heardDynamicAdapter().let {
            val helper = QuickAdapterHelper.Builder(it as BaseQuickAdapter<*, *>).build()
            topAfterAdapter()?.also { top ->
                helper.addAfterAdapter(top as BaseQuickAdapter<*, *>)
            }
            return helper.adapter
        }
    }

    /**
     * 元素适配器 顶部 ，用与显示少量格外信息
     *  query  模糊查询功能 ，展开查询数据功能
     */
    protected open fun topAdapter(): RecyclerView.Adapter<ViewHolder> {
        return queryTopAdapter.apply {
            onQcScanListener = {
                zxingBasic(0) { _, code ->
                    mViewModel.virtualViewRequest.updateView("FKeyword", code) {
                        mViewModel.virtualViewRequest.command("INVOKE_CLIENTQUERY")
                    }
//                    mViewModel.virtualViewRequest.updateView("FKeyword", code, 0)
                }
            }
            onClickActionListener { adapter ->
                this@BaseQueryDynamicActivity.currentFocus
                this.openState = !this.openState
                binding?.rvHeard?.expandAnimate(this.openState, height = 0)
                /*binding?.rvHeard?.postDelayed({
                    binding?.rvHeard?.visibility = if (this.openState) View.VISIBLE else View.GONE
                }, 150)*/
            }
            onEditorActionChangeListener = { text, key, position ->
                mViewModel.virtualViewRequest.updateView(key, text) {
                    mViewModel.virtualViewRequest.command("INVOKE_CLIENTQUERY")
                }
            }
        }
    }

    /**
     * 元素适配器 顶部 ，用与显示少量格外信息
     */
    protected open fun topAfterAdapter(): RecyclerView.Adapter<ViewHolder>? = null

    /**
     * 上部分适配器 ， 工序元素适配器
     */
    protected open fun heardDynamicAdapter(): RecyclerView.Adapter<ViewHolder>? {
//        return ProcessAdapter()
        return null
    }

    /**
     * 中间 适配器 通常用扫描
     */
    protected open fun middleAdapter(): RecyclerView.Adapter<ViewHolder>? = null

    /**
     * 底部数据显示 适配器
     */
    protected open fun tailAdapter(): RecyclerView.Adapter<ViewHolder>? = null

    private var lifecycleJob: Job? = null


    protected fun queryBasic(
        position: Int,
        custom: String? = null,
        bean: PropertyBean? = null,
        queryUnit: ((Int?, BaseInfoBean?) -> Unit),
    ) {
        currentFocus?.clearFocus()
        queryFlow.value = null
        val queryIntent = Intent(this, BasicVirProfileActivity::class.java)
        queryIntent.putExtra("scene", scene)
        queryIntent.putExtra("position", position)
        queryIntent.putExtra("key", bean?.tag)
        custom?.also {
            queryIntent.putExtra("custom", it)
        }
        bean?.parentId?.apply {
            queryIntent.putExtra("parentId", this)
            queryIntent.putExtra("parentName", "parentId")
        }
        if ("FLEXVALUE" == bean?.type) {
            bean.apply {
                queryIntent.putExtra("parentName", "parentId")
                queryIntent.putExtra("parentId", this.related)
                queryIntent.putExtra("flexId", this.flexId)
            }
        }

        queryLauncher.launch(queryIntent)
        lifecycleJob?.cancel()
        lifecycleJob = lifecycleScope.launch(Dispatchers.Main) {
            queryFlow.collectLatest { infoBean ->
                infoBean?.also {
                    queryUnit.invoke(position, it)
                    cancel()
                }
            }
        }
    }

    private var queryFlow = MutableStateFlow<BaseInfoBean?>(null)

    /**
     * 基础资料 数据回调回来
     */
    @Suppress("DEPRECATION")
    private val queryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data ?: return@registerForActivityResult
            runBlocking {
//                val pos = result.resultCode // 更新位置
                val infoBean = if (Build.VERSION.SDK_INT >= TIRAMISU) data.getSerializableExtra(
                    "baseInfo", BaseInfoBean::class.java
                ) else data.getSerializableExtra("baseInfo") as BaseInfoBean?
                infoBean?.let {
                    queryFlow.value = infoBean
                }
            }
        }

    protected fun zxingBasic(
        position: Int,
        queryUnit: ((Int?, String?) -> Unit),
    ) {
        currentFocus?.clearFocus()
        zxingFlow.value = null
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("请对准二维码进行扫描")
        integrator.setCameraId(0)
        integrator.setOrientationLocked(true)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(false)
        val zxingIntent = integrator.createScanIntent()
        zxingLauncher.launch(zxingIntent)
        lifecycleJob?.cancel()
        lifecycleJob = lifecycleScope.launch(Dispatchers.Main) {
            zxingFlow.collectLatest { str ->
                if (!str.isNullOrEmpty()) {
                    queryUnit.invoke(position, str)
                }
                cancel()
            }
        }
    }

    private var zxingFlow = MutableStateFlow<String?>(null)
    private val zxingLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data ?: return@registerForActivityResult
            val zxingResult = IntentIntegrator.parseActivityResult(result.resultCode, data)
            if (zxingResult.contents != null) {
                // 获取扫描结果
                val content = zxingResult.contents.trim { it <= ' ' }
                zxingFlow.value = content
            }
        }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding?.apply {
            rvTop.layoutManager = topLayoutManager()
            rvHeard.layoutManager = heardLayoutManager()
            rvTail.layoutManager = tailLayoutManager()
        }
    }
}