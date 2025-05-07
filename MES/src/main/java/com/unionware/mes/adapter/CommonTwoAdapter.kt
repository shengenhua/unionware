package com.unionware.mes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.mes.databinding.AdapterCommonBinding
import com.unionware.basicui.base.adapter.BasicDifferAdapter
import unionware.base.model.bean.CommonListBean

class CommonTwoAdapter(
    diffCallback: DiffUtil.ItemCallback<CommonListBean> = object :
        DiffUtil.ItemCallback<CommonListBean>() {
        override fun areItemsTheSame(oldItem: CommonListBean, newItem: CommonListBean): Boolean {
            return oldItem.key == newItem.key && oldItem.`val` == newItem.`val`
        }

        override fun areContentsTheSame(oldItem: CommonListBean, newItem: CommonListBean): Boolean {
            return oldItem.key == newItem.key && oldItem.`val` == newItem.`val`
        }

    },
    items: List<CommonListBean> = emptyList(),
) :
    BasicDifferAdapter<CommonListBean, AdapterCommonBinding>(diffCallback, items) {
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterCommonBinding>, position: Int, item: CommonListBean?
    ) {
        holder.binding.commonItem = item
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int
    ): DataBindingHolder<AdapterCommonBinding> {
        return DataBindingHolder(
            AdapterCommonBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}