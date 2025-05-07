package com.unionware.once.adapter.process

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.unionware.once.databinding.AdapterOnceMultiBarCodeBinding
import unionware.base.model.bean.barcode.MultiBarCodeBean

class MultiBarCodeAdapter :
    BaseMultiAdapter<MultiBarCodeBean, AdapterOnceMultiBarCodeBinding>() {

    override fun onBindViewHolder(
        holder: BaseDataBinding<AdapterOnceMultiBarCodeBinding>,
        position: Int,
        item: MultiBarCodeBean?
    ) {
        holder.binding.let { bind ->
            bind.item = item
            bind.clFeature.item = item

            bind.clFeature.also { it ->
                it.etSumInput.apply {
                    holder.textWatcher?.also {
                        removeTextChangedListener(it)
                    }
                    it.ivCheckDelete.setOnClickListener {
                        this.setText("1")
                        item?.qty = "1"
                    }
                    holder.textWatcher = addTextChangedListener {
                        item?.qty = it.toString()
                    }
                    setOnEditorActionListener { v, actionId, event ->
                        return@setOnEditorActionListener true
                    }
                }
            }

            bind.rvMulti.layoutManager = LinearLayoutManager(context)
            bind.rvMulti.adapter = ProcessMultiAdapter().apply {
                submitList(item?.collects)

                mOnItemOnItemClickArray?.let {
                    for (i in 0 until it.size()) {
                        val id = it.keyAt(i)
                        val listener = it.get(id)
                        addOnItemChildClickListener(id, listener)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int
    ): BaseDataBinding<AdapterOnceMultiBarCodeBinding> {
        return BaseDataBinding(
            AdapterOnceMultiBarCodeBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}