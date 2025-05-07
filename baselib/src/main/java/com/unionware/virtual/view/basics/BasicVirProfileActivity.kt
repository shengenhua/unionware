package com.unionware.virtual.view.basics

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.chad.library.adapter4.loadState.LoadState
import com.unionware.virtual.view.adapter.BasicProfileAdapter
import com.unionware.virtual.viewmodel.QueryListViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.model.req.FiltersReq


/**
 * 基础数据
 */
@AndroidEntryPoint
//@Route(path = RouterPath.MES.PATH_MES_BASIC)
class BasicVirProfileActivity : BaseVirQListActivity<QueryListViewModel>() {

    private val filtersReq: FiltersReq = FiltersReq(1)

    /**
     * 请求的地方
     */
    @JvmField
    @Autowired(name = "position")
    var position: Int = 0

    /**
     * 接口
     */
    @JvmField
    @Autowired(name = "key")
    var name: String? = null

    /**
     * 特殊 基础查询条件
     */
    @JvmField
    @Autowired(name = "parentId")
    var parentId: String? = null

    /**
     * 特殊 基础查询条件
     */
    @JvmField
    @Autowired(name = "parentName")
    var parentName: String? = null

    /**
     * 维度Id
     */
    @JvmField
    @Autowired(name = "flexId")
    var flexId: String? = null

    /**
     * 过滤条件
     */
    @JvmField
    @Autowired(name = "custom")
    var custom: String? = null

    private var adapter: BasicProfileAdapter = BasicProfileAdapter()

    override fun initViewObservable() {
        mViewModel.basicLiveData.observe(this) {
            if (mViewModel.pageIndex.value == 1) {
                adapterHelper?.trailingLoadState = LoadState.None
                adapter.submitList(it)
            } else {
                adapter.addAll(it)
            }
            if (it.size < 20) {
                adapterHelper?.trailingLoadState = LoadState.NotLoading(true)
            } else {
                adapterHelper?.trailingLoadState = LoadState.NotLoading(false)
            }
            if (isQuery && it.size == 1) {
                itemClickOpen(0)
            }
            isQuery = false
        }
    }

    override fun initView() {
        super.initView()
        setTitle("基础资料")
    }

    override fun queryAdapter(): BasicProfileAdapter {
        adapter.also {
            it.setOnItemClickListener { adapter, view, position ->
                itemClickOpen(position)
            }
        }
        return adapter
    }

    override fun itemClickOpen(position: Int) {
        val bean = adapter.items[position]
        val intent = intent.putExtra("baseInfo", bean)
        setResult(this.position, intent)
        finish()
    }

    override fun queryList(listSearchId: String, query: String?) {
        //
        filtersReq.filters = HashMap<String, Any>().apply {
            if (query?.isNotEmpty() == true) {
                put("keyword", query)
            }
            parentName?.let { put(it, parentId!!) }
            flexId?.let { put("flexId", it) }
        }
        custom?.let { filtersReq.custom = it}

        mViewModel.queryBasic(scene, name, filtersReq)
    }
}