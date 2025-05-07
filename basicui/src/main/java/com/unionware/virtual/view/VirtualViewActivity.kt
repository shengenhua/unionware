package com.unionware.virtual.view

import android.annotation.SuppressLint
import androidx.databinding.ViewDataBinding
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.lxj.xpopup.XPopup
import com.unionware.virtual.viewmodel.VirtualViewModel
import unionware.base.model.bean.PropertyBean
import unionware.base.model.req.ItemBean
import unionware.base.model.req.ViewReq
import unionware.base.model.resp.AnalysisResp
import unionware.base.app.utils.ReflectUtils

abstract class VirtualViewActivity<V : ViewDataBinding, VM : VirtualViewModel> :
    BaseVirtualActivity<V, VM>() {

    @JvmField
    @Autowired(name = "billFormId")
    var billFormId: String = ""

    @JvmField
    @Autowired(name = "billId")
    var billId: String = ""

    @JvmField
    @Autowired(name = "billEntryKey")
    var billEntryKey: String = ""

    @JvmField
    @Autowired(name = "billEntryId")
    var billEntryId: String = ""

    override fun initBaseViewObservable() {
        super.initBaseViewObservable()
        mViewModel.virtualLiveEvent.viewDataLiveData.observe(this) {
            virtualView(it)
        }
        mViewModel.showErrorDialogViewEvent.observe(this) {
            XPopup.Builder(this).asConfirm("提示", it.errorMag) {
                it.viewReq?.apply {
                    when(simulate){
                        "Command"->{
                            mViewModel.commandViewData(this)
                        }
                        "UpdateValue"->{
                            mViewModel.updateVirtualView(this)
                        }
                    }
                }
            }.show()
        }
    }

    override fun onBindVariableId(): MutableList<Pair<Int, Any>> {
        return mutableListOf()
    }

    /**
     * 虚拟视图
     */
    abstract fun virtualView(view: List<PropertyBean>)

    /**
     * 虚拟视图 数据
     */
    abstract fun virtualViewData(data: AnalysisResp)

    /**
     * 更新 字段 设置数据
     * UnionWare.Basic.Simulate.UpdateValue
     */
    open fun updateView(key: String?, value: String?) {
        mViewModel.updateVirtualView(
            ViewReq(mViewModel.virtualLiveEvent.pageIdLiveData.value)
                .apply {
            items = listOf(ItemBean(key, value))
        })
    }

    /**
     * 确认操作
     * UnionWare.Basic.Simulate.Command
     * @param command 步骤
     */
    open fun command(command: String) {
        mViewModel.commandViewData(command)
    }

    override fun initView() {
        super.initView()
        mViewModel.virtualLiveEvent.configLiveData.value = mutableMapOf(
            Pair("appSetId", id),
            Pair("formId", getFromId())
        ).apply {
            if (billFormId.isNotEmpty()) {
                put("billFormId", billFormId)
            }
            if (billId.isNotEmpty()) {
                put("billId", billId)
            }
            if (billEntryKey.isNotEmpty()) {
                put("billEntryKey", billEntryKey)
            }
            if (billEntryId.isNotEmpty()) {
                put("billEntryId", billEntryId)
            }
        }
//        mViewModel.confirmViewData()
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        mViewModel.apply {
            virtualLiveEvent.pageIdLiveData.value.let {
                if (it.isNullOrEmpty()) {
                    postFinishActivityEvent()
                    return@onBackPressed
                }
                it
            }.apply {
                closeVirtualView(this)
            }
        }
    }

    abstract fun getFromId(): String

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewModel(): Class<VM> {
        return ReflectUtils.getActualTypeArgument(1, this.javaClass) as? Class<VM>
            ?: throw IllegalArgumentException("找不到 ViewModelClass 实例，建议重写该方法")
    }
}