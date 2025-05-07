package com.unionware.basicui.base.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter4.BaseDifferAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder

abstract class BasicDifferAdapter<T : Any, DB : ViewDataBinding> :
    BaseDifferAdapter<T, DataBindingHolder<DB>> {

    constructor(diffCallback: DiffUtil.ItemCallback<T>) : super(diffCallback, emptyList())

    constructor(diffCallback: DiffUtil.ItemCallback<T>, items: List<T>) : super(
        AsyncDifferConfig.Builder(diffCallback).build(), items
    )

    constructor(config: AsyncDifferConfig<T>) : super(config, emptyList())
}