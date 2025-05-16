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
            holder.binding.rbThemeName.apply {
                MMKV.mmkvWithID("app")
                    .decodeInt("themeText", R.style.Default_TextSize_Medium).also {
                        isChecked = it == item.themeStyle
                    }
                text = themeName
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