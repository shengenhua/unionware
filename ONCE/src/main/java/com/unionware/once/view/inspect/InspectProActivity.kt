package com.unionware.once.view.inspect

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.unionware.once.viewmodel.inspect.InspectProViewModel
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.barcode.MultiBarCodeBean
import unionware.base.ext.showToast
import unionware.base.model.req.FiltersReq
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.model.ViewDisplay
import unionware.base.model.resp.ChecketReq
import java.util.stream.Collectors


/**
 * 巡检工序 (巡检)
 */
@AndroidEntryPoint
open class InspectProActivity : MultiBarcodeProcessActivity<InspectProViewModel>() {

    override fun onActionSubmitConfirm() {
        collectAdapter?.items?.withIndex()?.forEach {
            it.value.collects?.forEach { collect ->
                if (collect.value.isNullOrEmpty()) {
                    binding?.rvTail?.layoutManager?.scrollToPosition(it.index)
                    "条码${it.value.code}${collect.colName}不能为空!".showToast()
                    return@onActionSubmitConfirm
                }
            }
        }
        super.onActionSubmitConfirm()
    }

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            collectionLiveData.observe(this@InspectProActivity) {
                barcodeLiveData.value?.also { barcode ->
                    val bean = MultiBarCodeBean(barcode.code).apply {
                        materialId = barcode.materialId
                        materialCode = barcode.materialCode
                        materialName = barcode.materialName
                        materialSpec = barcode.materialSpec
                        qty = barcode.qty
                    }
                    bean.collects = mutableListOf<CollectMultiItem>().apply {
                        it?.forEach {
                            val item = CollectMultiItem()
                            item.code = it.code
                            item.value = it.value
                            item.name = it.name
                            item.id = it.id
                            item.colName = it.colName
                            item.colId = it.colId
                            item.colSeq = it.colSeq
                            item.colMethod = it.colMethod
                            item.colNumber = it.colNumber
                            item.tag = it.tag
                            item.stdValue = it.stdValue
                            add(item)
                        }
                    }
                    collectAdapter?.add(0, bean)
                    scanAdapter?.notifyItemChanged(0)
                }
            }
        }
    }

    override fun getItems(): List<Map<String, Any?>>? {
        return collectAdapter?.items?.map {
            mapOf(
                Pair("code", it.code),
                Pair("qty", it.qty ?: "0"),
                Pair("inspects", it.collects?.stream()?.map { collect ->
                    mapOf(
                        Pair("itemId", collect.colId),
                        Pair("itemValue", collect.value)
                    )
                }?.collect(Collectors.toList()))
            )
        }
    }

    override fun scanQuery(it: String) {
        if (processAdapter?.getItem("jobId")?.id.isNullOrEmpty()) {
            "请先选择工序".showToast()
            scanAdapter?.notifyItemChanged(0)
            return
        }
        super.scanQuery(it)
    }

    override fun InspectProViewModel.barcodeObserve() {
        barcodeLiveData.observe(this@InspectProActivity) {
            mViewModel.getInspectOption(
                FiltersReq(
                    mapOf(
                        Pair("jobId", processAdapter?.getItem("jobId")?.id),
                        Pair("materialId", it.materialId),
                        Pair("typeId", "2")
                    )
                ),
                scene
            )
        }
    }


    override fun initData() {
//        mViewModel.getCollectOption(FiltersReq(mapOf(Pair("jobId", jobId))), scene)
//        Watermark.getInstance().show(this, "巡检")
    }

    override fun headQueryBasic(position: Int, adapter: BaseQuickAdapter<ViewDisplay, *>) {
        if (adapter.items[position].key == "jobId" &&
            (mViewModel.barcodeItemCountLiveData.value ?: 0) > 0
        ) {
            "存在有扫描的条码，无法修改工序".showToast()
            return
        }
        queryBasic(
            position,
            adapter.items[position].code,
            adapter.items[position].parentId,
            adapter.items[position].parentName,
        ) { pos, infoBean ->
            infoBean?.apply {
                pos?.also {
                    adapter.items[it].value = this.name
                    adapter.items[it].id = this.id
                    adapter.notifyItemChanged(it)
                }
            }
        }
    }

    /**
     * 扫描条码 上报的数据
     */
    override fun queryFilters(barcode: String): FiltersReq = FiltersReq(
        mapOf(
            Pair("primaryCode", barcode),
            Pair("taskId", taskId),
            Pair("type", "xj"),
        )
    )

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(ViewDisplay(
            "工序", "jobId", "jobId", "66960F290DE7F4", false, isRequired = true
        ).apply {
            parentId = primaryId
            parentName = "primaryId"
        })
        return items
    }

    override fun submitReport() {
        mViewModel.checketReport(ChecketReq().apply {
            data =
                mutableListOf(HashMap<String, Any>(mapOf(Pair("items", getItems() as Any))).apply {
                    processAdapter?.items?.forEach {
                        if (it.key?.isNotEmpty() == true && it.value?.isNotEmpty() == true) {
                            put(it.key ?: "", it.id ?: it.value as Any)
                        }
                    }
                    put("type", "2")
                    put("planId", taskId)
                })
        })
    }

    override fun topAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
}
