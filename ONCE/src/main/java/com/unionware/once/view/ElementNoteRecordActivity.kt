package com.unionware.once.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.basicui.base.activity.BaseProcessActivity
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.once.app.RouterOncePath
import com.unionware.once.viewmodel.ElementNoteRecordViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.app.utils.sound.SoundPoolUtil
import unionware.base.ext.showToast
import unionware.base.ext.tryBigDecimalToZeros
import unionware.base.model.ViewDisplay
import unionware.base.util.InputFilterDecimals


/**
 * 磷钙生产记录单汇报
 * Author: sheng
 * Date:2025/2/19
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_LGSC)
class ElementNoteRecordActivity : BaseProcessActivity<ElementNoteRecordViewModel>() {
    /**
     * 填写资料
     */
    var processAdapter: ProcessAdapter? = null

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            elementNoteRecordLive.observe(this@ElementNoteRecordActivity) {
                processAdapter?.clearData()
                processAdapter?.setFocusable(tag = "workShopId")
                "提交成功".showToast()
                SoundPoolUtil.getInstance()
                    .playAudio(this@ElementNoteRecordActivity, SoundType.Default.SUBMIT_SUCCESS)
            }
        }
    }

    override fun initView() {
        super.initView()
        setTitle(title)
        binding?.clBody?.visibility = View.GONE
        processAdapter?.setFocusable(tag = "workShopId")
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            ViewDisplay(
                "生产车间",
                tag = "workShopId",
                key = "workShopId",
                code = "BD_Department",
                isRequired = true
            ), ViewDisplay(
                "产品编码",
                tag = "materialId",
                key = "materialId",
                code = "BD_MATERIAL",
                isRequired = true
            ), ViewDisplay(
                "班组", tag = "termId", key = "termId", code = "PRD_ShiftGroup", isRequired = true
            ), ViewDisplay(
                "班次",
                tag = "classesId",
                key = "classesId",
                code = "ENG_SHIFTSLICE",
                isRequired = true
            ), ViewDisplay(
                "磷钙批号", tag = "lotCode", key = "lotCode", isEdit = true, isRequired = true
            ), ViewDisplay(
                "乏酸量",
                tag = "value1",
                key = "value1",
                isEdit = true,
                isRequired = true,
                isNumber = true
            ).apply {
                inputFilters = mutableListOf(
                    InputFilterDecimals(2)
                ).toTypedArray()
            }, ViewDisplay(
                "合成批数",
                tag = "value2",
                key = "value2",
                isEdit = true,
                isRequired = true,
                isNumber = true
            ).apply {
                inputFilters = mutableListOf(
                    InputFilterDecimals(2)
                ).toTypedArray()
            }, ViewDisplay(
                "班组产量",
                tag = "value3",
                key = "value3",
                isEdit = true,
                isRequired = true,
                isNumber = true
            ).apply {
                inputFilters = mutableListOf(
                    InputFilterDecimals(2)
                ).toTypedArray()
            }, ViewDisplay(
                "磷钙渣子量",
                tag = "value4",
                key = "value4",
                isEdit = true,
                isRequired = true,
                isNumber = true
            ).apply {
                inputFilters = mutableListOf(
                    InputFilterDecimals(2)
                ).toTypedArray()
            }, ViewDisplay(
                "骨末子量",
                tag = "value5",
                key = "value5",
                isEdit = true,
                isRequired = true,
                isNumber = true
            ).apply {
                inputFilters = mutableListOf(
                    InputFilterDecimals(2)
                ).toTypedArray()
            }, ViewDisplay(
                "氧化钙用量",
                tag = "value6",
                key = "value6",
                isEdit = true,
                isRequired = true,
                isNumber = true
            ).apply {
                inputFilters = mutableListOf(
                    InputFilterDecimals(2)
                ).toTypedArray()
            }, ViewDisplay(
                "乏酸浓度",
                tag = "value7",
                key = "value7",
                isEdit = true,
                isRequired = true,
                isNumber = true
            ).apply {
                inputFilters = mutableListOf(
                    InputFilterDecimals(2)
                ).toTypedArray()
            }, ViewDisplay(
                "乏灰浓度",
                tag = "value8",
                key = "value8",
                isEdit = true,
                isRequired = true,
                isNumber = true
            ).apply {
                inputFilters = mutableListOf(
                    InputFilterDecimals(2)
                ).toTypedArray()
            }, ViewDisplay(
                "备注", tag = "remark", key = "remark", isEdit = true
            )
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
        mViewModel.faultRecord(HashMap<String, Any>().apply {
            processAdapter?.items?.forEach { it ->
                if (it.key?.isNotEmpty() == true) {
                    if (it.id?.isNotEmpty() == true || it.key == "id") {
                        put(it.key ?: "", it.id.tryBigDecimalToZeros() as Any)
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