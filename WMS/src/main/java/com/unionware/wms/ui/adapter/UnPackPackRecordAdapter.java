package com.unionware.wms.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;

import unionware.base.room.table.UnPackScanInfo;

/**
 * @Author : pangming
 * @Time : On 2023/6/13 15:30
 * @Description : UnPackPackRecordAdapter
 */

public class UnPackPackRecordAdapter extends BaseQuickAdapter<UnPackScanInfo, BaseViewHolder> {
    public UnPackPackRecordAdapter() {
        super(R.layout.item_unpack_pack_record);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, UnPackScanInfo unPackScanInfo) {
        holder.setText(R.id.tv_pack, unPackScanInfo.getPackCode());
    }
}
