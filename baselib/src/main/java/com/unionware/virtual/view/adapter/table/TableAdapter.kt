package com.unionware.virtual.view.adapter.table

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.chad.library.adapter4.BaseDifferAdapter
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterTableBinding
import unionware.base.model.bean.ViewBean
import java.util.stream.Collectors


class TableAdapter(
    diffCallback: DiffUtil.ItemCallback<Map<String, Any>> = object :
        DiffUtil.ItemCallback<Map<String, Any>>() {
        override fun areItemsTheSame(
            oldItem: Map<String, Any>,
            newItem: Map<String, Any>,
        ): Boolean {
            oldItem.forEach { (k, v) ->
                if (!newItem.containsKey(k) || newItem[k] != v) {
                    return false
                }
            }
            return true
        }

        override fun areContentsTheSame(
            oldItem: Map<String, Any>,
            newItem: Map<String, Any>,
        ): Boolean {
            oldItem.forEach { (k, v) ->
                if (!newItem.containsKey(k) || newItem[k] != v) {
                    return false
                }
            }
            return true
        }
    },
    items: List<Map<String, Any>> = emptyList(),
) : BaseDifferAdapter<Map<String, Any>, DataBindingHolder<unionware.base.databinding.AdapterTableBinding>>(
    diffCallback,
    items
) {

    companion object {
        //        private var showView: List<ViewBean>? = null
        private var heardView: Map<String, Any>? = null

        private fun getView(map: Map<String, Any>): Map<String, Any> {
            return map.filter { heardView?.containsKey(it.key) ?: true }
        }

        fun setHeardView(view: List<ViewBean>?): Companion {
            val showView = view?.parallelStream()?.filter { bean: ViewBean -> bean.isVisible }
                ?.collect(Collectors.toList())

            heardView = showView?.parallelStream()?.collect(
                Collectors.toMap({ it.key },
                    { it.name },
                    { k1, k2 -> k2 },
                    { LinkedHashMap() })
            )
            return this
        }

        fun build(tableAdapter: TableAdapter): RecyclerView.Adapter<ViewHolder> {
            tableAdapter.let { adapter ->
                val helper = QuickAdapterHelper.Builder(adapter).build()
                heardView?.apply {
                    helper.addBeforeAdapter(TableAdapter().apply {
                        heardView?.apply {
                            submitList(mutableListOf(this))
                        }
                    })
                }
                return helper.adapter
            }
        }
    }

    override fun submitList(list: List<Map<String, Any>>?) {
        val newList: MutableList<Map<String, Any>> = mutableListOf()
        list?.forEach {
            newList.add(getView(it))
        }
        super.submitList(newList)
    }

    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterTableBinding>, position: Int, item: Map<String, Any>?,
    ) {

        holder.binding.rvList.layoutManager = GridLayoutManager(context, item?.size ?: 0)

        holder.binding.rvList.adapter = item?.let { map ->
            BoxAdapter(map.values.parallelStream().map { it.toString() }
                .collect(Collectors.toList()))
        }
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): DataBindingHolder<AdapterTableBinding> {
        return DataBindingHolder(
            AdapterTableBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

}