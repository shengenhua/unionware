package com.unionware.once.adapter.dynamic

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.once.databinding.OnceAdapterJobTitleBinding
import unionware.base.model.ViewDisplay

/**
 * 单据体和子单据体 里面的 扫描框
 */
class JobTitleAdapter(item: ViewDisplay = ViewDisplay("工序", "jobName")) :
    BaseSingleItemAdapter<ViewDisplay, JobTitleAdapter.OnceAdapterJobTitleDataBinding>(item) {

    open class OnceAdapterJobTitleDataBinding(binding: OnceAdapterJobTitleBinding) :
        DataBindingHolder<OnceAdapterJobTitleBinding>(binding)

    var openState: Boolean = true
        set(value) {
            stateUpdate(value)
            field = value
        }

    private var itemEditorActionListener: OnItemEditorActionListener? = null

    fun onEditorActionArray(listener: OnItemEditorActionListener) = apply {
        itemEditorActionListener = listener
    }

    override fun onBindViewHolder(
        holder: OnceAdapterJobTitleDataBinding,
        item: ViewDisplay?,
    ) {
        holder.binding.also { bind ->
            item?.also {
                bind.item = it
            }
            bind.clInput.setOnClickListener {
                if (bind.ivArrowDown.isVisible) {
                    itemEditorActionListener?.onItemEditor(this@JobTitleAdapter, item?.value ?: "")
                }
            }
            bind.clFeature.setOnClickListener {
                if (bind.ivArrowDown.isVisible) {
                    itemEditorActionListener?.onItemEditor(this@JobTitleAdapter, item?.value ?: "")
                }
            }
            if (openState) {
                bind.ivArrowDown.animate().rotation(-180f)
            } else {
                bind.ivArrowDown.animate().rotation(0f)
            }
        }
    }

    private fun stateUpdate(state: Boolean) {
        getViewBinding(0)?.also {
            if (state) {
                it.binding.ivArrowDown.animate().rotation(-180f)
            } else {
                it.binding.ivArrowDown.animate().rotation(0f)
            }
        }
    }

    fun getViewBinding(position: Int): OnceAdapterJobTitleDataBinding? {
        val binding = recyclerView.findViewHolderForLayoutPosition(position)
        if (binding == null || binding !is OnceAdapterJobTitleDataBinding) {
            return null
        }
        return binding
    }

    fun interface OnItemEditorActionListener {
        fun onItemEditor(adapter: BaseSingleItemAdapter<ViewDisplay, *>, text: String)
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): OnceAdapterJobTitleDataBinding {
        return OnceAdapterJobTitleDataBinding(
            OnceAdapterJobTitleBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}