package com.unionware.emes.adapter.process

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.emes.databinding.AdapterMultiFileItemBinding
import unionware.base.model.bean.CollectMultiItem

class FileItemAdapterListener :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<CollectMultiItem, DataBindingHolder<AdapterMultiFileItemBinding>> {
    override fun onBind(
        holder: DataBindingHolder<AdapterMultiFileItemBinding>,
        position: Int,
        item: CollectMultiItem?
    ) {
        holder.binding.also {
            it.item = item/*item?.uri?.also { uri ->
                it.acivFile.setImageURI(uri)
            }*/
        }
    }

    override fun onCreate(
        context: Context, parent: ViewGroup, viewType: Int
    ): DataBindingHolder<AdapterMultiFileItemBinding> {
        return DataBindingHolder(
            AdapterMultiFileItemBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}