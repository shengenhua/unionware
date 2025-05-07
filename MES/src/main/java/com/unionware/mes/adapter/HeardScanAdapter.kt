package com.unionware.mes.adapter

import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.mes.databinding.AdapterMesScanHeardBinding
import unionware.base.model.bean.barcode.BarCodeBean

class HeardScanAdapter(
    item: BarCodeBean = BarCodeBean(""),
    private var firstFocusable: Boolean = true,
) :
    BaseSingleItemAdapter<BarCodeBean, HeardScanAdapter.HeardScanDataBinding>(item) {

    open class HeardScanDataBinding(binding: AdapterMesScanHeardBinding) :
        DataBindingHolder<AdapterMesScanHeardBinding>(binding) {
        var textWatcher: TextWatcher? = null
    }

    private var onEditorActionListener: OnEditorActionListener? = null

    override fun onBindViewHolder(
        holder: HeardScanDataBinding,
        item: BarCodeBean?,
    ) {
        holder.binding.also { bind ->
            bind.etBarCodeInput.apply {
                setOnEditorActionListener { v, actionId, event ->
                    item?.code = v.text.toString()
                    onEditorActionListener?.onEditor(v.text.toString())
                    return@setOnEditorActionListener false
                }
                setOnClearListener {
//                onEditorActionListener?.onEditor("")
                }
                /*holder.textWatcher?.also {
                    removeTextChangedListener(it)
                }
                holder.textWatcher = addTextChangedListener {
                    this.selectAll()
                }*/
            }
            bind.etBarCodeInput.apply {
                item?.code = ""
                setText("")
                if (firstFocusable) {
                    isFocusable = true
                    isFocusableInTouchMode = true
                    requestFocus()
                    hideSoftInput(this)
                }
                firstFocusable = true
            }
        }
    }

    private fun hideSoftInput(view: View) {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun setOnEditorActionListener(listener: OnEditorActionListener?) = apply {
        this.onEditorActionListener = listener
    }

    fun getBarCode(): String {
        return this.item?.code ?: ""
    }


    fun interface OnEditorActionListener {
        fun onEditor(value: String)
    }


    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): HeardScanDataBinding {
        return HeardScanDataBinding(
            AdapterMesScanHeardBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}