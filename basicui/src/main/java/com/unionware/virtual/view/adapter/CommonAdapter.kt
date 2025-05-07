package com.unionware.virtual.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter4.BaseDifferAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterVirCommonBinding
import unionware.base.model.bean.CommonListBean

class CommonAdapter(
    items: List<CommonListBean> = emptyList(),
    diffCallback: DiffUtil.ItemCallback<CommonListBean> = object :
        DiffUtil.ItemCallback<CommonListBean>() {
        override fun areItemsTheSame(oldItem: CommonListBean, newItem: CommonListBean): Boolean {
            return oldItem.key == newItem.key && oldItem.`val` == newItem.`val`
        }

        override fun areContentsTheSame(oldItem: CommonListBean, newItem: CommonListBean): Boolean {
            return oldItem.key == newItem.key && oldItem.`val` == newItem.`val`
        }

    }
) :
    BaseDifferAdapter<CommonListBean, DataBindingHolder<AdapterVirCommonBinding>>(
        diffCallback,
        items
    ) {
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterVirCommonBinding>, position: Int, item: CommonListBean?
    ) {
        holder.binding.commonItem = item
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int
    ): DataBindingHolder<AdapterVirCommonBinding> {
        return DataBindingHolder(
            AdapterVirCommonBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}