package com.unionware.once.view

import android.text.InputFilter
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.basicui.base.activity.BaseProcessActivity
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.once.app.RouterOncePath
import com.unionware.once.viewmodel.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.app.utils.sound.SoundPoolUtil
import unionware.base.ext.showToast
import unionware.base.model.ViewDisplay
import unionware.base.util.InputFilterMinMax

/**
 * 工单进度
 * Author: sheng
 * Date:2024/9/18
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_GDJD)
class ScheduleActivity : BaseProcessActivity<ScheduleViewModel>() {
    /**
     * 填写资料
     */
    var processAdapter: ProcessAdapter? = null

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            updateJobScheduleLive.observe(this@ScheduleActivity) {
                processAdapter?.clearData()
                processAdapter?.setFocusable(tag = "order")
                "提交成功".showToast()
                SoundPoolUtil.getInstance()
                    .playAudio(this@ScheduleActivity, SoundType.Default.SUBMIT_SUCCESS)
            }
        }
    }

    override fun initView() {
        super.initView()
        setTitle(title)
        binding?.clBody?.visibility = View.GONE
        processAdapter?.addOnEditorActionArray("order") { _, _, _ ->
            processAdapter?.setFocusable(tag = "orderProgress")
        }
        processAdapter?.setFocusable(tag = "order")
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            ViewDisplay("工单号", tag = "order", key = "prdNo", isEdit = true, isRequired = true),
            ViewDisplay(
                "进度(%)",
                tag = "orderProgress",
                key = "jobSchedule",
                isEdit = true,
                isRequired = true,
                isNumber = true
            ).apply {
                inputFilters = mutableListOf(
                    InputFilter.LengthFilter(3),
                    InputFilterMinMax(0, 100)
                ).toTypedArray()
            }
        )
        return items
    }

    override fun onActionSubmitConfirm() {
        processAdapter?.items?.firstOrNull { it.isRequired && it.value.isNullOrEmpty() }?.also {
            if (it.isRequired && it.value.isNullOrEmpty()) {
                if (it.type == 2) {
                    "请选择${it.title}".showToast()
                } else {
                    "${it.title}不允许为空!".showToast()
                }
                return@onActionSubmitConfirm
            }
        }
        mViewModel.updateJobSchedule(HashMap<String, Any>().apply {
            processAdapter?.items?.forEach {
                if (it.key?.isNotEmpty() == true && it.value?.isNotEmpty() == true) {
                    if ("jobSchedule" == it.key) {
                        put(it.key ?: "", it.value?.toInt() ?: 0)
                    } else {
                        put(it.key ?: "", it.value ?: "" as Any)
                    }
                }
            }
        })
    }

    override fun heardProcessAdapter(): ProcessAdapter {
        return super.heardProcessAdapter().apply { processAdapter = this }
    }

    override fun topAdapter(): RecyclerView.Adapter<ViewHolder>? = null
}