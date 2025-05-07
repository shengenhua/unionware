package com.unionware.emes.view.process.base

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.chad.library.adapter4.BaseQuickAdapter
import com.unionware.basicui.base.activity.BaseProcessActivity
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.emes.R
import com.unionware.mes.adapter.HeardScanAdapter
import com.unionware.emes.adapter.barcode.QueryBarCodeAdapter
import com.unionware.emes.viewmodel.process.ProcessViewModel
import unionware.base.ext.showToast
import unionware.base.model.bean.barcode.QueryBarCodeBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ReportReq


/**
 * 工序 Activity 列表里面带有基础查询
 */
open class QueryProcessActivity<VM : ProcessViewModel> : BaseProcessActivity<VM>() {
    /**
     * 顶部显示
     */
    protected var topAdapter: ProcessAdapter? = null

    /**
     * 填写资料
     */
    protected var processAdapter: ProcessAdapter? = null

    /**
     * 扫描的条码
     */
    protected var barCodeAdapter: QueryBarCodeAdapter? = null

    /**
     * 扫描框
     */
    protected var scanAdapter: HeardScanAdapter? = null

    override fun initView() {
        super.initView()
        scanAdapter?.setOnEditorActionListener {
            if (it.isEmpty()) {
                "请扫描条码!".showToast()
                return@setOnEditorActionListener
            }
            if (barCodeAdapter?.items?.firstOrNull { item -> item.code == it } != null) {
                "当前条码已扫描,请勿重复扫描".showToast()
                scanAdapter?.notifyItemChanged(0)
                return@setOnEditorActionListener
            }
            mViewModel.queryBarcode(queryFilters(it), scene)
            //
        }
        barCodeAdapter?.addOnItemChildClickListener(R.id.tbDelete) { baseQuickAdapter, view, i ->
            baseQuickAdapter.removeAt(i)
            mViewModel.barcodeItemCountLiveData.value = binding?.rvTail?.adapter?.itemCount ?: 0
        }
        barCodeAdapter?.addOnItemChildClickListener(R.id.tvQuery) { baseQuickAdapter, view, position ->
            itemClick(baseQuickAdapter, position)
        }
    }

    private fun checkAging(
        baseQuickAdapter: BaseQuickAdapter<QueryBarCodeBean, *>, i: Int,
    ): Boolean {
        if (baseQuickAdapter.items[i].id == null) {
            //没有选择
            itemClick(baseQuickAdapter, i)
            return true
        }
        return false
    }

    protected open fun itemClick(
        baseQuickAdapter: BaseQuickAdapter<QueryBarCodeBean, *>, position: Int,
    ) = Unit

    /**
     * 扫描条码 上报的数据
     */
    protected open fun queryFilters(barcode: String): FiltersReq = FiltersReq(
        mapOf(
            Pair("primaryCode", barcode), Pair("taskId", taskId)
        )
    )

    override fun initBaseViewObservable() {
        super.initBaseViewObservable()
        mViewModel.barcodeELiveData.observe(this) {
            scanAdapter?.notifyItemChanged(0)
        }
    }


    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            barcodeObserve()
            submitObserve()
        }
    }

    protected open fun VM.submitObserve() {
        submitLiveData.observe(this@QueryProcessActivity) {
            //提交成功
//            postFinishShowToast("提交成功")
            if (isPdaContinuousReport) {
                //连续上报
                processAdapter?.clearData()
                barCodeAdapter?.submitList(null)
                barcodeItemCountLiveData.value = 0
                barcodeLiveData.value = null
            } else {
                postFinishShowToast("提交成功")
            }
        }
    }

    /**
     * 条码扫描 出来的数据
     */
    protected open fun VM.barcodeObserve() {
        barcodeLiveData.observe(this@QueryProcessActivity) {
            val bean = QueryBarCodeBean(it.code).apply {
                materialId = it.materialId
                materialCode = it.materialCode
                materialName = it.materialName
                materialSpec = it.materialSpec
                qty = it.qty
            }
            barCodeAdapter?.add(0, bean)
            scanAdapter?.notifyItemChanged(0)
            barCodeAdapter?.let { adapter -> checkAging(adapter, adapter.itemCount - 1) }
        }
    }


    override fun onActionSubmitConfirm() {
        if (barCodeAdapter?.items.isNullOrEmpty()) {
            ("无提交的条码数据，请检查！").showToast()
            return
        }
        processAdapter?.items?.forEach {
            if (it.isRequired && it.value.isNullOrEmpty()) {
                "${it.title}不允许为空!".showToast()
                return@onActionSubmitConfirm
            }
        }
        barCodeAdapter?.items?.withIndex()?.forEach {
            if (it.value.id == null) {
                binding?.rvTail?.layoutManager?.scrollToPosition(it.index)
                "条码${it.value.code}没有选择${it.value.queryName}".showToast()
                return@onActionSubmitConfirm
            }
        }
        submitReport()
    }

    /**
     * 提交数据
     */
    protected open fun submitReport() {
        mViewModel.submitReport(ReportReq().apply {
            data = mutableListOf(ReportReq.DataReq().apply {
                jobId = this@QueryProcessActivity.jobId
                taskId = this@QueryProcessActivity.taskId
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
    }

    /**
     * 获取条码列表中的 数据
     */
    protected open fun getItems(): List<Map<String, String?>>? {
        return barCodeAdapter?.items?.map {
            mapOf(Pair("code", it.code), Pair("qty", it.qty))
        }
    }

    override fun heardProcessAdapter(): ProcessAdapter {
        return super.heardProcessAdapter().apply { processAdapter = this }
    }

    override fun middleAdapter(): RecyclerView.Adapter<ViewHolder>? {
        scanAdapter = HeardScanAdapter()
        return scanAdapter
    }

    override fun tailAdapter(): BaseQuickAdapter<*, *> {
        barCodeAdapter = barCodeAdapter ?: QueryBarCodeAdapter()
        return barCodeAdapter as QueryBarCodeAdapter
    }
}