package com.unionware.wms.ui.adapter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;
import com.unionware.wms.ui.adapter.BarcodeDetailsAdapter;
import unionware.base.model.bean.EntityBean;

import java.util.List;

public class BarcodeListAdapter extends BaseQuickAdapter<List<EntityBean>, BaseViewHolder>{
    private Activity mContext;
    private BarcodeDetailsAdapter adapter;

    public BarcodeListAdapter(Activity mContext, @Nullable List<List<EntityBean>> data) {
        super(R.layout.item_edit_content, data);
        this.mContext = mContext;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, List<EntityBean> list) {
        RecyclerView rv_list = holder.getView(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new BarcodeDetailsAdapter(list);
        rv_list.setAdapter(adapter);
    }

    public void notifyItem(int pos) {
        adapter.notifyItemChanged(pos);
    }

}
