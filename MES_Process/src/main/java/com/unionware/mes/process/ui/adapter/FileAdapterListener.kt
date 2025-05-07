package com.unionware.mes.process.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.mes.process.databinding.AdptListenterFileBinding
import unionware.base.model.bean.CollectMultiItem

/**
 * Author: sheng
 * Date:2024/12/4
 */
class FileAdapterListener :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<CollectMultiItem,  DataBindingHolder<AdptListenterFileBinding>> {

    override fun onBind(
        holder: DataBindingHolder<AdptListenterFileBinding>,
        position: Int,
        item: CollectMultiItem?,
    ) {
        holder.binding.also {
            it.item = item/*item?.uri?.also { uri ->
                it.acivFile.setImageURI(uri)
            }*/
        }
    }

    override fun onCreate(
        context: Context, parent: ViewGroup, viewType: Int,
    ): DataBindingHolder<AdptListenterFileBinding> {
        return DataBindingHolder(
            AdptListenterFileBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}