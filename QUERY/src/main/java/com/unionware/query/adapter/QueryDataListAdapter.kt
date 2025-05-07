package com.unionware.query.adapter

import androidx.recyclerview.widget.DiffUtil
import com.unionware.virtual.view.adapter.virtual.VirMultiItemAdapter
import unionware.base.ext.tryBigDecimalToZeros
import unionware.base.model.bean.PropertyBean

/**
 * Author: sheng
 * Date:2025/3/6
 */
class QueryDataListAdapter :
    VirMultiItemAdapter<PropertyBean>(object : DiffUtil.ItemCallback<PropertyBean>() {
        override fun areItemsTheSame(oldItem: PropertyBean, newItem: PropertyBean): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: PropertyBean, newItem: PropertyBean): Boolean {
            return oldItem.value.tryBigDecimalToZeros() == newItem.value.tryBigDecimalToZeros()
        }
    }) {
        
}