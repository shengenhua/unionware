package com.unionware.once.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.once.databinding.AdapterOnceFentityListBinding
import com.unionware.once.model.FentityView
import com.unionware.virtual.view.adapter.BaseQueryAdapter


class EntityListAdapter : BaseQueryAdapter<FentityView, AdapterOnceFentityListBinding>() {
    init {
//        setItemAnimation(AnimationType.SlideInRight)
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterOnceFentityListBinding>, position: Int, item: FentityView?
    ) {
        holder.binding.item = item
        holder.binding.rvList.layoutManager = LinearLayoutManager(context)
        holder.binding.rvList.adapter = item?.view?.let { FetityViewAdapter(it) }
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int
    ): DataBindingHolder<AdapterOnceFentityListBinding> {
        return DataBindingHolder(
            AdapterOnceFentityListBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}