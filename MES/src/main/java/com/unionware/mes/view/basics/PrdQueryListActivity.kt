package com.unionware.mes.view.basics

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.unionware.basicui.base.activity.BaseQueryListActivity
import com.unionware.basicui.base.adapter.BasicBillListAdapter
import com.unionware.basicui.base.viewmodel.BaseQueryListViewModel
import com.unionware.mes.app.RouterMESPath
import com.unionware.path.RouterPath
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.ext.bigDecimalToZeros


/**
 * 数据 生产单列表
 */
@AndroidEntryPoint
@Route(path = RouterMESPath.MES.PATH_MES_PRDLIST)
class PrdQueryListActivity : BaseQueryListActivity<BaseQueryListViewModel>() {
    private var adapter: BasicBillListAdapter? = null


    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.viewLiveData.observe(this) {
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
        mViewModel.query(scene = scene, "66E005BB0920D9", primaryId, query)
    }

    override fun itemClickOpen(position: Int) {
        if (position >= (mViewModel.dataLiveData.value?.size ?: 0)) {
            return
        }
        ARouter.getInstance().build(RouterMESPath.MES.PATH_MES_LIST)
            .withSerializable("scene", scene)
//            .withSerializable("itemSearchId", itemSearchId)
//            .withSerializable("listSearchId", listSearchId)
//            .withSerializable("orderSearchId", orderSearchId)
            .withSerializable("useStyleId", useStyleId).withSerializable("primaryId", primaryId)
            .withSerializable(
                "orderId",
                mViewModel.dataLiveData.value?.get(position)?.get("id")?.bigDecimalToZeros() ?: ""
            ).withSerializable(
                "title", mViewModel.dataLiveData.value?.get(position)?.get("code").toString()
            ).navigation()
    }

}