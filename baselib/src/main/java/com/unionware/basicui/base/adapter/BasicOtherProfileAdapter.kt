package com.unionware.basicui.base.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterBasicOtherBinding
import unionware.base.api.util.ConvertUtils

class BasicOtherProfileAdapter(
    diffCallback: DiffUtil.ItemCallback<Map<String, String>> = object :
        DiffUtil.ItemCallback<Map<String, String>>() {
        override fun areItemsTheSame(
            oldItem: Map<String, String>,
            newItem: Map<String, String>
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Map<String, String>,
            newItem: Map<String, String>
        ): Boolean {
            return newItem == oldItem
        }

    }
) : BasicDifferAdapter<Map<String, String>, AdapterBasicOtherBinding>(diffCallback) {

    var view: List<unionware.base.model.bean.ViewBean>? = null

    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterBasicOtherBinding>,
        position: Int,
        item: Map<String, String>?
    ) {
//        holder.binding.item = item
        val list = ConvertUtils.convertMapToList(view, item)
        val sb = StringBuilder()
        list.forEach {
            sb.append("${it.key}:${it.`val`}")
            sb.append("\n")
        }
        holder.binding.tvBaseInfoContent.text = sb.toString()
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int
    ): DataBindingHolder<AdapterBasicOtherBinding> {
        return DataBindingHolder(
            AdapterBasicOtherBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}