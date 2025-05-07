package com.unionware.emes.view.process

import com.unionware.emes.view.process.base.MultiBarcodeProcessActivity
import com.unionware.emes.viewmodel.process.ProcessViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.ext.showToast
import unionware.base.model.ViewDisplay


/**
 * 采集工序 （数据采集）
 */
@AndroidEntryPoint
open class CollectProcessActivity : MultiBarcodeProcessActivity<ProcessViewModel>() {

    override fun onActionSubmitConfirm() {
        collectAdapter?.items?.withIndex()?.forEach {
            it.value.collects?.forEach { collect ->
                if (collect.value.isNullOrEmpty()) {
                    binding?.rvTail?.layoutManager?.scrollToPosition(it.index)
                    "条码${it.value.code}${collect.colName}不能为空!".showToast()
                    return@onActionSubmitConfirm
                }
            }
        }
        super.onActionSubmitConfirm()
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            //采集作业 去掉激光器
            /*ViewDisplay(
                "激光器", "laserId", "laserId", "BOS_ASSISTANTDATA_SELECT", true, isRequired = true
            ).apply {
                parentId = "666fe12a8a79d2"
                parentName = "parentId"
            }, */
            ViewDisplay("备注", "remark", "remark", null, true)
        )
        return items
    }

}