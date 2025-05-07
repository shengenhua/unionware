package com.unionware.once.adapter.process

import android.text.TextWatcher
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.model.bean.CollectMultiItem

abstract class BaseMultiAdapter<T : Any, DB : ViewDataBinding> :
    BaseQuickAdapter<T, BaseMultiAdapter.BaseDataBinding<DB>>() {
    init {
//        setItemAnimation(AnimationType.SlideInRight)
    }

    open class BaseDataBinding<BDB : ViewDataBinding> : DataBindingHolder<BDB> {
        constructor(binding: BDB) : super(binding)
        constructor(itemView: View) : super(itemView)
        constructor(resId: Int, parent: ViewGroup) : super(resId, parent)

        var textWatcher: TextWatcher? = null
    }

    var textWatcher: TextWatcher? = null


    protected var mOnItemOnItemClickArray: SparseArray<OnItemChildClickListener<CollectMultiItem>>? =
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