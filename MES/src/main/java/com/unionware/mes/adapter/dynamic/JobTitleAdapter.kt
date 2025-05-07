package com.unionware.mes.adapter.dynamic

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.mes.databinding.AdapterJobTitleBinding
import unionware.base.model.ViewDisplay

/**
 * 单据体和子单据体 里面的 扫描框
 */
class JobTitleAdapter(item: ViewDisplay = ViewDisplay("工序", "jobName")) :
    BaseSingleItemAdapter<ViewDisplay, JobTitleAdapter.JobTitleDataBinding>(item) {

    open class JobTitleDataBinding(binding: AdapterJobTitleBinding) :
        DataBindingHolder<AdapterJobTitleBinding>(binding)

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
        holder: JobTitleDataBinding,
        item: ViewDisplay?,
    ) {
        holder.binding.also { bind ->
            item?.also {
                bind.item = it
            }
            bind.clInput.setOnClickListener {
                itemEditorActionListener?.onItemEditor(this@JobTitleAdapter, item?.value ?: "")
            }
            bind.clFeature.setOnClickListener {
                itemEditorActionListener?.onItemEditor(this@JobTitleAdapter, item?.value ?: "")
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

    fun getViewBinding(position: Int): JobTitleDataBinding? {
        val binding = recyclerView.findViewHolderForLayoutPosition(position)
        if (binding == null || binding !is JobTitleDataBinding) {
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
    ): JobTitleDataBinding {
        return JobTitleDataBinding(
            AdapterJobTitleBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}