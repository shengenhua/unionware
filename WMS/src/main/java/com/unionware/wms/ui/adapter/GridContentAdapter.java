package com.unionware.wms.ui.adapter;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;

import unionware.base.model.bean.CommonListBean;

public class GridContentAdapter extends BaseQuickAdapter<CommonListBean, BaseViewHolder> {
    public GridContentAdapter() {
        super(R.layout.item_grid_info_content);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, CommonListBean bean) {
        holder.setText(R.id.tv_grid_title, bean.getKey());
        holder.setText(R.id.tv_grid_content, bean.getVal());
    }
}

