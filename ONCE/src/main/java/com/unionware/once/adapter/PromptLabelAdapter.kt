package com.unionware.once.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.once.R
import com.unionware.once.databinding.AdapterOnceLabelBinding
import com.unionware.once.databinding.AdapterOnceLabelTextBinding

class PromptLabelAdapter :
    BaseSingleItemAdapter<List<String>, DataBindingHolder<AdapterOnceLabelBinding>>() {

    var onLabelItemClickListener: ((position: Int, text: String) -> Unit)? = null

    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterOnceLabelBinding>,
        item: List<String>?,
    ) {
//        holder.binding.content = content
        holder.binding.apply {
            rvLabel.adapter = item?.let {
                LabelAdapter(it).apply {
                    setOnItemClickListener { adapter, view, position ->
                        if (this.select == adapter.getItem(position)) {
                            return@setOnItemClickListener
                        }
                        this.select = adapter.getItem(position)
                        items.withIndex().forEach {
                            notifyItemChanged(it.index)
                        }
                        onLabelItemClickListener?.invoke(position, select ?: "")
                    }
                    this.select = this.items[0]
                    onLabelItemClickListener?.invoke(0, select ?: "")
                }
            }
            rvLabel.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            viewDiver.visibility = if (item.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
    }


    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): DataBindingHolder<AdapterOnceLabelBinding> {
        return DataBindingHolder(
            AdapterOnceLabelBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }


    class LabelAdapter(var list: List<String> = emptyList(), var select: String? = null) :
        BaseQuickAdapter<String, DataBindingHolder<AdapterOnceLabelTextBinding>>(list) {

        override fun onBindViewHolder(
            holder: DataBindingHolder<AdapterOnceLabelTextBinding>,
            position: Int,
            item: String?,
        ) {
            holder.binding.item = item
            holder.binding.apply {
                if (select.equals(item)) {
                    tvTitle.setBackgroundResource(R.drawable.item_label)
                    tvTitle.setTextColor(
                        context.resources.getColor(
                            unionware.base.R.color.white,
                            context.resources.newTheme()
                        )
                    )
                } else {
                    tvTitle.setBackgroundResource(android.R.color.transparent)
                    tvTitle.setTextColor(
                        context.resources.getColor(
                            unionware.base.R.color.black,
                            context.resources.newTheme()
                        )
                    )
                }
            }
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int,
        ): DataBindingHolder<AdapterOnceLabelTextBinding> {
            return DataBindingHolder(
                AdapterOnceLabelTextBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )
        }

    }

}