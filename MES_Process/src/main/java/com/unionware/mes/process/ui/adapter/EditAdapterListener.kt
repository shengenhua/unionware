package com.unionware.mes.process.ui.adapter

import android.content.Context
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.mes.process.databinding.AdptListenerEditBinding
import unionware.base.model.bean.CollectMultiItem

/**
 * Author: sheng
 * Date:2024/12/4
 */
class EditAdapterListener :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<CollectMultiItem, EditAdapterListener.EditItemDataBinding> {

    open class EditItemDataBinding(binding: AdptListenerEditBinding) :
        DataBindingHolder<AdptListenerEditBinding>(binding) {
        var textWatcher: TextWatcher? = null
    }

    override fun onBind(
        holder: EditItemDataBinding,
        position: Int,
        item: CollectMultiItem?,
    ) {
        if (item?.colMethod == 1) {
            item.value = item.stdValue
        }
        holder.binding.item = item
        holder.binding.also { bind ->
            bind.item = item
            bind.ivCheckDelete.setOnClickListener {
                bind.etInput.setText("")
                item?.id = null
            }
            bind.etInput.apply {
                when (item?.colMethod) {
                    1 -> {
                        inputType =
                            EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
                    }

                    4 -> {
                        inputType = InputType.TYPE_CLASS_TEXT
                    }
                }
                holder.textWatcher?.also {
                    removeTextChangedListener(it)
                }
                holder.textWatcher = addTextChangedListener {
                    item?.value = it.toString()
                }
                setOnEditorActionListener { v, actionId, event ->
                    return@setOnEditorActionListener true
                }
            }
        }
    }

    override fun onCreate(
        context: Context, parent: ViewGroup, viewType: Int,
    ): EditItemDataBinding {
        return EditItemDataBinding(
            AdptListenerEditBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}