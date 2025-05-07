package com.unionware.emes.view.process

import android.util.SparseArray
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unionware.basicui.base.activity.BaseProcessActivity
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.emes.R
import com.unionware.mes.adapter.HeardScanAdapter
import com.unionware.emes.bean.SubJobsBean
import com.unionware.emes.viewmodel.process.ProcessViewModel
import com.unionware.mes.adapter.SelectAdapter
import com.unionware.virtual.view.adapter.ButtonAdapter
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.ext.showToast
import unionware.base.model.SelectBean
import unionware.base.model.req.FiltersReq
import unionware.base.model.req.ReportReq

/**
 * 包装作业，包装工序
 * Author: sheng
 * Date:2024/9/18
 */
@AndroidEntryPoint
class PackProcessActivity : BaseProcessActivity<ProcessViewModel>() {

    /**
     * 附加工序
     */
    @JvmField
    @Autowired(name = "FSubJobs")
    var FSubJobs: String = ""


    fun interface OnButtonClickListener {
        fun onClick(id: Int)
    }

    private var buttonClickArray: SparseArray<OnButtonClickListener>? = null

    /**
     * 填写资料
     */
    var processAdapter: ProcessAdapter? = null

    /**
     * 扫描框
     */
    var scanAdapter: HeardScanAdapter? = null

    /**
     * 勾选的 附加工序
     */
    private var selectAdapter: SelectAdapter<SubJobsBean>? = null

    /**
     * 底部按钮
     */
    private var buttonAdapter: ButtonAdapter? = ButtonAdapter()

    private var fJobList: List<SubJobsBean>? = null
    private var selectList: List<SelectBean<SubJobsBean>>? = null

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel.apply {
            submitLiveData.observe(this@PackProcessActivity) {
                if (isPdaContinuousReport) {
                    //连续上报
                    selectAdapter?.submitList(null)
                    barcodeItemCountLiveData.value = 0
                    scanAdapter?.notifyItemChanged(0)
                    barcodeLiveData.value = null
                } else {
                    postFinishShowToast("提交成功")
                }
            }
            barcodeLiveData.observe(this@PackProcessActivity) { it ->
                it?.apply {
                    selectAdapter?.submitList(selectList?.onEach {
                        it.isCheck = false
                    })
                }
            }
        }
    }

    override fun initData() {
        fJobList = Gson().fromJson<List<SubJobsBean>?>(
            FSubJobs,
            object : TypeToken<List<SubJobsBean?>?>() {}.type
        )?.filter { it.id != 0 } ?: mutableListOf()

        selectList = fJobList?.filter {
            it.isExecute != "0"
        }?.map {
            SelectBean<SubJobsBean>().apply {
                conntent = it.name
                bean = it
            }
        }
    }

    override fun initView() {
        super.initView()
        binding?.actvScanSum?.visibility = View.GONE
        scanAdapter?.setOnEditorActionListener { it ->
            if (it.isEmpty()) {
                selectAdapter?.submitList(null)
                mViewModel.barcodeLiveData.value = null
                return@setOnEditorActionListener
            }
            mViewModel.queryBarcode(queryFilters(it), scene)

            currentFocus?.also {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }

        binding?.rvBottom?.apply {
            adapter = buttonAdapter
            layoutManager = GridLayoutManager(mContext, 2)
        }

        buttonAdapter?.let {
            it.addOnItemChildClickListener(R.id.acBtn) { adapter, view, position ->
                val item = adapter.getItem(position)
                buttonClickArray?.get(item?.id ?: -1)?.onClick(item?.id ?: -1)
            }
        }

        addButton(ButtonAdapter.AdapterButtonValue(0, "提交")) {

            selectAdapter?.items?.filter { !it.isCheck }?.map {
                "存在未勾选附加工序".showToast()
                return@addButton
            }
            val items = getItems()
            if (mViewModel.barcodeLiveData.value?.code.isNullOrEmpty()) {
                "请扫描条码".showToast()
                return@addButton
            }
            if (items.isNullOrEmpty()) {
                "未勾选附加工序".showToast()
                return@addButton
            }
            mViewModel.submitReport(ReportReq().apply {
                data = mutableListOf(ReportReq.DataReq().apply {
                    jobId = this@PackProcessActivity.jobId
                    taskId = this@PackProcessActivity.taskId
                    params = HashMap<String, Any>().apply {
                        putAll(mapOf(Pair("items", items as Any)))
                        put("barcode", mViewModel.barcodeLiveData.value?.code ?: "")
                    }
                })
            })
        }
    }


    private fun addButton(
        button: ButtonAdapter.AdapterButtonValue,
        onClick: OnButtonClickListener,
    ) {
        if (buttonClickArray == null) {
            SparseArray<OnButtonClickListener>().also { this.buttonClickArray = it }
        }
        buttonClickArray?.put(button.id, onClick)
        buttonAdapter?.also {
            it.add(button)
            binding?.rvBottom?.layoutManager =
                GridLayoutManager(mContext, if (it.itemCount > 3) 2 else it.itemCount)
        }
    }

    /**
     * 获取条码列表中的 数据
     */
    fun getItems(): List<Map<String, String?>>? {
        return selectAdapter?.items?.filter { it.isCheck }?.map {
            mapOf(Pair("jobId", it.bean?.id.toString()))
        }
    }

    /**
     * 扫描条码 上报的数据
     */
    fun queryFilters(barcode: String): FiltersReq = FiltersReq(
        mapOf(
            Pair("primaryCode", barcode), Pair("taskId", taskId)
        )
    )


    override fun heardProcessAdapter(): ProcessAdapter {
        return super.heardProcessAdapter().apply { processAdapter = this }
    }

    override fun middleAdapter(): RecyclerView.Adapter<ViewHolder>? {
        scanAdapter = HeardScanAdapter()
        return scanAdapter
    }

    override fun tailAdapter(): RecyclerView.Adapter<ViewHolder> {
        selectAdapter = selectAdapter ?: SelectAdapter<SubJobsBean>().apply {
            setOnItemClickListener { adapter, view, position ->
                adapter.items[position].also {
                    it.isCheck = !it.isCheck
                }
                adapter.notifyItemChanged(position)
            }
        }
        return selectAdapter as SelectAdapter<SubJobsBean>
    }

    override fun initBaseViewObservable() {
        super.initBaseViewObservable()
        mViewModel.barcodeELiveData.observe(this) {
            scanAdapter?.notifyItemChanged(0)
        }
    }

    override fun topAdapter(): RecyclerView.Adapter<ViewHolder>? = null
    override fun enableToolBarMenu(): Boolean = false
}