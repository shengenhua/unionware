package com.unionware.once.adapter.dynamic

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseDifferAdapter
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.once.databinding.OnceAdapterDynamicEntryBinding
import com.unionware.virtual.view.adapter.virtual.VirtualViewAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import unionware.base.ext.tryBigDecimalToZeros
import unionware.base.model.bean.PropertyBean
import unionware.base.model.bean.barcode.DynamicEntryBean


/**
 * 单据体数据 适配器
 * Author: sheng
 * Date:2024/11/21
 */
class EntryCollectsAdapter(
    val activity: Activity,
    /**是否是子单据体*/
    private var haveSub: Boolean = false,
    /**是否需要删除按钮*/
    private var haveDelete: Boolean = true,
) : BaseDifferAdapter<DynamicEntryBean, EntryCollectsAdapter.EntryItemDataBinding>(object :
    DiffUtil.ItemCallback<DynamicEntryBean>() {
    override fun areItemsTheSame(
        oldItem: DynamicEntryBean,
        newItem: DynamicEntryBean,
    ): Boolean {
        return oldItem.tag == newItem.tag
    }

    override fun areContentsTheSame(
        oldItem: DynamicEntryBean,
        newItem: DynamicEntryBean,
    ): Boolean {
        val oldMap = oldItem.viewList?.associateBy { it.key }
        newItem.viewList?.forEach {
            if (oldMap?.containsKey(it.key) == false) {
                return false
            }
            val oldBean = oldMap?.get(it.key)
            if (oldBean?.value.tryBigDecimalToZeros() != it.value.tryBigDecimalToZeros()
                || oldBean?.valueName.tryBigDecimalToZeros() != it.valueName.tryBigDecimalToZeros()
                || oldBean?.valueNumber.tryBigDecimalToZeros() != it.valueNumber.tryBigDecimalToZeros()
                || oldBean?.isVisible != it.isVisible
                || oldBean.isEnable != it.isEnable
            ) {
                return false
            }
        }
        return true
    }
}) {
    private var changedUpdate = MutableStateFlow(false)

    init {
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                activity.currentFocus?.apply {
                    changedUpdate.value = true
                    clearFocus()
                    (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(windowToken, 0)

                    postDelayed({ changedUpdate.value = false }, 300)
                }
                super.onItemRangeChanged(positionStart, itemCount)
            }
        })
    }

    open class EntryItemDataBinding(binding: OnceAdapterDynamicEntryBinding) :
        DataBindingHolder<OnceAdapterDynamicEntryBinding>(binding) {
        var virtualViewAdapter: VirtualViewAdapter = VirtualViewAdapter()
    }

    fun setNewHaveSub(haveSub: Boolean) {
        if (this.haveSub != haveSub) {
            this.haveSub = haveSub
            notifyItemRangeChanged(0, itemCount)
        }
    }

    var entryUpdateValue: ((PropertyBean?, position: Int) -> Unit)? = null

    var queryUpdateValue: ((adapter: BaseQuickAdapter<PropertyBean, *>, PropertyBean?, position: Int, parentPosition: Int) -> Unit)? =
        null

    override fun onBindViewHolder(
        holder: EntryItemDataBinding,
        position: Int,
        item: DynamicEntryBean?,
    ) {
        holder.binding.apply {
            //            this.position = item?.tag
            this.position = (position + 1).toString()
            item?.viewList?.also {
                rvDetails.layoutManager =
                    if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        object : GridLayoutManager(context, 2) {
                            override fun canScrollVertically(): Boolean = true
                            override fun canScrollHorizontally(): Boolean = true
                        }
                    } else {
                        object : LinearLayoutManager(context) {
                            override fun canScrollVertically(): Boolean = false
                            override fun canScrollHorizontally(): Boolean = false
                        }
                    }
                rvDetails.adapter = holder.virtualViewAdapter.apply {
                    submitList(it)
                    rvDetails.postDelayed({
                        notifyItemRangeChanged(0, it.size)
                    }, 150)
                    setItemUpdateValue { bean ->
                        if (changedUpdate.value) {
                            changedUpdate.value = false
                            return@setItemUpdateValue
                        }
                        when (bean?.type) {
                            "COMBOX", "COMBOBOX" -> {

                            }

                            else -> {
                                this@EntryCollectsAdapter.items[position].viewList?.firstOrNull { it.key == bean?.key }
                                    ?.apply {
                                        value = bean?.value
                                    }
                            }
                        }
                        entryUpdateValue?.invoke(bean, position)
                    }
                    itemFocus = {
                        if (it) {
                            activity.currentFocus?.clearFocus()
                        }
                    }
                    queryItemListener = { bean, pos ->
                        queryUpdateValue?.invoke(this, bean, pos, position)
                    }
                    setOnItemClickListener { _, _, _ ->
                    }
                }
            }
            tvSub.visibility = if (haveSub) ViewGroup.VISIBLE else ViewGroup.INVISIBLE
            tvDelete.visibility = if (haveDelete) ViewGroup.VISIBLE else ViewGroup.INVISIBLE
        }
    }

    fun focusView(position: Int) {
        getViewBinding(position)?.apply {
            this.virtualViewAdapter.setCursor()
        }
    }

    private fun getViewBinding(position: Int): EntryItemDataBinding? {
        if (recyclerView.findViewHolderForLayoutPosition(position) == null) {
            return null
        }
        return recyclerView.findViewHolderForLayoutPosition(position) as EntryItemDataBinding
    }


    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): EntryItemDataBinding {
        return EntryItemDataBinding(
            OnceAdapterDynamicEntryBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}