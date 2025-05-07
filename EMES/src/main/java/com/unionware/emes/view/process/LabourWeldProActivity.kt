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
 * 焊接工序(焊接)(人工焊)
 */
@AndroidEntryPoint
open class LabourWeldProActivity : ProcessActivity<ProcessViewModel>() {
    override fun ProcessViewModel.barcodeObserve() {
        barcodeLiveData.observe(this@LabourWeldProActivity) {
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
            ViewDisplay(
                "烙铁温度(°C)",
                "temperature",
                "temperature",
                isEdit = true,
                isNumber = true
            ),
            ViewDisplay(
                "波峰焊温度(°C)",
                "bfhTemperature",
                "bfhTemperature",
                isEdit = true,
                isNumber = true
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