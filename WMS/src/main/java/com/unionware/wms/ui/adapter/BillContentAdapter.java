package com.unionware.wms.ui.adapter;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;

import unionware.base.model.bean.CommonListBean;


public class BillContentAdapter extends BaseQuickAdapter<CommonListBean, BaseViewHolder> {
    public BillContentAdapter() {
        super(R.layout.item_bill_info_content);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, CommonListBean bean) {
        holder.setText(R.id.tv_show_content, bean.getKey() + ":" + bean.getVal());
    }
}

