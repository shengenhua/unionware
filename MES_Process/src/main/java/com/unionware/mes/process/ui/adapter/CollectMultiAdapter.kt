package com.unionware.mes.process.ui.adapter

import com.chad.library.adapter4.BaseMultiItemAdapter
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.MultiItemType

/**
 * 多菜单适配器
 *  输入
 *  选择
 *  文件上传
 */
class CollectMultiAdapter(multiItems: List<CollectMultiItem> = emptyList()) :
    BaseMultiItemAdapter<CollectMultiItem>(multiItems) {
    init {
        addItemType(MultiItemType.EDIT.type, EditAdapterListener())
        addItemType(MultiItemType.SELECT.type, SelectAdapterListener())
        addItemType(MultiItemType.FILE.type, FileAdapterListener())
        addItemType(MultiItemType.EDITTEXT.type, EditAdapterListener())
    }

    override fun getItemViewType(position: Int, list: List<CollectMultiItem>): Int {
        return list[position].colMethod ?: 0
    }
}