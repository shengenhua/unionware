package com.unionware.emes.view.process

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.emes.R
import com.unionware.emes.view.process.base.ProcessActivity
import com.unionware.emes.viewmodel.process.CastingProViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.model.ViewDisplay
import unionware.base.model.req.ReportReq


/**
 * 浇封固化工序(浇封固化)
 */
@AndroidEntryPoint
open class CastingCuringProActivity : ProcessActivity<CastingProViewModel>() {

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            ratifyLiveData.observe(this@CastingCuringProActivity) {
                //特批人校验成功
                it?.also {
                    specialRatifyPop?.dismiss()
                    if (twoSponsorsLiveData.value.isNullOrEmpty()) {
                        twoSponsorsLiveData.value = mutableListOf()
                    }
                    twoSponsorsLiveData.value?.apply {
                        this.add(newSponsorsLiveData.value ?: "TIMEUPDATEPASSTIVE")
                    }
                    mViewModel.queryBarcode(queryFilters(scanAdapter?.getBarCode().let {
                        if (it.isNullOrEmpty()) {
                            barcodeELiveData.value ?: ""
                        } else {
                            it
                        }
                    }), scene)
                }
            }
            quadraticLiveData.observe(this@CastingCuringProActivity) { it ->
                processAdapter?.apply {
                    processViewLiveData.value?.putAll(items.associate {
                        Pair(it.tag, it.value)
                    })
                }
                if (it) {
                    topAdapter?.changedItemValue(
                        "jobName",
                        "${this@CastingCuringProActivity.title}(时长上报)"
                    )
                    processAdapter?.submitList(heardItems2())
                    barCodeAdapter?.submitList(twoBarLiveData.value)
                    barcodeItemCountLiveData.value = twoBarLiveData.value?.size ?: 0
                } else {
                    topAdapter?.changedItemValue("jobName", this@CastingCuringProActivity.title)
                    processAdapter?.submitList(heardItems())
                    barCodeAdapter?.submitList(oneBarLiveData.value)
                    barcodeItemCountLiveData.value = oneBarLiveData.value?.size ?: 0
                }
            }
            timeUpdatedLiveData.observe(this@CastingCuringProActivity) {
            }
            failureLiveData.observe(this@CastingCuringProActivity) {
                if (it?.data == "TIMEUPDATEPASSTIVE") {//TIMEUPDATEPASSTIVE
                    onSpecialSubmit(it.errorMsg)
                }
            }
        }
    }

    override fun CastingProViewModel.barcodeObserve() {
        barcodeLiveData.observe(this@CastingCuringProActivity) {
            barCodeAdapter?.add(0, it)
            if (quadraticLiveData.value == true) {
                twoBarLiveData.value = barCodeAdapter?.items
            } else {
                oneBarLiveData.value = barCodeAdapter?.items
            }
            scanAdapter?.notifyItemChanged(0)
        }
    }

    override fun removeBarCode(position: Int) {
        super.removeBarCode(position)
        mViewModel.apply {
            if (quadraticLiveData.value == true) {
                twoBarLiveData.value = barCodeAdapter?.items
            } else {
                oneBarLiveData.value = barCodeAdapter?.items
            }
        }
    }

    override fun onBindLayout(): Int {
        return unionware.base.R.layout.activity_process
    }

    override fun initView() {
        super.initView()
        binding?.fabSwitch?.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                mViewModel.quadraticLiveData.value = !mViewModel.quadraticLiveData.value!!
            }
        }
    }

    /*override fun queryFilters(barcode: String): FiltersReq {
        return FiltersReq(HashMap<String?, String?>().apply {
            putAll(mapOf(Pair("primaryCode", barcode), Pair("taskId", taskId)))
        })
    }*/

    override fun onActionSubmitConfirm(operator: String, code: String) {
        mViewModel.ratifyChecked(
            mapOf(
                Pair("ratifyId", operator),
                Pair("ratifyPassword", code),
                Pair("taskId", taskId)
            )
        )
    }

    override fun getItems(): List<MutableMap<String, String>>? {
        return barCodeAdapter?.items?.map {
            return@map HashMap<String, String>().apply {
                putAll(mapOf(Pair("code", it.code), Pair("qty", it.qty ?: "0")))
                if (!it.startTime.isNullOrEmpty()) {
                    put("start_time", it.startTime!!)
                }
                if (!it.endTime.isNullOrEmpty()) {
                    put("end_time", it.endTime!!)
                }
            }
        }
    }


    override fun submitReport() {
        if (mViewModel.quadraticLiveData.value == true) {
            timeUpdated()
        } else {
            mViewModel.submitReport(getReportReq())
        }
    }

    private fun timeUpdated() {
        mViewModel.timeUpdated(getReportReq(mViewModel.ratifyLiveData.value))
    }

    private fun getReportReq(ratifyId: String? = null): ReportReq {
        return ReportReq().apply {
            data = mutableListOf(ReportReq.DataReq().apply {
                jobId = this@CastingCuringProActivity.jobId
                taskId = this@CastingCuringProActivity.taskId
                params = HashMap<String, Any>().apply {
                    putAll(mapOf(Pair("items", getItems() as Any)))
                    ratifyId?.apply {
                        put("ratifyId", this)
                    }
                    processAdapter?.items?.forEach {
                        if (it.key?.isNotEmpty() == true) {
                            if (it.id?.isNotEmpty() == true) {
                                put(it.key ?: "", it.id ?: "" as Any)
                            } else if (it.value?.isNotEmpty() == true) {
                                put(it.key ?: "", it.value ?: "" as Any)
                            }
                        }
                    }
                }
            })
        }
    }

    /**
     * 二次汇报
     */
    private fun heardItems2(): MutableList<ViewDisplay> {
        return mutableListOf()
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            ViewDisplay(
            "温度(°C)",
            "temperature",
            "temperature",
            null,
            true,
            isRequired = true,
            isNumber = true
        ).apply {
            mViewModel.processViewLiveData.value?.get(tag)?.apply {
                value = this
            }
        }, ViewDisplay(
            "湿度(%RH)", "humidity", "humidity", null, true, isRequired = true, isNumber = true
        ).apply {
            mViewModel.processViewLiveData.value?.get(tag)?.apply {
                value = this
            }
        }, ViewDisplay(
            "供应商", "SupplierId", "SupplierId", "BOS_ASSISTANTDATA_SELECT"
        ).apply {
            mViewModel.processViewLiveData.value?.get(tag)?.apply {
                value = this
            }
            parentName = "parentId"
            parentId = "6721f97f65d56e"
        }, ViewDisplay(
            "胶配比", "GlueRatio", "GlueRatio", isEdit = true, isRequired = true
        ).apply {
            mViewModel.processViewLiveData.value?.get(tag)?.apply {
                value = this
            }
        }, ViewDisplay("备注", "remark", "remark", null, true).apply {
            mViewModel.processViewLiveData.value?.get(tag)?.apply {
                value = this
            }
        })
        return items
    }

    override fun topAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>? {
        topAdapter = ProcessAdapter().also {
            it.submitList(
                mutableListOf(
                    ViewDisplay("工序", "jobName").apply {
                        value = this@CastingCuringProActivity.title
                    },
                )
            )
        }
        return topAdapter
    }

    override fun heardLayoutManager(): RecyclerView.LayoutManager {
        return if (isLandscape()) {
            GridLayoutManager(this, 2)
        } else {
            super.heardLayoutManager()
        }
    }
}