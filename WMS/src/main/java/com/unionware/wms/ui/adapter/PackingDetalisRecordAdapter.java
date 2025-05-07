package com.unionware.wms.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import com.unionware.wms.R;
import com.unionware.wms.model.bean.BarcodeDetailsBean;

/**
 * 装箱明细详情适配器
 */
public class PackingDetalisRecordAdapter extends BaseQuickAdapter<BarcodeDetailsBean, BaseViewHolder> {
    public PackingDetalisRecordAdapter() {
        super(R.layout.item_rerord_detalis_info);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BarcodeDetailsBean data) {
        holder.setText(R.id.tv_detalis_content, data.getBarCode());
        holder.setText(R.id.tv_detalis_qty_content, String.valueOf(data.getQty()));
        holder.setText(R.id.tv_detalis_md_name_content, data.getMaterialName());
        holder.setText(R.id.tv_detalis_flot_content, null != data.getLot() ? data.getLot() : "");
    }
}
