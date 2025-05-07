package com.unionware.wms.ui.activity

import android.view.KeyEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.unionware.wms.R
import com.unionware.wms.databinding.ActivityBasicDataBinding
import com.unionware.wms.inter.basedata.BasicDataContract
import com.unionware.wms.inter.basedata.BasicDataPresenter
import com.unionware.wms.ui.adapter.BaseInfoAdapter
import unionware.base.model.req.FiltersReq
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.app.view.base.viewbinding.BaseBindActivity
import unionware.base.ext.showToast
import unionware.base.model.bean.BaseInfoBean
import javax.inject.Inject

@AndroidEntryPoint
class BasicDataActivity : BaseBindActivity<ActivityBasicDataBinding>(), BasicDataContract.View {

    @JvmField
    @Inject
    var presenter: BasicDataPresenter? = null


    @JvmField
    @Autowired(name = "scene")
    var scene: String? = null

    /**
     *父类资料id
     */
    @JvmField
    @Autowired(name = "lookupId")
    var lookupId: String? = null

    /**
     * 业务对象Id
     */
    @JvmField
    @Autowired(name = "parentId")
    var parentId: String? = null

    /**
     * 维度Id
     */
    @JvmField
    @Autowired(name = "flexId")
    var flexId: String? = null

    @JvmField
    @Autowired(name = "title")
    var title: String? = null

    @JvmField
    @Autowired(name = "position")
    var position: Int? = -1


    private var filtersReq: FiltersReq? = null
    private var baseInfoAdapter: BaseInfoAdapter? = null

    override fun onBindLayout(): Int {
        return R.layout.activity_basic_data
    }

    override fun initView() {
        presenter!!.attach(this)
        baseInfoAdapter = BaseInfoAdapter()

        mBind.icWmsToolbar.tbTitle.text = title
        mBind.icWmsToolbar.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        layoutInflater.inflate(unionware.base.R.layout.view_empty, null).also {
            it.findViewById<ImageView>(unionware.base.R.id.iv_empty_icon)
                .setImageResource(unionware.base.R.mipmap.ic_empty_bill)
            it.findViewById<TextView>(unionware.base.R.id.tv_empty_tips).text = "未找到数据"
            baseInfoAdapter?.setEmptyView(it)
            baseInfoAdapter?.isUseEmpty = false
        }
        mBind.rvList.run {
            layoutManager = LinearLayoutManager(context)
            adapter = baseInfoAdapter
        }

        baseInfoAdapter!!.setOnItemClickListener { adapter, view, position ->
            val bean = adapter.data[position] as BaseInfoBean
            val intent = intent.putExtra("baseInfo", bean)
            setResult(this.position!!, intent)
            finish()
        }
        mBind.smRefresh.apply {
            setOnRefreshListener {
                baseInfoAdapter?.data?.clear()
                filtersReq?.pageIndex = 1
                filtersReq?.filters?.remove("keyword")
                presenter?.queryBasicData(scene, lookupId, filtersReq)
//                finishRefresh()
            }
            setOnLoadMoreListener {
                val index: Int = filtersReq!!.pageIndex
                filtersReq?.pageIndex = index + 1
                presenter?.queryBasicData(scene, lookupId, filtersReq)
//                finishLoadMore()
            }
        }
        mBind.icScanView.etInProgressSearch.also {
            it.setOnKeyListener { v, keyCode, event ->
                if (event != null &&
                    event.keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_DOWN
                ) {
                    filtersReq!!.pageIndex = 1
                    filtersReq!!.filters["keyword"] = it.getText().toString()
                    presenter?.queryBasicData(scene, lookupId, filtersReq)

                    it.requestFocus()
                    it.isFocusable = true
                    it.isFocusableInTouchMode = true
                    it.setSelection(0, it.text?.length ?:0)
                    return@setOnKeyListener true
                }
                false
            }
        }
    }

    override fun initData() {
        val params: MutableMap<String, Any> = java.util.HashMap()
        //备注 辅助资料才传parentId
        if (parentId != null) {
            params["parentId"] = parentId!!
        }
        //备注 仓库类型才传parentId，flexId
        if (flexId != null) {
            params["flexId"] = flexId!!
        }
//        filtersReq = FiltersReq(params)
        filtersReq = FiltersReq(1, params)

        presenter!!.queryBasicData(scene, lookupId, filtersReq)
    }

    override fun showFailedView(msg: String?) {
        msg?.showToast()
        if (1 == filtersReq?.pageIndex) {
            baseInfoAdapter?.isUseEmpty = true
        }
    }

    override fun showBasicDataList(list: List<BaseInfoBean?>?) {
        list!!.let {
            if (1 == filtersReq!!.pageIndex) {
                if (it.isEmpty()) {
                    baseInfoAdapter?.isUseEmpty = true
                }
                baseInfoAdapter?.setNewInstance(it as MutableList)
                mBind.smRefresh.finishLoadMore()
            } else {
                baseInfoAdapter?.addData(it)
                mBind.smRefresh.finishLoadMore()
            }
        }

        mBind.icScanView.etInProgressSearch.run {
            isFocusable = true
            isFocusableInTouchMode = true
            setSelection(0, text?.length ?: 0)
            requestFocus()
        }
    }

}