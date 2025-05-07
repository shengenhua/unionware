package com.unionware.basicui.base.activity

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter4.loadState.LoadState
import com.unionware.basicui.base.viewmodel.BasicViewModel
import com.unionware.path.RouterPath
import com.unionware.virtual.view.adapter.BasicProfileAdapter
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.model.req.FiltersReq


/**
 * 基础数据
 */
@AndroidEntryPoint
@Route(path = RouterPath.APP.MES.PATH_MES_BASIC)
class BasicProfileActivity : BaseQueryListActivity<BasicViewModel>() {

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

    private var adapter: BasicProfileAdapter = BasicProfileAdapter()

    override fun initViewObservable() {
        mViewModel.basicLiveData.observe(this) {
            if (mViewModel.pageIndex.value == 1) {
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
                itemClick(it[0])
            } else {
                isQuery = false
            }
            binding?.smRefresh?.finishRefresh()
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
        /*val bean = adapter.items[position]
        val intent = intent.putExtra("baseInfo", bean)
        setResult(this.position, intent)
        finish()*/
        itemClick(adapter.items[position])
    }

    private fun itemClick(bean: unionware.base.model.bean.BaseInfoBean) {
        val intent = intent.putExtra("baseInfo", bean)
        setResult(this.position, intent)
        finish()
    }

    override fun inputQuery(query: String?) {
        //
        filtersReq.filters = HashMap<String, Any>().apply {
            if (query?.isNotEmpty() == true) {
                put("keyword", query)
            }
            put(parentName ?: "parentId", parentId ?: "")
        }
        mViewModel.queryBasic(scene, name ?: "BOS_ASSISTANTDATA_SELECT", filtersReq)
    }
}