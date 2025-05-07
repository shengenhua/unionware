package com.unionware.once.view.inspect

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.basicui.base.activity.BaseQueryListActivity
import com.unionware.basicui.base.adapter.BasicBillListAdapter
import com.unionware.once.app.RouterOncePath
import com.unionware.once.viewmodel.inspect.InspectQueryViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.ext.bigDecimalToZeros
import unionware.base.model.req.FiltersReq

/**
 * 巡检 列表
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_INSPECTION)
class InspectListActivity : BaseQueryListActivity<InspectQueryViewModel>() {
    private var adapter: BasicBillListAdapter? = null

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.viewLiveData.observe(this) {
            //add
            if (mViewModel.pageIndex.value == 1) {
                adapter?.submitList(mutableListOf())
                adapter?.submitList(it)
            } else {
                adapter?.addAll(it)
            }
        }
    }

    override fun initView() {
        super.initView()
    }

    override fun queryAdapter(): BasicBillListAdapter {
        adapter = adapter ?: BasicBillListAdapter()
        adapter?.also {
            it.setOnItemClickListener { adapter, view, position ->
                itemClickOpen(position)
            }
        }
        return adapter as BasicBillListAdapter
    }

    override fun inputQuery(query: String?) {
        //调用接口查询
        mViewModel.queryJobList(FiltersReq(mapOf(Pair("keyword", query))), scene)
    }

    override fun itemClickOpen(position: Int) {
        val intent = Intent(this, InspectDetailsActivity::class.java).apply {
            putExtra("scene", scene)
            putExtra("primaryId", primaryId)
            putExtra(
                "id",
                mViewModel.dataLiveData.value?.get(position)?.get("id")?.bigDecimalToZeros() ?: ""
            )
            putExtra(
                "title", mViewModel.dataLiveData.value?.get(position)?.get("code").toString()
            )
        }
        startActivity(intent)
    }

}