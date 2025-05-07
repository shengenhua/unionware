package com.unionware.wms.ui.adapter

import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.unionware.wms.R
import unionware.base.model.bean.EntityBean
import com.unionware.wms.model.event.EditIndexEvent
import org.greenrobot.eventbus.EventBus

class BarcodeDetailsAdapter(data: MutableList<EntityBean>?) :
    BaseQuickAdapter<EntityBean, BaseViewHolder>(R.layout.item_show_info_detailis,
        data
    ) {
    override fun convert(holder: BaseViewHolder, bean: EntityBean) {
        val isBase =
            "BASEDATA" == bean!!.type || "DATETIME" == bean.type || "ASSISTANT" == bean!!.type
        val tv_content: TextView = holder.getView(R.id.tv_show_content)
        val et_content: EditText = holder.getView(R.id.et_show_input)
        val iv_query: ImageView = holder.getView(R.id.iv_base_info_query)
        et_content.visibility = if (bean.isEnable) View.VISIBLE else View.GONE
        tv_content.visibility = if (bean.isEnable) View.INVISIBLE else View.VISIBLE
        iv_query.visibility = if (bean.isEnable && isBase) View.VISIBLE else View.GONE
        holder.setText(R.id.tv_show_title, bean.property.name)
        holder.setText(R.id.tv_show_content, bean.value)
        holder.setText(R.id.et_show_input, bean.value)
        et_content.setOnEditorActionListener { textView: TextView, i: Int, keyEvent: KeyEvent? ->
            bean.value = textView.text.toString()
            EventBus.getDefault().post(bean)
            false
        }
        iv_query.setOnClickListener { view: View? ->
            EventBus.getDefault().post(EditIndexEvent(bean.index,
                holder.layoutPosition,
                getViewByPosition(holder.layoutPosition, R.id.et_show_input) as EditText?))
        }

        holder.setGone(R.id.view_edit_diver, holder.layoutPosition == data.size-1)
    }
}