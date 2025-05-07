package com.unionware.query.adapter

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.query.databinding.AdapterQueryTopBinding
import unionware.base.model.bean.PropertyBean

/**
 * Author: sheng
 * Date:2025/3/6
 */
class QueryTopAdapter :
    BaseSingleItemAdapter<String, QueryTopAdapter.QueryTopDataBinding>("") {

    open class QueryTopDataBinding(binding: AdapterQueryTopBinding) :
        DataBindingHolder<AdapterQueryTopBinding>(binding)

    private var queryValue: String? = ""
    var openState: Boolean = true
        set(value) {
            stateUpdate(value)
            field = value
            onOpenStateListener?.invoke(field)
        }

    private var clickArrowActionListener: OnClickArrowActionListener? = null

    /**
     * 选项回车事件的回调接口
     */
    var onEditorActionChangeListener: ((text: String, key: String, position: Int) -> Unit)? =
        null


    /**
     * 扫码条码点击
     */
    var onQcScanListener: (() -> Unit)? = null

    /**
     * 扫码条码点击
     */
    var onOpenStateListener: ((openState: Boolean) -> Unit)? = null

    fun onClickActionListener(listener: OnClickArrowActionListener) = apply {
        clickArrowActionListener = listener
    }

    override fun onBindViewHolder(
        holder: QueryTopDataBinding,
        item: String?,
    ) {
        holder.binding.also { bind ->
            bind.tvScan.setOnClickListener {
                onQcScanListener?.invoke()
            }
            bind.clInput.setOnClickListener {
                clickArrowActionListener?.onArrowClick(
                    this@QueryTopAdapter
                )
            }
            bind.clFeature.setOnClickListener {
                clickArrowActionListener?.onArrowClick(
                    this@QueryTopAdapter
                )
            }
            bind.etValue.apply {
                setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus && this.text.toString() != queryValue) {
                        onEditorActionChangeListener?.invoke(
                            this.text.toString(),
                            this.tag.toString(),
                            holder.layoutPosition
                        )
                    }
                }
                setOnEditorActionListener { _, i, keyEvent ->
                    if (isFastClick()) {
                        if (i == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)) {
                            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                                onEditorActionChangeListener?.invoke(
                                    this.text.toString(),
                                    this.tag.toString(),
                                    holder.layoutPosition
                                )
                            }
                        } else {
                            onEditorActionChangeListener?.invoke(
                                this.text.toString(),
                                this.tag.toString(),
                                holder.layoutPosition
                            )
                        }
                    }
                    true
                }
            }

            if (openState) {
                bind.ivArrowDown.animate().rotation(-180f)
            } else {
                bind.ivArrowDown.animate().rotation(0f)
            }
        }
    }

    private fun stateUpdate(state: Boolean) {
        getViewBinding(0)?.also {
            if (state) {
                it.binding.ivArrowDown.animate().rotation(-180f)
            } else {
                it.binding.ivArrowDown.animate().rotation(0f)
            }
        }
    }

    public fun updateValue(bean: PropertyBean) {
        getViewBinding(0)?.also {
            queryValue = bean.value
            it.binding.etValue.setText(bean.value)
            it.binding.etValue.setSelection(
                bean.value?.length ?: 0
            )
        }
    }

    fun getViewBinding(position: Int): QueryTopDataBinding? {
        val binding = recyclerView.findViewHolderForLayoutPosition(position)
        if (binding == null || binding !is QueryTopDataBinding) {
            return null
        }
        return binding
//        return recyclerView.findViewHolderForLayoutPosition(position) as DataBindingHolder<AdapterQueryTopBinding>
    }


    companion object {
        var lastClickTime: Long = 0L
        const val MIN_DELAY_TIME: Int = 200
    }

    private fun isFastClick(): Boolean {
        val currentClickTime: Long = System.currentTimeMillis()
        return if ((lastClickTime - currentClickTime) >= MIN_DELAY_TIME) {
            false
        } else {
            lastClickTime = currentClickTime
            true
        }
    }

    fun interface OnClickArrowActionListener {
        fun onArrowClick(adapter: BaseSingleItemAdapter<String, *>)
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): QueryTopDataBinding {
        return QueryTopDataBinding(
            AdapterQueryTopBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}