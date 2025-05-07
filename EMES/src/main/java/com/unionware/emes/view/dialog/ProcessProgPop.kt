package com.unionware.emes.view.dialog

import android.content.Context
import android.text.InputFilter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.impl.ConfirmPopupView
import com.unionware.emes.R
import com.unionware.virtual.view.adapter.CommonAdapter
import unionware.base.model.bean.CommonListBean
import unionware.base.util.InputFilterMinMax

/**
 *  工序进度
 */
class ProcessProgPop(
    context: Context, val list: List<CommonListBean>? = null, val title: String? = "进度确认",
) :
    ConfirmPopupView(context, R.layout.prcess_prog_pop_emes) {
    var confirmListener: ((Int) -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        initView()
        initData()
    }

    var adapter: CommonAdapter? = null

    private fun initView() {
        adapter = CommonAdapter()
        adapter?.submitList(list)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_list)
        findViewById<TextView>(R.id.tvTitle).text = title ?: "进度确认"
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        findViewById<TextView>(R.id.tv_confirm).setOnClickListener {
            confirmListener?.invoke(
                findViewById<AppCompatEditText>(R.id.acetProg).text.toString().ifEmpty { "0" }.toInt()
            )
        }

        findViewById<AppCompatEditText>(R.id.acetProg).apply {
            setFilters(
                mutableListOf(
                    InputFilter.LengthFilter(3),
                    InputFilterMinMax(0, 100)
                ).toTypedArray()
            )
        }
    }

    private fun initData() {
    }
}