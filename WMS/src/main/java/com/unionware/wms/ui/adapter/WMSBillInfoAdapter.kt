package com.unionware.wms.ui.adapter

import android.app.Activity
import android.widget.CheckBox
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import unionware.base.model.bean.BillBean
import unionware.base.ui.NoTouchRecyclerView

/**
 * @Author : pangming
 * @Time : On 2024/7/9 18:19
 * @Description : WMSBillInfoAdapter
 * 目前只用type = 1的情况
 */

class WMSBillInfoAdapter(private val type: Int, private val mContext: Activity) :
    BaseQuickAdapter<BillBean, BaseViewHolder>(
        R.layout.item_wms_bill_content
    ) {
    var onItemCheckedListener: OnItemCheckedListener? = null
    var isSelect: Boolean = false
    override fun convert(holder: BaseViewHolder, bean: BillBean) {
        val rv_list = holder.getView<NoTouchRecyclerView>(R.id.rv_list)
        if (bean.list != null && bean.list.size > 0) {
            //第一个显示加粗
            holder.setText(R.id.tv_bill_code, bean.list.get(0).key + ":" + bean.list.get(0).`val`)
        }

        when (type) {
            0 -> {
                holder.setVisible(R.id.tv_content_index, true)
                rv_list.layoutManager = LinearLayoutManager(mContext)
                val adapter = CommonContentAdapter()
                rv_list.adapter = adapter
                adapter.setNewInstance(bean.list)
                holder.setText(R.id.tv_content_index, (holder.layoutPosition + 1).toString())
            }

            1 -> {
                rv_list.layoutManager = LinearLayoutManager(mContext)
                val billAdapter =
                    BillContentAdapter()
                rv_list.adapter = billAdapter
                //排除第一个不显示
                if (bean.list != null && bean.list.size > 1) {
                    billAdapter.setNewInstance(bean.list.subList(1, bean.list.size))
                } else if (bean.list != null && bean.list.size == 1) {
                    billAdapter.setNewInstance(mutableListOf())
                } else {
                    billAdapter.setNewInstance(bean.list)
                }

            }

            else -> {
                rv_list.layoutManager = GridLayoutManager(mContext, 3)
                val gridAdapter = GridContentAdapter()
                rv_list.adapter = gridAdapter
                gridAdapter.setNewInstance(bean.list)
            }
        }
        if (isSelect) {
            holder.setVisible(R.id.cb_select, true)
        } else {
            holder.setVisible(R.id.cb_select, false)
        }
        val cb_select = holder.getView<CheckBox>(R.id.cb_select)
        cb_select.setOnCheckedChangeListener(null)
        if (bean.isSelect == null) {
            cb_select.isChecked = false
        } else {
            cb_select.isChecked = bean.isSelect
            onItemCheckedListener?.setItemChecked(bean, bean.isSelect)
        }
        cb_select.setOnCheckedChangeListener { buttonView, isChecked ->
            bean.isSelect = isChecked
            onItemCheckedListener?.setItemChecked(bean, bean.isSelect)
        }
    }

    fun interface OnItemCheckedListener {
        fun setItemChecked(bean: BillBean, isSelect: Boolean)
    }
}