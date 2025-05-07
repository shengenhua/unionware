package com.unionware.basicui.main.menu.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.basicui.R
import unionware.base.model.bean.MenuTypeBean
import unionware.base.app.utils.DrawableConvertUtil
import androidx.core.graphics.toColorInt
import unionware.base.ext.getColorPrimary
import unionware.base.util.ThemeUtil.Companion.getColorPrimary


class MenuGridAdapter :
    BaseQuickAdapter<MenuTypeBean, BaseViewHolder>(R.layout.item_menu_config_grid) {
    override fun convert(holder: BaseViewHolder, item: MenuTypeBean) {
        holder.getView<ImageView>(R.id.iv_menu_icon).also {
            if (item.color.isNotEmpty()) {
                it.backgroundTintList = ColorStateList.valueOf(item.color.toColorInt())
            } else {
                it.backgroundTintList = ColorStateList.valueOf(context.theme.getColorPrimary())
            }
        }
        /*if (item.color.isNotEmpty()) {
            holder.setBackgroundColor(R.id.iv_menu_icon, Color.parseColor(item.color))
        } else {
            holder.setBackgroundColor(
                R.id.iv_menu_icon,
                context.resources.getColor(unionware.base.R.color.mesAppColor, context.theme)
            )
        }*/
        holder.setImageResource(R.id.iv_menu_logo, DrawableConvertUtil.getMenuDrawable(item.icon))
        holder.setText(R.id.tv_menu_title, item.name)
    }
}