package com.unionware.wms.ui.adapter

import android.app.Activity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import unionware.base.model.bean.BillBean
import unionware.base.ui.NoTouchRecyclerView

class BillInfoAdapter(private val type: Int, private val mContext: Activity) : BaseQuickAdapter<BillBean, BaseViewHolder>(R.layout.item_bill_content) {
    override fun convert(holder: BaseViewHolder, bean: BillBean) {
        val rv_list = holder.getView<NoTouchRecyclerView>(R.id.rv_list)
        holder.setGone(R.id.tv_bill_code, type == 2 || type == 0 || null == bean.code)
        holder.setGone(R.id.view_diver, type == 2 || type == 0 || null == bean.code)
        holder.setText(R.id.tv_bill_code, "单据编号：" + bean.code)
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
                billAdapter.setNewInstance(bean.list)
            }
            else -> {
                rv_list.layoutManager = GridLayoutManager(mContext, 3)
                val gridAdapter = GridContentAdapter()
                rv_list.adapter = gridAdapter
                gridAdapter.setNewInstance(bean.list)
            }
        }
    }
}