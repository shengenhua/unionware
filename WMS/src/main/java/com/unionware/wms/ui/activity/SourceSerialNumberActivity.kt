package com.unionware.wms.ui.activity

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.unionware.wms.R
import com.unionware.wms.databinding.ActivitySourceSerialNumberBinding
import com.unionware.wms.inter.wms.scan.SerialNumberListContract
import com.unionware.wms.inter.wms.scan.SerialNumberListPresenter
import unionware.base.model.req.ViewReq
import com.unionware.wms.ui.adapter.SourceSerialNumberAdapter
import unionware.base.model.req.FiltersReq
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.app.utils.ToastUtil
import unionware.base.app.view.base.viewbinding.BaseBindActivity
import unionware.base.model.bean.SerialNumberInfoBean
import javax.inject.Inject

/**
 * @Author : pangming
 * @Time : On 2024/8/28 10:58
 * @Description : SourceSerialNumberActivity
 */
@AndroidEntryPoint
class SourceSerialNumberActivity: BaseBindActivity<ActivitySourceSerialNumberBinding>() , SerialNumberListContract.View,
    OnRefreshListener, OnLoadMoreListener{
    @Inject
    @JvmField
    var presenter: SerialNumberListPresenter? = null
    var adapter: SourceSerialNumberAdapter? = null
    var req: FiltersReq? = null
    var viewReq: ViewReq? = null
    var emptyView: View? = null
    override fun onBindLayout(): Int {
        return R.layout.activity_source_serial_number
    }

    override fun initView() {
        presenter!!.attach(this)
        mBind.toolbar.setNavigationOnClickListener {finish() }
        emptyView = layoutInflater.inflate(unionware.base.R.layout.view_empty, null).also {
            it.findViewById<ImageView>(unionware.base.R.id.iv_empty_icon)
                .setImageResource(unionware.base.R.mipmap.ic_empty_bill)
            it.findViewById<TextView>(unionware.base.R.id.tv_empty_tips).text = "暂无记录"
        }
        adapter = SourceSerialNumberAdapter()
        mBind.rvList.layoutManager = LinearLayoutManager(mContext)
        mBind.rvList.adapter = adapter
        mBind.smRefresh.setOnRefreshListener(this)
        mBind.smRefresh.setOnLoadMoreListener(this)

    }

    override fun initData() {
        viewReq = ViewReq(intent.getStringExtra("pageId"))
        viewReq!!.also {
            it.command = "INVOKE_QUERYSERIAL"
            it.params = HashMap()
            presenter?.getSerialNumberList(it)
        }
    }

    override fun showFailedView(msg: String?) {
        msg?.let { ToastUtil.showToastCenter(it) }
    }

    override fun showList(list: MutableList<SerialNumberInfoBean.SerialNumberDetailBean>?) {
        adapter?.let {
            if (list != null) {
                it.setNewInstance(list)
            }
        }
    }

    override fun showEmptyView() {
        adapter?.let {
            it.data.clear()
            it.notifyDataSetChanged()
            it.setEmptyView(emptyView!!)
        }

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshLayout.finishRefresh()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        refreshLayout.finishLoadMore()
    }


}