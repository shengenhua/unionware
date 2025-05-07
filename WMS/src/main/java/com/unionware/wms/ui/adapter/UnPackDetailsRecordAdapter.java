package com.unionware.wms.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;

import unionware.base.room.table.UnPackScanInfo;

/**
 * @Author : pangming
 * @Time : On 2023/6/13 14:32
 * @Description : UnPackDetailsRecordAdapter
 */

public class UnPackDetailsRecordAdapter extends BaseQuickAdapter<UnPackScanInfo, BaseViewHolder> {
    public UnPackDetailsRecordAdapter() {
        super(R.layout.item_unpack_scan_record);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, UnPackScanInfo unPackScanInfo) {
        holder.setText(R.id.tv_details,unPackScanInfo.getDetailCode());
        holder.setText(R.id.tv_detalis_qty_content,unPackScanInfo.getDetailQty());
    }
}
