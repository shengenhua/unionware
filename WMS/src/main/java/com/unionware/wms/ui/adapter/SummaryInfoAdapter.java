package com.unionware.wms.ui.adapter;


import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;
import com.unionware.wms.ui.adapter.SummaryContentAdapter;
import unionware.base.model.bean.BillBean;

import java.util.List;

public class SummaryInfoAdapter extends BaseQuickAdapter<BillBean, BaseViewHolder> {
    private Activity mContext;
    private SummaryContentAdapter adapter;


    public SummaryInfoAdapter(Activity mContext, @Nullable List<BillBean> data) {
        super(R.layout.item_common_list, data);
        this.mContext = mContext;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BillBean bean) {
        RecyclerView rv_list = holder.findView(R.id.rv_list);
//        holder.setGone(R.id.tv_bill_code, true);
//        holder.setGone(R.id.view_diver, true);
        if (rv_list == null) return;
        rv_list.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new SummaryContentAdapter();
        rv_list.setAdapter(adapter);
        adapter.setNewInstance(bean.getList());

    }


    public SummaryContentAdapter getAdapter() {
        return adapter;
    }


}

