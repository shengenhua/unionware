package com.unionware.basicui.main.menu.adapter

import android.graphics.Color
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import unionware.base.R
import unionware.base.app.utils.DrawableConvertUtil
import unionware.base.room.table.Favourite

class FavoriteAdapter : BaseQuickAdapter<Favourite, BaseViewHolder>(R.layout.item_menu_config_grid) {
    override fun convert(helper: BaseViewHolder, item: Favourite) {
        if (item.color?.isNotEmpty() == true) {
            helper.setBackgroundColor(R.id.iv_menu_icon, Color.parseColor(item.color))
        }
        helper.setImageResource(R.id.iv_menu_logo, DrawableConvertUtil.getMenuDrawable(item.icon))
        helper.setText(R.id.tv_menu_title, item.name)
    }
}