package com.unionware.emes.view.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.lxj.xpopup.impl.ConfirmPopupView
import com.unionware.emes.R
import com.unionware.mes.databinding.AdapterScanListBinding

class ScanListPop(context: Context, val list: List<String>) :
    ConfirmPopupView(context, R.layout.scan_list_pop_emes) {

    var adapterDeleteListener: ((String?) -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        initView()
        initData()
    }


    private fun initView() {
        findViewById<TextView>(R.id.tv_cancel).visibility = View.GONE
//        findViewById<TextView>(R.id.tv_confirm).visibility = View.GONE

        val recyclerView = findViewById<RecyclerView>(R.id.rv_list)
        "已扫描SN".also { findViewById<TextView>(R.id.tvTitle).text = it }
        recyclerView.adapter = ScanAdapter(list).apply {
            this.addOnItemChildClickListener(R.id.tbDelete) { baseQuickAdapter, view, i ->
                baseQuickAdapter.removeAt(i)
                adapterDeleteListener?.invoke(baseQuickAdapter.getItem(i))
                if (list.isEmpty()) {
                    dismiss()
                }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun initData() = Unit

    class ScanAdapter(list: List<String>) :
        BaseQuickAdapter<String, DataBindingHolder<AdapterScanListBinding>>(list) {
        override fun onBindViewHolder(
            holder: DataBindingHolder<AdapterScanListBinding>,
            position: Int,
            item: String?,
        ) {
            holder.binding.apply {
                content = item
            }
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int,
        ): DataBindingHolder<AdapterScanListBinding> {
            return DataBindingHolder(
                AdapterScanListBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )
        }
    }
}