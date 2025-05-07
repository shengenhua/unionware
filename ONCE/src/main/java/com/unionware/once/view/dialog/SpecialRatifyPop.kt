package com.unionware.once.view.dialog

import android.content.Context
import android.text.InputType
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.impl.ConfirmPopupView
import com.unionware.basicui.base.adapter.ProcessAdapter
import com.unionware.once.R
import unionware.base.model.ViewDisplay
import unionware.base.model.bean.BaseInfoBean

class SpecialRatifyPop(
    context: Context, val title: String? = "提交(验证特批人)", val isCancel: Boolean = true
) :
    ConfirmPopupView(context, R.layout.special_ratify_pop_once) {

    var confirmListener: ((String?, String?) -> Unit)? = null
    var adapterItemListener: ((String?, String?) -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        initView()
        initData()
    }

    var adapter: ProcessAdapter? = null

    private fun initView() {
        val list: MutableList<ViewDisplay> = ArrayList()
        list.add(ViewDisplay("特批人", "Operator", "operator", "66960F120DE7EF", true))
        list.add(ViewDisplay("授权码", "AccreditCode", "accreditCode", null, true).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        })
        adapter = ProcessAdapter()
        if (isCancel) {
            findViewById<TextView>(R.id.tv_cancel).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.tv_cancel).visibility = View.GONE
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rv_list)
        findViewById<TextView>(R.id.tvTitle).text = title ?: "提交(验证特批人)"
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        findViewById<TextView>(R.id.tv_confirm).setOnClickListener {
            confirmListener?.invoke(
                adapter?.getItem("operator")?.id,
                adapter?.getItemValue("accreditCode")
            )
        }
        adapter?.addOnItemChildClickListener(unionware.base.R.id.ivQuery) { baseQuickAdapter, view, position ->
            adapterItemListener?.invoke(
                baseQuickAdapter.items[position].key, baseQuickAdapter.items[position].code
            )
        }
        adapter?.submitList(list)
    }

    private fun initData() {
    }

    fun updateAdapter(tag: String, infoBean: BaseInfoBean) {
        adapter?.items?.withIndex()?.firstOrNull {
            it.value.key == tag
        }?.also {
            it.value.value = infoBean.name
            it.value.id = infoBean.id
            adapter?.notifyItemChanged(it.index)
        }
    }
}