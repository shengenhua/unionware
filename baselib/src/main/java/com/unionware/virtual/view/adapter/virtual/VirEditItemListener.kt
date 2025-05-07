package com.unionware.virtual.view.adapter.virtual

import android.content.Context
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.R
import unionware.base.databinding.AdapterVirtualEditBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import unionware.base.model.bean.PropertyBean
import unionware.base.ext.bigDecimalToZeros
import unionware.base.ext.tryBigDecimalToZeros

class VirEditItemListener(private val isLockShow: Boolean = false) :
    BaseMultiItemAdapter.OnMultiItem<PropertyBean, VirEditItemListener.EditItemDataBinding>() {

    open class EditItemDataBinding(binding: AdapterVirtualEditBinding) :
        DataBindingHolder<AdapterVirtualEditBinding>(binding) {
        var textWatcher: TextWatcher? = null
    }

    init {
        MainScope().launch {
            focusFlow.observeForever {
                getEditBinding(it)?.apply {
                    this.binding.etScanInput.visibility = View.VISIBLE
                    this.binding.tvValueName.visibility = View.INVISIBLE
                    this.binding.etScanInput.apply {
                        if (requestFocus()) {
                            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                                this,
                                0
                            )
                        }
                    }
                }
            }
        }
    }

    private var focusFlow = MutableLiveData(-1)

    fun focusPosition(position: Int) {
        focusFlow.postValue(position)
    }


    private fun getEditBinding(position: Int): EditItemDataBinding? {
        val binding = adapter?.recyclerView?.findViewHolderForLayoutPosition(position)
        if (binding == null || binding !is EditItemDataBinding) {
            return null
        }
        return try {
            binding
        } catch (e: Exception) {
            null
        }
    }


    /**
     * 选项回车事件的回调接口
     */
    var onEditorActionChangeListener: ((view: EditText?, bean: PropertyBean?, position: Int) -> Unit)? =
        null

    private val baseQuery = setOf(/*基础资料*/"BASEDATA",/*辅助资料*/"ASSISTANT",/**/"FLEXVALUE")
    private val garCodeGroup = setOf("FBarCodeId_Proxy", "FBoxCodeId_Proxy", "FNewCodeId")
    override fun onBind(holder: EditItemDataBinding, position: Int, bean: PropertyBean?) {
        val item = bean?.clone()
        holder.binding.apply {
            updateItem(item)
            item?.also {
                ivQuery.visibility =
                    if (baseQuery.contains(it.type) && !garCodeGroup.contains(it.key)) View.VISIBLE else View.GONE
                ivScanLock.visibility = if (isLockShow) View.VISIBLE else View.GONE
            }
            ivDelete.apply {
                setOnClickListener {
                    etScanInput.apply {
                        setText("")
                        if (isFocused) {
                            clearFocus()
                        } else {
                            onEditorActionChangeListener?.invoke(
                                etScanInput,
                                item,
                                holder.layoutPosition
                            )
                        }
                    }
                }
            }
            tvValueNameUpdate(item)
            tvValueName.setOnClickListener {
                tvValueName.visibility = View.INVISIBLE
                etScanInput.visibility = View.VISIBLE
                if (etScanInput.requestFocus()) {
                    (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                        etScanInput,
                        0
                    )
                }
            }

            etScanInput.apply {
                clearFocus()
                (item?.isEnable == true && !item.isLock).apply { // 判断是否可以编辑
                    isEnabled = this
                    //不能编辑需要修改的东西
                    ivScanLock.setImageResource(if (!this) R.drawable.dw_lock else R.drawable.dw_unlock)
                    //隐藏删除和查询
                    ivDelete.visibility = if (this) View.VISIBLE else View.GONE
                    ivQuery.also { iv ->
                        if (!this && iv.visibility == View.VISIBLE) {
                            iv.visibility = View.GONE
                        }
                    }
                    //不能编辑需要修改的东西
                    hint = if (this) item?.name else ""
                    if (this) {
                        tvValueName.visibility = View.VISIBLE
                        etScanInput.visibility = View.INVISIBLE
                    } else {
                        tvValueName.visibility = View.INVISIBLE
                        etScanInput.visibility = View.VISIBLE
                    }
                }

                inputType = when (item?.type) {
                    "INTEGER" -> EditorInfo.TYPE_CLASS_NUMBER
                    "DECIMAL" -> EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
                    else -> EditorInfo.TYPE_CLASS_TEXT
                }

                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        tvValueName.visibility = View.INVISIBLE
                        etScanInput.visibility = View.VISIBLE
                    } else {
                        tvValueName.visibility = View.VISIBLE
                        etScanInput.visibility = View.INVISIBLE
                    }
                    if (!hasFocus) {
                        if (item?.value?.tryBigDecimalToZeros() ==
                            bean?.value?.tryBigDecimalToZeros()
                        ) {
                            return@setOnFocusChangeListener
                        }
                        onEditorActionChangeListener?.invoke(this, item, holder.layoutPosition)
                        tvValueNameUpdate(item)
                    }
                }
                setOnEditorActionListener { _, i, keyEvent ->
                    if (!isFastClick()) {
                        if (i == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) {
                            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                                onEditorActionChangeListener?.invoke(
                                    this, item, holder.layoutPosition
                                )
                            }
                        } else {
                            onEditorActionChangeListener?.invoke(this, item, holder.layoutPosition)
                        }
                        tvValueNameUpdate(item)
                    }
                    true
                }

                /*holder.textWatcher?.also {
                    removeTextChangedListener(it)
                }
                holder.textWatcher = addTextChangedListener {
//                        item?.value = text.toString()
                }*/
            }
        }
    }

    private fun AdapterVirtualEditBinding.tvValueNameUpdate(item: PropertyBean?) {
        when (item?.display) {
            "Name" -> {
                tvValueName.text = item.valueName
            }

            "Number" -> {
                tvValueName.text = item.valueNumber
            }

            else -> {
                tvValueName.text = item?.value
            }
        }
    }

    private fun AdapterVirtualEditBinding.updateItem(item: PropertyBean?) {
        item?.apply {
            value = when (type) {
                "INTEGER", "DECIMAL" -> {
                    value?.bigDecimalToZeros()
                }

                else -> value?.trim()?.tryBigDecimalToZeros()
            }
        }
        this.item = item
    }


    companion object {
        var lastClickTime: Long = 0
        const val MIN_DELAY_TIME: Int = 200
    }

    private fun isFastClick(): Boolean {
        val currentClickTime: Long = System.currentTimeMillis()
        return if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            false
        } else {
            lastClickTime = currentClickTime
            true
        }
    }

    override fun onCreate(
        context: Context, parent: ViewGroup, viewType: Int,
    ): EditItemDataBinding {
        return EditItemDataBinding(
            AdapterVirtualEditBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}