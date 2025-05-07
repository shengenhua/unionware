package com.unionware.basicui.base.activity

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.loadState.LoadState
import com.chad.library.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.google.zxing.integration.android.IntentIntegrator
import unionware.base.R
import com.unionware.basicui.app.BasicBaseActivity
import com.unionware.basicui.base.viewmodel.BaseQueryListViewModel
import unionware.base.databinding.ActivityBaseQueryListBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import unionware.base.app.utils.ReflectUtils


/**
 * 列表
 */
abstract class BaseQueryListActivity<VM : BaseQueryListViewModel> :
    BasicBaseActivity<ActivityBaseQueryListBinding, VM>() {

    @JvmField
    @Autowired(name = "primaryId")
    var primaryId: String = ""

    @JvmField
    @Autowired(name = "useStyleId")
    var useStyleId: String = ""


    open var adapterHelper: QuickAdapterHelper? = null

    open var isQuery = false

    override fun onBindVariableId(): MutableList<Pair<Int, Any>> {
        return mutableListOf()
    }

    override fun initViewObservable() {
        mViewModel.viewLiveData.observe(this) {
            lifecycleScope.launch {
                delay(200)
                if (it.isEmpty()) {
//                    adapterHelper?.leadingLoadState = LoadState.Error(Exception("暂无数据"))
//                    adapterHelper?.leadingLoadState = LoadState.NotLoading(false)
                    adapterHelper?.trailingLoadState = LoadState.NotLoading(true)
                } else if (it.size < 20) {
//                    adapterHelper?.leadingLoadState = LoadState.NotLoading(false)
                    adapterHelper?.trailingLoadState = LoadState.NotLoading(true)
                } else {
                    adapterHelper?.trailingLoadState = LoadState.NotLoading(false)
                }
                delay(200)
                if (isQuery && it.size == 1) {
                    itemClickOpen(0)
                }
                isQuery = false
                binding?.smRefresh?.finishRefresh()
            }
            /*if (it.isEmpty()) {
                adapterHelper?.trailingLoadState = LoadState.NotLoading(false)
            } else {
                adapterHelper?.trailingLoadState = LoadState.NotLoading(true)
            }*/
        }
    }

    override fun onBindLayout(): Int {
        return R.layout.activity_base_query_list
    }

    override fun initView() {
        setTitle(title)
        queryAdapter()?.let {
//            it.setOnItemClickListener(itemClick())
            binding!!.rvQueryData.apply {
                layoutManager = LinearLayoutManager(this@BaseQueryListActivity)
            }
            this.adapterHelper = QuickAdapterHelper.Builder(it)
                /*.setLeadingLoadStateAdapter(object : LeadingLoadStateAdapter.OnLeadingListener {
                    override fun onLoad() {
                        //设置页数为 1
                        mViewModel.pageIndex.value = 1
                        inputQuery(
                            binding?.queryLayout?.etInspectInput?.text.toString()
                        )
                    }
                    override fun isAllowLoading(): Boolean {
                        // 是否允许触发“加载更多”
                        return true
                    }
                })*/
                .setTrailingLoadStateAdapter(object : TrailingLoadStateAdapter.OnTrailingListener {
                    override fun onLoad() {
                        //加载更多
                        //判断接口是不是最后的数据的了没有更多数据了
//                        adapterHelper?.leadingLoadState = LoadState.NotLoading(true)

                        //加页数
                        mViewModel.pageIndex.value = mViewModel.pageIndex.value?.plus(1)
                        inputQuery(
                            binding?.queryLayout?.etInspectInput?.text.toString()
                        )

                    }

                    override fun onFailRetry() {

                    }
                })
                .setConfig(ConcatAdapter.Config.DEFAULT)
                .build()

//            adapterHelper?.trailingLoadState = LoadState.NotLoading(false)
//            adapterHelper?.leadingLoadState = LoadState.NotLoading(false)
//            adapterHelper?.trailingLoadState = LoadState.None
//            adapterHelper?.leadingLoadState = LoadState.None
            binding!!.rvQueryData.adapter = adapterHelper?.adapter
        }

        binding?.smRefresh?.apply {
            setOnRefreshListener {
                mViewModel.pageIndex.value = 1
                inputQuery(
                    binding?.queryLayout?.etInspectInput?.text.toString()
                )
            }
        }

        binding?.queryLayout?.let {
            it.etInspectInput.setOnEditorActionListener { v, actionId, event ->
                isQuery = true
                mViewModel.pageIndex.value = 1
                inputQuery(v.text.toString())
                return@setOnEditorActionListener false
            }
            it.etInspectInput.setOnClearListener {
                mViewModel.pageIndex.value = 1
                inputQuery("")
            }
            it.ivCommonScan.setOnClickListener {
                val integrator = IntentIntegrator(this)
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                integrator.setPrompt("请对准二维码进行扫描")
                integrator.setCameraId(0)
                integrator.setOrientationLocked(true)
                integrator.setBeepEnabled(false)
                integrator.setBarcodeImageEnabled(false)
                val zxingIntent = integrator.createScanIntent()
                scanLauncher.launch(zxingIntent)
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
                isQuery = true
                inputQuery(content)
            }
        }

    protected open fun inputQuery(query: String?) {
        mViewModel.query(scene = scene, configId = primaryId, keyword = query)
    }


    protected open fun itemClickOpen(position: Int) {
        // 表示跳转
//        val intent = Intent(this, CollectProcessActivity::class.java)
//        startActivity(intent)
        // 暂时关闭
        /*ARouter.getInstance().build(MESPath.openPath(MESPath.PathTag.DETAILS))
            .withSerializable("scene", scene)
            .navigation()*/
    }

    protected open fun queryAdapter(): BaseQuickAdapter<*, *>? {
        return null
    }

    override fun initData() {
        inputQuery(binding?.queryLayout?.etInspectInput?.text?.toString() ?: "")
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewModel(): Class<VM> {
        return ReflectUtils.getActualTypeArgument(0, this.javaClass) as? Class<VM>
            ?: throw IllegalArgumentException("找不到 ViewModelClass 实例，建议重写该方法")
    }
}