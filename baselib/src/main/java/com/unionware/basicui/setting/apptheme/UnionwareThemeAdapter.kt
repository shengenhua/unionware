package com.unionware.basicui.setting.apptheme

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterAppThemeBinding
import unionware.base.ext.getColorPrimary
import unionware.base.model.local.UnionwareTheme

/**
 * Author: sheng
 * Date:2025/4/10
 */
class UnionwareThemeAdapter :
    BaseQuickAdapter<UnionwareTheme, DataBindingHolder<AdapterAppThemeBinding>>() {
    @SuppressLint("MissingPermission", "Recycle")
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterAppThemeBinding>,
        position: Int,
        item: UnionwareTheme?,
    ) {
        item?.apply {
            /*holder.binding.cvTheme.setCardBackgroundColor(
                context.getColorPrimary(themeStyle)
            )*/
            holder.binding.cvTheme.setCardBackgroundColor(
                themeColor ?: context.theme.getColorPrimary()
            )
//            if(themeStyle != -1){
            /*context.obtainStyledAttributes(themeStyle, intArrayOf()).apply {
                this.getColor(androidx.appcompat.R.styleable.AppCompatTheme_colorPrimary, context.theme.getColorPrimary()).also {
                    holder.binding.cvTheme.setCardBackgroundColor(it)
                }
            }*/
//            }
            holder.binding.tvThemeName.text = themeName
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): DataBindingHolder<AdapterAppThemeBinding> {
        return DataBindingHolder(
            AdapterAppThemeBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}