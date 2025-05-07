package com.unionware.wms.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.unionware.wms.R;

import unionware.base.model.bean.PrintTemplateBean;

/**
 * @Author : pangming
 * @Time : On 2023/6/7 14:30
 * @Description : PrintTemplateAdapter
 */

public class PrintTemplateAdapter extends BaseQuickAdapter<PrintTemplateBean, BaseViewHolder> {
    public PrintTemplateAdapter(){super(R.layout.item_template);}
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, PrintTemplateBean printTemplateBean) {
        baseViewHolder.setText(R.id.tv_menu_name, printTemplateBean.getTempName());
    }
}
