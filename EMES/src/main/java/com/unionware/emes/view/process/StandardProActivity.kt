package com.unionware.emes.view.process

import android.view.View
import com.unionware.emes.view.process.base.ProcessActivity
import com.unionware.emes.viewmodel.process.ProcessViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.model.ViewDisplay


/**
 * 标准作业工序(标准作业)
 */
@AndroidEntryPoint
open class StandardProActivity : ProcessActivity<ProcessViewModel>() {

    override fun initView() {
        super.initView()
        binding?.apply {
            isContinueReport.visibility = View.VISIBLE
            isContinueReport.setOnFocusChangeListener { v, hasFocus ->
                isPdaContinuousReport = hasFocus
            }
        }
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        val items
                : MutableList<ViewDisplay> =
            mutableListOf(
                ViewDisplay("备注", "remark", "remark", null, true)
            )
        return items
    }

    override fun getItems(): List<Map<String, String?>>? {
        return super.getItems()
    }
}