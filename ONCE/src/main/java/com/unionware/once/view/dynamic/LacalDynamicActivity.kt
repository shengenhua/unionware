package com.unionware.once.view.dynamic

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.XPopup
import com.unionware.lib_base.utils.ext.botToTopAnimate
import com.unionware.lib_base.utils.ext.rightToLeftAnimate
import com.unionware.once.R
import com.unionware.once.adapter.dynamic.BottomFeatureAdapter
import com.unionware.once.adapter.dynamic.EntryCollectsAdapter
import com.unionware.once.adapter.dynamic.ScanBarcodeAdapter
import com.unionware.once.viewmodel.dynamic.LacalDynamicViewModel
import com.unionware.virtual.view.adapter.virtual.VirtualViewDiffAdapter
import unionware.base.model.req.ViewReq


/**
 * 独立本地动态装配
 * Author: sheng
 * Date:2024/11/19
 */
open class LacalDynamicActivity<VM : LacalDynamicViewModel> : LacalBaseDynamicActivity<VM>() {
    /**
     * 单据头 适配器
     */
    open var headCollectsAdapter: VirtualViewDiffAdapter = VirtualViewDiffAdapter()

    /**
     * 扫描框 适配器 扫描条码+数量+确认
     */
    open var scanBarcodeAdapter: ScanBarcodeAdapter = ScanBarcodeAdapter()

    /**
     * 单据体 适配器
     */
    open var entryCollectsAdapter: EntryCollectsAdapter = EntryCollectsAdapter(
        this,
        haveDelete = false
    )

