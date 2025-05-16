package com.unionware.basicui.setting.apptheme

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.tencent.mmkv.MMKV
import unionware.base.R
import unionware.base.databinding.AdapterAppTextThemeBinding
import unionware.base.model.local.UnionwareTextTheme

/**
 * Author: sheng
 * Date:2025/4/10
 */
class UnionwareTextThemeAdapter :
    BaseQuickAdapter<UnionwareTextTheme, DataBindingHolder<AdapterAppTextThemeBinding>>() {
    @SuppressLint("MissingPermission", "Recycle")
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterAppTextThemeBinding>,
        position: Int,
        item: UnionwareTextTheme?,
    ) {
        item?.apply {
            holder.binding.apply {
                tvShowName.text = themeName
                context.obtainStyledAttributes(item.themeStyle, arrayOf(R.attr.font10).toIntArray())
                    .apply {
                        tvShowName.textSize = this.getDimension(0, 10f)
                    }
                MMKV.mmkvWithID("app")
                    .decodeInt("themeText", R.style.Default_TextSize_Medium).also {
                        rbThemeName.isChecked = it == item.themeStyle
                    }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): DataBindingHolder<AdapterAppTextThemeBinding> {
        return DataBindingHolder(
            AdapterAppTextThemeBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}