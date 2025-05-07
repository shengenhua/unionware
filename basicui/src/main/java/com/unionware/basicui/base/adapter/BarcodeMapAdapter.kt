package com.unionware.basicui.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterBarcodeMapBinding
import unionware.base.model.bean.BarcodeMapBean
import com.unionware.virtual.view.adapter.CommonAdapter
import unionware.base.ext.getColorPrimary

class BarcodeMapAdapter(
    diffCallback: DiffUtil.ItemCallback<BarcodeMapBean> = object :
        DiffUtil.ItemCallback<BarcodeMapBean>() {
        override fun areItemsTheSame(oldItem: BarcodeMapBean, newItem: BarcodeMapBean): Boolean {
            return oldItem.tag == newItem.tag
        }

        override fun areContentsTheSame(oldItem: BarcodeMapBean, newItem: BarcodeMapBean): Boolean {
            return oldItem.isSelect == newItem.isSelect
        }
    },
    private val isDelete: Boolean = false,
) : BasicDifferAdapter<BarcodeMapBean, AdapterBarcodeMapBinding>(diffCallback = diffCallback) {
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterBarcodeMapBinding>,
        position: Int,
        item: BarcodeMapBean?,
    ) {
        // app:cardBackgroundColor="@{bean.isSelect ? @color/mesAppColor : @color/white}"
        holder.binding.apply {
            bean = item
            tbDelete.visibility = if (isDelete) View.VISIBLE else View.GONE
            if (item?.list.isNullOrEmpty()) {
                viewDiver.visibility = View.GONE
                rvList.visibility = View.GONE
            } else {
                viewDiver.visibility = View.VISIBLE
                rvList.visibility = View.VISIBLE
                rvList.layoutManager = LinearLayoutManager(context)
                item?.list?.also {
                    rvList.adapter = CommonAdapter(items = it)
                }
            }

            cvBarcode.apply {
                if (item?.isSelect == true) {
                    setCardBackgroundColor(context.theme.getColorPrimary())
                } else {
                    setCardBackgroundColor(
                        context.resources.getColor(android.R.color.white, context.theme)
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): DataBindingHolder<AdapterBarcodeMapBinding> {
        return DataBindingHolder(
            AdapterBarcodeMapBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}