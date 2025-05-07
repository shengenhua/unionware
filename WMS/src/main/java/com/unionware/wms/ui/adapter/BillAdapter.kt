package com.unionware.wms.ui.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import unionware.base.model.bean.BillBean

class BillAdapter(private val mContext: Context) :
    BaseQuickAdapter<BillBean, BaseViewHolder>(R.layout.adapter_bill) {
    override fun convert(holder: BaseViewHolder, item: BillBean) {
        holder.setText(R.id.tv_bill_code, "条码：" + item.code)
        val adapter = CommonContentAdapter()
        val rvList = holder.getView<RecyclerView>(R.id.rv_list)

        rvList.layoutManager = LinearLayoutManager(mContext)
        rvList.adapter = adapter
        adapter.setNewInstance(item.list)
    }

    fun getPosByCode(key: String): Int {
        return data.withIndex().indexOfFirst {
           it.value.barcode == key
        }
    }
}