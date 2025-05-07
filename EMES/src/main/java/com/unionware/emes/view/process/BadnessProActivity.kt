package com.unionware.emes.view.process

import com.unionware.emes.bean.SpecialReportReq
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.ViewDisplay
import unionware.base.model.bean.barcode.MultiBarCodeBean
import com.unionware.emes.view.process.base.MultiBarcodeProcessActivity
import com.unionware.emes.viewmodel.process.BadnessProViewModel
import unionware.base.ext.showToast
import unionware.base.model.req.FiltersReq
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.model.bean.barcode.BarCodeBean

/**
 * 不良返修工序(不良返修)
 */
@AndroidEntryPoint
class BadnessProActivity : MultiBarcodeProcessActivity<BadnessProViewModel>() {

    override fun getItems(): List<Map<String, String?>>? {
        return collectAdapter?.items?.map {
            mapOf(
                Pair("code", it.code),
                Pair("qty", it.qty),
//                Pair("reasonId", it.id)
                Pair("phenomena", it.collects?.get(0)?.value)
            )
        }
    }

    override fun queryFilters(barcode: String): FiltersReq = FiltersReq(
        mapOf(
            Pair("primaryCode", barcode), Pair("taskId", taskId), Pair("type", "bl"),
        )
    )

    override fun BadnessProViewModel.barcodeObserve() {
        barcodeLiveData.observe(this@BadnessProActivity) { it: BarCodeBean ->

            val bean = MultiBarCodeBean(it.code).apply {
                materialId = it.materialId
                materialCode = it.materialCode
                materialName = it.materialName
                materialSpec = it.materialSpec
                qty = it.qty
            }
            val item = CollectMultiItem().apply {
                colMethod = 1
                colName = "不良现象"
                tag = "reasonText"
            }
            processAdapter?.items?.also { headItems ->
                headItems.firstOrNull { it.tag == "poorReason" }?.apply {
                    if (value?.isNotEmpty() == true) {
                        item.value = value
                        item.stdValue = value
                    }
                }
            }
            bean.collects = mutableListOf(item)
            collectAdapter?.add(0, bean)
            scanAdapter?.notifyItemChanged(0)
        }
    }

    override fun initData() {
//        super.initData()
    }

    override fun submitReport() {
        collectAdapter?.items?.forEach {
            /*if (it.id?.isNullOrEmpty()) {
                "条码${it.code}未选择${it.queryName}"
                return@submitReport
            }*/
            //reasonText
            if (it.collects?.get(0)?.value.isNullOrEmpty()) {
                "条码${it.code}未填写${it.collects?.get(0)?.colName}".showToast()
                return@submitReport
            }
        }
        mViewModel.repaired(SpecialReportReq().apply {
            data = HashMap<String, Any>().apply {
                putAll(mapOf(Pair("items", getItems() as Any)))
                /*processAdapter?.items?.forEach {
                    if (it.key?.isNotEmpty() == true && it.value?.isNotEmpty() == true) {
                        put(it.key ?: "", it.id ?: it.value as Any)
                    }
                }*/
                put("jobId", this@BadnessProActivity.jobId)
                put("taskId", this@BadnessProActivity.taskId)
            }
            /*SpecialReportReq.DataReq().apply {
                jobId = this@BadnessProActivity.jobId
                taskId = this@BadnessProActivity.taskId
                items = getItems()
            }*/
        })
    }

    /*override fun itemClick(baseQuickAdapter: BaseQuickAdapter<QueryBarCodeBean, *>, position: Int) {
        queryBasic(
            position,
            "BOS_ASSISTANTDATA_SELECT",
            "666fad128a79ca",
            "parentId"
        ) { pos, infoBean ->
            infoBean?.apply {
                baseQuickAdapter.items[pos!!].id = this.id
                baseQuickAdapter.items[pos].name = this.name
                baseQuickAdapter.notifyItemChanged(position)
            }
        }
    }*/

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            /*ViewDisplay(
                "不良原因",
                "poorReason",
                "",
                "BOS_ASSISTANTDATA_SELECT",
                true
            ).apply {
                parentName = "parentId"
                parentId = "666fad128a79ca"
            }*/ViewDisplay("不良现象", "poorReason", isEdit = true)
        )
        return items
    }
}