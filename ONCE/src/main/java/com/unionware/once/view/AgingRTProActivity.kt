package com.unionware.once.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.lxj.xpopup.XPopup
import com.unionware.basicui.base.activity.BaseProcessActivity
import com.unionware.basicui.base.adapter.BarcodeMapAdapter
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.once.adapter.EntityListAdapter
import com.unionware.once.adapter.PromptLabelAdapter
import com.unionware.once.app.RouterOncePath
import com.unionware.once.databinding.AdapterNumberDetailsBinding
import com.unionware.once.model.SpecialReportReq
import com.unionware.once.view.dialog.ScanListPop
import com.unionware.once.viewmodel.AgingProViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.ext.bigDecimalToZeros
import unionware.base.ext.showToast
import unionware.base.model.ViewDisplay
import unionware.base.model.bean.BaseInfoBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.resp.CommonListDataResp
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException


/**
 * 老化架转移 工序 （老化架转移）
 */
@AndroidEntryPoint
@Route(path = RouterOncePath.ONCE.PATH_ONCE_AGEING_RACK_TRANSFER)
open class AgingRTProActivity : BaseProcessActivity<AgingProViewModel>() {
    /**
     * 顶部显示
     */
    private var topAdapter: ProcessAdapter? = null

    private var middleHelper: QuickAdapterHelper? = null

    /**
     * 中间 的view
     */
    private var bodyAdapter: ProcessAdapter? = null

    /**
     * 原老化架 详情
     */
    private var tableAdapter: EntityListAdapter? = null

    /**
     * 扫描sn数据 详情
     */
    private var snAdapter: BarcodeMapAdapter = BarcodeMapAdapter(isDelete = true)

    private var labelAdapter = PromptLabelAdapter()

    override fun initViewObservable() {
        mViewModel.apply {
            viewLiveData.observe(this@AgingRTProActivity) {
                if (it.isNullOrEmpty()) {
                    bodyAdapter?.removeAtRange(IntRange(0, bodyAdapter?.itemCount ?: 0))
                    tableAdapter?.removeAtRange(IntRange(0, tableAdapter?.itemCount ?: 0))
                    snAdapter.removeAtRange(IntRange(0, snAdapter.itemCount))
                    middleHelper?.removeAdapter(labelAdapter)
                } else {
                    labelAdapter.item = mutableListOf("原老化架号")
                    bodyAdapter?.submitList(middleItems())
                    middleHelper?.addAfterAdapter(labelAdapter)
                    tableAdapter?.submitList(it)
                }
            }
            dataLiveData.observe(this@AgingRTProActivity) {
                if (it.isEmpty()) {
                    topAdapter?.changedItemValue("code", "")
                    topAdapter?.setFocusable(tag = "code")
                } else {/*viewInfoLiveData.value?.apply {
                        this.firstOrNull { bean -> bean.key == "moNo" }?.apply {
                            labelAdapter.content = "${this.name}:${it[0]["moNo"].toString()}"
                        }
                    }
                    labelAdapter.notifyItemChanged(0)*//*bodyAdapter?.getItem("oldCode")?.also { item ->
                        item.value = it[0]["frameName"].toString()
                        item.id = it[0]["frameId"].toString()
                    }*/
                }
            }
            snLiveData.observe(this@AgingRTProActivity) {
                if (it.tag.isEmpty()) {
                    bodyAdapter?.clearData("barcode")
                    bodyAdapter?.setFocusable(tag = "barcode")
                    return@observe
                }
                if (snAdapter.items.any { barcode -> barcode.tag == it.tag }) {
                    "当前扫描的数据与已扫码数据不是同一生产订单".showToast()
                    return@observe
                }
                snAdapter.add(0, it)
            }
            submitLiveData.observe(this@AgingRTProActivity) {
                bodyAdapter?.removeAtRange(IntRange(0, bodyAdapter?.itemCount ?: 0))
                tableAdapter?.removeAtRange(IntRange(0, tableAdapter?.itemCount ?: 0))
                snAdapter.removeAtRange(IntRange(0, snAdapter.itemCount))
                middleHelper?.removeAdapter(labelAdapter)
                topAdapter?.clearData()
            }
        }
    }

