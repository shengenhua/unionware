package com.unionware.virtual.view.adapter.virtual

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterVirtualTimeBinding
import com.unionware.lib_base.utils.ext.formatter
import unionware.base.model.bean.PropertyBean
import unionware.base.app.utils.DateFormatUtils
import unionware.base.ui.datepicker.CustomDatePicker

class VirTimeItemListener :
    BaseMultiItemAdapter.OnMultiItem<PropertyBean, DataBindingHolder<AdapterVirtualTimeBinding>>() {

    /**
     * 选项回车事件的回调接口
     */
    var onTimeActionChangeListener: ((time: String?, bean: PropertyBean?, position: Int) -> Unit)? =
        null

    /**
     * 显示窗口 获取焦点
     */
    var onFocusShowListener: (() -> Unit)? = null

    override fun onBind(
        holder: DataBindingHolder<AdapterVirtualTimeBinding>, position: Int, bean: PropertyBean?,
    ) {
        val item = bean?.clone()
        holder.binding.apply {
            this.item = item?.also {
                it.value = it.value?.formatter(it.display ?: "yyyy-MM-dd")
            }
            ivArrows.setOnClickListener {
                if (isFastClick()) {
                    initTimePick(context, item, position)
                }
            }
            tvDataTime.setOnClickListener {
                if (isFastClick()) {
                    initTimePick(context, item, position)
                }
            }
        }
    }

    private fun initTimePick(context: Context?, item: PropertyBean?, position: Int) {
        onFocusShowListener?.invoke()
        //时间选择器
        val beginTimestamp = DateFormatUtils.str2Long("1980-01-01", false)
        val endTimestamp = DateFormatUtils.str2Long("2100-01-01", false)
        val picker = CustomDatePicker(context, { timestamp: Long ->
            val newItem = item?.clone()
            val time = DateFormatUtils.long2Str(timestamp)
            newItem?.value = time
            onTimeActionChangeListener?.invoke(time, newItem, position)

        }, beginTimestamp, endTimestamp)

        when (item?.type) {
            "TIME" -> {//时分秒
                picker.setOnlyShowTime(true)
            }

            "DATE" -> {//日期
                picker.setCanShowPreciseTime(false)
            }

            "DATETIME" -> {//长日期
                picker.setCanShowPreciseTime(true)
            }

            else -> {
                picker.setCanShowPreciseTime(false)
            }
        }
        /*if (("TIME" == item?.type)) {
            picker.setOnlyShowTime(true)
        } else {
            picker.setOnlyShowTime(false)
        }*/
        picker.setCancelable(false)
        picker.setScrollLoop(false)
        picker.setCanShowAnim(false)
        picker.show(System.currentTimeMillis())
    }

    companion object {
        var lastClickTime: Long = 0L
        const val MIN_DELAY_TIME: Int = 200
    }

    private fun isFastClick(): Boolean {
        val currentClickTime: Long = System.currentTimeMillis()
        return if ((lastClickTime - currentClickTime) >= MIN_DELAY_TIME) {
            false
        } else {
            lastClickTime = currentClickTime
            true
        }
    }

    override fun onCreate(
        context: Context, parent: ViewGroup, viewType: Int,
    ): DataBindingHolder<AdapterVirtualTimeBinding> {
        return DataBindingHolder(
            AdapterVirtualTimeBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}