package com.unionware.basicui.base.adapter

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterBillListBinding
import com.unionware.virtual.view.adapter.CommonAdapter
import unionware.base.model.bean.BillBean
import unionware.base.ext.tryBigDecimalToZeros

class BasicBillListAdapter(
    diffCallback: DiffUtil.ItemCallback<BillBean> = object :
        DiffUtil.ItemCallback<BillBean>() {
        override fun areItemsTheSame(oldItem: BillBean, newItem: BillBean): Boolean {
            return oldItem.code == newItem.code && oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BillBean, newItem: BillBean): Boolean {
            oldItem.dataMap.forEach { (k, v) ->
                if (!newItem.dataMap.containsKey(k) || newItem.dataMap[k] != v) {
                    return false
                }
            }
            return true
        }

    },
) : BasicDifferAdapter<BillBean, AdapterBillListBinding>(diffCallback) {

    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterBillListBinding>, position: Int, item: BillBean?,
    ) {
        (item?.codeName.isNullOrEmpty() || item?.codeName.equals("null")).also {
            holder.binding.apply {
                tvBillCode.visibility = if (it) ViewGroup.GONE else ViewGroup.VISIBLE
                viewDiver.visibility = if (it) ViewGroup.GONE else ViewGroup.VISIBLE
            }
        }
        holder.binding.billData = item

        item?.list?.also {
            holder.binding.rvList.layoutManager =
                if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    GridLayoutManager(context, 2).apply {
                        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return if (it.size % 2 == 1 && position == it.size - 1) {
                                    2
                                } else {
                                    1
                                }
                            }
                        }
                    }
                } else {
                    LinearLayoutManager(context)
                }
            holder.binding.rvList.adapter = CommonAdapter(items = it)
        }
        if (item?.dataMap?.isNotEmpty() == true
            && item.dataMap.containsKey("qty")
            && item.dataMap.containsKey("rptQty")
        ) {
            // qty:派工数量   rptQty:良品汇报数量
            item.dataMap.filter { it.key == "qty" || it.key == "rptQty" }.apply {
                try {
                    val qty = this["qty"].toString().tryBigDecimalToZeros()?.toDouble() ?: 0.0
                    val rptQty = this["rptQty"].toString().tryBigDecimalToZeros()?.toDouble() ?: 0.0
                    /**
                     * 派工数量-汇报数量=剩余数量，
                     * 剩余数量=派工数量的话，就是白色，                    汇报数量 = 0
                     * 如果剩余数量大于0小于派工数量，就是在生产，绿色，      汇报数量 >0  汇报数量 < 派工数量
                     * 如果剩余数量=0，就是完工的，蓝色。                   汇报数量 == 派工数量
                     */
                    qty.minus(rptQty).also {
                        (when (it) {// 0-0=0
                            qty -> getColor(unionware.base.R.color.white) //白色
                            in 0.0..qty -> {
                                if (it == 0.0) {
                                    getColor(unionware.base.R.color.blue_card) //蓝色
                                } else {
                                    getColor(unionware.base.R.color.green_card)//绿色
                                }
                            } //绿色
                            else -> getColor(unionware.base.R.color.white)
                        }).apply { holder.binding.cvBillItem.setCardBackgroundColor(this) }
                    }

                } catch (_: Exception) {
                    holder.binding.cvBillItem.setCardBackgroundColor(getColor(unionware.base.R.color.white))
                }
            }
        } else {
            holder.binding.cvBillItem.setCardBackgroundColor(getColor(unionware.base.R.color.white))
        }
    }

    private fun getColor(@ColorRes resId: Int): Int {
        return context.resources.getColor(resId, context.resources.newTheme())
    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): DataBindingHolder<AdapterBillListBinding> {
        return DataBindingHolder(
            AdapterBillListBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}