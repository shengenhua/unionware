package com.unionware.once.view.basic

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.unionware.basicui.base.activity.BaseQueryListActivity
import com.unionware.basicui.base.adapter.BasicBillListAdapter
import com.unionware.basicui.base.viewmodel.BaseQueryListViewModel
import com.unionware.once.app.RouterOncePath
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.ext.bigDecimalToZeros
import unionware.base.model.req.FiltersReq


/**
 * 数据 工序单列表
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_LIST)
class OnceQueryListActivity : BaseQueryListActivity<BaseQueryListViewModel>() {
    private var adapter: BasicBillListAdapter? = null

    @JvmField
    @Autowired(name = "orderId")
    var orderId: String = ""

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

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun inputQuery(query: String?) {
        mViewModel.query(
            scene,
//            listSearchId,
            "67EDEF8248ABC0",
            filtersReq = FiltersReq(mViewModel.pageIndex.value).apply {
                filters = mutableMapOf<String, Any>("configId" to primaryId).also { map ->
                    if (!query.isNullOrEmpty()) {
                        map["keyword"] = query
                    }
                    if (orderId.isNotEmpty()) {
                        map["orderId"] = orderId
                    }
                }
            })
    }

    override fun itemClickOpen(position: Int) {
        //67edef0e48abbd
        val path = when (primaryId) {
            "67edef0e48abbd" -> {
                RouterOncePath.ONCE.PATH_MES_LACAL_DYNAMIC
            }

            else -> {
                //其他情况
                RouterOncePath.ONCE.PATH_MES_LACAL_DYNAMIC
            }
        }
        val bean = adapter?.getItem(position)
        ARouter.getInstance().build(path)
            .withSerializable("appId", primaryId)
            .withSerializable("scene", scene)
            .withSerializable("primaryId", primaryId)
            .withSerializable("jobName", title)
            .withSerializable(
                "id",
                bean?.dataMap?.get("id")?.bigDecimalToZeros() ?: ""
            )
            .withSerializable(
                "title",
                bean?.dataMap?.get("code").toString()
            )
            .navigation()
    }

}