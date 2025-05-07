package com.unionware.wms.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;
import com.unionware.wms.ui.adapter.ScanAdapter;
import unionware.base.model.bean.EntityBean;

/**
 * @Author : pangming
 * @Time : On 2023/6/7 15:38
 * @Description : TransAdapter
 */

public class TransAdapter extends ScanAdapter {
    @Override
    protected void convert(@NonNull BaseViewHolder holder, @Nullable EntityBean bean) {
        super.convert(holder, bean);
        if("label".equals(bean.getKey())||"in".equals(bean.getKey())||"out".equals(bean.getKey())||"details".equals(bean.getKey())){
            holder.getView(R.id.tv_scan_default).setVisibility(View.INVISIBLE);
            holder.getView(R.id.tv_scan_lock).setVisibility(View.INVISIBLE);
        }
        if(!"label".equals(bean.getKey())){
            holder.getView(R.id.iv_base_info_query).setVisibility(View.GONE);
        }else {
            holder.getView(R.id.iv_base_info_query).setVisibility(View.VISIBLE);
        }
    }
}
