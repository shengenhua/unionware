package com.unionware.wms.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;

import unionware.base.room.table.TransScanInfo;

/**
 * @Author : pangming
 * @Time : On 2023/6/13 18:00
 * @Description : TransPackRecordAdapter
 */

public class TransPackRecordAdapter extends BaseQuickAdapter<TransScanInfo, BaseViewHolder> {
    public TransPackRecordAdapter() {
        super(R.layout.item_trans_pack_record);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, TransScanInfo transScanInfo) {
        holder.setText(R.id.tv_in_barcode,transScanInfo.getInBarcode());
    }
}
