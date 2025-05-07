package com.unionware.wms.ui.adapter;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;

import unionware.base.model.bean.CommonListBean;

public class CommonContentAdapter extends BaseQuickAdapter<CommonListBean, BaseViewHolder> {
    public CommonContentAdapter() {
        super(unionware.base.R.layout.item_common_content);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, CommonListBean bean) {
        holder.setText(R.id.tv_show_title, bean.getKey() + ":");
        holder.setText(R.id.tv_show_content, bean.getVal());
        if ("FRowType".equals(bean.getId())) {
            if ("Parent".equals(bean.getVal()) || "套件父项".equals(bean.getVal())) {
                //父项
                holder.setTextColor(R.id.tv_show_title, getResColor(R.color.parent_red));
                holder.setTextColor(R.id.tv_show_content, getResColor(R.color.parent_red));
            } else if ("Son".equals(bean.getVal()) || "套件子项".equals(bean.getVal())) {
                //子项
                holder.setTextColor(R.id.tv_show_title, getResColor(R.color.parent_purple));
                holder.setTextColor(R.id.tv_show_content, getResColor(R.color.parent_purple));
            }
        } else {
            holder.setTextColor(R.id.tv_show_title, getResColor(unionware.base.R.color.black));
            holder.setTextColor(R.id.tv_show_content, getResColor(unionware.base.R.color.black));
        }
    }

    private int getResColor(@ColorRes int color) {
        return getContext().getResources().getColor(color, getContext().getTheme());
    }
}

