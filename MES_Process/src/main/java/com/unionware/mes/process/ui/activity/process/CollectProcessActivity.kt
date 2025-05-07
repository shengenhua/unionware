package com.unionware.mes.process.ui.activity.process

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.mes.process.databinding.LayoutEmptyCollectionBinding
import com.unionware.mes.process.ui.activity.process.base.BaseCollectProcessActivity
import com.unionware.mes.process.viewmodel.CollectProcessViewModel
import com.unionware.path.RouterPath
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.ext.showToast
import unionware.base.model.ViewDisplay
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.barcode.MultiBarCodeBean
import unionware.base.model.req.FiltersReq


/**
 * 工序 （数据采集）
 */
@AndroidEntryPoint
@Route(path = RouterPath.APP.MES.PATH_MES_PROCESS_COLLECT)
open class CollectProcessActivity : BaseCollectProcessActivity<CollectProcessViewModel>() {

    override fun initView() {
        super.initView()
        binding?.rvMiddle?.visibility = View.GONE
        scanAdapter?.setOnEditorActionListener {
            if (mViewModel.barcodeCetLvDt.value == null) {
                "当前没有条码采集项目采集项目".showToast()
                scanAdapter?.notifyItemChanged(0)
                return@setOnEditorActionListener
            }
            if (collectAdapter?.items?.firstOrNull { item -> item.code == it } != null) {
                "当前条码已扫描,请勿重复扫描".showToast()
                scanAdapter?.notifyItemChanged(0)
                return@setOnEditorActionListener
            }
            mViewModel.queryBarcode(queryFilters(it), scene)
        }
    }

    override fun initData() {
//        super.initData()
        //取工序普通采集方案 显示在上面
        mViewModel.getCollectOption(
            FiltersReq(mutableMapOf(Pair("jobId", jobId as Any))), "1", scene, "6592C0EA0F63E5"
        )
        //条码 扫描条码携带出来
        mViewModel.getCollectOption(
            FiltersReq(mutableMapOf(Pair("jobId", jobId as Any))), "2", scene, "6592C0EA0F63E5"
        )
    }

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            processCetLvDt.observe(this@CollectProcessActivity) {
                //
                if (it.isEmpty()) {
                    //没有数据 提示空布局
                    LayoutEmptyCollectionBinding.inflate(this@CollectProcessActivity.layoutInflater)
                        .apply {
                            this.actvEmptyTitle.text = "当前没有采集项目"
                            processAdapter.isStateViewEnable = true
                            processAdapter.stateView = this.root
                        }
                    "当前没有采集项目".showToast()
                } else {
                    //更新数据
                    processAdapter.submitList(it)
                }
            }
            barcodeCetLvDt.observe(this@CollectProcessActivity) {
                if (it.isEmpty()) {
                    //没有数据 提示
                    //且隐藏扫描框
                    //没有数据 提示空布局
                    LayoutEmptyCollectionBinding.inflate(this@CollectProcessActivity.layoutInflater)
                        .apply {
                            this.actvEmptyTitle.text = "未设置条码采集项目"
                            collectAdapter?.isStateViewEnable = true
                            collectAdapter?.stateView = this.root
                        }
                    binding?.rvMiddle?.visibility = View.GONE
                    binding?.actvScanSum?.visibility = View.GONE
                    "未设置条码采集项目".showToast()
                } else {
                    //保存数据 后续时候
                    binding?.rvMiddle?.visibility = View.VISIBLE
                    binding?.actvScanSum?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun CollectProcessViewModel.barcodeObserve() {
        barcodeLiveData.observe(this@CollectProcessActivity) { it ->
            if (barcodeCetLvDt.value.isNullOrEmpty()) {
                "条码没有采集项目".showToast()
                return@observe
            }
            val bean = MultiBarCodeBean(it.code).apply {
                materialId = it.materialId
                materialCode = it.materialCode
                materialName = it.materialName
                materialSpec = it.materialSpec
                qty = it.qty
            }
            bean.collects = mutableListOf<CollectMultiItem>().apply {
                barcodeCetLvDt.value?.forEach {
                    val item = CollectMultiItem()
                    item.code = it.code
                    item.value = it.value
                    item.name = it.name
                    item.id = it.id
                    item.colName = it.colName
                    item.colId = it.colId
                    item.colSeq = it.colSeq
                    item.colMethod = it.colMethod
                    item.colNumber = it.colNumber
                    item.tag = it.tag
                    item.stdValue = it.stdValue
                    add(item)
                }
            }
            collectAdapter?.add(0, bean)
            scanAdapter?.notifyItemChanged(0)
        }
    }

    override fun onActionSubmitConfirm() {
        mViewModel.apply {
            if (processCetLvDt.value.isNullOrEmpty()) {
                "无采集项目".showToast()
                return@onActionSubmitConfirm
            }
            if (barcodeCetLvDt.value.isNullOrEmpty()) {
                "条码无采集项目".showToast()
                return@onActionSubmitConfirm
            }
        }
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
//            ViewDisplay("备注", "remark", "remark", null, true)
        )
        return items
    }
}