package com.unionware.wms.ui.adapter

import android.app.Activity
import android.widget.CheckBox
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import unionware.base.model.bean.BillBean
import unionware.base.ui.NoTouchRecyclerView
import java.math.BigDecimal

/**
 * @Author : pangming
 * @Time : On 2024/7/16 10:35
 * @Description : NormalScanBillInfoAdapter
 */
class NormalScanBillInfoAdapter(private val type: Int, private val mContext: Activity) :
    BaseQuickAdapter<BillBean, BaseViewHolder>(
        R.layout.item_normal_bill_content
    ) {
    var isQuery: Boolean = false
    var isSelect: Boolean = false
    var isNoCode: Boolean = false //无条码采集模式
    var isNoCodeByMaterial: Boolean = false //按产品档案
    var isSTK_StockCountInput:Boolean = false //是否盘点
    override fun convert(holder: BaseViewHolder, bean: BillBean) {
        val rvList = holder.getView<NoTouchRecyclerView>(R.id.rv_list)
        holder.setGone(R.id.tv_bill_code, type == 2 || type == 0 || null == bean.code)
        holder.setGone(R.id.view_diver, type == 2 || type == 0 || null == bean.code)
        holder.setGone(R.id.tbQuery, !isQuery)
        holder.setText(R.id.tv_bill_code, "单据编号：" + bean.code)
        when (type) {
            0 -> {
                holder.setVisible(R.id.tv_content_index, true)
                rvList.layoutManager = LinearLayoutManager(mContext)
                val adapter = CommonContentAdapter()
                rvList.adapter = adapter
                adapter.setNewInstance(bean.list)
                holder.setText(R.id.tv_content_index, (holder.layoutPosition + 1).toString())
            }

            1 -> {
                rvList.layoutManager = LinearLayoutManager(mContext)
                val billAdapter =
                    BillContentAdapter()
                rvList.adapter = billAdapter
                billAdapter.setNewInstance(bean.list)
            }

            else -> {
                rvList.layoutManager = GridLayoutManager(mContext, 3)
                val gridAdapter = GridContentAdapter()
                rvList.adapter = gridAdapter
                gridAdapter.setNewInstance(bean.list)
            }
        }
        if (bean.dataMap?.isNotEmpty() == true && bean.dataMap.containsKey("FDisplayQuantity")) {
            bean.dataMap.filter { it.key == "FDisplayQuantity" }.apply {
                val list = this["FDisplayQuantity"].toString().split("/")
                //数量(已扫/未扫/应扫)
                val cardView: CardView = holder.getView<CardView>(R.id.cd_bill_item)

                if (list.size > 2) {
                    if(isSTK_StockCountInput){
                        //盘点特殊处理 已扫数量大于0就是蓝色
                        if (BigDecimal(list[0]).toDouble() > 0.0) {
                            cardView.setCardBackgroundColor(context.resources.getColor(R.color.item_have_more,
                                context.resources.newTheme()))
                        }else{
                            cardView.setCardBackgroundColor(context.resources.getColor(
                                unionware.base.R.color.white,
                                context.resources.newTheme()))
                        }
                    }else {
                        if (BigDecimal(list[1]).toDouble() <= 0.0) { //扫满或超发
                            cardView.setCardBackgroundColor(context.resources.getColor(R.color.item_have_more,
                                context.resources.newTheme()))
                        } else if (BigDecimal(list[1]).toDouble() < BigDecimal(list[2]).toDouble()) {
                            cardView.setCardBackgroundColor(context.resources.getColor(R.color.item_have_not_full,
                                context.resources.newTheme()))
                        } else {
                            cardView.setCardBackgroundColor(context.resources.getColor(
                                unionware.base.R.color.white,
                                context.resources.newTheme()))
                        }
                    }
                }

            }
        }
        holder.setVisible(R.id.img_no_code, false)
        if (isSelect) {
            //指定分录扫描
            //区分按产品档案控制无条码制单
            if (isNoCode || (isNoCodeByMaterial && bean.dataMap.containsKey("IsBarCodeManage") && bean.dataMap["IsBarCodeManage"].toString() == "false")
            ) {
                //不启用条码管理
                holder.setVisible(R.id.cb_select, false)
                holder.setVisible(R.id.img_no_code, true)
            } else {
                holder.setVisible(R.id.cb_select, true)
            }

        } else {
            //一般扫描
            if (isNoCode || (isNoCodeByMaterial && bean.dataMap.containsKey("IsBarCodeManage") && bean.dataMap["IsBarCodeManage"].toString() == "false")
            ) {
                holder.setVisible(R.id.img_no_code, true)
            }
            holder.setVisible(R.id.cb_select, false)
        }
        val cb_select = holder.getView<CheckBox>(R.id.cb_select)
        if (bean.isSelect == null) {
            cb_select.isChecked = false
        } else {
            cb_select.isChecked = bean.isSelect
        }
        cb_select.setOnCheckedChangeListener { buttonView, isChecked ->
            bean.isSelect = isChecked
            if (isChecked) {
                for (i in 0 until data.size) {
                    if (i != holder.layoutPosition && data[i].isSelect) {
                        val c: CheckBox? = getViewByPosition(i, R.id.cb_select) as? CheckBox
                        c!!.isChecked = !c.isChecked
                        data[i].isSelect = c.isChecked
                    }
                }
            }
        }
    }

    fun isSelectItem(): Boolean {
        for (i in 0 until data.size) {
            if (data[i].isSelect) return true
        }
        return false
    }

    fun setSelectItem(position: Int) {
        val checkBox: CheckBox? = getViewByPosition(position, R.id.cb_select) as? CheckBox
        checkBox!!.isChecked = !checkBox.isChecked
        data[position].isSelect = checkBox.isChecked
        if (checkBox.isChecked) {
            for (i in 0 until data.size) {
                if (i != position && data[i].isSelect) {
                    val c: CheckBox? = getViewByPosition(i, R.id.cb_select) as? CheckBox
                    c!!.isChecked = !c.isChecked
                    data[i].isSelect = c.isChecked
                }
            }
        }
    }

    fun getSelectItem(): String {
        for (i in 0 until data.size) {
            if (data[i].isSelect) {
                return data[i].linkId
            }
        }
        return ""
    }
}