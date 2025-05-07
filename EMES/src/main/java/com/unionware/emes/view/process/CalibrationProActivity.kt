package com.unionware.emes.view.process

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.lxj.xpopup.XPopup
import com.unionware.emes.view.dialog.CollectionPop
import com.unionware.emes.view.process.base.ProcessActivity
import com.unionware.emes.viewmodel.process.CalibrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.ext.showToast
import unionware.base.model.ViewDisplay
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.barcode.MultiBarCodeBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ReportReq

/**
 * 标定工序 （标定）
 */
@AndroidEntryPoint
class CalibrationProActivity : ProcessActivity<CalibrationViewModel>() {
    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            fileLiveData.observe(this@CalibrationProActivity) {

            }
            ratifyLiveData.observe(this@CalibrationProActivity) { it ->
                //特批人校验成功
                it?.also {
                    specialRatifyPop?.dismiss()
                    if (sponsorsLiveData.value.isNullOrEmpty()) {
                        sponsorsLiveData.value = mutableListOf()
                    }
                    sponsorsLiveData.value?.apply {
                        this.add(newSponsorsLiveData.value ?: "TIMEUPDATEPASSTIVE")
                    }
//                    mViewModel.queryBarcode(queryFilters(scanAdapter?.getBarCode() ?: ""), scene)
                    mViewModel.queryBarcode(
                        queryFilters(scanAdapter?.getBarCode().let {
                            if (it.isNullOrEmpty()) {
                                barcodeELiveData.value ?: ""
                            } else {
                                it
                            }
                        }), scene
                    )
                }
            }
            failureLiveData.observe(this@CalibrationProActivity) {
                if (it?.data == "TIMEUPDATEPASSTIVE") {
                    onSpecialSubmit(it.errorMsg)
                }
            }
            barcodeCheckLive.observe(this@CalibrationProActivity) {
                /*//标定测试汇报记录 成功与否
                it?.let { it1 ->
                    barCodeAdapter?.add(it1)
                }*/
                scanAdapter?.notifyItemChanged(0)
                collectionPop?.dismiss()
                mUIChangeLiveData.getTTSSucOrFailEvent().postValue(true)
                "检测成功".showToast()
            }
            gasBarcodeLiveData.observe(this@CalibrationProActivity) {
                processAdapter?.items?.withIndex()?.firstOrNull { it.value.tag == "gasBarcode" }
                    ?.apply {
                        this.value.value = it.code
                        this.value.infoList = it.infoList
                        processAdapter?.notifyItemChanged(this.index)
                        if (it.code.isNotEmpty()) {
                            scanAdapter?.notifyItemChanged(0)
                        }
                    }
            }
        }
    }

    override fun heardLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(this)
    }

    private var collectionPop: CollectionPop? = null
    private fun showCollectionPop(bean: MultiBarCodeBean) {
        if (collectionPop?.isShow == true) {
            return
        }
        collectionPop = CollectionPop(
            this,
            this.title,
            taskId,
            headAdapter?.getItemValueByTag("gasBarcode") ?: "",
            bean,
            mViewModel
        )
        XPopup.Builder(mContext).maxWidth(2000).dismissOnBackPressed(false)
            .dismissOnTouchOutside(false).asCustom(collectionPop).show()
    }


    override fun CalibrationViewModel.barcodeObserve() {
        barcodeLiveData.observe(this@CalibrationProActivity) { it ->
            val bean = MultiBarCodeBean(it.code).apply {
                materialId = it.materialId
                materialCode = it.materialCode
                materialName = it.materialName
                materialSpec = it.materialSpec
                qty = it.qty
            }
            processAdapter?.items?.also { headItems ->
                headItems.firstOrNull { it.tag == "gasName" }?.apply {
                    if (id?.isNotEmpty() == true) {
                        bean.gasId = id
                        bean.gasName = value
                    }
                }
            }
            bean.collects = mutableListOf<CollectMultiItem>().apply {
                collectionLiveData.value?.forEach {
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
            showCollectionPop(bean)
//            scanAdapter?.notifyItemChanged(0)
        }
    }

    override fun initData() {
        mViewModel.getCollectOption(FiltersReq(mapOf(Pair("jobId", jobId))), scene)
    }


    override fun onActionSubmitConfirm() {
        submitReport()
    }

    override fun submitReport() {
        mViewModel.submitReport(ReportReq().apply {
            data = mutableListOf(ReportReq.DataReq().apply {
                jobId = this@CalibrationProActivity.jobId
                taskId = this@CalibrationProActivity.taskId
                params = HashMap<String, Any>().apply {
                    processAdapter?.items?.forEach {
                        if (it.key?.isNotEmpty() == true && it.value?.isNotEmpty() == true) {
                            put(it.key ?: "", it.id ?: it.value as Any)
                        }
                    }
                }
            })
        })
    }

    override fun onActionSubmitConfirm(operator: String, code: String) {
        mViewModel.ratifyChecked(
            mapOf(
                Pair("ratifyId", operator),
                Pair("ratifyPassword", code),
                Pair("taskId", taskId),
                Pair("type", "lh")
            )
        )
    }

    override fun initView() {
        super.initView()
        scanAdapter?.setOnEditorActionListener { it ->
            if (processAdapter?.items?.firstOrNull { it.tag == "gasName" && !it.id.isNullOrEmpty() } == null) {
                "请先选择气体".showToast()
                scanAdapter?.notifyItemChanged(0)
                return@setOnEditorActionListener
            }
            if (processAdapter?.items?.firstOrNull { it.tag == "gasBarcode" && !it.value.isNullOrEmpty() } == null) {
                "请先扫描气体物料条码".showToast()
                scanAdapter?.notifyItemChanged(0)
                return@setOnEditorActionListener
            }/*if (barCodeAdapter?.items?.firstOrNull { item -> item.code == it } != null) {
                "当前条码已扫描,请勿重复扫描".showToast()
                scanAdapter?.notifyItemChanged(0)
                return@setOnEditorActionListener
            }*/
            mViewModel.queryBarcode(queryFilters(it), scene)
        }
        headAdapter?.addOnEditorActionArray("gasBarcode") { _, _, text ->
            if (text.isNotEmpty()) {
                //气体物料条码 扫描
                mViewModel.queryGasBarcode(FiltersReq(mapOf(Pair("primaryCode", text))), scene)
            }
        }
    }

    override fun queryFilters(barcode: String) = FiltersReq(
        mapOf(
            Pair("primaryCode", barcode), Pair("taskId", taskId),
//            Pair("feature", "TIMEUPDATE")
            Pair("type", "lh")
        )
    )

    override fun heardItems(): MutableList<ViewDisplay> {
        val items: MutableList<ViewDisplay> = mutableListOf(
            ViewDisplay(
                "气体", "gasName", code = "66C2FF38D6702D", //"BOS_ASSISTANTDATA_SELECT",
                isEdit = true
            ).apply {
                parentName = "taskId"
                parentId = taskId
            }, ViewDisplay(
                "气体物料条码", "gasBarcode", isEdit = true
            ), ViewDisplay("备注", "remark", "remark", null, true)
        )
        return items
    }

    override fun headQueryBasic(position: Int, adapter: BaseQuickAdapter<ViewDisplay, *>) {
        queryOtherBasic(
            position,
            adapter.items[position].code,
            adapter.items[position].parentId,
            adapter.items[position].parentName,
        ) { pos, infoBean ->
            infoBean?.apply {
                adapter.items[pos!!].value = this.map?.get("gasName")
                adapter.items[pos].id = this.map?.get("gasId")
                adapter.items[pos].infoList = this.list
                adapter.notifyItemChanged(pos)

                if (adapter.items[pos].tag == "gasName") {
                    headAdapter?.setFocusable(tag = "gasBarcode")
                }
            }
        }
    }

    override fun getItems(): List<Map<String, String?>>? {
        return barCodeAdapter?.items?.map {
            mapOf(
                Pair("code", it.code),
                Pair("qty", it.qty),
            )
        }
    }
}