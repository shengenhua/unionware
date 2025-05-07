package com.unionware.basicui.view.spinner

import android.content.Context
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.IdRes
import androidx.viewbinding.ViewBinding

/**
 * Author: sheng
 * Date:2024/12/23
 */
abstract class SpinnerPopAdapter<T, VB : ViewBinding>(
    val context: Context,
    var items: List<T> = emptyList(),
) : BaseAdapter() {
    private var mOnItemClickListener: OnItemClickListener<T>? = null
    private var mOnItemChildClickArray: SparseArray<OnItemClickListener<T>>? = null

    open fun setOnItemClickListener(listener: OnItemClickListener<T>) {
        mOnItemClickListener = listener
    }

    fun addOnItemChildClickListener(@IdRes id: Int, listener: OnItemClickListener<T>) = apply {
        mOnItemChildClickArray =
            (mOnItemChildClickArray ?: SparseArray<OnItemClickListener<T>>(2)).apply {
                put(id, listener)
            }
    }

    fun removeOnItemChildClickListener(@IdRes id: Int) = apply {
        mOnItemChildClickArray?.remove(id)
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): T? = items.getOrNull(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return onViewHolder(context, parent).let {
            mOnItemClickListener?.apply {
                it.root.setOnClickListener { v ->
                    onItemClick(v, position, getItem(position))
                }
            }
            mOnItemChildClickArray?.let { array ->
                for (i in 0 until array.size()) {
                    val id = array.keyAt(i)
                    it.root.findViewById<View>(id)?.let { childView ->
                        childView.setOnClickListener { v ->
                            if (position == -1) {
                                return@setOnClickListener
                            }
                            array[id]?.onItemClick(v, position, getItem(position))
                        }
                    }
                }
            }
            onBindViewHolder(it, position, getItem(position))
            it.root
        }
    }

    abstract fun onViewHolder(context: Context, parent: ViewGroup?): VB

    abstract fun onBindViewHolder(viewBinding: VB, position: Int, t: T?)

    fun interface OnItemClickListener<T> {
        fun onItemClick(view: View?, position: Int, t: T?)
    }

}