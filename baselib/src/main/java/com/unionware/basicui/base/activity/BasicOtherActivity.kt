package com.unionware.basicui.base.activity

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.chad.library.adapter4.loadState.LoadState
import com.unionware.basicui.base.adapter.BasicOtherProfileAdapter
import com.unionware.basicui.base.viewmodel.BasicViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.api.util.ConvertUtils


/**
 * 基础数据
 */
@AndroidEntryPoint
class BasicOtherActivity : BaseQueryListActivity<BasicViewModel>() {

    private val filtersReq: unionware.base.model.req.FiltersReq =
        unionware.base.model.req.FiltersReq(1)

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

    private var adapter: BasicOtherProfileAdapter = BasicOtherProfileAdapter()

    override fun initViewObservable() {
        mViewModel.otherBasicLiveData.observe(this) {
            adapter.view = it.view
            adapter.submitList(it.data)
            if (it.data.size <= 20) {
                adapterHelper?.leadingLoadState = LoadState.NotLoading(true)
            } else {
                adapterHelper?.leadingLoadState = LoadState.NotLoading(false)
            }
            if (isQuery && it.data.size == 1) {
                itemClick(it.data[0], it.view)
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

    override fun queryAdapter(): BasicOtherProfileAdapter {
        adapter.also {
            it.setOnItemClickListener { adapter, view, position ->
                itemClickOpen(position)
            }
        }
        return adapter
    }

    override fun itemClickOpen(position: Int) {
        itemClick(adapter.items[position], adapter.view)
    }

    private fun itemClick(bean: Map<String, String>, view: List<unionware.base.model.bean.ViewBean>?) {
        val intent = intent.putExtra("baseInfo", unionware.base.model.bean.SerializableMap().apply {
            map = bean
            list = ConvertUtils.convertMapToList(view, bean)
        })
        setResult(this.position, intent)
        finish()
    }

    override fun inputQuery(query: String?) {
        filtersReq.filters = HashMap<String, Any>().apply {
            if (query?.isNotEmpty() == true) {
                put("keyword", query)
            }
            put(parentName ?: "parentId", parentId ?: "")
        }
        mViewModel.queryOtherBasic(scene, name ?: "BOS_ASSISTANTDATA_SELECT", filtersReq)
    }
}