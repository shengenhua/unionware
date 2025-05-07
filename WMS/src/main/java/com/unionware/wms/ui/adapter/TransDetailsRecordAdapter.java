package com.unionware.wms.ui.adapter;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;

import unionware.base.room.table.TransScanInfo;

/**
 * @Author : pangming
 * @Time : On 2023/6/13 17:52
 * @Description : TransDetailsRecordAdapter
 */

public class TransDetailsRecordAdapter extends BaseQuickAdapter<TransScanInfo, BaseViewHolder> {
    public TransDetailsRecordAdapter() {
        super(R.layout.item_trans_details_record);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, TransScanInfo transScanInfo) {

        //有转出箱码
        if (!TextUtils.isEmpty(transScanInfo.getOutBarcode())) {
            holder.setText(R.id.tv_out_barcode_content, transScanInfo.getOutBarcode());
            //有子项条码
            if (!TextUtils.isEmpty(transScanInfo.getDetailCode())) {
                holder.setText(R.id.tv_details_content, transScanInfo.getDetailCode());
                holder.setText(R.id.tv_detalis_qty_content, transScanInfo.getDetailQty());
            } else {
                //没有子项条码
                holder.getView(R.id.tv_detalis_title).setVisibility(View.GONE);
                holder.getView(R.id.tv_details_content).setVisibility(View.GONE);
                holder.getView(R.id.tv_detalis_qty_title).setVisibility(View.GONE);
                holder.getView(R.id.tv_detalis_qty_content).setVisibility(View.GONE);
            }

        } else {
            //没有转出箱
            holder.getView(R.id.tv_out_barcode_title).setVisibility(View.GONE);
            holder.getView(R.id.tv_out_barcode_content).setVisibility(View.GONE);
            //有子项
            holder.setText(R.id.tv_details_content, transScanInfo.getDetailCode());
            holder.setText(R.id.tv_detalis_qty_content, transScanInfo.getDetailQty());
        }
    }
}
