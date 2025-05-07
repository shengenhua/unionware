package com.unionware.basicui.main.menu.adapter

import androidx.core.graphics.toColorInt
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.basicui.R
import okhttp3.internal.notifyAll
import unionware.base.model.bean.MenuTypeBean


class MenuAdapter : BaseQuickAdapter<MenuTypeBean, BaseViewHolder>(R.layout.item_menu_title) {
    private var selectedPosition = 0 // 选中的位置
    override fun convert(holder: BaseViewHolder, item: MenuTypeBean) {
        holder.setText(R.id.tv_menu_name, item.name)
        val isSelected = holder.layoutPosition == selectedPosition
        holder.setVisible(R.id.tv_packing_menu_diver, isSelected)
        if (isSelected) {
            holder.setBackgroundColor(R.id.tv_menu_name, "#FFFFFF".toColorInt())
        } else {
            holder.setBackgroundColor(R.id.tv_menu_name, "#F4F4F4".toColorInt())
        }
    }

    fun setSelectedPosition(selectedPosition: Int) {
        /*if (this.selectedPosition == selectedPosition || selectedPosition < 0 || selectedPosition >= itemCount) {
            return
        }
        val oldPosition = this.selectedPosition
        this.selectedPosition = selectedPosition
        notifyItemChanged(oldPosition)
        notifyItemChanged(this.selectedPosition)*/
        this.selectedPosition = selectedPosition
        notifyDataSetChanged()
    }
}