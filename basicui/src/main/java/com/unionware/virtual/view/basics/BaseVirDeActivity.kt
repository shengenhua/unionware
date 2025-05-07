package com.unionware.virtual.view.basics

import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.chad.library.adapter4.BaseQuickAdapter
import com.unionware.basicui.R
import unionware.base.databinding.ActivityVirDetailsBinding
import com.unionware.virtual.view.BaseVirtualActivity
import com.unionware.virtual.view.adapter.ButtonAdapter
import com.unionware.virtual.view.adapter.CommonAdapter
import com.unionware.virtual.viewmodel.DetailsViewModel
import unionware.base.app.utils.ReflectUtils

/**
 * 详情数据
 */
open class BaseVirDeActivity<VM : DetailsViewModel> :
    BaseVirtualActivity<ActivityVirDetailsBinding, VM>() {


    @JvmField
    @Autowired(name = "entryId")
    var entryId: String = ""


    @JvmField
    @Autowired(name = "entryKey")
    var entryKey: String = ""

    override fun onBindVariableId(): MutableList<Pair<Int, Any>> {
        return mutableListOf()
    }

    protected open var heardAdapter: CommonAdapter? = CommonAdapter()
    private var buttonAdapter: ButtonAdapter? = ButtonAdapter()

    private var buttonClickArray: SparseArray<ButtonAdapter.OnButtonClickListener>? = null


    override fun initViewObservable() {
        mViewModel.viewLiveData.observe(this) {
            heardAdapter?.submitList(it)
        }
    }

    override fun onBindLayout(): Int {
        return R.layout.activity_vir_details
    }

    override fun initView() {
        setTitle(title)
        binding!!.run {
            rvDetailsHeard.layoutManager = LinearLayoutManager(mContext)
            rvDetailsHeard.adapter = heardAdapter
            rvDetailsTail.adapter = buttonAdapter
            rvDetailsTail.layoutManager = GridLayoutManager(mContext, 2)
            // 设置布局方向为从右到左
            rvDetailsTail.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }

        buttonAdapter?.let {
            it.addOnItemChildClickListener(R.id.acBtn) { adapter, view, position ->
                if (!buttonItemClick(adapter, view, position)) {
                    val item = adapter.getItem(position)
                    buttonClickArray?.get(item?.id!!)?.onClick(item.id!!)
                }
            }
        }
    }

    protected open fun buttonItemClick(
        adapter: BaseQuickAdapter<ButtonAdapter.AdapterButtonValue, *>, view: View, position: Int
    ): Boolean {
        return false
    }


    protected fun addButton(
        button: ButtonAdapter.AdapterButtonValue, onClick: ButtonAdapter.OnButtonClickListener
    ) {
        buttonClickArray = (buttonClickArray ?: SparseArray<ButtonAdapter.OnButtonClickListener>()).apply {
            button.id?.let { put(it, onClick) }
        }
//        buttonAdapter.notifyDataSetChanged()
        buttonAdapter?.let {
            it.add(button)
            binding?.rvDetailsTail?.layoutManager =
                GridLayoutManager(mContext, if (it.itemCount > 3) 3 else it.itemCount)
        }
    }

    protected fun addButton(vararg name: ButtonAdapter.AdapterButtonValue) {
        name.toList().forEach {
            buttonAdapter?.add(it)
        }
    }

    override fun initData() {
        mViewModel.query(
            scene,
            itemSearchId, mapOf(
                Pair("primaryId", primaryId),
                Pair("entryId", entryId),
                Pair("entryKey", entryKey)
            )
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewModel(): Class<VM> {
        return ReflectUtils.getActualTypeArgument(0, this.javaClass) as? Class<VM>
            ?: throw IllegalArgumentException("找不到 ViewModelClass 实例，建议重写该方法")
    }
}