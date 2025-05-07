package com.unionware.once.view

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter4.BaseQuickAdapter
import com.unionware.basicui.base.activity.BaseProcessActivity
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.once.app.RouterOncePath
import com.unionware.once.viewmodel.GelatinBindViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.ext.bigDecimalToZeros
import unionware.base.ext.showToast
import unionware.base.ext.tryBigDecimalToZeros
import unionware.base.model.ViewDisplay
import unionware.base.model.req.FiltersReq

/**
 * 明胶半成品货位绑定
 * Author: sheng
 * Date:2025/1/13
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_MJBCPHWBD)
class GelatinBindActivity() : BaseProcessActivity<GelatinBindViewModel>(), Parcelable {
    /**
     * 填写资料
     */
    var processAdapter: ProcessAdapter? = null

    constructor(parcel: Parcel) : this() {

    }

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            gelatinBindLive.observe(this@GelatinBindActivity) {
                "绑定成功".showToast()
                processAdapter?.clearData()
                processAdapter?.setFocusable(tag = "BarCode")
            }
            basicLiveData.observe(this@GelatinBindActivity) { bean ->
                bean?.apply {
                    processAdapter?.apply {
                        items.withIndex().firstOrNull { it.value.tag == "StockId" }?.also {
                            processAdapter?.items?.firstOrNull { it.tag == "StockLoc" }
                                ?.also { stockLoc ->
                                    if (!bean.stock?.fStockFlexItem.isNullOrEmpty()) {
                                        stockLoc.parentId =
                                            bean.stock?.fStockFlexItem?.get(0)?.get("flexId")
                                                ?.bigDecimalToZeros()
                                        stockLoc.isRequired = true
                                    } else {
                                        stockLoc.parentId = ""
                                        stockLoc.isRequired = false
                                    }
                                }
                            items[it.index].value = bean.stock?.name ?: ""
                            items[it.index].id = bean.stock?.id?.bigDecimalToZeros() ?: ""
                            notifyItemChanged(it.index)
                        }
                        items.withIndex().firstOrNull { it.value.tag == "StockLoc" }?.also {
                            bean.stockLoc?.apply {
                                if (this.isNotEmpty()) {
                                    this[0].firstNotNullOf { it.value }.apply {
                                        items[it.index].value = this.name ?: ""
                                        items[it.index].id = this.id?.bigDecimalToZeros() ?: ""
                                        notifyItemChanged(it.index)
                                    }
                                }
                            }
                        }
                    }
                }
                bean?.apply {
                    processAdapter?.apply {
                        items.withIndex().firstOrNull { it.value.tag == "StockId" }?.also {
                            if (!items[it.index].value.isNullOrEmpty() && items[it.index].id != bean.stock?.id.bigDecimalToZeros()) {
                                processAdapter?.clearData("StockLoc")
                                processAdapter?.items?.firstOrNull { it.tag == "StockLoc" }?.parentId =
                                    ""
                            }
                            processAdapter?.items?.firstOrNull { it.tag == "StockLoc" }?.also {
                                if (!bean.stock?.fStockFlexItem.isNullOrEmpty()) {
                                    it.parentId = bean.stock?.fStockFlexItem?.get(0)?.get("flexId")
                                        ?.bigDecimalToZeros()
                                    it.isRequired = true
                                } else {
                                    it.parentId = ""
                                    it.isRequired = false
                                }
                            }
                            items[it.index].value = bean.stock?.name ?: ""
                            items[it.index].id = bean.stock?.id.bigDecimalToZeros() ?: ""
                            notifyItemChanged(it.index)
                        }
                    }
                }
            }
        }
    }

    override fun initView() {
        super.initView()
        setTitle(title)
        binding?.clBody?.visibility = View.GONE
        processAdapter?.addOnEditorActionArray("BarCode") { _, _, text ->
            if (text.isEmpty()) {
                return@addOnEditorActionArray
            }
            processAdapter?.setFocusable(tag = "StockLocBarcode")
        }
        processAdapter?.addOnEditorActionArray("StockLocBarcode") { adapter, position, text ->
            if (text.isEmpty()) {
                return@addOnEditorActionArray
            }
            mViewModel.queryBasic(scene, "UNW_WMS_BINCODE", FiltersReq().apply {
                filters = mutableMapOf<String?, Any?>().apply {
                    put("primaryCode", text)
                }
            })
        }
        processAdapter?.setFocusable(tag = "BarCode")
    }

    override fun headQueryBasic(position: Int, adapter: BaseQuickAdapter<ViewDisplay, *>) {
        if (adapter.items[position].tag == "StockLoc") {
            if (adapter.items.firstOrNull { it.tag == "StockId" }?.value.isNullOrEmpty()) {
                "请先选择仓库".showToast()
                return
            }
            if (adapter.items[position].parentId.isNullOrEmpty()) {
                "当前仓库没有仓位选择".showToast()
                return
            }
        }
        queryBasic(
            position,
            adapter.items[position].code,
            adapter.items[position].parentId,
            adapter.items[position].parentName,
        ) { pos, infoBean ->
            infoBean?.apply {
                if (adapter.items[pos!!].key == "StockId") {
                    if (!adapter.items[pos].value.isNullOrEmpty() && adapter.items[pos].id != this.id) {
                        processAdapter?.clearData("StockLoc")
                        processAdapter?.items?.firstOrNull { it.tag == "StockLoc" }?.parentId = ""
                    }
                    processAdapter?.items?.firstOrNull { it.tag == "StockLoc" }?.also {
                        if (!this.fStockFlexItem.isNullOrEmpty()) {
                            it.parentId =
                                this.fStockFlexItem?.get(0)?.get("flexId")?.bigDecimalToZeros()
                            it.isRequired = true
                        } else {
                            it.parentId = ""
                            it.isRequired = false
                        }
                    }
                }
                adapter.items[pos].value = this.name
                adapter.items[pos].id = this.id
                adapter.notifyItemChanged(pos)
            }
        }
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            ViewDisplay(
                "吨袋二维码",
                tag = "BarCode",
                key = "BarCode",
                isEdit = true,
                isRequired = true
            ),
            ViewDisplay(
                "库位条码",
                tag = "StockLocBarcode",
                isEdit = true
            ),
            ViewDisplay(
                "仓库",
                tag = "StockId",
                key = "StockId",
                code = "BD_STOCK",
                isRequired = true
            ),
            ViewDisplay(
                "仓位",
                tag = "StockLoc",
                key = "StockLoc",
                code = "BOS_FLEXVALUE_SELECT",
                isRequired = true
            ).apply {
                parentName = "flexId"
            }
        )
        return items
    }

    override fun onActionSubmitConfirm() {
        processAdapter?.items?.firstOrNull { it.isRequired && it.value.isNullOrEmpty() }?.also {
            if (it.isRequired && it.value.isNullOrEmpty()) {
                if (it.type == 2) {
                    "请选择${it.title}".showToast()
                } else {
                    "${it.title}不允许为空!".showToast()
                }
                return@onActionSubmitConfirm
            }
        }
        mViewModel.stockBindAction(HashMap<String, Any>().apply {
            processAdapter?.items?.forEach {
                if (it.key?.isNotEmpty() == true) {
                    if (it.key == "StockLoc") {
                        (mutableListOf<Map<String, Any>>().also { list ->
                            if (!it.id.isNullOrEmpty()) {
                                list.add(mutableMapOf<String, Any>().also { map ->
                                    map["FlexId"] =
                                        (it.parentId.tryBigDecimalToZeros()?.toInt() as Any)
                                    map["FlexValue"] =
                                        (it.id.tryBigDecimalToZeros()?.toInt() as Any)
                                })
                            }
                        }).apply {
                            put(it.key ?: "", this as Any)
                        }
                    } else if (it.id?.isNotEmpty() == true || it.key == "id") {
                        it.key?.apply {
                            put(this, it.id.tryBigDecimalToZeros()?.toInt() as Any)
                        }
                    } else {
                        it.key?.apply {
                            put(this, it.value ?: "")
                        }
                    }
                }
            }
        })
    }

    override fun heardProcessAdapter(): ProcessAdapter {
        return super.heardProcessAdapter().apply { processAdapter = this }
    }

    override fun topAdapter(): RecyclerView.Adapter<ViewHolder>? = null
    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GelatinBindActivity> {
        override fun createFromParcel(parcel: Parcel): GelatinBindActivity {
            return GelatinBindActivity(parcel)
        }

        override fun newArray(size: Int): Array<GelatinBindActivity?> {
            return arrayOfNulls(size)
        }
    }
}