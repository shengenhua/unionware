package com.unionware.wms.ui.adapter

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import unionware.base.room.table.UnPackScanInfo

/**
 * @Author : pangming
 * @Time : On 2023/6/1 15:37
 * @Description : UnPackScanAdapter
 */

class UnPackScanRecordAdapter :
    BaseQuickAdapter<UnPackScanInfo?, BaseViewHolder>(R.layout.item_unpack_scan_record) {
    var clickListener :OnClickListener?=null
    var isSubItem :Boolean = false;
    override fun convert(holder: BaseViewHolder, item: UnPackScanInfo?) {
        if(item?.id==0L){
            holder.getView<TextView>(R.id.tv_title).text = "包装条码信息"
            holder.getView<TextView>(R.id.tv_barcode_type).text = "包装条码"
            holder.getView<TextView>(R.id.tv_barcode).text = item.packCode
            holder.getView<View>(R.id.tv_num_title).visibility = View.GONE
            holder.getView<View>(R.id.tv_num).visibility = View.GONE
            holder.getView<View>(R.id.tv_delete).visibility = View.GONE
            holder.getView<View>(R.id.tv_delete_all).visibility = View.VISIBLE
            holder.getView<View>(R.id.tv_delete_all).setOnClickListener(View.OnClickListener {
                clickListener?.OnClick(item, holder.layoutPosition)
            })
        }else{
            holder.getView<TextView>(R.id.tv_title).text = "子项条码信息"
            holder.getView<TextView>(R.id.tv_barcode_type).text = "子项条码"
            holder.getView<TextView>(R.id.tv_barcode).text = item?.detailCode
            holder.getView<TextView>(R.id.tv_num_title).visibility = View.VISIBLE
            holder.getView<TextView>(R.id.tv_num).visibility = View.VISIBLE
            holder.getView<TextView>(R.id.tv_num_title).text = "子项条码数量"
            holder.getView<TextView>(R.id.tv_num).text = item?.detailQty

            holder.getView<View>(R.id.tv_delete).visibility = View.VISIBLE
            holder.getView<View>(R.id.tv_delete_all).visibility = View.GONE
            holder.getView<View>(R.id.tv_delete).setOnClickListener(View.OnClickListener {
                if (item != null) {
                    clickListener?.OnClick(item, holder.layoutPosition)
                }
            })
            if(isSubItem && holder.layoutPosition==0){
                holder.getView<View>(R.id.tv_delete_all).visibility = View.VISIBLE
                holder.getView<View>(R.id.tv_delete_all).setOnClickListener(View.OnClickListener {
                    if (item != null) {
                        clickListener?.clearAllByDetails()
                    }
                })
            }
        }
    }
    interface OnClickListener {
        fun OnClick(bean: UnPackScanInfo, position: Int)
        fun clearAllByDetails()
    }
}