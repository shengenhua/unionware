package com.unionware.query.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.query.adapter.QueryTopAdapter.QueryTopDataBinding
import com.unionware.query.databinding.AdapterQueryHeardBtnBinding
import com.unionware.query.databinding.AdapterQueryTopBinding
import com.unionware.virtual.view.adapter.virtual.VirEditItemListener.EditItemDataBinding

/**
 * Author: sheng
 * Date:2025/3/6
 */
class QueryHeadBtnAdapter :
    BaseSingleItemAdapter<String, QueryHeadBtnAdapter.QueryHeadBtnDataBinding>() {

    open class QueryHeadBtnDataBinding(binding: AdapterQueryHeardBtnBinding) :
        DataBindingHolder<AdapterQueryHeardBtnBinding>(binding)

    private var onItemClickListener: OnItemClickListener? = null

    fun onClickActionListener(listener: OnItemClickListener) = apply {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(
        holder: QueryHeadBtnDataBinding,
        item: String?,
    ) {
        holder.binding.btnQuery.setOnClickListener {
            onItemClickListener?.onClick(this)
        }
    }

    fun getViewBinding(position: Int): QueryHeadBtnDataBinding? {
        val binding = recyclerView.findViewHolderForLayoutPosition(position)
        if (binding == null || binding !is QueryHeadBtnDataBinding) {
            return null
        }
        return binding
    }

    fun interface OnItemClickListener {
        fun onClick(adapter: BaseSingleItemAdapter<String, *>)
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): QueryHeadBtnDataBinding {
        return QueryHeadBtnDataBinding(
            AdapterQueryHeardBtnBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}