package com.unionware.basicui.setting.apptheme

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.tencent.mmkv.MMKV
import com.unionware.basicui.setting.apptheme.TextStyleAdapter.TextStyle
import unionware.base.R
import unionware.base.databinding.AdapterTextSytleBinding

/**
 * Author: sheng
 * Date:2025/4/10
 */
class TextStyleAdapter :
    BaseQuickAdapter<TextStyle, DataBindingHolder<AdapterTextSytleBinding>>() {

    class TextStyle(
        var attrId: Int,
        var textName: String,
    )

    @SuppressLint("MissingPermission", "Recycle")
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterTextSytleBinding>,
        position: Int,
        item: TextStyle?,
    ) {
        item?.apply {
            holder.binding.tvTextShow.apply {
                text = textName
                MMKV.mmkvWithID("app")
                    .decodeInt("themeText", R.style.Default_TextSize_Medium).apply {
                        context.obtainStyledAttributes(this, arrayOf(attrId).toIntArray())
                            .apply {
                                textSize = this.getDimension(0, 10f)
                            }
                    }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): DataBindingHolder<AdapterTextSytleBinding> {
        return DataBindingHolder(
            AdapterTextSytleBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}