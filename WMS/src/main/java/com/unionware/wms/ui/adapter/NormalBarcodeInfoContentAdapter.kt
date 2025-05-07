package com.unionware.wms.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import com.unionware.wms.utlis.CommonUtils
import unionware.base.model.bean.BarcodeBean
import unionware.base.model.bean.PropertyBean
import java.math.BigDecimal

/**
 * @Author : pangming
 * @Time : On 2024/7/19 16:01
 * @Description : NormalBarcodeInfoContentAdapter
 */

class NormalBarcodeInfoContentAdapter :
    BaseQuickAdapter<PropertyBean, BaseViewHolder>(R.layout.item_show_info_content) {
    override fun convert(holder: BaseViewHolder, item: PropertyBean) {
        holder.setText(R.id.tv_show_title, item.name + ":")
        if (item.type == "DECIMAL" && !item.value.isEmpty() && (CommonUtils.isDouble(item.value) || CommonUtils.isInteger(
                item.value
            ))
        ) {
            item.value = BigDecimal(item.value).stripTrailingZeros().toPlainString()
        }
        holder.setText(R.id.tv_show_content, item.value)
    }

    fun setValue(tag: String, bean: BarcodeBean) {
        data.withIndex().firstOrNull {
            it.value?.key.equals(tag)
        }?.also {
            it.value?.isEnable = bean.isEnabled
            it.value?.value =
                if (it.value?.type == "ASSISTANT" || it.value?.type == "BASEDATA")
                    bean.number
                else
                    bean.value

            notifyItemChanged(it.index)
        }
    }
}