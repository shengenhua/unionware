package com.unionware.emes.view.process

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.unionware.emes.view.process.base.OptionProcessActivity
import com.unionware.emes.viewmodel.process.ProcessViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.model.ViewDisplay
import unionware.base.model.bean.CollectSelectBean

/**
 * 检查准备 工序 （检查准备）
 */
@AndroidEntryPoint
class ReadyProcessActivity : OptionProcessActivity<ProcessViewModel>() {

    override fun tailLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(mContext)
    }

    override fun initView() {
        super.initView()
        binding?.actvScanSum?.visibility = View.GONE
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        //
        val items: MutableList<ViewDisplay> = mutableListOf(
            ViewDisplay("判定结果", "result", "result").apply {
                type = 2
                value = CollectSelectBean.Y.str
                id = CollectSelectBean.Y.toString()
            },
            ViewDisplay("备注", "remark", "remark", null, true)
        )
        return items
    }

}