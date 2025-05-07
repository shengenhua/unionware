package com.unionware.emes.adapter.process

import com.chad.library.adapter4.BaseMultiItemAdapter
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.MultiItemType

/**
 * 多菜单适配器
 *  输入
 *  选择
 *  文件上传
 */
class ProcessMultiAdapter(multiItems: List<CollectMultiItem> = emptyList()) :
    BaseMultiItemAdapter<CollectMultiItem>(multiItems) {
    init {
        addItemType(MultiItemType.EDIT.type, EditItemAdapterListener())
        addItemType(MultiItemType.SELECT.type, SelectItemAdapterListener())
        addItemType(MultiItemType.FILE.type, FileItemAdapterListener())
        addItemType(MultiItemType.EDITTEXT.type, EditItemAdapterListener())

//        setItemAnimation(AnimationType.SlideInRight)
    }

    override fun getItemViewType(position: Int, list: List<CollectMultiItem>): Int {
        return list[position].colMethod ?: 0
    }
}