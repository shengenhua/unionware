package com.unionware.once.view.dynamic

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.util.SparseArray
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
import com.unionware.basicui.base.activity.BasicOtherActivity
import com.unionware.basicui.base.activity.BasicProfileActivity
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.lib_base.utils.ext.expandAnimate
import com.unionware.once.R
import com.unionware.once.adapter.dynamic.BottomFeatureAdapter
import com.unionware.once.adapter.dynamic.JobTitleAdapter
import com.unionware.once.databinding.OnceActivityDynamicProcessBinding
import com.unionware.once.view.basic.OnceBaseActivity
import com.unionware.once.viewmodel.dynamic.LacalDynamicViewModel
import com.unionware.virtual.view.adapter.ButtonAdapter
import com.unionware.virtual.view.basics.BasicVirProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import unionware.base.app.utils.ReflectUtils
import unionware.base.model.ViewDisplay
import unionware.base.model.bean.BaseInfoBean
import unionware.base.model.bean.PropertyBean
import unionware.base.model.bean.SerializableMap

/**
 * 动态装配
 * Author: sheng
 * Date:2024/11/19
 */
abstract class LacalBaseDynamicActivity<VM : LacalDynamicViewModel> :
    OnceBaseActivity<OnceActivityDynamicProcessBinding, VM>() {

    override fun onBindVariableId(): MutableList<Pair<Int, Any>> = mutableListOf()
    override fun onBindLayout(): Int = R.layout.once_activity_dynamic_process

    override fun enableToolBarMenu(): Boolean = true
    override fun initData() = Unit
    protected open fun onActionSubmitConfirm(operator: String, code: String) = Unit
    override fun backShowDialog(): Boolean = false
    override fun onBindViewModel(): Class<VM> {
        @Suppress("UNCHECKED_CAST")
        return ReflectUtils.getActualTypeArgument(0, this.javaClass) as? Class<VM>
            ?: throw IllegalArgumentException("找不到 ViewModelClass 实例，建议重写该方法")
    }

    override fun onActionSubmit() {
        currentFocus?.clearFocus()
        super.onActionSubmit()
    }


    @JvmField
    @Autowired(name = "primaryId")
    var primaryId: String = ""

    /**
     * 工序名称
     */
    @JvmField
    @Autowired(name = "jobName")
    var jobName: String = ""

    /**
     * 执行单号 code=TASK100042
     */
    @JvmField
    @Autowired(name = "code")
    var code: String = ""

    /**
     *  工序id jobId=2791257
     */
    @JvmField
    @Autowired(name = "jobId")
    var jobId: String = ""

    /**
     *  业务属性id
     */
    @JvmField
    @Autowired(name = "propId")
    var propId: String = ""

    /**
     * 派工单id id=100042
     */
    @JvmField
    @Autowired(name = "taskId")
    var taskId: String = ""

    /**
     * 汇报标识 reportRuleId
     */
    @JvmField
    @Autowired(name = "reportRuleId")
    var reportRuleId: String = ""//UNW_XMES_REPORT_DYNAMIC

    /**
     * 汇报标识 reportRuleId
     */
    @JvmField
    @Autowired(name = "id")
    var id: String = ""//UNW_XMES_REPORT_DYNAMIC
    /**
     *  是否继续汇报
     */
    @JvmField
    @Autowired(name = "isPdaContinuousReport")
    var isPdaContinuousReport: Boolean = false

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
        mViewModel.virtualView.submitLiveData.observe(this){
            if(isPdaContinuousReport){
                caretView()
            }else{
                mViewModel.postFinishActivityEvent()
            }
        }
    }

    /**
     * 开始创建虚拟视图
     */
    abstract fun caretView()

    override fun initView() {
        setTitle(title)
        binding!!.run {
            topAdapter()?.also {
                rvTop.adapter = it
                rvTop.layoutManager = topLayoutManager()
            }
            heardAdapter().also {
                rvHeard.adapter = it
                rvHeard.layoutManager = heardLayoutManager()
            }
            middleAdapter()?.also {
                rvMiddle.adapter = it
                rvMiddle.layoutManager = middleLayoutManager()
            }
            tailAdapter().also {
                rvTail.setItemViewCacheSize(20)
                rvTail.adapter = it
                rvTail.layoutManager = tailLayoutManager()
            }
            bottomAdapter().also {
                rvBottom.adapter = it
                rvBottom.layoutManager = bottomLayoutManager()
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mViewModel.virtualView.entryOrSubLiveData.value == false) {
                    mViewModel.virtualView.entryOrSubLiveData.value = true
                } else {
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
            }
        })
        caretView()
    }

    protected open fun heardLayoutManager(): RecyclerView.LayoutManager = if (isLandscape()) {
        GridLayoutManager(this, 2).apply {
            /*spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (itemCount % 2 == 1 && position == itemCount - 1) {
                        2
                    } else {
                        1
                    }
                }
            }*/
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

    open var topAdapter: JobTitleAdapter = JobTitleAdapter()

    /**
     * 元素适配器 顶部 ，用与显示少量格外信息
     */
    protected open fun topAdapter(): RecyclerView.Adapter<ViewHolder>? {
        return topAdapter.apply {
            item = ViewDisplay("工序", "jobName").apply {
                value = this@LacalBaseDynamicActivity.jobName
            }
            onEditorActionArray { adapter, text ->
                this@LacalBaseDynamicActivity.currentFocus
                this.openState = !this.openState
                binding?.rvHeard?.expandAnimate(this.openState)
            }
        }
       /* return JobTitleAdapter(ViewDisplay("工序", "jobName").apply {
            value = this@LacalBaseDynamicActivity.title
        }).apply {
            onEditorActionArray { adapter, text ->
                this@LacalBaseDynamicActivity.currentFocus
                this.openState = !this.openState
                binding?.rvHeard?.expandAnimate(this.openState)
            }
        }*/
    }

    /**
     * 元素适配器 顶部 ，用与显示少量格外信息
     */
    protected open fun topAfterAdapter(): RecyclerView.Adapter<ViewHolder>? = null

    /**
     * 上部分适配器 ， 工序元素适配器
     */
    protected open fun heardDynamicAdapter(): RecyclerView.Adapter<ViewHolder> {
        return ProcessAdapter()
    }

    /**
     * 中间 适配器 通常用扫描
     */
    protected open fun middleAdapter(): RecyclerView.Adapter<ViewHolder>? = null

    /**
     * 底部数据显示 适配器
     */
    protected open fun tailAdapter(): RecyclerView.Adapter<ViewHolder>? = null


    /**
     * 底部功能适配器
     */
    open var featureAdapter: BottomFeatureAdapter = BottomFeatureAdapter()
    private var buttonClickArray: SparseArray<ButtonAdapter.OnButtonClickListener>? = null

    /**
     * 子单据体底部功能适配器
     */
    open var subFeatureAdapter: BottomFeatureAdapter = BottomFeatureAdapter()
    private var subButtonClickArray: SparseArray<ButtonAdapter.OnButtonClickListener>? = null

    /**
     * 底部功能适配器
     */
    protected open fun bottomAdapter(): RecyclerView.Adapter<ViewHolder>? {
        subFeatureAdapter.let {
            it.addOnItemChildClickListener(R.id.acBtn) { adapter, view, position ->
                val item = adapter.getItem(position)
                subButtonClickArray?.get(item?.id ?: -1)?.onClick(item?.id ?: -1)
            }
        }
        return featureAdapter.let {
            it.addOnItemChildClickListener(R.id.acBtn) { adapter, view, position ->
                val item = adapter.getItem(position)
                buttonClickArray?.get(item?.id ?: -1)?.onClick(item?.id ?: -1)
            }
        }
    }

    protected fun addButton(
        button: BottomFeatureAdapter.AdapterButtonValue,
        onClick: ButtonAdapter.OnButtonClickListener,
    ) {
        if (buttonClickArray == null) {
            SparseArray<ButtonAdapter.OnButtonClickListener>().also { this.buttonClickArray = it }
        }
        buttonClickArray?.put(button.id, onClick)
        featureAdapter.add(button)
    }

    protected fun addSubButton(
        button: BottomFeatureAdapter.AdapterButtonValue,
        onClick: ButtonAdapter.OnButtonClickListener,
    ) {
        if (subButtonClickArray == null) {
            SparseArray<ButtonAdapter.OnButtonClickListener>().also { this.subButtonClickArray = it }
        }
        subButtonClickArray?.put(button.id, onClick)
        subFeatureAdapter.add(button)
    }

    private var lifecycleJob: Job? = null

    /**
     * 头部查询基础资料
     */
    protected open fun headQueryBasic(
        position: Int,
        adapter: BaseQuickAdapter<ViewDisplay, *>,
    ) {
        queryBasic(
            position,
            adapter.items[position].code,
            adapter.items[position].parentId,
            adapter.items[position].parentName,
        ) { pos, infoBean ->
            infoBean?.apply {
                adapter.items[pos!!].value = this.name
                adapter.items[pos].id = this.id
                adapter.notifyItemChanged(pos)
            }
        }
    }

    protected open fun queryBasic(
        position: Int,
        key: String? = null,
        parentId: String? = null,
        parentName: String? = null,
        queryUnit: ((Int?, BaseInfoBean?) -> Unit),
    ) {
        currentFocus?.clearFocus()
        queryFlow.value = null
        val queryIntent = Intent(this, BasicProfileActivity::class.java)
        queryIntent.putExtra("scene", scene)
        queryIntent.putExtra("position", position)
        queryIntent.putExtra("key", key)
        parentId?.apply {
            queryIntent.putExtra("parentId", this)
        }
        parentName?.apply {
            queryIntent.putExtra("parentName", this)
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

        /*if (bean?.key == "FTonbagId" && propId == "162158") {
            queryIntent.putExtra("key", "67C516DBE0095E")
        }*/

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

    protected open fun queryOtherBasic(
        position: Int,
        key: String? = null,
        parentId: String? = null,
        parentName: String? = null,
        queryUnit: ((Int?, SerializableMap?) -> Unit),
    ) {
        currentFocus?.clearFocus()
        otherQueryFlow.value = null
        val queryIntent = Intent(this, BasicOtherActivity::class.java)
        queryIntent.putExtra("scene", scene)
        queryIntent.putExtra("position", position)
        queryIntent.putExtra("key", key)
        parentId?.apply {
            queryIntent.putExtra("parentId", this)
        }
        parentName?.apply {
            queryIntent.putExtra("parentName", this)
        }
        otherQueryLauncher.launch(queryIntent)
        lifecycleJob?.cancel()
        lifecycleJob = lifecycleScope.launch(Dispatchers.Main) {
            otherQueryFlow.collectLatest { infoBean ->
                infoBean?.also {
                    queryUnit.invoke(position, it)
                    cancel()
                }
            }
        }
    }

    private var otherQueryFlow = MutableStateFlow<SerializableMap?>(null)

    /**
     * 基础资料 数据回调回来
     */
    @Suppress("DEPRECATION")
    private val otherQueryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data ?: return@registerForActivityResult
            runBlocking {
//                val pos = result.resultCode // 更新位置
                val infoBean = if (Build.VERSION.SDK_INT >= TIRAMISU) data.getSerializableExtra(
                    "baseInfo", SerializableMap::class.java
                ) else data.getSerializableExtra("baseInfo") as SerializableMap?
                infoBean?.let {
                    otherQueryFlow.value = infoBean
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
            zxingFlow.collectLatest { infoBean ->
                infoBean?.also {
                    queryUnit.invoke(position, it)
                    cancel()
                }
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
            rvMiddle.layoutManager = middleLayoutManager()
            rvTail.layoutManager = tailLayoutManager()
            rvBottom.layoutManager = bottomLayoutManager()
        }
    }
}