    override fun initView() {
        super.initView()
        setTitle(title)
        binding?.actvScanSum?.visibility = View.GONE/*bodyAdapter?.addOnEditorActionArray("newCode") { baseQuickAdapter, position, text ->
            //
        }*/
        topAdapter?.addOnEditorActionArray("code") { baseQuickAdapter, position, text ->
            if (text.isNotEmpty()) {
                mViewModel.queryAgingByCode(queryFilters(text))
            }
        }
        topAdapter?.setFocusable(index = 0)
        bodyAdapter?.addOnItemChildClickListener(unionware.base.R.id.ivQuery) { adapter, view, position ->
            queryBasic(
                position,
                adapter.items[position].code,
                adapter.items[position].parentId,
                adapter.items[position].parentName,
            ) { pos, infoBean ->
                infoBean?.apply {
                    adapter.items[pos!!].value = this.name
                    adapter.items[pos].id = this.id
                    adapter.notifyItemChanged(pos)
                }
            }
        }
        bodyAdapter?.addOnEditorActionArray("barcode") { adapter, position, text ->
            //保存sn
            if (text.isNotEmpty()) {
                mViewModel.querySn(
                    scene, "673721F579E362", FiltersReq(
                        mapOf(
                            "barcode" to text
                        )
                    )
                )
                bodyAdapter?.clearData("barcode")
                bodyAdapter?.setFocusable(tag = "barcode")
            }
            /*snAdapter.add(BarcodeMapBean("barcode").also {
                it.tagName = "条码"
                it.tag = text
            })*/
        }
        bodyAdapter?.addOnEditorActionArray("moNo") { adapter, position, text ->
            if (text.isEmpty()) {
                bodyAdapter?.setFocusable(tag = "moNo")
                return@addOnEditorActionArray
            }
            if (tableAdapter?.items?.any { item -> item.map?.get("moNo") == text } == false) {
                "未找到当前扫描工单号的数据".showToast()
                bodyAdapter?.clearData("moNo")
                bodyAdapter?.setFocusable(tag = "moNo")
                return@addOnEditorActionArray
            }
        }
        bodyAdapter?.addOnEditorActionArray("frameId") { adapter, position, text ->
            if (text.isNotEmpty()) {
                //气体物料条码 扫描
                val viewDisplay = adapter.getItem(position)
                mViewModel.query(scene, viewDisplay?.code.toString(), FiltersReq(
                    mapOf(
                        "keyword" to text,
                        Pair(viewDisplay?.parentName ?: "parentId", viewDisplay?.parentId ?: "")
                    )
                ), object : ICallback<CommonListDataResp<BaseInfoBean>?> {
                    override fun onSuccess(data: CommonListDataResp<BaseInfoBean>?) {
                        data?.apply {
                            adapter.items[position].value = data.data[0]?.name
                            adapter.items[position].id = data.data[0]?.id
                            adapter.notifyItemChanged(position)
                        }
                    }

                    override fun onFailure(e: ApiException?) {
                        adapter.getItem(position)?.value = ""
                        adapter.notifyItemChanged(position)
                        bodyAdapter?.setFocusable(tag = "frameId")
                    }
                })
            }
        }
        bodyAdapter?.addOnSelectArray("option") { adapter, position ->
            showSelectDialog(adapter.getItem(position), position)
        }
        tableAdapter?.setOnItemClickListener { adapter, position, text ->
            //点击跳转 详情界面
//            "详情界面 开发中".showToast()
        }
        snAdapter.addOnItemChildClickListener(unionware.base.R.id.tbDelete) { baseQuickAdapter, view, i ->
            baseQuickAdapter.removeAt(i)
        }
        //
//        bodyAdapter?.submitList(middleItems())
//        labelAdapter.item = mutableListOf("原老化架号")
        labelAdapter.onLabelItemClickListener = { position, text ->
            binding?.rvTail?.adapter = when (position) {
                0 -> {
                    tableAdapter
                }

                1 -> {
                    snAdapter
                }

                else -> {
                    tableAdapter
                }
            }
        }
//        helper?.addBeforeAdapter(labelAdapter)
    }

