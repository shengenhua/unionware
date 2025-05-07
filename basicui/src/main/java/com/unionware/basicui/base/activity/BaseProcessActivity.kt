package com.unionware.basicui.base.activity

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
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
import com.lxj.xpopup.XPopup
import com.unionware.basicui.R
import com.unionware.basicui.app.BasicBaseActivity
import com.unionware.basicui.base.adapter.BasicBillListAdapter
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.basicui.base.viewmodel.BaseCollectViewModel
import unionware.base.databinding.ActivityProcessBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import unionware.base.app.utils.ReflectUtils
import unionware.base.model.ViewDisplay


open class BaseProcessActivity<VM : BaseCollectViewModel> :
    BasicBaseActivity<ActivityProcessBinding, VM>() {

    @JvmField
    @Autowired(name = "primaryId")
    var primaryId: String = ""

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
     * 派工单id id=100042
     */
    @JvmField
    @Autowired(name = "taskId")
    var taskId: String = ""

    /**
     *  是否继续汇报
     */
    @JvmField
    @Autowired(name = "isPdaContinuousReport")
    var isPdaContinuousReport: Boolean = false


    private var tailAdapter: RecyclerView.Adapter<ViewHolder>? = null

    override fun onBindVariableId(): MutableList<Pair<Int, Any>> {
        return mutableListOf()
    }

    override fun initBaseViewObservable() {
        super.initBaseViewObservable()
        mViewModel.barcodeLiveData.observe(this) {
            lifecycleScope.launch {
                delay(300)//一个延迟同时
                binding?.rvTail?.scrollToPosition(0)
                mViewModel.barcodeItemCountLiveData.value = binding?.rvTail?.adapter?.itemCount ?: 0
            }
        }
        mViewModel.showErrorDialogViewEvent.observe(this) {
            XPopup.Builder(this).asConfirm("提示", it.errorMag) {
                if (it.filtersReq != null) {
                    mViewModel.queryBarcode(it.filtersReq!!)
                } else if (it.reportReq != null) {
                    mViewModel.submitReport(it.reportReq!!)
                }
            }.show()
        }
        barcodeItemCountObserve()
    }

    protected open fun barcodeItemCountObserve() {
        mViewModel.barcodeItemCountLiveData.observe(this) {
            binding?.scanSum = it ?: 0
        }
    }

    override fun enableToolBarMenu(): Boolean {
        return true
    }

    override fun onBindLayout(): Int {
        return R.layout.activity_process
    }

    override fun initView() {
        /*if (code.isEmpty()) {
            setTitle(title)
        } else {
            setTitle("${title}(${code})")
        }*/
        setTitle(code)
        binding!!.run {
            rvHeard.layoutManager = heardLayoutManager()
            rvHeard.adapter = heardAdapter()

            middleAdapter()?.also {
                rvMiddle.layoutManager = middleLayoutManager()
                rvMiddle.adapter = it
            }

            rvTail.layoutManager = tailLayoutManager()
            rvTail.adapter = tailAdapter().also {
                tailAdapter = it
            }
        }
    }

    protected open fun heardLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(this)
    }

    protected open fun middleLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(this)
    }

    protected open fun tailLayoutManager(): RecyclerView.LayoutManager {
        if (isLandscape()) {
            //StaggeredGridLayoutManager
            return GridLayoutManager(this, 2)
        }
        /*setStackFromEnd(true)//列表再底部开始展示，反转后由上面开始展示
        setReverseLayout(true)//列表翻转*/
        return LinearLayoutManager(this)
    }

    protected var headAdapter: ProcessAdapter? = null

    /**
     * 上部分适配器 最终
     */
    protected open fun heardAdapter(): RecyclerView.Adapter<ViewHolder> {
        headAdapter = heardProcessAdapter()
        headAdapter.let {
            headAdapter?.submitList(heardItems())
            headAdapter?.addOnItemChildClickListener(R.id.ivQuery) { adapter, _, position ->
                headQueryBasic(position, adapter)
            }
            val helper = QuickAdapterHelper.Builder(it as BaseQuickAdapter<*, *>).build()
            topAdapter()?.also { top ->
                helper.addBeforeAdapter(top as BaseQuickAdapter<*, *>)
            }
            topAfterAdapter()?.also { top ->
                helper.addAfterAdapter(top as BaseQuickAdapter<*, *>)
            }
            return helper.adapter
        }
    }

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
//                adapter.notifyItemChanged(pos)
                adapter.notifyItemChanged(pos, 200)
            }
        }
    }

    /**
     * 元素适配器 顶部 ，用与显示少量格外信息
     */
    protected open fun topAdapter(): RecyclerView.Adapter<ViewHolder>? {
        return ProcessAdapter().also {
            it.submitList(
                mutableListOf(
                    ViewDisplay("工序", "jobName").apply { value = this@BaseProcessActivity.title },
                )
            )
        }
    }

    /**
     * 元素适配器 顶部 ，用与显示少量格外信息
     */
    protected open fun topAfterAdapter(): RecyclerView.Adapter<ViewHolder>? {
        return null
    }

    /**
     * 元素适配器 items
     */
    protected open fun heardItems(): MutableList<ViewDisplay> {
        return mutableListOf()
    }

    /**
     * 上部分适配器 ， 工序元素适配器
     */
    protected open fun heardProcessAdapter(): ProcessAdapter {
        return ProcessAdapter()
    }

    /**
     * 中间 适配器 通常用扫描
     */
    protected open fun middleAdapter(): RecyclerView.Adapter<ViewHolder>? {
        return null
    }

    /**
     * 底部适配器
     */
    protected open fun tailAdapter(): RecyclerView.Adapter<ViewHolder> {
        return BasicBillListAdapter()
    }

    override fun initData() {
//        mViewModel.query()
    }

    protected open fun onActionSubmitConfirm(operator: String, code: String) = Unit

    private var lifecycleJob: Job? = null
    protected fun queryBasic(
        position: Int,
        key: String? = null,
        parentId: String? = null,
        parentName: String? = null,
        queryUnit: ((Int?, unionware.base.model.bean.BaseInfoBean?) -> Unit),
    ) {
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

    protected fun queryOtherBasic(
        position: Int,
        key: String? = null,
        parentId: String? = null,
        parentName: String? = null,
        queryUnit: ((Int?, unionware.base.model.bean.SerializableMap?) -> Unit),
    ) {
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

    private var queryFlow = MutableStateFlow<unionware.base.model.bean.BaseInfoBean?>(null)

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
                    "baseInfo", unionware.base.model.bean.BaseInfoBean::class.java
                ) else data.getSerializableExtra("baseInfo") as unionware.base.model.bean.BaseInfoBean?
                infoBean?.let {
                    queryFlow.value = infoBean
                }
            }
        }


    private var otherQueryFlow = MutableStateFlow<unionware.base.model.bean.SerializableMap?>(null)

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
                    "baseInfo", unionware.base.model.bean.SerializableMap::class.java
                ) else data.getSerializableExtra("baseInfo") as unionware.base.model.bean.SerializableMap?
                infoBean?.let {
                    otherQueryFlow.value = infoBean
                }
            }
        }

    override fun backShowDialog(): Boolean {
        mViewModel.barcodeItemCountLiveData.value?.also {
            return@backShowDialog it > 0
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewModel(): Class<VM> {
        return ReflectUtils.getActualTypeArgument(0, this.javaClass) as? Class<VM>
            ?: throw IllegalArgumentException("找不到 ViewModelClass 实例，建议重写该方法")
    }
}