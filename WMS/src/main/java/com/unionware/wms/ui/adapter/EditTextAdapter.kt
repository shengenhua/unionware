package com.unionware.wms.ui.adapter

import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.wms.databinding.ItemEditTextBinding
import unionware.base.ext.strToInt


class EditTextAdapter :
    com.chad.library.adapter4.BaseQuickAdapter<String, EditTextAdapter.EditViewDataBinding>() {
    open class EditViewDataBinding(binding: ItemEditTextBinding) : DataBindingHolder<ItemEditTextBinding>(binding) {
        var textWatcher: TextWatcher? = null
    }

    var data: MutableList<Int> = mutableListOf()
        get() {
            if (field.size != itemCount) {
                field.clear()
                for (i in 0 until itemCount) {
                    field.add(getItem(i)?.strToInt() ?: 0)
                }
            }
            return field
        }

    override fun onBindViewHolder(holder: EditViewDataBinding, position: Int, item: String?) {
        holder.binding.apply {
//            this.item = item
            etScanInput.apply {
                holder.textWatcher?.let {
                    removeTextChangedListener(it)
                }
                setText(item)
                holder.textWatcher = addTextChangedListener {
                    data[position] = it.toString().strToInt()
                }
            }
        }

    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): EditViewDataBinding {
        return EditViewDataBinding(
            ItemEditTextBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}