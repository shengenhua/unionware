package com.unionware.once.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.basicui.base.activity.BaseProcessActivity
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.once.app.RouterOncePath
import com.unionware.once.viewmodel.FaultRecordViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.app.utils.sound.SoundPoolUtil
import unionware.base.ext.showToast
import unionware.base.ext.tryBigDecimalToZeros
import unionware.base.model.ViewDisplay
import unionware.base.model.req.FiltersReq

/**
 * 工单生产故障记录
 * Author: sheng
 * Date:2024/9/18
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_GDSCGZJL)
class FaultRecordActivity : BaseProcessActivity<FaultRecordViewModel>() {
    /**
     * 填写资料
     */
    var processAdapter: ProcessAdapter? = null

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            faultRecordLive.observe(this@FaultRecordActivity) {
                processAdapter?.clearData()
                processAdapter?.setFocusable(tag = "orderNo")
                "提交成功".showToast()
                SoundPoolUtil.getInstance()
                    .playAudio(this@FaultRecordActivity, SoundType.Default.SUBMIT_SUCCESS)
            }
            dataLiveData.observe(this@FaultRecordActivity) {
                processAdapter?.items?.withIndex()?.firstOrNull { it.value.tag == "barcode" }
                    ?.apply {
                        value.id = if (it.isNotEmpty()) it["id"].toString() else ""
                        if (it.isNotEmpty()) {
                            viewLiveData.value?.also {
                                value.infoList = it
                                processAdapter?.notifyItemChanged(index)
                            }
                        } else {
                            value.value = ""
                            value.infoList = null
                            processAdapter?.notifyItemChanged(index)
                        }
                    }
            }
        }
    }

    override fun initView() {
        super.initView()
        setTitle(title)
        binding?.clBody?.visibility = View.GONE
        processAdapter?.addOnEditorActionArray("orderNo") { _, _, text ->
            if (text.isEmpty()) {
                return@addOnEditorActionArray
            }
            mViewModel.query(
                scene,
                "PRD_MO",
                FiltersReq(mutableMapOf(Pair("primaryCode", text as Any)))
            )
        }
        processAdapter?.setFocusable(tag = "orderNo")
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            ViewDisplay(
                "工单号",
                tag = "orderNo",
                key = "orderNo",
                isEdit = true,
                isRequired = true
            ),
            ViewDisplay(
                "故障类别",
                tag = "typeId",
                key = "typeId",
                code = "BOS_ASSISTANTDATA_SELECT",
                isRequired = true
            ).apply {
                parentName = "parentId"
                parentId = "6721f47765d56a"
            },
            ViewDisplay("故障描述", tag = "description", key = "description", isEdit = true),
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