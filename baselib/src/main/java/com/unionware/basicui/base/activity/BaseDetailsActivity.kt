package com.unionware.basicui.base.activity

import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.chad.library.adapter4.BaseQuickAdapter
import unionware.base.R
import com.unionware.basicui.app.BasicBaseActivity
import com.unionware.basicui.base.viewmodel.BaseDetailsViewModel
import unionware.base.databinding.ActivityBaseDetailsBinding
import com.unionware.virtual.view.adapter.ButtonAdapter
import com.unionware.virtual.view.adapter.CommonAdapter
import unionware.base.app.utils.ReflectUtils

/**
 * 详情数据
 */
open class BaseDetailsActivity<VM : BaseDetailsViewModel> :
    BasicBaseActivity<ActivityBaseDetailsBinding, VM>() {

    @JvmField
    @Autowired(name = "primaryId")
    var primaryId: String = ""

    @JvmField
    @Autowired(name = "id")
    var id: String = ""

    /*@JvmField
    @Autowired(name = "itemSearchId")
    var schema: String = ""*/

    override fun onBindVariableId(): MutableList<Pair<Int, Any>> {
        return mutableListOf()
    }

    fun interface OnButtonClickListener {
        fun onClick(id: Int)
    }

    protected open var heardAdapter: CommonAdapter? = CommonAdapter()
    open var buttonAdapter: ButtonAdapter? = ButtonAdapter()

    private var buttonClickArray: SparseArray<OnButtonClickListener>? = null


    override fun initViewObservable() {
        mViewModel.viewLiveData.observe(this) {
            heardAdapter?.submitList(it)
        }
    }

    override fun onBindLayout(): Int {
        return R.layout.activity_base_details
    }

    override fun initView() {
//        setTitle("执行单号(${title})")
        setTitle(title)
        binding!!.run {
            rvDetailsHeard.layoutManager = LinearLayoutManager(mContext)
            rvDetailsHeard.adapter = heardAdapter
            tailAdapter()?.also {
                rvDetailsTail.adapter = it
                rvDetailsTail.layoutManager = tailLayoutManager()
            }
            bottomAdapter()?.also {
                rvDetailsBottom.adapter = it
                rvDetailsBottom.layoutManager = bottomLayoutManager()
                // 设置布局方向为从右到左
                rvDetailsBottom.layoutDirection = View.LAYOUT_DIRECTION_RTL
            }
        }

    }

    protected open fun bottomAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>? {
        return buttonAdapter?.let {
            it.addOnItemChildClickListener(R.id.acBtn) { adapter, view, position ->
                if (!buttonItemClick(adapter, view, position)) {
                    val item = adapter.getItem(position)
                    buttonClickArray?.get(item?.id ?: -1)?.onClick(item?.id ?: -1)
                }
            }
        }
    }

    protected open fun bottomLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(this, 6).apply { // 分成 6 份
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (itemCount >= 3 && position < itemCount - (itemCount % 3)) {
                        return 2 // 每个数据 占据 2 个 span
                    } else if ((itemCount % 3) == 2) {
                        return 3 //如果还剩 2 个数据， 每个数据占据 3 个span
                    } else {
                        return 6 // 一个数据占据 6 个span
                    }
                }
            }
        }
    }

    protected open fun tailLayoutManager(): RecyclerView.LayoutManager {
        /*return GridLayoutManager(this, 3).apply { // 分成 6 份
            *//*spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (itemCount >= 3 && position < itemCount - (itemCount % 3)) {
                        return 2 // 每个数据 占据 2 个 span
                    } else if ((itemCount % 3) == 2) {
                        return 3 //如果还剩 2 个数据， 每个数据占据 3 个span
                    } else {
                        return 6 // 一个数据占据 6 个span
                    }
                }
            }*//*
        }*/
        return LinearLayoutManager(this)
    }

    protected open fun tailAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>? {
        return null
    }

    protected open fun buttonItemClick(
        adapter: BaseQuickAdapter<ButtonAdapter.AdapterButtonValue, *>,
        view: View,
        position: Int,
    ): Boolean {
        return false
    }

    protected fun revampButtonText(id: Int, text: String) {
        revampButtonText(id, text, null)
    }

    protected fun revampButtonText(id: Int, text: String, onClick: OnButtonClickListener?) {
        buttonAdapter?.items?.withIndex()?.forEach {
            if (it.value.id == id) {
                it.value.text = text
                buttonAdapter?.notifyItemChanged(it.index)
                onClick?.also {
                    buttonClickArray?.put(id, onClick)
                }
                return@forEach
            }
        }
    }

    protected fun addButton(text: String, onClick: OnButtonClickListener) {
        addButton(
            ButtonAdapter.AdapterButtonValue(buttonAdapter?.itemCount ?: 0, text), onClick
        )
    }

    protected fun addButton(
        button: ButtonAdapter.AdapterButtonValue,
        onClick: OnButtonClickListener,
    ) {
        buttonAdapter?.items?.firstOrNull { it.id == button.id }?.also {
            return
        }
        if (buttonClickArray == null) {
            SparseArray<OnButtonClickListener>().also { this.buttonClickArray = it }
        }
        buttonClickArray?.put(button.id, onClick)
        buttonAdapter?.also {
            it.add(button)
        }
    }

    protected fun addButton(vararg name: ButtonAdapter.AdapterButtonValue) {
        name.toList().forEach {
            buttonAdapter?.add(it)
        }
    }

    override fun initData() {
//        mViewModel.query(schema, id)
        mViewModel.query(scene = scene, primaryId = id)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewModel(): Class<VM> {
        return ReflectUtils.getActualTypeArgument(0, this.javaClass) as? Class<VM>
            ?: throw IllegalArgumentException("找不到 ViewModelClass 实例，建议重写该方法")
    }
}