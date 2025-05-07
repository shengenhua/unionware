package com.unionware.query.view.base

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.loadState.LoadState
import com.chad.library.adapter4.loadState.trailing.TrailingLoadStateAdapter
import com.unionware.lib_base.utils.ext.expandAnimate
import com.unionware.query.adapter.QueryHeadBtnAdapter
import com.unionware.query.adapter.QueryListAdapter
import com.unionware.query.viewmodel.DynamicQueryViewModel
import com.unionware.virtual.view.adapter.virtual.VirtualViewDiffAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import unionware.base.ext.strToInt
import unionware.base.model.req.ViewReq
import unionware.base.model.resp.ActionResp


/**
 * 动态装配
 * Author: sheng
 * Date:2024/11/19
 */
open class QueryDynamicActivity<VM : DynamicQueryViewModel> : BaseQueryDynamicActivity<VM>() {
    /**
     * 单据头 适配器
     */
    open var headCollectsAdapter: VirtualViewDiffAdapter = VirtualViewDiffAdapter()
    open var queryHeadBtnAdapter: QueryHeadBtnAdapter = QueryHeadBtnAdapter()
    open var queryListAdapter: QueryListAdapter = QueryListAdapter()

    open var queryListAdapterHelper: QuickAdapterHelper? = null


    /**
     *  是否显示过滤体条件
     */
    @JvmField
    @Autowired(name = "showFilter")
    var showFilter: Boolean = true

