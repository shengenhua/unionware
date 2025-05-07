package com.unionware.once.adapter.dynamic

import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.lib_base.utils.ext.scaleAnimate
import com.unionware.lib_base.utils.ext.topToBotAnimate
import com.unionware.once.databinding.OnceAdapterDynamicScanBinding
import unionware.base.model.bean.barcode.BarCodeBean

/**
 * 单据体和子单据体 里面的 扫描框
 */
class ScanBarcodeAdapter(item: BarCodeBean = BarCodeBean("")) :
    BaseSingleItemAdapter<BarCodeBean, ScanBarcodeAdapter.HeardScanDataBinding>(item) {

    open class HeardScanDataBinding(binding: OnceAdapterDynamicScanBinding) :
        DataBindingHolder<OnceAdapterDynamicScanBinding>(binding) {
        var textWatcher: TextWatcher? = null
    }

    private var showQty: Boolean = false

    fun showUI(showCode: Boolean = false, showQty: Boolean = false, view: View? = null) {
        this.showQty = showQty
        if (showCode) {
            this.recyclerView.topToBotAnimate(View.VISIBLE)
//            this.recyclerView.expandAnimate(true)
            view?.scaleAnimate(View.GONE)
        } else {
            this.recyclerView.topToBotAnimate(View.GONE)
//            this.recyclerView.expandAnimate(false)
            view?.scaleAnimate(View.VISIBLE)
        }
        notifyItemChanged(0)
    }

    public fun isShow(): Boolean {
        return this.recyclerView.visibility == View.VISIBLE
    }

    private var onEditorActionListener: OnEditorActionListener? = null

    override fun onBindViewHolder(
        holder: HeardScanDataBinding,
        item: BarCodeBean?,
    ) {
        holder.binding.also { bind ->
            bind.etBarCodeInput.apply {
                setText(item?.code ?: "")
                setOnEditorActionListener { v, actionId, event ->
                    item?.code = v.text.toString()
                    onEditorActionListener?.onEditor(v.text.toString(), this.tag.toString())
                    return@setOnEditorActionListener false
                }
                setOnClearListener {}
            }

            bind.etQty.apply {
                setText(item?.qty ?: "")
                visibility = if (showQty) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                setOnEditorActionListener { v, actionId, event ->
                    item?.qty = v.text.toString()
                    onEditorActionListener?.onEditor(v.text.toString(), this.tag.toString())
                    return@setOnEditorActionListener false
                }
            }
            if (item?.code.isNullOrEmpty()) {
                bind.etBarCodeInput.requestFocus()
            } else if (item?.qty.isNullOrEmpty()) {
                bind.etQty.requestFocus()
            } else {
                bind.etBarCodeInput.requestFocus()
            }
        }
    }

    private fun getViewBinding(position: Int): HeardScanDataBinding? {
        if (recyclerView.findViewHolderForLayoutPosition(position) == null) {
            return null
        }
        return recyclerView.findViewHolderForLayoutPosition(position) as HeardScanDataBinding
    }

    fun setOnEditorActionListener(listener: OnEditorActionListener?) = apply {
        this.onEditorActionListener = listener
    }

    fun update(barcode: String? = null, barQty: String? = null) {
        barcode.also {
            this.item?.code = it ?: ""
        }
        barQty.also {
            this.item?.qty = it ?: ""
        }
        getViewBinding(0)?.binding?.apply {
            this@ScanBarcodeAdapter.item?.also {
                etBarCodeInput.setText(it.code)
                etQty.setText(it.qty)
                if (it.code.isEmpty()) {
                    etBarCodeInput.requestFocus()
                } else if (it.qty.isNullOrEmpty()) {
                    etQty.requestFocus()
                } else {
                    etBarCodeInput.requestFocus()
                }
            }
        }
    }

    fun getBarCode(): String {
        return this.item?.code ?: ""
    }

    fun getItem(): BarCodeBean? {
        return this.item
    }


    fun interface OnEditorActionListener {
        fun onEditor(value: String, tag: String)
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): HeardScanDataBinding {
        return HeardScanDataBinding(
            OnceAdapterDynamicScanBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}