package com.unionware.query.adapter

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.BaseDifferAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterVirBillListBinding
import com.unionware.virtual.view.adapter.CommonAdapter
import unionware.base.model.bean.BillBean


class QueryListAdapter(
    diffCallback: DiffUtil.ItemCallback<BillBean> = object :
        DiffUtil.ItemCallback<BillBean>() {
        override fun areItemsTheSame(oldItem: BillBean, newItem: BillBean): Boolean {
            return oldItem.dataMap?.get("id") == newItem.dataMap?.get("id")
        }

        override fun areContentsTheSame(oldItem: BillBean, newItem: BillBean): Boolean {
            oldItem.dataMap.forEach { (k, v) ->
                if (!newItem.dataMap.containsKey(k) || newItem.dataMap[k] != v) {
                    return false
                }
            }
            return true
        }

    },
) : BaseDifferAdapter<BillBean, DataBindingHolder<AdapterVirBillListBinding>>(diffCallback) {
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterVirBillListBinding>, position: Int, item: BillBean?,
    ) {
        holder.binding.apply {
            billData = item
            tvBillCodeName.visibility = View.GONE
            tvBillCode.visibility = View.GONE
            viewDiver.visibility = View.GONE
        }
        item?.list?.also {
            holder.binding.rvList.layoutManager =
                if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    GridLayoutManager(context, 2).apply {
                        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return if (it.size % 2 == 1 && position == it.size - 1) {
                                    2
                                } else {
                                    1
                                }
                            }
                        }
                    }
                } else {
                    LinearLayoutManager(context)
                }
            holder.binding.rvList.adapter = CommonAdapter(items = it)
        }
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): DataBindingHolder<AdapterVirBillListBinding> {
        return DataBindingHolder(
            AdapterVirBillListBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}