    /**
     * 执行单号 code=TASK100042
     */
    @JvmField
    @Autowired(name = "clickData")
    var clickData: ActionResp? = null

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            virtualView.apply {
                pageIdLiveData.observe(this@QueryDynamicActivity) {
                    if (clickData != null && it != null) {
                        mViewModel.virtualViewRequest.command("INVOKE_CLIENTQUERY")
                        queryTopAdapter.apply {
                            if (this.openState) {
                                binding?.rvHeard?.expandAnimate(false, height = 0)
                            }
                            this.openState = false
                        }
                    }
                }
                headCollectsLiveData.observe(this@QueryDynamicActivity) { head ->
                    head.firstOrNull { it.key == "FKeyword" }?.also {
                        queryTopAdapter.updateValue(it)
                    }

                    if (showFilter) {
                        head.filter { it.key != "FKeyword" }.apply {
                            if (this.isEmpty()) {
                                binding?.rvHeard?.visibility = View.GONE
                                queryTopAdapter.getViewBinding(0)?.binding?.ivArrowDown?.visibility =
                                    View.GONE
                            } else {
                                queryTopAdapter.getViewBinding(0)?.binding?.ivArrowDown?.visibility =
                                    View.VISIBLE
                            }
                            headCollectsAdapter.submitList(this, false)
                            //true
                        }
                    } else {
                        binding?.rvHeard?.visibility = View.GONE
                        queryTopAdapter.getViewBinding(0)?.binding?.ivArrowDown?.visibility =
                            View.GONE
                    }
                    /*headCollectsAdapter.submitList(
                        head.filter { it.key != "FKeyword" }, false
                    )*/
                    lifecycle?.lifecycleScope?.launch {
                        binding?.rvTail?.requestLayout()
                        delay(200)
                        binding?.rvHeard?.requestLayout()
                        delay(200)
                        headCollectsAdapter.setCursorPosition()
                    }
                    //INVOKE_CLIENTOPTIONS 创建完虚拟视图去查询参数 只需要查询一次
                    mViewModel.virtualViewRequest.command("INVOKE_CLIENTOPTIONS")
                }
                entryAndSubViewLiveData.observe(this@QueryDynamicActivity) {

                }
                clickRowLiveData.observe(this@QueryDynamicActivity) {
                    it.action?.get(0)?.apply {
                        when (this.name) {
                            "WMS_QUERYVIEW" -> {
                                //点击行 事件
                                ARouter.getInstance().build("/query/dyamic")
                                    .withSerializable("scene", scene)
                                    .withSerializable("title", title)
                                    .withSerializable("reportFormId", reportFormId)
                                    .withSerializable("primaryId", primaryId)
                                    .withSerializable("clickData", this)
                                    .navigation()
                            }

                            else -> {
                                //其他
                            }
                        }
                    }
                }
                dataListLiveData.observe(this@QueryDynamicActivity) {
                    dataListOptionsLiveData.value?.apply {
                        if (this["pageIndex"]?.strToInt(1) == 1 && it.isEmpty()) {
                            queryListAdapter.submitList(it)
                            queryListAdapterHelper?.trailingLoadState =
                                LoadState.NotLoading(true)
                            return@observe
//                            queryListAdapterHelper?.leadingLoadState = LoadState.NotLoading(true)
                        } else if (this["pageIndex"]?.strToInt(1) == 1) {
                            queryListAdapterHelper?.trailingLoadState = LoadState.None
                            queryListAdapter.submitList(it)
                        } else {
                            queryListAdapter.addAll(it)
                        }
                        val pageSize = this["pageSize"]?.strToInt() ?: 20
                        lifecycleScope.launch {
                            delay(200)
                            if (it.size < pageSize) {
                                queryListAdapterHelper?.trailingLoadState =
                                    LoadState.NotLoading(true)
                            } else {
                                queryListAdapterHelper?.trailingLoadState =
                                    LoadState.NotLoading(false)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun initView() {
        super.initView()
        mViewModel.virtualViewRequest.createView(ViewReq().apply {
            formId = reportFormId
            if (clickData != null) {
                clickData?.apply {
                    params = mutableMapOf<String, Any>().also {
                        it["schemaId"] = this.actionDetailResp.schemaId//数据查询方案id
                        this.actionDetailResp.filter?.apply {
                            it["filter"] = this//固定 过滤条件
                        }
                    }
                }
            } else {
                params = mutableMapOf<String, Any>(
                    Pair("SchemaId", this@QueryDynamicActivity.primaryId),//数据查询方案id
                ).apply {
                    filter?.also {
                        put("filter", it)//固定 过滤条件
                    }
                }
            }
        })
    }

    override fun tailAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>? {
        return queryListAdapter.apply {
            setOnItemClickListener { adapter, view, position ->
                mViewModel.virtualView.optionsLiveData.value?.apply {
                    if (this["requireClientClickRowEvent"]?.toString().toBoolean()) {
                        adapter.getItem(position)?.apply {
                            mViewModel.virtualViewRequest.command(
                                "INVOKE_CLIENTCLICKROW",
                                this.dataMap
                            )
                        }
                    }
                }
            }
        }.let {
            this.queryListAdapterHelper = QuickAdapterHelper.Builder(it)
                .setTrailingLoadStateAdapter(object : TrailingLoadStateAdapter.OnTrailingListener {
                    override fun onLoad() {
                        mViewModel.virtualViewRequest.command("INVOKE_CLIENTQUERYNEXT")
                    }

                    override fun onFailRetry() {
                    }
                })
                .setConfig(ConcatAdapter.Config.DEFAULT)
                .build()
            queryListAdapterHelper?.also {
//                it.trailingLoadState = LoadState.NotLoading(true)
//                it.leadingLoadState = LoadState.NotLoading(true)
            }?.adapter
        }
    }


    override fun heardDynamicAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        //单据头
        return headCollectsAdapter.apply {
            itemFocus = {
                if (it) {
                    currentFocus?.apply {
                        clearFocus()
                    }
                }
            }
            setItemUpdateValue {
                mViewModel.virtualView.focusPosition.value = -1
                mViewModel.virtualViewRequest.updateView(it?.key, it?.value, 0)
            }
            queryItemListener = { bean, position ->
                mViewModel.virtualViewRequest.commandQuery(
                    "INVOKE_GETCUSTOMFILTER",
                    bean
                ) {
                    queryBasic(position, it?.customFilter, bean.clone()) { _, infoBean ->
                        mViewModel.virtualView.focusPosition.value = -1
                        mViewModel.virtualViewRequest.updateView(bean.key, infoBean?.code, 0)
                    }
                }
            }
        }
    }

    override fun topAfterAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>? {
        if (!showFilter) {
            return null
        }
        return QueryHeadBtnAdapter().apply {
            onClickActionListener {
                mViewModel.virtualViewRequest.command("INVOKE_CLIENTQUERY")
                queryTopAdapter.apply {
                    if (this.openState) {
                        binding?.rvHeard?.expandAnimate(false, height = 0)
                    }
                    this.openState = false
                }
            }
        }
    }

    override fun topAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        return super.topAdapter().apply {
            queryTopAdapter.onOpenStateListener = {
                if (it) {
                    headCollectsAdapter.setCursorPosition()
                } else {
                    currentFocus?.apply {
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(this.windowToken, 0)
                    }
                }
            }
        }
    }
}