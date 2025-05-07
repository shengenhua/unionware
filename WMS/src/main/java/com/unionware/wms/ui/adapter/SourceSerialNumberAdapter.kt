package com.unionware.wms.ui.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import unionware.base.model.bean.SerialNumberInfoBean

/**
 * @Author : pangming
 * @Time : On 2024/8/28 15:25
 * @Description : SourceSerialNumberAdapter
 */

class SourceSerialNumberAdapter: BaseQuickAdapter<SerialNumberInfoBean.SerialNumberDetailBean, BaseViewHolder>(R.layout.item_source_serial_number) {
    override fun convert(holder: BaseViewHolder, item: SerialNumberInfoBean.SerialNumberDetailBean) {
       holder.setText(R.id.tv_serial_number_content,item.serialNo)
        holder.getView<TextView>(R.id.tv_status_content).also {
            if(item.status == 0){
                it.text = "未扫"
            }else if(item.status == 1){
                it.text = "已扫"
            }else if(item.status == 2){
                it.text = "新增"
            }
        }
    }
}