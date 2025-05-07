package com.unionware.once.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.basicui.base.activity.BaseProcessActivity
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.once.adapter.CheckAdapter
import com.unionware.once.app.RouterOncePath
import com.unionware.once.viewmodel.BadnessJudgeViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.app.utils.sound.SoundPoolUtil
import unionware.base.ext.showToast
import unionware.base.ext.tryBigDecimalToZeros
import unionware.base.model.SelectBean
import unionware.base.model.ViewDisplay
import unionware.base.model.req.FiltersReq

/**
 * 不良检修判断
 * Author: sheng
 * Date:2024/9/18
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_BLJXPD)
class BadnessJudgeActivity : BaseProcessActivity<BadnessJudgeViewModel>() {
    /**
     * 填写资料
     */
    var processAdapter: ProcessAdapter? = null

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            repairedJudgeLive.observe(this@BadnessJudgeActivity) {
                processAdapter?.clearData()
                processAdapter?.setFocusable(tag = "barCode")
                checkAdapter?.items?.withIndex()?.forEach {
                    it.value.isCheck = false
                    checkAdapter?.notifyItemChanged(it.index)
                }
                "提交成功".showToast()
                SoundPoolUtil.getInstance()
                    .playAudio(this@BadnessJudgeActivity, SoundType.Default.SUBMIT_SUCCESS)
            }
            repairedDataLiveData.observe(this@BadnessJudgeActivity) { data ->
                if (data.isNullOrEmpty()) {
                    processAdapter?.clearData("barCode")
                    processAdapter?.setFocusable(tag = "barCode")
                } else {
                    processAdapter?.items?.withIndex()?.firstOrNull { it.value.tag == "barCode" }
                        ?.apply {
                            value.id = data["id"].toString()
                            "扫描成功".showToast()
                        }
                }
            }
        }
    }

    override fun initView() {
        super.initView()
        setTitle(title)
        binding?.clBody?.visibility = View.GONE
        processAdapter?.addOnEditorActionArray("barCode") { _, _, text ->
            if (text.isEmpty()) {
                return@addOnEditorActionArray
            }
            mViewModel.repairedJudgeQuery(
                scene,
                "67209FE30343FB",
                FiltersReq(mutableMapOf(Pair("barCode", text as Any)))
            )
        }
        processAdapter?.setFocusable(tag = "barCode")
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            ViewDisplay("产品SN", tag = "barCode", key = "id", isEdit = true, isRequired = true)
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
        mViewModel.repairedJudge(HashMap<String, Any>().apply {
            processAdapter?.items?.firstOrNull { it.tag == "barCode" }?.apply {
                put("id", this.id.tryBigDecimalToZeros() as Any)
                put("barCode", this.value.tryBigDecimalToZeros() as Any)
            }
            put(
                "workbenchRepair",
                checkAdapter?.items?.firstOrNull { it.bean == "workbenchRepair" }?.isCheck
                    ?: false
            )
            /*processAdapter?.items?.forEach { it ->
                if (it.key?.isNotEmpty() == true) {
                    if (it.id?.isNotEmpty() == true) {
                        put(it.key ?: "", it.id.tryBigDecimalToZeros() as Any)
                    } else {
                        put(it.key ?: "", it.value ?: "" as Any)
                    }
                }
                put(
                    "workbenchRepair",
                    checkAdapter?.items?.firstOrNull { it.bean == "workbenchRepair" }?.isCheck
                        ?: false
                )
            }*/
        })
    }

    override fun heardProcessAdapter(): ProcessAdapter {
        return super.heardProcessAdapter().apply { processAdapter = this }
    }

    override fun topAdapter(): RecyclerView.Adapter<ViewHolder>? = null

    /**
     * 填写资料
     */
    private var checkAdapter: CheckAdapter<String>? = null
    override fun topAfterAdapter(): RecyclerView.Adapter<ViewHolder> {
        checkAdapter = checkAdapter ?: CheckAdapter<String>().apply {
            setOnItemClickListener { adapter, _, position ->
                adapter.items[position].also {
                    it.isCheck = !it.isCheck
                }
                adapter.notifyItemChanged(position)
            }
        }
        checkAdapter?.submitList(mutableListOf(SelectBean<String>().apply {
            conntent = "维修工作台返修"
            isCheck = false
            bean = "workbenchRepair"
        }))
        return checkAdapter as CheckAdapter<*>
    }
}