package com.unionware.wms.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;

import unionware.base.model.bean.CommonListBean;

public class SummaryContentAdapter extends BaseQuickAdapter<CommonListBean, BaseViewHolder> {
    private boolean expand;


    public SummaryContentAdapter() {
        super(unionware.base.R.layout.item_common_content);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, CommonListBean bean) {
        holder.setText(R.id.tv_show_title, bean.getKey() + ":");
        holder.setText(R.id.tv_show_content, bean.getVal());
    }

    @Override
    public int getItemCount() {
        return expand ? super.getItemCount() : 2;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }
}

