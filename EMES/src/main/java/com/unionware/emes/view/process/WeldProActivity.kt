package com.unionware.emes.view.process

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unionware.emes.R
import com.unionware.emes.view.process.base.ProcessActivity
import com.unionware.emes.viewmodel.process.ProcessViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.ext.showToast
import unionware.base.model.ViewDisplay


/**
 * 焊接工序(焊接)
 */
@AndroidEntryPoint
open class WeldProActivity : ProcessActivity<ProcessViewModel>() {

    /*
        override fun onActionSubmitConfirm() {
            if (barCodeAdapter?.items?.isEmpty() == true) {
                "无提交的条码数据，请检查".showToast()
                return
            }
            mViewModel.submitReport(ReportReq().apply {
                data = mutableListOf(ReportReq.DataReq().apply {
                    jobId = this@WeldProActivity.jobId
                    taskId = this@WeldProActivity.taskId
                    params = HashMap<String, Any>().apply {
                        putAll(mapOf(Pair("items", getItems() as Any)))
                        processAdapter?.items?.forEach {
                            if (it.key?.isNotEmpty() == true && it.value?.isNotEmpty() == true) {
                                put(it.key ?: "", it.id ?: it.value as Any)
                            }
                        }
                    }
                })
            })
        }*/
    override fun ProcessViewModel.barcodeObserve() {
        barcodeLiveData.observe(this@WeldProActivity) {
            it?.apply {
                barCodeAdapter?.add(0, it)
                scanAdapter?.notifyItemChanged(0)
            }
        }
    }

    override fun getItems(): List<Map<String, String?>>? {
        return barCodeAdapter?.items?.map { it ->
            mutableMapOf(Pair("code", it.code), Pair("qty", it.qty ?: "0")).apply {
            }
        }
    }

    override fun initView() {
        super.initView()
        barCodeAdapter?.addOnItemChildClickListener(R.id.tbDelete) { baseQuickAdapter, view, i ->
            baseQuickAdapter.removeAt(i)
            mViewModel.barcodeItemCountLiveData.value = binding?.rvTail?.adapter?.itemCount ?: 0
        }
    }

    override fun submitReport() {
        var haveTemp = false
        processAdapter?.items?.filter { it.tag == "temperature" || it.tag == "bfhTemperature" }
            ?.forEach {
                if (!it.value.isNullOrEmpty()) {
                    haveTemp = true
                }
            }
        if (haveTemp) {
            super.submitReport()
        } else {
            "请填写 烙铁温度(°C) 或 波峰焊温度(°C)".showToast()
        }
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            //激光器编码查询（辅助资料）ParentId=666fe12a8a79d2
            ViewDisplay(
                "激光器", "laserId", "laserId", "BOS_ASSISTANTDATA_SELECT", true
            ).apply {
                parentId = "666fe12a8a79d2"
                parentName = "parentId"
            }, ViewDisplay(
                "烙铁温度(°C)",
                "temperature",
                "temperature",
                isEdit = true,
//                isRequired = true,
                isNumber = true
            ), ViewDisplay(
                "波峰焊温度(°C)",
                "bfhTemperature",
                "bfhTemperature",
                isEdit = true,
                isNumber = true
            ), ViewDisplay(
                "设备功率(W)", "power", "power", isEdit = true, isNumber = true
            ), ViewDisplay(
                "焊接速度(mm/s)",
                "speed",
                "speed",
                isEdit = true,
                isNumber = true
            ), ViewDisplay(
                "脉冲频率(HZ)", "pulse", "pulse", isEdit = true, isNumber = true
            ), ViewDisplay("备注", "remark", "remark", isEdit = true)
        )
        return items
    }


    override fun heardLayoutManager(): RecyclerView.LayoutManager {
        return if (isLandscape()) {
            GridLayoutManager(this, 2)
        } else {
            super.heardLayoutManager()
        }
    }
}