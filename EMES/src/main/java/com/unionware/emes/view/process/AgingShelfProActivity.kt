package com.unionware.emes.view.process

import com.chad.library.adapter4.BaseQuickAdapter
import com.unionware.emes.R
import unionware.base.model.bean.barcode.QueryBarCodeBean
import com.unionware.emes.view.process.base.QueryProcessActivity
import com.unionware.emes.viewmodel.process.ProcessViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.model.ViewDisplay


/**
 * 老化上架工序（老化测试作业 -- 老化上架）
 */
@AndroidEntryPoint
open class AgingShelfProActivity : QueryProcessActivity<ProcessViewModel>() {

    override fun ProcessViewModel.barcodeObserve() {
        barcodeLiveData.observe(this@AgingShelfProActivity) {
            val bean = QueryBarCodeBean(it.code).apply {
                materialId = it.materialId
                materialCode = it.materialCode
                materialName = it.materialName
                materialSpec = it.materialSpec
                qty = it.qty
                showQuery = false
                queryName = "老化架号"
                processAdapter?.getItem(tag = "aging")?.also { viewDisplay ->
                    id = viewDisplay.id
                    name = viewDisplay.value
                }
            }

            barCodeAdapter?.add(0, bean)
            scanAdapter?.notifyItemChanged(0)
//            barCodeAdapter?.apply { checkAging(this, 0) }
        }
    }

    private fun checkAging(
        baseQuickAdapter: BaseQuickAdapter<QueryBarCodeBean, *>, i: Int,
    ): Boolean {
        if (baseQuickAdapter.items[i].id == null) {
            //没有选择
            itemClick(baseQuickAdapter, i)
            return true
        }
        return false
    }

    override fun getItems(): List<Map<String, String?>>? {
        /* return barCodeAdapter?.items?.map {
             mapOf(
                 Pair("code", it.code),
                 Pair("frameId", it.id)
             )
         }*/

        return barCodeAdapter?.items?.map { it ->
            mutableMapOf(
                Pair("code", it.code),
                Pair("qty", it.qty),
                Pair("frameId", it.id)
            ).apply {
            }
        }
    }

    override fun initView() {
        super.initView()
        barCodeAdapter?.addOnItemChildClickListener(R.id.tvQuery) { baseQuickAdapter, view, position ->
//            itemClick(baseQuickAdapter, position)
        }
    }

    override fun headQueryBasic(position: Int, adapter: BaseQuickAdapter<ViewDisplay, *>) {
        queryBasic(
            position,
            adapter.items[position].code,
            adapter.items[position].parentId,
            adapter.items[position].parentName,
        ) { pos, infoBean ->
            infoBean?.apply {
                adapter.items[pos!!].value = this.name
                adapter.items[pos].id = this.id
                adapter.notifyItemChanged(pos)
                barCodeAdapter?.items?.withIndex()?.forEach {
                    it.value.id = this.id
                    it.value.name = this.name
                    barCodeAdapter?.notifyItemChanged(it.index)
                }
            }
        }
    }

    override fun itemClick(baseQuickAdapter: BaseQuickAdapter<QueryBarCodeBean, *>, position: Int) {
        queryBasic(
            position,
            "BOS_ASSISTANTDATA_SELECT",
            "666fad2a8a79cc",
            "parentId"
        ) { pos, infoBean ->
            infoBean?.apply {
                baseQuickAdapter.items[position].id = this.id
                baseQuickAdapter.items[position].name = this.name
                baseQuickAdapter.notifyItemChanged(position)
            }
        }
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        val items
                : MutableList<ViewDisplay> =
            mutableListOf(
                ViewDisplay("老化架号", tag = "aging", isEdit = false).apply {
                    code = "BOS_ASSISTANTDATA_SELECT"
                    parentName = "parentId"
                    parentId = "666fad2a8a79cc"
                },
                ViewDisplay("备注", "remark", "remark", null, true)
            )
        return items
    }

}