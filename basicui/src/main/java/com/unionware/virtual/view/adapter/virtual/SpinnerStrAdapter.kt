package com.unionware.virtual.view.adapter.virtual

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import unionware.base.databinding.AdapterSpinnerItemBinding
import com.unionware.basicui.view.spinner.SpinnerPopAdapter

/**
 * Author: sheng
 * Date:2024/12/23
 */
class SpinnerStrAdapter(context: Context, items: List<Map<String,String>> = emptyList()) :
    SpinnerPopAdapter<Map<String, String>, AdapterSpinnerItemBinding>(context, items) {
    override fun onBindViewHolder(
        viewBinding: AdapterSpinnerItemBinding,
        position: Int,
        t: Map<String,String>?,
    ) {
        viewBinding.text1.text = t?.get("Name")
    }

    override fun onViewHolder(context: Context, parent: ViewGroup?): AdapterSpinnerItemBinding {
        return AdapterSpinnerItemBinding.inflate(LayoutInflater.from(context), parent, false)
    }
}