    /**
     * 子单据体 适配器
     */
    open var subEntryCollectsAdapter: EntryCollectsAdapter = EntryCollectsAdapter(this, false)

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            virtualView.apply {
                headCollectsLiveData.observe(this@LacalDynamicActivity) { head ->
                    if (scanBarcodeAdapter.isShow()) {
                        head.firstOrNull { it.key == "FBarCodeScan" }?.apply {
                            scanBarcodeAdapter.update(value)
                        }
                        head.firstOrNull { it.key == "FBarQtyScan" }?.apply {
                            scanBarcodeAdapter.update(barQty = value)
                        }
                    }
                    headCollectsAdapter.submitList(
                        head.filter { it.key != "FBarCodeScan" && it.key != "FBarQtyScan" },
                        focusPosition.value == -1 && currentFocus == null
                    )
                }
                entryOrSubLiveData.observe(this@LacalDynamicActivity) {
                    if (it) {
                        mViewModel.virtualView.focusPosition.value = 1
                        binding?.rvBottom?.botToTopAnimate {
                            binding?.rvBottom?.adapter = featureAdapter
                        }
                        binding?.rvTail?.rightToLeftAnimate(duration = 200) {
                            binding?.rvTail?.adapter = entryCollectsAdapter
                        }
                        entryAndSubViewLiveData.value?.also { view ->
                            scanBarcodeAdapter.showUI(
                                view.entry.hasBarCode, view.entry.hasBarQty
                                //, binding?.fabAdd
                            )
                        }
                    } else {
                        mViewModel.virtualView.focusPosition.value = 2
                        binding?.rvBottom?.botToTopAnimate {
                            binding?.rvBottom?.adapter = subFeatureAdapter
                        }
                        binding?.rvTail?.rightToLeftAnimate(duration = 200) {
                            binding?.rvTail?.adapter = subEntryCollectsAdapter
                        }
                        entryAndSubViewLiveData.value?.also { view ->
                            scanBarcodeAdapter.showUI(
                                view.subEntry.hasBarCode,
                                view.subEntry.hasBarQty,
//                                binding?.fabAdd
                            )
                        }
                    }
                }
                entryAndSubViewLiveData.observe(this@LacalDynamicActivity) {
                    if (it?.entry?.isShow == false) {
                        //隐藏单据体
                        binding?.clBodyBottom?.visibility = View.GONE
                        topAdapter.getViewBinding(0)?.binding?.ivArrowDown?.visibility =
                            View.GONE
                    } else if (entryOrSubLiveData.value == true) {
                        //判断是否有子单据体显示
                        entryCollectsAdapter.setNewHaveSub(it?.subEntry?.isShow ?: true)
                    }
                    //保存是否 显示扫描框，只会第一次获取
                    entryOrSubLiveData.value = true
                }

                entryCollectsLiveData.observe(this@LacalDynamicActivity) {
                    entryCollectsAdapter.submitList(it)
                    if (entryOrSubLiveData.value == true) {
                        binding?.rvTail?.postDelayed({
                            virtualView.entryPositionLiveData.value?.apply {
                                if (getRecyclerPosition(this) == true) {
                                    binding?.rvTail?.smoothScrollToPosition(if (it.isEmpty()) 0 else it.size - 1)
                                }
                                if (focusPosition.value == 1 && currentFocus == null) {
                                    entryCollectsAdapter.focusView(this)
                                }
                            }
                        }, 300)
                    }
                }
                subEntryCollectsLiveData.observe(this@LacalDynamicActivity) {
                    subEntryCollectsAdapter.submitList(it)
                    if (entryOrSubLiveData.value == false) {
                        binding?.rvTail?.postDelayed({//smoothScrollToPositi
                            virtualView.entryPositionLiveData.value?.apply {
                                if (getRecyclerPosition(this) == true) {
                                    binding?.rvTail?.smoothScrollToPosition(if (it.isEmpty()) 0 else it.size - 1)
                                }
                                if (focusPosition.value == 2 && currentFocus == null) {
                                    subEntryCollectsAdapter.focusView(this)
                                }
                            }
                        }, 300)
                    }
                }
            }
        }
    }

    private fun getRecyclerPosition(position: Int): Boolean? {
        // 获取RecyclerView的LayoutManager对象
        if (position == -1) {
            return true
        }
        return binding?.rvTail?.layoutManager?.let {
            when (it) {
                is LinearLayoutManager -> {
                    position != it.findFirstVisibleItemPosition() && position != it.findLastVisibleItemPosition()
                }

                is GridLayoutManager -> {
                    position != it.findFirstVisibleItemPosition() && position != it.findLastVisibleItemPosition()
                }

                else -> {
                    true
                }
            }
        }
    }


    override fun caretView() {
        mViewModel.virtualViewRequest.createView(ViewReq().apply {
//            formId = reportRuleId
            formId = "RRJY_DBAO_RAWCHECKET"
//            primaryId = primaryId
            params = mapOf(
                Pair("billId", id),
            )
        })
    }

    override fun initView() {
        super.initView()
        binding?.apply {
            fabAdd.visibility = View.GONE
            fabAdd.setOnClickListener {
                /*调用新增接口*/
                mViewModel.virtualView.entryPositionLiveData.value = -1
                mViewModel.virtualViewRequest.command(
                    "INVOKE_NEWENTRYROW"/* 手动新新增加*/,
                    if (mViewModel.virtualView.entryOrSubLiveData.value == true) "FEntry" else "FSubEntry"
                )
            }
        }
        addSubButton(BottomFeatureAdapter.AdapterButtonValue(0, "返回")) {
            mViewModel.virtualView.entryOrSubLiveData.value = true
        }
    }


    override fun middleAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>? {
        //单据体虚拟视图 扫描框
        return scanBarcodeAdapter.apply {
            setOnEditorActionListener { value, tag ->
                //调用更新接口 更新 视图
                mViewModel.virtualView.focusPosition.value = 0
                mViewModel.virtualViewRequest.updateView(tag, value)
            }
            addOnItemChildClickListener(R.id.tvVerify) { _, _, _ ->
                mViewModel.virtualView.focusPosition.value = 0
                mViewModel.virtualViewRequest.command("INVOKE_BARCODECONFIRM"/* 条码确认按钮*/)
            }
            addOnItemChildClickListener(R.id.ivCommonScan) { _, _, _ ->
                zxingBasic(0) { _, text ->
                    mViewModel.virtualView.focusPosition.value = 0
                    mViewModel.virtualViewRequest.updateView("FBarCodeScan", text)
                }
            }
        }
    }

    override fun heardDynamicAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        //单据头
        return headCollectsAdapter.apply {
            itemFocus = {
                if (it) {
                    currentFocus?.apply {
                        clearFocus()
                    }
                }
            }
            setItemUpdateValue {
                mViewModel.virtualView.focusPosition.value = -1
                mViewModel.virtualViewRequest.updateView(it?.key, it?.value, 0)
            }
            queryItemListener = { bean, position ->
                mViewModel.virtualViewRequest.commandQuery(
                    "INVOKE_GETCUSTOMFILTER",
                    bean
                ) {
                    queryBasic(position, it?.customFilter, bean.clone()) { _, infoBean ->
                        mViewModel.virtualView.focusPosition.value = -1
                        mViewModel.virtualViewRequest.updateView(bean.key, infoBean?.code, 0)
                    }
                }
            }
        }
    }

    override fun onActionSubmitConfirm() {
        //提交
        mViewModel.virtualViewRequest.command("Save"/* 保存按钮*/)
    }

    override fun tailAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>? {/*初始化 单据体,子单单据体的 监听*/
        subEntryCollectsAdapter.apply {
            entryUpdateValue = { bean, position ->
                mViewModel.virtualView.focusPosition.value = 2
                mViewModel.virtualViewRequest.updateView(bean?.key, bean?.value, position)
            }
            queryUpdateValue = { adapter, _, pos, itemPos ->
                mViewModel.virtualViewRequest.commandQuery(
                    "INVOKE_GETCUSTOMFILTER",
                    adapter.getItem(pos)
                ) {
                    queryBasic(pos, it?.customFilter, adapter.getItem(pos)) { _, infoBean ->
                        mViewModel.virtualView.focusPosition.value = 2
                        mViewModel.virtualViewRequest.updateView(
                            adapter.getItem(pos)?.key,
                            infoBean?.code,
                            itemPos
                        )
                    }
                }
            }
            addOnItemChildClickListener(R.id.tvDelete) { _, _, position ->
                XPopup.Builder(this@LacalDynamicActivity)
                    .asConfirm("删除", "是否确认删除？") {
                        //删除行 子单据体
                        mViewModel.virtualViewRequest.commandRow(
                            "INVOKE_DELETEENTRYROW"/*删除行*/,
                            "FSubEntryCollects"/*子单据体*/,
                            position
                        )
                    }.show()
            }
        }
        //单据体
        return entryCollectsAdapter.apply {
            entryUpdateValue = { bean, position ->
                mViewModel.virtualView.focusPosition.value = 1
                mViewModel.virtualViewRequest.updateView(bean?.key, bean?.value, position)
            }

            queryUpdateValue = { adapter, _, pos, itemPos ->
                mViewModel.virtualViewRequest.commandQuery(
                    "INVOKE_GETCUSTOMFILTER",
                    adapter.getItem(pos)
                ) {
                    queryBasic(pos, it?.customFilter, adapter.getItem(pos)) { _, infoBean ->
                        mViewModel.virtualView.focusPosition.value = 1
                        mViewModel.virtualViewRequest.updateView(
                            adapter.getItem(pos)?.key,
                            infoBean?.code,
                            itemPos
                        )
                    }
                }
            }
            addOnItemChildClickListener(R.id.tvSub) { _, _, position ->
                //切换子单据体
                mViewModel.virtualViewRequest.commandRow(
                    "INVOKE_ENTRYROWCLICK" /*行点击事件*/, "FEntryCollects", position
                )
                mViewModel.virtualView.entryOrSubLiveData.value = false
            }
            addOnItemChildClickListener(R.id.tvDelete) { _, _, position ->
                XPopup.Builder(this@LacalDynamicActivity)
                    .asConfirm("删除", "是否确认删除？") {
                        //删除行 单据体列表其中一个
                        mViewModel.virtualViewRequest.commandRow(
                            "INVOKE_DELETEENTRYROW"/*删除行*/, "FEntryCollects"/*单据体*/, position
                        )
                    }.show()
            }
        }
    }
}