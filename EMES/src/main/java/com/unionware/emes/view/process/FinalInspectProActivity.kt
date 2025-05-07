package com.unionware.emes.view.process

import com.unionware.emes.view.process.base.ProcessActivity
import com.unionware.emes.viewmodel.process.ChecketProcessViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.model.ViewDisplay


/**
 * 终检工序（终检）
 */
@AndroidEntryPoint
open class FinalInspectProActivity : ProcessActivity<ChecketProcessViewModel>() {

    override fun initView() {
        super.initView()
    }

    /*override fun onActionSubmitConfirm() {
        if (barCodeAdapter?.items?.isEmpty() == true) {
            "无提交的条码数据，请检查".showToast()
            return
        }
        mViewModel.submitReport(ReportReq().apply {
            data = mutableListOf(ReportReq.DataReq().apply {
                jobId = this@FinalInspectProActivity.jobId
                taskId = this@FinalInspectProActivity.taskId
                params = HashMap(
                    mapOf(
                        Pair("remark", processAdapter?.getItemValue("remark") as Any),
                        Pair("items", getItems() as Any)
                    )
                )
            })
        })
    }*/

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            ViewDisplay("备注", "remark", "remark", null, true)
        )
        return items
    }

    override fun getItems(): List<Map<String, String?>>? {
        return super.getItems()
    }

}