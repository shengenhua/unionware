package com.unionware.once.view

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.base.lib_ui.utils.SoundType
import com.unionware.basicui.base.activity.BaseProcessActivity
import com.unionware.basicui.base.adapter.BarcodeMapAdapter
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.once.adapter.HeardScanAdapter
import com.unionware.once.app.RouterOncePath
import com.unionware.once.viewmodel.PackProcessViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import unionware.base.app.utils.sound.SoundPoolUtil
import unionware.base.ext.showToast
import unionware.base.model.ViewDisplay
import unionware.base.model.req.FiltersReq

/**
 * 装箱核对
 * Author: sheng
 * Date:2024/9/18
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_ZXHD)
class PackCheckActivity : BaseProcessActivity<PackProcessViewModel>() {

    /**
     * 填写资料
     */
    var processAdapter: ProcessAdapter? = null

    /**
     * 扫描框
     */
    var scanAdapter: HeardScanAdapter? = null

    /**
     * 需要扫描 的条码显示
     */
    private var barcodeAdapter: BarcodeMapAdapter? = null

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            packBarcodeLiveData.observe(this@PackCheckActivity) {
                it?.also {
                    barcodeAdapter?.submitList(it)
                    scanAdapter?.notifyItemChanged(0)
                    lifecycleScope.launch {
                        delay(100)//一个延迟同时
                        binding?.rvTail?.scrollToPosition(0)
                    }
                }
                mUIChangeLiveData.getTTSSucOrFailEvent().postValue(true)
            }
            checkPackLiveData.observe(this@PackCheckActivity) {
                barcodeAdapter?.submitList(null)
                processAdapter?.clearData()
                processAdapter?.setFocusable(tag = "packCode")
                binding?.scanSum = 0
                "提交成功".showToast()
                SoundPoolUtil.getInstance()
                    .playAudio(this@PackCheckActivity, SoundType.Default.SUBMIT_SUCCESS)
            }
        }
    }

    override fun initView() {
        super.initView()
        setTitle(title)
        scanAdapter?.setOnEditorActionListener { it ->
            if (it.isEmpty()) {
                return@setOnEditorActionListener
            }

            barcodeAdapter?.items?.withIndex()?.firstOrNull { item ->
                item.value.tag == it.trim()
            }.also { it ->
                if (it == null) {
                    "没有当前条码".showToast()
                    mViewModel.mUIChangeLiveData.getTTSSucOrFailEvent().postValue(false)
                    scanAdapter?.notifyItemChanged(0)
                    return@setOnEditorActionListener
                }
                if (it.value.isSelect) {
                    "当前条码已扫码".showToast()
                    mViewModel.mUIChangeLiveData.getTTSSucOrFailEvent().postValue(false)
                    scanAdapter?.notifyItemChanged(0)
                    return@setOnEditorActionListener
                }
                it.value.isSelect = true
                barcodeAdapter?.notifyItemChanged(it.index)

                binding?.scanSum = barcodeAdapter?.items?.filter { it.isSelect }?.size ?: 0
            }
            mViewModel.packBarcodeLiveData.value = barcodeAdapter?.items?.sortedBy {
                it.isSelect
            } ?: barcodeAdapter?.items
            scanAdapter?.notifyItemChanged(0)
        }
        processAdapter?.addOnEditorActionArray("packCode") { adapter, postion, text ->
            if (text.isNotEmpty()) {
                mViewModel.packQuery(scene, filtersReq = FiltersReq(
                    mutableMapOf<String, Any>(
                        Pair(
                            "packCode", text
                        )
                    )
                ).apply {
                    pageEnabled = false
                })
                adapter.getItem(postion)?.isEditVerify = true
            } else {
                barcodeAdapter?.submitList(null)
                processAdapter?.clearData()
                processAdapter?.setFocusable(tag = "packCode")
                adapter.getItem(postion)?.isEditVerify = false
            }
            adapter.notifyItemChanged(postion)
        }
        processAdapter?.setFocusable(tag = "packCode")
    }

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            ViewDisplay("箱码", tag = "packCode", isEdit = true),
//            ViewDisplay("备注", "remark", "remark", null, true)
        )
        return items
    }

    override fun onActionSubmitConfirm() {
        if (barcodeAdapter?.items.isNullOrEmpty()) {
            "没有需要扫描的数据".showToast()
            return@onActionSubmitConfirm
        }
        barcodeAdapter?.items?.firstOrNull {
            !it.isSelect
        }?.also {
            "存在未扫描的数据".showToast()
            return@onActionSubmitConfirm
        }
        mViewModel.checkPackRecord(
            mapOf(Pair("PackCode", processAdapter?.getItemValueByTag("packCode") ?: ""))
        )
    }

    /**
     * 获取条码列表中的 数据
     */
    fun getItems(): List<Map<String, String?>>? {
        return barcodeAdapter?.items?.map {
            mapOf(Pair("code", it.tag))
        }
    }

    override fun heardProcessAdapter(): ProcessAdapter {
        return super.heardProcessAdapter().apply { processAdapter = this }
    }


    override fun middleAdapter(): RecyclerView.Adapter<ViewHolder>? {
        scanAdapter = HeardScanAdapter(firstFocusable = false)
        return scanAdapter
    }

    override fun tailAdapter(): RecyclerView.Adapter<ViewHolder> {
        barcodeAdapter = barcodeAdapter ?: BarcodeMapAdapter()
        return barcodeAdapter as BarcodeMapAdapter
    }

    override fun topAdapter(): RecyclerView.Adapter<ViewHolder>? = null
}