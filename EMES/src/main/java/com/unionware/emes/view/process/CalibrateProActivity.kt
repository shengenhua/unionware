package com.unionware.emes.view.process

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.chad.library.adapter4.BaseQuickAdapter
import com.lxj.xpopup.XPopup
import com.unionware.emes.R
import com.unionware.emes.adapter.barcode.CalibrateBarCodeAdapter
import com.unionware.emes.viewmodel.process.ProcessViewModel
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.mes.adapter.HeardScanAdapter
import unionware.base.model.ViewDisplay
import unionware.base.model.req.ReportReq
import com.unionware.basicui.base.activity.BaseProcessActivity
import unionware.base.ext.showToast
import unionware.base.model.req.FiltersReq
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.model.bean.barcode.CalibrateBarCodeBean

/**
 * 校准工序（校准）
 */
@AndroidEntryPoint
open class CalibrateProActivity : BaseProcessActivity<ProcessViewModel>() {
    /**
     * 顶部显示
     */
    private var topAdapter: ProcessAdapter? = null

    /**
     * 填写资料
     */
    private var processAdapter: ProcessAdapter? = null

    /**
     * 扫描的条码
     */
    private var barCodeAdapter: CalibrateBarCodeAdapter? = null

    /**
     * 扫描框
     */
    private var scanAdapter: HeardScanAdapter? = null

    override fun initViewObservable() {
        mViewModel.apply {
            barcodeLiveData.observe(this@CalibrateProActivity) {
                val bean = CalibrateBarCodeBean(it.code).apply {
                    materialId = it.materialId
                    materialCode = it.materialCode
                    materialName = it.materialName
                    materialSpec = it.materialSpec
                    qty = it.qty
                }

                barCodeAdapter?.add(0, bean)
                scanAdapter?.notifyItemChanged(0)
                barCodeAdapter?.let { adapter -> checkMA(adapter, 0) }
            }
            submitLiveData.observe(this@CalibrateProActivity) {
                if (isPdaContinuousReport) {
                    //连续上报
                    barCodeAdapter?.submitList(null)
                    barcodeItemCountLiveData.value = 0
                    processAdapter?.clearData()
                } else {
                    postFinishShowToast("提交成功")
                }
            }
        }
    }

    override fun initView() {
        super.initView()
        scanAdapter?.setOnEditorActionListener {
            if (barCodeAdapter?.items?.firstOrNull { item -> item.code == it } != null) {
                "当前条码已扫描,请勿重复扫描".showToast()
                scanAdapter?.notifyItemChanged(0)
                return@setOnEditorActionListener
            }
            //调用接口查询扫描的是否正确
            mViewModel.queryBarcode(queryFilters(it), scene)
        }
        barCodeAdapter?.addOnItemChildClickListener(R.id.tbDelete) { baseQuickAdapter, view, i ->
            baseQuickAdapter.removeAt(i)
            mViewModel.barcodeItemCountLiveData.value = binding?.rvTail?.adapter?.itemCount ?: 0
        }
        barCodeAdapter?.addOnItemChildClickListener(R.id.btnCalibration4) { baseQuickAdapter, view, i ->
            ma4Dialog(baseQuickAdapter, i)
        }
        barCodeAdapter?.addOnItemChildClickListener(R.id.btnCalibration20) { baseQuickAdapter, view, i ->
            ma20Dialog(baseQuickAdapter, i)
        }
    }

    /**
     * 扫描条码 上报的数据
     */
    protected open fun queryFilters(barcode: String): FiltersReq = FiltersReq(
        mapOf(
            Pair("primaryCode", barcode), Pair("taskId", taskId)
        )
    )

    private fun checkMA(
        baseQuickAdapter: BaseQuickAdapter<CalibrateBarCodeBean, *>, i: Int,
    ) {
        if (baseQuickAdapter.items[i].calibration4 == null) {
            ma4Dialog(baseQuickAdapter, i)
        } else if (baseQuickAdapter.items[i].calibration20 == null) {
            ma20Dialog(baseQuickAdapter, i)
        }
    }

