package com.unionware.mes.adapter.dynamic

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.mes.databinding.AdapterBotFunBinding

class BottomFeatureAdapter(override var items: List<AdapterButtonValue> = emptyList()) :
    BaseQuickAdapter<BottomFeatureAdapter.AdapterButtonValue, DataBindingHolder<AdapterBotFunBinding>>() {

    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterBotFunBinding>, position: Int, item: AdapterButtonValue?,
    ) {
        holder.binding.buttonValue = item
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): DataBindingHolder<AdapterBotFunBinding> {
        return DataBindingHolder(
            AdapterBotFunBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    class AdapterButtonValue(
        var id: Int = -1, var text: String? = null,
    )

}