package com.unionware.virtual.view.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterVirButtonBinding

class ButtonAdapter(override var items: List<AdapterButtonValue> = emptyList()) :
    BaseQuickAdapter<ButtonAdapter.AdapterButtonValue, DataBindingHolder<AdapterVirButtonBinding>>() {
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterVirButtonBinding>, position: Int, item: AdapterButtonValue?
    ) {
        item?.apply {
            holder.binding.acBtn.backgroundTintList = ColorStateList.valueOf(color)
            holder.binding.buttonValue = text
        }
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int
    ): DataBindingHolder<AdapterVirButtonBinding> {
        return DataBindingHolder(
            AdapterVirButtonBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    class AdapterButtonValue(
        var id: Int = -1, var text: String? = null, var color: Int =  0xFF164DE5.toInt(),
    )

    fun interface OnButtonClickListener {
        fun onClick(id: Int)
    }
}