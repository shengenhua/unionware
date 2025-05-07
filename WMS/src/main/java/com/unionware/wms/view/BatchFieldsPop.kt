package com.unionware.wms.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.unionware.wms.R
import com.unionware.wms.databinding.PopBatchFieldBinding
import com.unionware.wms.inter.basedata.BasicDataOnEditorActionContract
import com.unionware.wms.inter.basedata.BasicDataOnEditorActionPresenter
import com.unionware.wms.inter.wms.scan.NormalScanPresenter
import com.unionware.wms.model.bean.NormalScanConfigBean
import com.unionware.wms.ui.activity.BasicDataActivity
import com.unionware.wms.ui.adapter.NorMalScanAdapter
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.app.utils.DateFormatUtils
import unionware.base.ext.bigDecimalToZeros
import unionware.base.ext.showToast
import unionware.base.model.bean.BaseInfoBean
import unionware.base.model.bean.PropertyBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ViewReq
import unionware.base.ui.datepicker.CustomDatePicker
import javax.inject.Inject

/**
 * Author: sheng
 * Date:2025/2/25
 */
@AndroidEntryPoint
class BatchFieldsPop(
    context: Context,
    val scene: String,
    private val normalScanConfigBean: NormalScanConfigBean,
    val launcher: ActivityResultLauncher<Intent>,
    val presenter: NormalScanPresenter,
    val pageId: String,
) : BottomPopupView(context), BasicDataOnEditorActionContract.View {

    @JvmField
    @Inject
    var basicDataOnEditorActionPresenter: BasicDataOnEditorActionPresenter? = null
    private lateinit var binding: PopBatchFieldBinding
    private val propertyBeanList = mutableListOf<PropertyBean?>()

    init {
        basicDataOnEditorActionPresenter?.attach(this)
    }

    /**
     *  初始化某项
     * @return
     */
    private fun initItem(bean: PropertyBean) {
        norMalScanAdapter.data.withIndex()
            .firstOrNull { bean.key == it.value?.key }?.apply {
                value?.apply {
                    value = ""
                    id = ""
                    code = ""
                    fStockFlexItem = null
                }
//                norMalScanAdapter.notifyItemChanged(index)
                updateAdapter(index)
            }
        /*val key = mutableListOf<String>().apply {
            add(bean.key)
            bean.key?.let {
                when (it) {
                    "FStockId" -> {
                        "FStockLocId.FF"
                    }

                    "FInStockId" -> {
                        "FInStockLocId.FF";
                    }

                    else -> {
                        it
                    }
                }
            }?.let { add(it) }
        }

        norMalScanAdapter.data.withIndex()
            .firstOrNull { key.contains(it.value?.key) || key.contains(bean.related) }?.apply {
                value?.apply {
                    value = bean.value
                    id = bean.id
                    fStockFlexItem = null
                }
                norMalScanAdapter.notifyItemChanged(index)
                updateAdapter(index)
            }*/
    }

    private var norMalScanAdapter: NorMalScanAdapter = NorMalScanAdapter().apply {
        isLockShow = false
        onEditorActionChangeListener =
            NorMalScanAdapter.OnEditorActionChangeListener { view, bean, position ->
                when (bean.type) {
                    "COMBOBOX", "RADIOBOX", "DATETIME", "CHECKBOX" -> {
                        focusMoveDown(position)
                    }

                    else -> {
                        if (!bean.value.isNullOrEmpty()) {
                            queryData(bean, position)
                        } else if (bean.value.isEmpty()) {
                            //空的情况，空回车，重置这个项的值，仓库的话要关联仓位重现处理 对比采集界面处理
                            initItem(bean)
                            focusMoveDown(position)
                        } else {
                            focusMoveDown(position)
                        }
                    }
                }
            }
        addChildClickViewIds(R.id.iv_base_info_query, R.id.tv_scan_lock, R.id.tv_scan_default)
        setOnItemChildClickListener { adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
            data[position]?.apply {
                if (view.id == R.id.iv_base_info_query) {
                    query(position, this)
                }
            }
        }
        checkChangeListener = NorMalScanAdapter.CheckChangeListener { bean, position, isCheck ->
            data[position]?.apply {
                value = isCheck.toString()
            }
        }
    }


    override fun addInnerContent() {
        binding = PopBatchFieldBinding.inflate(
            LayoutInflater.from(context), bottomPopupContainer, false
        )
        bottomPopupContainer.addView(binding.root)
    }

    private fun updateAdapter(position: Int) {
        mutableListOf<PropertyBean?>().also { list ->
            val flexData = norMalScanAdapter.data.filter { it?.related.isNullOrEmpty() }

            propertyBeanList.forEach { bean ->
                if (flexData.any { it?.key == bean?.key }) {
                    list.add(flexData.firstOrNull { it?.key == bean?.key }?.clone())
                } else if (flexData.any {
                        it?.key == bean?.related
                                && !it?.fStockFlexItem.isNullOrEmpty()
                                && it?.fStockFlexItem?.any { flex -> flex["flexId"].bigDecimalToZeros() == bean?.flexId.bigDecimalToZeros() } == true
                    }) {
                    if (norMalScanAdapter.data.any { it?.key == bean?.key }) {
                        list.add(norMalScanAdapter.data.firstOrNull { it?.key == bean?.key }
                            ?.clone())
                    } else {
                        list.add(bean?.clone())
                    }
                }
            }
        }.also {
            norMalScanAdapter.setNewInstance(it)
            norMalScanAdapter.focusMoveDown(position)
        }
    }

    override fun onCreate() {
        super.onCreate()
        propertyBeanList.apply {
            normalScanConfigBean.batchFields.forEach {
                add(PropertyBean(it.property.key, it.property.name).apply {
//                    isLock = true
                    type = it.property.type
                    tag = it.property.lookupId
                    entity = it.property.entity
                    entityId = it.property.entityId.bigDecimalToZeros()
//                    parentId = it.property.lookupId
                    source = it.property.source
                    enums = it.property.enums
                    related = it.property.related
                    flexId = it.property.flexId?.bigDecimalToZeros()
                    this.isEnable = true
                })
            }
        }.also {
            mutableListOf<PropertyBean?>().also { list ->
                it.filter { it?.related.isNullOrEmpty() }.forEach {
                    list.add(it)
                }
            }.also {
                norMalScanAdapter.setNewInstance(it)
            }
        }
        binding.apply {
            rvProperty.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = norMalScanAdapter
            }

            butSubmit.setOnClickListener {
                norMalScanAdapter.data.also {
                    /*if (it.any { it?.value.isNullOrEmpty() }) {
                        "请输入完整信息".showToast()
                        return@setOnClickListener
                    }*/
                    it.let {
                        mutableMapOf<String, Any>().apply {
                            it.filter { bean -> !(bean?.code ?: bean?.value).isNullOrEmpty() }
                                .forEach { bean ->
                                    bean?.key?.apply {
                                        put(this, bean.code ?: bean.value)
                                    }
                                }
                        }
                    }.also {
                        presenter.commandSubmitViewData(ViewReq("INVOKE_BATCHFILL", pageId).apply {
                            params = it
                        })
                        dismiss()
                    }
                }
            }
            butCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    /**
     * query 图标 点击
     *
     * @param position
     */
    private fun query(position: Int, bean: PropertyBean) {
        when (bean.type) {
            "FLEXVALUE",
            "ITEMCLASS",
            "BASEDATA", "ASSISTANT",
                -> {
                //辅助属性 要用parentId
                Intent(context, BasicDataActivity::class.java).apply {
                    putExtra("title", bean.name)
                    putExtra("lookupId", bean.tag)
                    putExtra("parentId", bean.parentId)
                    when (bean.type) {
                        "ASSISTANT" -> {
                            putExtra("lookupId", bean.tag)
                            putExtra("parentId", bean.parentId)
                        }

                        "ITEMCLASS" -> {
                            if (getRelatedId(bean).isEmpty()) {
                                putExtra("lookupId", getRelatedId(bean))
//                            if (!bean.parentId.isNullOrEmpty()) {
//                                putExtra("lookupId", bean.parentId)
                            } else {
                                //获取关联Related
                                "请先选择${getRelatedName(bean)}".showToast()
                            }
                        }

                        "BASEDATA" -> {
                            putExtra("lookupId", bean.tag)
                        }

                        "FLEXVALUE" -> {
                            if (getRelatedId(bean).isEmpty()) {
                                "请先选择${getRelatedName(bean)}".showToast()
                                return
                            } else {
                                putExtra("lookupId", bean.tag)
                                putExtra("parentId", getRelatedId(bean))
                                putExtra("flexId", bean.flexId)
                            }
                        }
                    }
                    putExtra("position", position)
                    launcher.launch(this)
                }
            }

            "DATETIME" -> {
                //日期选择
                initTimePick(position)
            }

            "COMBOBOX" -> {
                //下拉列表
                initComBox(position)
            }

            "CHECKBOX" -> {
                //已调整方式，这里不在使用
                //复选框    有是否选择 每个都是独立的互不影响
                initCheckBox(position)
            }

            "RADIOBOX" -> {
                //待调整
                //单选框   有是否选择，不独立，会相互影响，多个单选框只有一个是true,可以都是false
                initRadioBox(position)
            }
        }
    }

    private fun getRelatedId(bean: PropertyBean): String {
        return norMalScanAdapter.data.firstOrNull { bean.related == it?.key }.let {
            it?.id ?: ""
        }
    }

    private fun getRelatedName(bean: PropertyBean): String {
        return norMalScanAdapter.data.firstOrNull { bean.related == it?.key }.let {
            it?.name ?: ""
        }
    }

    private fun initTimePick(pos: Int) {
        CustomDatePicker(
            context,
            {
                DateFormatUtils.long2Str(it, false).apply {
                    norMalScanAdapter.data[pos]?.value = this
                    norMalScanAdapter.setEditTextValue(pos, this)
                }
            },
            DateFormatUtils.str2Long("1980-01-01", false),
            DateFormatUtils.str2Long("2100-01-01", false)
        ).apply {
            setCancelable(false)
            setCanShowPreciseTime(false)
            setScrollLoop(false)
            setCanShowAnim(false)
            show(System.currentTimeMillis())
        }

    }

    private fun initComBox(position: Int) {
        val enums = norMalScanAdapter.data[position]?.enums
        XPopup.Builder(context).isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .asBottomList(
                norMalScanAdapter.data[position]?.name ?: "",
                enums?.map { it["Name"] ?: "" }?.toTypedArray()
            ) { pos, text ->
                val key = norMalScanAdapter.data[position]?.key
                norMalScanAdapter.data.withIndex().filter { it.value?.related == key }.forEach {
                    val value = enums?.get(pos)?.get("Value")
                    if (value != it.value?.parentId) {
                        it.value?.parentId = value
                        it.value?.value = text
                        norMalScanAdapter.setEditTextValue(it.index, text)
                    }
                }
            }.show()
    }

    private fun initCheckBox(position: Int) {
        XPopup.Builder(context).isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .asBottomList(
                norMalScanAdapter.data[position]?.name ?: "", arrayOf("是", "否")
            ) { pos, text ->
                if (text.equals("是")) {
                    norMalScanAdapter.data[position]?.value = "true"
                    norMalScanAdapter.setEditTextValue(position, "true")
                } else {
                    norMalScanAdapter.data[position]?.value = "false"
                    norMalScanAdapter.setEditTextValue(position, "false")
                }
            }.show()
    }

    private fun initRadioBox(position: Int) {
        XPopup.Builder(context).isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
            .asBottomList(
                norMalScanAdapter.data[position]?.name ?: "", arrayOf("是", "否")
            ) { pos, text ->
                if (text.equals("是")) {
                    norMalScanAdapter.data[position]?.value = "true"
                    norMalScanAdapter.setEditTextValue(position, "true")
                } else {
                    norMalScanAdapter.data[position]?.value = "false"
                    norMalScanAdapter.setEditTextValue(position, "false")
                }
            }.show()
    }

    private fun queryData(bean: PropertyBean, position: Int) {
        var lookupId = ""
        val filtersReq = FiltersReq()
        val params = mutableMapOf<String, Any>()
        when (bean.type) {
            "DATETIME" -> {
                "日期只支持选择".showToast()
                return
            }

            "COMBOBOX" -> {
                "下拉类别类型只支持选择".showToast()
                return
            }

            "FLEXVALUE" -> {
                if (getRelatedId(bean).isEmpty()) {
                    "请先选择${getRelatedName(bean)}".showToast()
                    return
                }
                params["parentId"] = getRelatedId(bean)// bean.parentId
                params["flexId"] = bean.flexId
            }

            "ITEMCLASS" -> {
//                if (bean.parentId.isNullOrEmpty()) {
                if (getRelatedId(bean).isEmpty()) {
                    "请先选择${getRelatedName(bean)}".showToast()
                    return
                }
            }

            "BASEDATA" -> {

            }

            "ASSISTANT" -> {
                params["parentId"] = bean.parentId
            }

            else -> {
                when (bean.key) {
                    "FStockId", "FInStockId" -> {
                        //库位条码特殊处理，如何判断
                        params["primaryCode"] = bean.value
                        filtersReq.filters = params
                        val pkey = bean.key.let {
                            when (it) {
                                "FStockId" -> {
                                    "FStockLocId.FF"
                                }

                                "FInStockId" -> {
                                    "FInStockLocId.FF";
                                }

                                else -> {
                                    it
                                }
                            }
                        }
                        basicDataOnEditorActionPresenter?.queryBinCodeData(
                            scene,
                            filtersReq,
                            position,
                            pkey
                        )
                        return
                    }

                    else -> {
                        norMalScanAdapter.focusMoveDown(position)
                        return
                    }
                }
            }
        }
        lookupId = bean.tag
        params["primaryCode"] = bean.value
        filtersReq.filters = params
        basicDataOnEditorActionPresenter?.queryBasicData(scene, lookupId, filtersReq, position);
    }


    fun setUpdateEditTextValue(position: Int, infoBean: BaseInfoBean) {
        if (norMalScanAdapter.data.size <= position) return
        norMalScanAdapter.data[position]?.apply {
            this.id = infoBean.id
            this.code = infoBean.code
            this.value = infoBean.name
            this.fStockFlexItem = infoBean.fStockFlexItem
            norMalScanAdapter.notifyItemChanged(position)
        }
        updateAdapter(position)
        /*propertyBeanList.firstOrNull { it?.key == norMalScanAdapter.data[position]?.key }?.apply {
            this.id = infoBean.id
            this.value = infoBean.name
            this.fStockFlexItem = infoBean.fStockFlexItem
        }*/
    }

    override fun showFailedView(msg: String?) {
        msg?.showToast()
    }

    override fun showBasicDataList(list: List<BaseInfoBean?>?, postion: Int) {
        if (list.isNullOrEmpty()) {
            norMalScanAdapter.data[postion]?.apply {
                "$name:${value}找不到相应的数据".showToast()
                value = ""
            }
            norMalScanAdapter.setEditTextValue(postion, "")
        } else {
            list[0]?.apply {
                setUpdateEditTextValue(postion, this)
            }
        }
    }
}