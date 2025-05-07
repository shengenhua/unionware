package com.unionware.wms.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;
import com.unionware.wms.model.bean.BarcodeDetailsBean;
import com.unionware.wms.model.bean.ProgressInfoBean;

public class InProgressAdapter extends BaseQuickAdapter<ProgressInfoBean, BaseViewHolder> {

    public InProgressAdapter() {
        super(R.layout.item_in_progress_content);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, ProgressInfoBean bean) {
        holder.setText(R.id.tv_progress_content, null != bean.getBoxCode() ? bean.getBoxCode() : "--"); // 包装条码
        holder.setText(R.id.tv_progress_time, bean.getDate());
        holder.setText(R.id.tv_progress_capacity, "箱容量：" + bean.getCapacity());
        holder.setText(R.id.tv_progress_num, "已装件数：" + bean.getCount());
    }
}
