package com.unionware.wms.ui.adapter;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;

import unionware.base.model.bean.BaseInfoBean;

public class BaseInfoAdapter extends BaseQuickAdapter<BaseInfoBean, BaseViewHolder> {
    public BaseInfoAdapter() {
        super(R.layout.item_base_info_content);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BaseInfoBean bean) {
        String val = null == bean.getName() ?  bean.getCode():bean.getName() + "\n" + bean.getCode();
        holder.setText(R.id.tv_base_info_content, val);

    }
}

