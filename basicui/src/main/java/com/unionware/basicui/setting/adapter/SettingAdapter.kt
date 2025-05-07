package com.unionware.basicui.setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterSettingBinding
import com.unionware.basicui.setting.bean.SettingBean
import unionware.base.route.URouter


/**
 * Author: sheng
 * Date:2025/1/3
 */
class SettingAdapter : BaseQuickAdapter<SettingBean, DataBindingHolder<AdapterSettingBinding>>() {
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterSettingBinding>,
        position: Int,
        item: SettingBean?,
    ) {
        holder.binding.apply {
            item?.also {
                sbItem.setLeftText(it.name)
                if (it.drawable != -1) {
                    if (it.drawable == 0) {
                        sbItem.setRightDrawable(null)
                    } else {
                        sbItem.setRightDrawable(it.drawable)
                    }
                }
                if (it.textColor != -1) {
                    sbItem.setLeftTextColor(it.textColor)
                }
                sbItem.setOnClickListener {
                    holder.itemView.performClick()
                }
                swBtu.setOnCheckedChangeListener { _, isChecked ->
                }
                swBtu.isChecked = it.switch == true
                when (it.type) {
                    4 -> {
                        sbItem.setRightDrawable(null)
                        swBtu.visibility = View.VISIBLE
                    }

                    else -> swBtu.visibility = View.GONE
                }
                swBtu.setOnCheckedChangeListener { _, isChecked ->
                    it.switch = isChecked
                }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): DataBindingHolder<AdapterSettingBinding> {
        return DataBindingHolder(
            AdapterSettingBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}