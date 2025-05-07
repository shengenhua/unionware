package com.unionware.wms.ui.adapter

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import unionware.base.room.table.TransScanInfo

/**
 * @Author : pangming
 * @Time : On 2023/6/6 15:40
 * @Description : TransScanRecordAdapter
 */

class TransScanRecordAdapter :BaseQuickAdapter<TransScanInfo?,BaseViewHolder>(R.layout.item_trans_scan_record){
    var clickListener : OnClickListener?=null
    override fun convert(holder: BaseViewHolder, item: TransScanInfo?) {
        if(item?.id==0L){
            holder.getView<TextView>(R.id.tv_title).visibility = View.VISIBLE
            holder.getView<TextView>(R.id.tv_title).text = "转入箱码信息"

            holder.getView<TextView>(R.id.tv_barcode_type).visibility = View.VISIBLE
            holder.getView<TextView>(R.id.tv_barcode).visibility = View.VISIBLE

            holder.getView<TextView>(R.id.tv_barcode_type).text = "转入箱码"
            holder.getView<TextView>(R.id.tv_barcode).text = item.inBarcode

            holder.getView<View>(R.id.tv_detail_title).visibility = View.GONE
            holder.getView<View>(R.id.tv_details).visibility = View.GONE

            holder.getView<View>(R.id.tv_num_title).visibility = View.GONE
            holder.getView<View>(R.id.tv_num).visibility = View.GONE

            holder.getView<View>(R.id.tv_delete).visibility = View.GONE

            holder.getView<View>(R.id.tv_delete_all).visibility = View.VISIBLE
            holder.getView<View>(R.id.tv_delete_all).setOnClickListener(View.OnClickListener {
                clickListener?.clearAll()
            })
        }else {
            holder.getView<TextView>(R.id.tv_title).text = "转出箱码信息"
            if(TextUtils.isEmpty(item?.outBarcode)){
                holder.getView<TextView>(R.id.tv_barcode_type).visibility = View.GONE
                holder.getView<TextView>(R.id.tv_barcode).visibility = View.GONE
            }else{
                holder.getView<TextView>(R.id.tv_barcode_type).visibility = View.VISIBLE
                holder.getView<TextView>(R.id.tv_barcode).visibility = View.VISIBLE
                holder.getView<TextView>(R.id.tv_barcode_type).text = "转出箱码"
                holder.getView<TextView>(R.id.tv_barcode).text = item?.outBarcode
            }
            if(TextUtils.isEmpty(item?.detailCode)){
                holder.getView<View>(R.id.tv_detail_title).visibility = View.GONE
                holder.getView<View>(R.id.tv_details).visibility = View.GONE

                holder.getView<View>(R.id.tv_num_title).visibility = View.GONE
                holder.getView<View>(R.id.tv_num).visibility = View.GONE

            }else{
                holder.getView<View>(R.id.tv_detail_title).visibility = View.VISIBLE
                holder.getView<View>(R.id.tv_details).visibility = View.VISIBLE
                holder.getView<TextView>(R.id.tv_detail_title).text = "子项条码"
                holder.getView<TextView>(R.id.tv_details).text = item?.detailCode

                holder.getView<View>(R.id.tv_num_title).visibility = View.VISIBLE
                holder.getView<View>(R.id.tv_num).visibility = View.VISIBLE
                holder.getView<TextView>(R.id.tv_num_title).text = "子项条码数量"
                holder.getView<TextView>(R.id.tv_num).text = item?.detailQty
            }

            holder.getView<View>(R.id.tv_delete).visibility = View.VISIBLE
            holder.getView<View>(R.id.tv_delete).setOnClickListener(View.OnClickListener {
                item?.let { it1 -> clickListener?.OnClick(it1, holder.layoutPosition) }
            })
            if(holder.layoutPosition==1){
                holder.getView<View>(R.id.tv_delete_all).visibility = View.GONE
            }else if(holder.layoutPosition>1){
                holder.getView<View>(R.id.tv_delete_all).visibility = View.GONE
                holder.getView<View>(R.id.tv_title).visibility = View.GONE
            }
        }
    }
    interface OnClickListener {
        fun OnClick(bean: TransScanInfo, position: Int)
        fun clearAll()
    }
}