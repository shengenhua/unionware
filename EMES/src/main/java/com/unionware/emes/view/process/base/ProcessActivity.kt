package com.unionware.emes.view.process.base

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.chad.library.adapter4.BaseQuickAdapter
import com.lxj.xpopup.XPopup
import com.unionware.basicui.base.activity.BaseProcessActivity
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.emes.R
import com.unionware.mes.adapter.HeardScanAdapter
import com.unionware.emes.adapter.barcode.BarCodeAdapter
import com.unionware.emes.view.dialog.SpecialRatifyPop
import com.unionware.emes.viewmodel.process.ProcessViewModel
import unionware.base.ext.showToast
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ReportReq


/**
 * 工序 Activity
 */
open class ProcessActivity<VM : ProcessViewModel> : BaseProcessActivity<VM>() {
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
    protected var barCodeAdapter: BarCodeAdapter? = null

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
        }
        barCodeAdapter?.addOnItemChildClickListener(R.id.tbDelete) { baseQuickAdapter, view, i ->
            baseQuickAdapter.removeAt(i)
            mViewModel.barcodeItemCountLiveData.value = binding?.rvTail?.adapter?.itemCount ?: 0
            removeBarCode(i)
        }
    }

    override fun initBaseViewObservable() {
        super.initBaseViewObservable()
        mViewModel.barcodeELiveData.observe(this) {
            scanAdapter?.notifyItemChanged(0)
        }
    }

    /**
     * 删除条码 可以做的操作
     */
    protected open fun removeBarCode(position: Int) = Unit

    /**
     * 扫描条码 上报的数据
     */
    protected open fun queryFilters(barcode: String): FiltersReq = FiltersReq(
        mapOf(
            Pair("primaryCode", barcode),
            Pair("taskId", taskId)
        )
    )


    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            submitObserve()
            barcodeObserve()
        }
    }

    private fun VM.submitObserve() {
        submitLiveData.observe(this@ProcessActivity) {
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

    protected open fun VM.barcodeObserve() {
        barcodeLiveData.observe(this@ProcessActivity) {
            barCodeAdapter?.add(0, it)
            scanAdapter?.notifyItemChanged(0)
        }
    }


    override fun onActionSubmitConfirm() {
        if (barCodeAdapter?.items.isNullOrEmpty()) {
            ("无提交的条码数据，请检查！").showToast()
            return
        }
        processAdapter?.items?.forEach {
            if (it.isRequired && it.value.isNullOrEmpty()) {
                if (it.type == 2) {
                    "请选择${it.title}".showToast()
                } else {
                    "${it.title}不允许为空!".showToast()
                }
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
                jobId = this@ProcessActivity.jobId
                taskId = this@ProcessActivity.taskId
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
        barCodeAdapter = barCodeAdapter ?: BarCodeAdapter()
        return barCodeAdapter as BarCodeAdapter
    }


    protected open var specialRatifyPop: SpecialRatifyPop? = null

    protected open fun isSpecialPopCancel(): Boolean = true

    protected open fun getNewSpecialRatifyPop(title: String?): SpecialRatifyPop =
        SpecialRatifyPop(this, title, isSpecialPopCancel()).also {
            it.confirmListener = { operator, code ->
                if (operator.isNullOrEmpty()) {
                    "请选择特批人!".showToast()
                } else if (code.isNullOrEmpty()) {
                    "请填写授权码!".showToast()
                } else {
                    onActionSubmitConfirm(operator, code)
                }
            }
            it.adapterItemListener = { tag, code ->
                queryBasic(0, code!!) { _, infoBean ->
                    infoBean?.apply {
                        it.updateAdapter(tag!!, this)
                    }
                }
            }
        }

    /**
     * 特批人 对话框
     */
    protected open fun onSpecialSubmit(title: String?) {
        specialRatifyPop?.dismiss()
        specialRatifyPop = getNewSpecialRatifyPop(title)
        XPopup.Builder(mContext).maxWidth(2000)
            .dismissOnBackPressed(false)
            .dismissOnTouchOutside(false).asCustom(specialRatifyPop).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        specialRatifyPop?.dismiss()
    }
}