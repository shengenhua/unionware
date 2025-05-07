package com.unionware.once.view.inspect

import android.content.Intent
import com.unionware.basicui.base.activity.BaseDetailsActivity
import com.unionware.once.viewmodel.inspect.InspectDetailsViewModel
import com.unionware.virtual.view.adapter.ButtonAdapter
import unionware.base.model.req.FiltersReq
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.ext.bigDecimalToZeros
/**
 * 巡检 详情
 */
@AndroidEntryPoint
class InspectDetailsActivity : BaseDetailsActivity<InspectDetailsViewModel>() {

    override fun initView() {
        super.initView()
//        heardAdapter.items
        addButton(ButtonAdapter.AdapterButtonValue(0, "汇报")) {
            mViewModel.dataLiveData.value?.also {
                val intent = Intent(
                    this, InspectProActivity::class.java
                ).apply {
                    putExtra("code", it["code"].toString())
                    putExtra("primaryId", id)
                    putExtra("taskId", it["id"]?.bigDecimalToZeros())
                    putExtra("jobId", it["jobId"]?.bigDecimalToZeros())
                    putExtra("materialId", it["materialId"]?.bigDecimalToZeros())
                    putExtra("title", "巡检")
                    putExtra("scene", scene)
                }

                startActivity(intent)
            }
        }
    }

    override fun initData() {
        mViewModel.queryJobInfo(FiltersReq(mapOf(Pair("primaryId", id))), scene)
    }
}