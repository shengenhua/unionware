package com.unionware.wms.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;
import com.unionware.wms.model.bean.BarcodeDetailsBean;

/**
 * 装箱明细详情适配器
 */
public class PackingRecordAdapter extends BaseQuickAdapter<BarcodeDetailsBean, BaseViewHolder> {
    public PackingRecordAdapter() {
        super(R.layout.item_rerord_packing_info);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BarcodeDetailsBean data) {
        holder.setText(R.id.tv_packing_content, data.getBoxCode());
        holder.setText(R.id.tv_packing_capacity_content, data.getCapacity());
        holder.setText(R.id.tv_packing_num_content, data.getCount());
        holder.setText(R.id.tv_packing_sup_content, null != data.getSupName() ? data.getSupName() : "");
    }
}
