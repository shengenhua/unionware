package com.unionware.once.view

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.lxj.xpopup.XPopup
import com.unionware.basicui.app.BasicBaseActivity
import com.unionware.basicui.base.adapter.BarcodeMapAdapter
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.once.R
import com.unionware.once.adapter.HeardScanAdapter
import com.unionware.once.app.RouterOncePath
import com.unionware.once.databinding.ActivityProductTransferBinding
import com.unionware.once.viewmodel.ProductTransferViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import unionware.base.ext.showToast
import unionware.base.model.ViewDisplay
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.ChecketReq

/**
 * 工单产品转移
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_GDCPZY)
class ProductTransferActivity :
    BasicBaseActivity<ActivityProductTransferBinding, ProductTransferViewModel>() {
    override fun onBindVariableId(): MutableList<Pair<Int, Any>> = mutableListOf()
    override fun onBindLayout(): Int = R.layout.activity_product_transfer
    override fun enableToolBarMenu(): Boolean = true
    override fun backShowDialog(): Boolean =
        mViewModel.barcodeItemCountLiveData.value?.let { it > 0 } ?: false

    /**
     * 填写资料
     */
    var processAdapter: ProcessAdapter = ProcessAdapter()

    /**
     * 扫描框
     */
    var scanAdapter: HeardScanAdapter = HeardScanAdapter(firstFocusable = false)

    /**
     * 扫描的条码
     */
    var barCodeAdapter: BarcodeMapAdapter = BarcodeMapAdapter(isDelete = true)

    override fun initView() {
        setTitle(title)
        binding?.run {
            rvHead.layoutManager = object : LinearLayoutManager(this@ProductTransferActivity) {
                override fun canScrollVertically(): Boolean {
                    return false
                }

                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
            rvHead.adapter = processAdapter.also {
                it.submitList(getHeadItem())
            }

            rvMiddle.layoutManager = LinearLayoutManager(this@ProductTransferActivity)
            rvMiddle.adapter = scanAdapter

            rvTail.layoutManager = if (isLandscape()) {
                GridLayoutManager(this@ProductTransferActivity, 2)
            } else {
                LinearLayoutManager(this@ProductTransferActivity)
            }
            rvTail.adapter = barCodeAdapter
        }

        barCodeAdapter.addOnItemChildClickListener(R.id.tbDelete) { baseQuickAdapter, view, i ->
            baseQuickAdapter.removeAt(i)

            lifecycleScope.launch {
                delay(300)//一个延迟同时
                mViewModel.barcodeItemCountLiveData.value = binding?.rvTail?.adapter?.itemCount ?: 0
            }
        }
    }

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            barcodeLiveData.observe(this@ProductTransferActivity) {
                if (processAdapter.getItemValueByTag(HEAD_ITEM_outOrder) == it.value?.get("billCode")) {
                    barCodeAdapter.add(0, it)
                } else {
                    "扫描条码不属于当前转入工单".showToast()
                }

                lifecycleScope.launch {
                    delay(300)//一个延迟同时
                    scanAdapter.notifyItemChanged(0)
                    binding?.rvTail?.scrollToPosition(0)
                    mViewModel.barcodeItemCountLiveData.value =
                        binding?.rvTail?.adapter?.itemCount ?: 0
                }
            }

            barcodeItemCountLiveData.observe(this@ProductTransferActivity) {
                binding?.scanSum = it
            }
            barcodeELiveData.observe(this@ProductTransferActivity) {
                scanAdapter.notifyItemChanged(0)
            }

            //提交确认
            submitLiveData.observe(this@ProductTransferActivity) {
                processAdapter.changedItemValue(HEAD_ITEM_outOrder)
                processAdapter.changedItemValue(HEAD_ITEM_inOrder)
                barCodeAdapter.submitList(null)
                processAdapter.setFocusable(tag = HEAD_ITEM_outOrder)
                mViewModel.barcodeItemCountLiveData.value = 0
            }
        }
    }

    override fun initData() {
        processAdapter.setFocusable(tag = HEAD_ITEM_outOrder)
        processAdapter.addOnEditorActionArray(
            HEAD_ITEM_outOrder,
            HEAD_ITEM_inOrder
        ) { adapter, position, text ->
            if (text.isEmpty()) {
                processAdapter.setFocusable(index = position)
            } else {
                currentFocus?.clearFocus()
                processAdapter.items.firstOrNull { it.value == text && adapter.items[position].tag != it.tag }
                    ?.apply {
                        "扫描工单与\"${title}\"相同".showToast()
                        processAdapter.changedItemValue(adapter.items[position].tag)
                    }
                if (processAdapter.getItemValueByTag(HEAD_ITEM_outOrder).isEmpty()) {
                    processAdapter.setFocusable(tag = HEAD_ITEM_outOrder)
                } else if (processAdapter.getItemValueByTag(HEAD_ITEM_inOrder).isEmpty()) {
                    processAdapter.setFocusable(tag = HEAD_ITEM_inOrder)
                } else {
                    scanAdapter.notifyItemChanged(0)
                }
            }
        }
        scanAdapter.setOnEditorActionListener {
            if (processAdapter.getItemValueByTag(HEAD_ITEM_outOrder).isEmpty()) {
                "未扫描转出工单".showToast()
                scanAdapter.notifyItemChanged(0)
                return@setOnEditorActionListener
            }
            if (barCodeAdapter.items.any { barcode -> barcode.tag == it }) {
                "条码已扫描".showToast()
                scanAdapter.notifyItemChanged(0)
                return@setOnEditorActionListener
            }
            //
            mViewModel.queryBarcode(
                FiltersReq(
                    mutableMapOf<String, Any>(
                        "primaryCode" to it,
                        "billCode" to processAdapter.getItemValueByTag(HEAD_ITEM_outOrder)
                    )
                )
            )
        }
    }

    override fun onActionSubmit() {
        if (processAdapter.getItemValueByTag(HEAD_ITEM_outOrder).isEmpty()) {
            "未扫描转出工单".showToast()
            return
        }

        if (processAdapter.getItemValueByTag(HEAD_ITEM_inOrder).isEmpty()) {
            "未扫描转入工单".showToast()
            return
        }
        if (barCodeAdapter.items.isEmpty()) {
            XPopup.Builder(this).asConfirm("提示", "是否提交(整单转移)？") {
                onActionSubmitConfirm("1")
            }.show()
        } else {
            XPopup.Builder(this).asConfirm("提示", "是否提交(部分转移)？") {
                onActionSubmitConfirm("2")
            }.show()
        }
    }

    fun onActionSubmitConfirm(type: String) {
        //提交生成[工单产品转移单]
        mViewModel.submitReport(ChecketReq().apply {
            data =
                mutableListOf(HashMap<String, Any>(mapOf(Pair("type", type))).apply {
                    getItems().also {
                        if (it.isNotEmpty()) {
                            put("list", it)
                        }
                    }
                    processAdapter.items.forEach {
                        if (it.key?.isNotEmpty() == true && it.value?.isNotEmpty() == true) {
                            put(it.key ?: "", it.id ?: it.value as Any)
                        }
                    }
                })
        })
    }

    private fun getItems(): List<String> {
        return barCodeAdapter.items.map {
            it.tag
        }
    }


    private fun getHeadItem(): List<ViewDisplay> {
        return mutableListOf(
            ViewDisplay(
                "转出工单",
                HEAD_ITEM_outOrder,
                key = "srcOrder",
                isEdit = true,
                isRequired = true
            ),
            ViewDisplay(
                "转入工单",
                HEAD_ITEM_inOrder,
                key = "tarOrder",
                isEdit = true,
                isRequired = true
            )
        )
    }

    private companion object {
        var HEAD_ITEM_outOrder = "outOrder"
        var HEAD_ITEM_inOrder = "inOrder"
    }
}