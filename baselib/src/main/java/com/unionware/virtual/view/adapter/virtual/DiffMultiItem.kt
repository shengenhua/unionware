package com.unionware.virtual.view.adapter.virtual

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.AsyncListDiffer.ListListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseMultiItemAdapter

abstract class DiffMultiItem<T : Any, V : RecyclerView.ViewHolder>(
    config: AsyncDifferConfig<T>,
    items: List<T>
) :
    BaseMultiItemAdapter.OnMultiItem<T, V>() {

    constructor(diffCallback: DiffUtil.ItemCallback<T>) : this(diffCallback, emptyList())

    constructor(diffCallback: DiffUtil.ItemCallback<T>, items: List<T>) : this(
        AsyncDifferConfig.Builder(diffCallback).build(), items
    )

    constructor(config: AsyncDifferConfig<T>) : this(config, emptyList())

    private val mDiffer: AsyncListDiffer<T> =
        AsyncListDiffer(AdapterListUpdateCallback(this.adapter!!), config)

    private val mListener: ListListener<T> = ListListener<T> { previousList, currentList ->
    }

    init {
        mDiffer.addListListener(mListener)
        mDiffer.submitList(items)
    }


}