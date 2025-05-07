package com.unionware.mes.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.basicui.view.spinner.SpinnerPop
import com.unionware.basicui.view.spinner.SpinnerPopAdapter
import com.unionware.mes.R
import com.unionware.mes.databinding.AdapterButtonBinding
import unionware.base.model.bean.DynamicConfigBean
import kotlinx.coroutines.launch

class JobMenuBtnAdapter(
    val activity: AppCompatActivity,
    item: DynamicConfigBean.JobItem = DynamicConfigBean.JobItem().also { it.name = "子工序" },
) : BaseSingleItemAdapter<DynamicConfigBean.JobItem, DataBindingHolder<AdapterButtonBinding>>(item) {
    var jobs: List<DynamicConfigBean.JobItem> = listOf()
        set(value) {
            if (value.size > 1) {
                item = DynamicConfigBean.JobItem().also { it.name = "子工序" }
            } else if (value.size == 1) {
                item = value[0]
            }
            field = value
        }

    private var jobItemClickListener: JobItemClickListener? = null

    fun setOnJobItemClickListener(listener: JobItemClickListener) {
        jobItemClickListener = listener
    }

    fun setOnJobItemClickListener(listener: (DynamicConfigBean.JobItem) -> Unit) {
        jobItemClickListener = object : JobItemClickListener {
            override fun onClick(jobItem: DynamicConfigBean.JobItem) {
                listener(jobItem)
            }
        }
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterButtonBinding>,
        item: DynamicConfigBean.JobItem?,
    ) {
        //#1e89ef
        item?.let {
            holder.binding.acBtn.backgroundTintList = ColorStateList.valueOf(it.itemColor)
            holder.binding.buttonValue = "${it.name} 汇报"
        }
        holder.binding.acBtn.setOnClickListener {
            if (jobs.size == 1) {
                jobItemClickListener?.onClick(jobs[0])
            } else {
                if (Lifecycle.Event.downTo(activity.lifecycle.currentState) == Lifecycle.Event.ON_DESTROY) {
                    // 销毁了 不然会有内存泄漏
                    return@setOnClickListener
                }
                activity.lifecycleScope.launch {
                    SpinnerPop(activity, it, ColorDrawable(Color.TRANSPARENT)).apply {
                        setBaseAdapter(JobMenuItemBtnAdapter(activity, jobs).apply {
                            this.addOnItemChildClickListener(R.id.acBtn) { _, _, t ->
                                popupWindow.dismiss()
                                t?.let { jobItemClickListener?.onClick(it) }
                            }
                        })
                    }.show()
                }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): DataBindingHolder<AdapterButtonBinding> {
        return DataBindingHolder(
            AdapterButtonBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    interface JobItemClickListener {
        fun onClick(jobItem: DynamicConfigBean.JobItem)
    }

    private class JobMenuItemBtnAdapter(
        context: Context,
        items: List<DynamicConfigBean.JobItem> = emptyList(),
    ) : SpinnerPopAdapter<DynamicConfigBean.JobItem, AdapterButtonBinding>(context, items) {
        override fun onViewHolder(context: Context, parent: ViewGroup?): AdapterButtonBinding {
            return AdapterButtonBinding.inflate(LayoutInflater.from(context), parent, false)
        }

        override fun onBindViewHolder(
            viewBinding: AdapterButtonBinding,
            position: Int,
            item: DynamicConfigBean.JobItem?,
        ) {
            item?.let {
                viewBinding.acBtn.backgroundTintList = ColorStateList.valueOf(it.itemColor)
                viewBinding.buttonValue = "${it.name}"
            }
        }
    }
}