package com.unionware.wms.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import unionware.base.model.bean.EntityBean

class BarcodeInfoContentAdapter(data: MutableList<EntityBean>?) :
    BaseQuickAdapter<EntityBean, BaseViewHolder>(
        R.layout.item_show_info_content,
        data
    ) {
    override fun convert(holder: BaseViewHolder, item: EntityBean) {
        holder.setText(R.id.tv_show_title, item.property.name + ":")
        holder.setText(R.id.tv_show_content, item.value)
    }
}


