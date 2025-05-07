package com.unionware.once.adapter.dynamic

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.once.databinding.OnceAdapterBotFunBinding

class BottomFeatureAdapter(override var items: List<AdapterButtonValue> = emptyList()) :
    BaseQuickAdapter<BottomFeatureAdapter.AdapterButtonValue, DataBindingHolder<OnceAdapterBotFunBinding>>() {

    override fun onBindViewHolder(
        holder: DataBindingHolder<OnceAdapterBotFunBinding>, position: Int, item: AdapterButtonValue?,
    ) {
        holder.binding.buttonValue = item
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): DataBindingHolder<OnceAdapterBotFunBinding> {
        return DataBindingHolder(
            OnceAdapterBotFunBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    class AdapterButtonValue(
        var id: Int = -1, var text: String? = null,
    )

}