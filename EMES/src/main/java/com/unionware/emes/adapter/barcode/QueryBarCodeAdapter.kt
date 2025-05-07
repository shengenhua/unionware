package com.unionware.emes.adapter.barcode

import android.content.Context
import android.text.TextWatcher
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.emes.R
import com.unionware.emes.databinding.AdapterQueryBarCodeBinding
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.barcode.QueryBarCodeBean

class QueryBarCodeAdapter :
    BaseQuickAdapter<QueryBarCodeBean, QueryBarCodeAdapter.QueryBarCodeDataBinding>() {
    open class QueryBarCodeDataBinding(binding: AdapterQueryBarCodeBinding) :
        DataBindingHolder<AdapterQueryBarCodeBinding>(binding) {

        var textWatcher: TextWatcher? = null
    }

    override fun onBindViewHolder(
        holder: QueryBarCodeDataBinding,
        position: Int,
        item: QueryBarCodeBean?,
    ) {
        holder.binding.also { bind ->
            bind.item = item
            bind.clFeature.item = item

            bind.clFeature.also { it ->
                it.etSumInput.apply {
                    holder.textWatcher?.also {
                        removeTextChangedListener(it)
                    }
                    it.ivCheckDelete.setOnClickListener {
                        this.setText("1")
                        item?.qty = "1"
                    }
                    /*if (item?.qty.equals("1")) {
                        this.isEnabled = false
                        it.ivCheckDelete.visibility = View.GONE
                    } else {
                        this.isEnabled = true
                        it.ivCheckDelete.visibility = View.VISIBLE
                    }*/
                    holder.textWatcher = addTextChangedListener {
                        item?.qty = it.toString()
                    }
                    setOnEditorActionListener { v, actionId, event ->
                        return@setOnEditorActionListener true
                    }
                }
            }
            bind.tvQuery.setCompoundDrawables(
                null, null, if (item?.showQuery == true) {
                    ContextCompat.getDrawable(context, unionware.base.R.drawable.ic_query)
                } else {
                    null
                }, null
            );
//                if (item?.agingName == null) "请选择老化架" else "架号：${item.agingName}"
        }

    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): QueryBarCodeDataBinding {
        return QueryBarCodeDataBinding(
            AdapterQueryBarCodeBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    private var mOnItemOnItemClickArray: SparseArray<OnItemChildClickListener<CollectMultiItem>>? =
        null

    fun addOnItemOnItemClickListener(
        @IdRes id: Int,
        listener: OnItemChildClickListener<CollectMultiItem>,
    ) = apply {
        mOnItemOnItemClickArray =
            (mOnItemOnItemClickArray
                ?: SparseArray<OnItemChildClickListener<CollectMultiItem>>(2)).apply {
                put(id, listener)
            }
    }
}