    private fun showSelectDialog(item: ViewDisplay?, position: Int) {
        //选择弹窗
        val select = mutableListOf("整单转移", "部分转移")
        XPopup.Builder(this).asCenterList(
            item?.title ?: "请选择一项", select.toTypedArray()
        ) { pos: Int, text: String? ->
            item?.value = text
            item?.id = select[pos]
            bodyAdapter?.notifyItemChanged(position)
            upodateFeature(text)
        }.show()
    }

    private fun upodateFeature(value: String?) {
        bodyAdapter?.apply {
            items.withIndex().firstOrNull { "barcode" == it.value.key }.also {
                if (it == null && "整单转移" != value) {
                    bodyAdapter?.add(ViewDisplay("扫描SN", "barcode", "barcode", isEdit = true))
                    labelAdapter.item = mutableListOf("原老化架号", "扫描条码")
                } else if (it != null && "整单转移" == value) {
                    this.removeAt(it.index)
                }
            }

            items.withIndex().firstOrNull { "moNo" == it.value.key }.also {
                if (it == null && "整单转移" == value) {
                    bodyAdapter?.add(ViewDisplay("转移工单号", "moNo", "moNo", isEdit = true))
                    labelAdapter.item = mutableListOf("原老化架号")
                } else if (it != null && "整单转移" != value) {
                    this.removeAt(it.index)
                }
            }
        }
    }

    override fun onActionSubmitConfirm() {
        if (tableAdapter?.items.isNullOrEmpty()) {
            "无提交的老化架数据，请检查".showToast()
            return
        }
        if (bodyAdapter?.getItem("n_frameId")?.id.isNullOrEmpty()) {
            "请选择新老化架号".showToast()
            return
        }
        bodyAdapter?.items?.firstOrNull { "option" == it.tag }?.also {
            if ("整单转移" == it.value) {
                //用的是  reportId
                if (bodyAdapter?.getItem("moNo")?.value.isNullOrEmpty()) {
                    "请填写转移工单号".showToast()
                    return
                }
                mViewModel.dataLiveData.value?.apply {
                    firstOrNull { map -> bodyAdapter?.getItem("moNo")?.value == map["moNo"] }.also { map ->
                        if (map.isNullOrEmpty()) {
                            "未找到当前扫描工单号的数据".showToast()
                            return
                        }
                    }
                }
            } else {
                // 部分转移
                if (snAdapter.items.isEmpty()) {
                    "没有扫描SN".showToast()
                    return
                }
            }
        }

        mViewModel.frameTransfer(SpecialReportReq().apply {
            data = HashMap<String, Any>().apply {
                putAll(mapOf(Pair("items", getItems() as Any)))
                if (bodyAdapter?.items?.any { it.key == "moNo" } == true) {
                    mViewModel.dataLiveData.value?.also {
                        it.firstOrNull { map -> bodyAdapter?.getItem("moNo")?.value == map["moNo"] }
                            ?.also { map ->
                                put("reportId", map["id"]?.bigDecimalToZeros() ?: "")
                            }
                    }
                } else {
                    mViewModel.dataLiveData.value?.also {
                        put("reportId", it[0]["id"]?.bigDecimalToZeros() ?: "")
                    }
                }
                bodyAdapter?.items?.firstOrNull { "option" == it.tag }?.also {
                    put(
                        "transferType", if ("整单转移" == it.value) {
                            1
                        } else {
                            2
                        }
                    )
                }
//                put("reportId", mViewModel.dataLiveData.value!![0]["id"]?.bigDecimalToZeros() ?: "")
            }
        })
    }