    private fun ma20Dialog(
        baseQuickAdapter: BaseQuickAdapter<CalibrateBarCodeBean, *>, i: Int,
    ) {
        val strings = arrayOf("无", "20.02", "20.03", "20.04")
        showCalibration(1, "请选择20mA校准值", strings, baseQuickAdapter, i)
    }

    private fun ma4Dialog(
        baseQuickAdapter: BaseQuickAdapter<CalibrateBarCodeBean, *>, i: Int,
    ) {
        val strings = arrayOf("无", "4.02", "4.03", "4.04")//、4.03、4.04
        showCalibration(0, "请选择4mA校准值", strings, baseQuickAdapter, i)
    }

    private fun showCalibration(
        calibrate: Int,
        title: String,
        strings: Array<String>,
        baseQuickAdapter: BaseQuickAdapter<CalibrateBarCodeBean, *>,
        pos: Int,
    ) {
        XPopup.Builder(this).asCenterList(title, strings) { position: Int, text: String ->
            if (calibrate == 0) {
                baseQuickAdapter.items[pos].calibration4 = position.toString()
                baseQuickAdapter.items[pos].calibration4Text = text
            } else {
                baseQuickAdapter.items[pos].calibration20 = position.toString()
                baseQuickAdapter.items[pos].calibration20Text = text
            }
            baseQuickAdapter.notifyItemChanged(pos)
            checkMA(baseQuickAdapter, pos)
        }.show()
    }

    override fun onActionSubmitConfirm() {
        barCodeAdapter?.items?.withIndex()?.forEach {
            if (it.value.calibration4 == null
                || it.value.calibration20 == null
            ) {
                binding?.rvTail?.layoutManager?.scrollToPosition(it.index)
                "条码${it.value.code}没有选择校验值".showToast()
                return@onActionSubmitConfirm
            }
        }
        mViewModel.submitReport(ReportReq().apply {
            data = mutableListOf(ReportReq.DataReq().apply {
                jobId = this@CalibrateProActivity.jobId
                taskId = this@CalibrateProActivity.taskId
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

    private fun getItems(): List<MutableMap<String, String>>? {
        return barCodeAdapter?.items?.map {
            return@map HashMap<String, String>().apply {
                putAll(
                    mapOf(
                        Pair("code", it.code),
                        Pair("qty", it.qty ?: "0")
                    )
                )
                if (it.calibration4?.isNotEmpty() == true) {
                    put("calibration4", it.calibration4!!)
                }
                if (it.calibration20?.isNotEmpty() == true) {
                    put("calibration20", it.calibration20!!)
                }
            }
        }
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            ViewDisplay("备注", "remark", "remark", null, true)
        )
        return items
    }

    /*override fun topAdapter(): RecyclerView.Adapter<ViewHolder>? {
        topAdapter = topAdapter ?: ProcessAdapter()
        topAdapter?.items = mutableListOf(
            ViewDisplay("工序", "jobId"),
        )
        return topAdapter
    }*/

    override fun heardProcessAdapter(): ProcessAdapter {
        return super.heardProcessAdapter().apply { processAdapter = this }
    }

    override fun middleAdapter(): RecyclerView.Adapter<ViewHolder>? {
        scanAdapter = HeardScanAdapter()
        return scanAdapter
    }

    override fun initBaseViewObservable() {
        super.initBaseViewObservable()
        mViewModel.barcodeELiveData.observe(this) {
            scanAdapter?.notifyItemChanged(0)
        }
    }

    override fun tailAdapter(): RecyclerView.Adapter<ViewHolder> {
        barCodeAdapter = CalibrateBarCodeAdapter()
        /*return barCodeAdapter?.let {
            scanAdapter = HeardScanAdapter()
            scanAdapter?.setItem(BarCodeBean(""), 0)
            val helper = QuickAdapterHelper.Builder(it).build()
            helper.addAfterAdapter(scanAdapter!!)

            return helper.adapter
        }!!*/
        return barCodeAdapter as CalibrateBarCodeAdapter
    }

}