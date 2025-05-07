package com.unionware.emes.adapter.barcode

import android.content.Context
import android.text.TextWatcher
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.widget.addTextChangedListener
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.emes.databinding.AdapterBarCodeBinding
import unionware.base.model.bean.CollectMultiItem
import unionware.base.model.bean.barcode.BarCodeBean


class BarCodeAdapter : BaseQuickAdapter<BarCodeBean, BarCodeAdapter.BarCodeDataBinding>() {
    init {
//        setItemAnimation(AnimationType.ScaleIn)
    }

    open class BarCodeDataBinding(binding: AdapterBarCodeBinding) :
        DataBindingHolder<AdapterBarCodeBinding>(binding) {

        var textWatcher: TextWatcher? = null
    }


    override fun onBindViewHolder(
        holder: BarCodeDataBinding, position: Int, item: BarCodeBean?,
    ) {
        holder.binding.apply {
            this.item = item

            this.clFeature.item = item

            this.clFeature.also { it ->
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
        }
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): BarCodeDataBinding {
        return BarCodeDataBinding(
            AdapterBarCodeBinding.inflate(
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