    fun getItems(): List<Map<String, String?>>? {
        return bodyAdapter?.items?.firstOrNull { "option" == it.tag }?.let {
            if ("整单转移" == it.value) tableAdapter?.items?.map { fentityView ->
                mapOf(
                    Pair("s_frameId", fentityView.map?.get("frameId") ?: ""),
                    Pair("n_frameId", bodyAdapter?.getItem("n_frameId")?.id),
                )
            } else snAdapter.items.map {
                mapOf(
                    Pair("s_frameId", it.value?.get("frameId").toString()),
                    Pair("n_frameId", bodyAdapter?.getItem("n_frameId")?.id),
                    Pair("barcode", it.value?.get("barcode").toString())
                )
            }
        }
        /* return tableAdapter?.items?.map {
             mapOf(
                 Pair("s_frameId", it.map?.get("frameId") ?: ""),
                 Pair("n_frameId", bodyAdapter?.getItem("n_frameId")?.id),
 //                Pair("barcode", )
             )
         }*/
    }

    /**
     * 扫描条码 上报的数据
     */
    protected open fun queryFilters(barcode: String): FiltersReq = FiltersReq(
        mapOf(
            Pair("moNo", barcode)
        )
    )/*.apply {
        *//*控制查询出来的数据不分页*//*
        pageEnabled = false
    }*/

    override fun middleAdapter(): RecyclerView.Adapter<ViewHolder>? {
        bodyAdapter = ProcessAdapter()
        bodyAdapter?.apply {
            middleHelper = QuickAdapterHelper.Builder(this).build()
        }
        return middleHelper?.adapter ?: bodyAdapter!!
    }

    private fun middleItems(): MutableList<ViewDisplay> {
        return mutableListOf(
            ViewDisplay(
                "转移选项", "option",
            ).apply {
                value = "整单转移"
                id = "整单转移"
                isEdit = false
                type = 2
            },
            ViewDisplay(
                "新老化架号", "frameId", "n_frameId", "BOS_ASSISTANTDATA_SELECT", true
            ).apply {
                parentId = "666fad2a8a79cc"
            },
            ViewDisplay("转移工单号", "moNo", "moNo", isEdit = true),
//            ViewDisplay("扫描SN", "barcode", "barcode", isEdit = true)
        )
    }

    override fun topAdapter(): RecyclerView.Adapter<ViewHolder>? {
        topAdapter = topAdapter ?: ProcessAdapter()
        topAdapter?.items = mutableListOf(
            ViewDisplay("查询", "code", isEdit = true)
        )
        return topAdapter
    }

    override fun tailLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(mContext)
    }

    //PromptLabelAdapter("原老化架")
    override fun tailAdapter(): RecyclerView.Adapter<ViewHolder> {
        tableAdapter = EntityListAdapter()
        /*tableAdapter?.apply {
            helper = QuickAdapterHelper.Builder(this).build()
        }
        return helper?.adapter ?: tableAdapter!!*/
        return tableAdapter!!
    }


    private class NumberAdapter(var list: MutableList<String> = mutableListOf()) :
        BaseSingleItemAdapter<MutableList<String>, DataBindingHolder<AdapterNumberDetailsBinding>>(
            list
        ) {

        fun addScan(data: String) {
            if (data.isNotEmpty()) {
                if (list.contains(data)) {
                    "条码已扫描".showToast()
                    return
                }
                list.add(data)
                notifyItemChanged(0)
            }
        }

        private var pop: ScanListPop? = null

        override fun onBindViewHolder(
            holder: DataBindingHolder<AdapterNumberDetailsBinding>,
            item: MutableList<String>?,
        ) {
            holder.binding.apply {
                content = "已扫描: ${item?.size ?: 0}"
                tvContent.setOnClickListener {
                    if (item.isNullOrEmpty()) {
                        "没有扫描的数据".showToast()
                        return@setOnClickListener
                    }
                    if (pop?.isShow == true) {
                        return@setOnClickListener
                    }
                    pop = ScanListPop(context, item).also { pop ->
                        pop.adapterDeleteListener = {
                            notifyItemChanged(0)
                        }
                        XPopup.Builder(context).maxWidth(2000).asCustom(pop).show()
                    }
                }
            }
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int,
        ): DataBindingHolder<AdapterNumberDetailsBinding> {
            return DataBindingHolder(
                AdapterNumberDetailsBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )
        }
    }

}