package com.unionware.mes.view.basics

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.unionware.basicui.base.activity.BaseQueryListActivity
import com.unionware.basicui.base.adapter.BasicBillListAdapter
import com.unionware.basicui.base.viewmodel.BaseQueryListViewModel
import com.unionware.mes.MESPath
import com.unionware.mes.app.RouterMESPath
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.ext.bigDecimalToZeros


/**
 * 数据 工序单列表
 */
@AndroidEntryPoint
@Route(path = RouterMESPath.MES.PATH_MES_LIST)
class QueryListActivity : BaseQueryListActivity<BaseQueryListViewModel>() {
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
            "661779469DFB27",
            filtersReq = unionware.base.model.req.FiltersReq(mViewModel.pageIndex.value).apply {
                filters = mutableMapOf<String, Any>("configId" to primaryId).also { map ->
                    if (!query.isNullOrEmpty()) {
                        map["keyword"] = query
                    }
                    /*query?.also {
                        map["keyword"] = query
                    }*/
                    if (orderId.isNotEmpty()) {
                        map["orderId"] = orderId
                    }
                }
            })
        /* //调用接口查询
         when (useStyleId) {
             "2" -> {
             }
             //1
             else -> {
                 mViewModel.query(listSearchId, primaryId, query)
             }
         }*/
    }

    override fun itemClickOpen(position: Int) {
        ARouter.getInstance().build(MESPath.openPath(MESPath.PathTag.DETAILS))
            .withSerializable("appId", primaryId)
            .withSerializable("scene", scene)
            .withSerializable("primaryId", primaryId)
            .withSerializable(
                "id",
                mViewModel.dataLiveData.value?.get(position)?.get("id")?.bigDecimalToZeros() ?: ""
            )
            .withSerializable(
                "title",
                mViewModel.dataLiveData.value?.get(position)?.get("code").toString()
            )
            .navigation()

        /*startActivity(Intent(this, DetailsActivity::class.java).apply {
            putExtra("appId", primaryId)
            putExtra("scene", scene)
//            putExtra("itemSearchId", itemSearchId)
            putExtra("primaryId", primaryId)
            putExtra(
                "id",
                mViewModel.dataLiveData.value?.get(position)?.get("id")?.bigDecimalToZeros() ?: ""
            )
            putExtra(
                "title", mViewModel.dataLiveData.value?.get(position)?.get("code").toString()
            )
        })*/
    }

}