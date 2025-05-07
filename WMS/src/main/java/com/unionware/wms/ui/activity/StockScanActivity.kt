package com.unionware.wms.ui.activity

import android.view.View
import androidx.fragment.app.Fragment
import com.unionware.wms.R
import com.unionware.wms.URLPath
import com.unionware.wms.databinding.CommonViewpageActivityBinding
import com.unionware.wms.ui.adapter.ScanPagerAdapter
import unionware.base.model.bean.BillBean
import com.unionware.wms.ui.fragment.*
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.app.view.base.viewbinding.BaseBindActivity

/*这个就是简单的tab，理应做成baseTab的类。跟ScanActivity功能重复，理应重构*/
@AndroidEntryPoint
class StockScanActivity : BaseBindActivity<CommonViewpageActivityBinding>() {
    private val mFragments: MutableList<Fragment> = ArrayList()
    private var adapter: ScanPagerAdapter? = null
    private var titles: MutableList<String> = arrayListOf()
    private var bean: BillBean? = null
    private var isLPN: Boolean = false
    private var scene: String? = null

    override fun onBindLayout(): Int {
        return R.layout.common_viewpage_activity
    }


    @Suppress("DEPRECATION")
    override fun initView() {
        setSupportActionBar(mToolbar)
        bean = intent.getSerializableExtra("bean") as BillBean?
        scene = intent.getStringExtra("scene").toString()
        isLPN = intent.getBooleanExtra("isLPN", false);
        mBind.tvTabTitle.setText(bean!!.code)
        mBind.toolbar.setNavigationOnClickListener { finish() }
        mBind.tvScanInProgress.visibility = View.GONE
        initTab()
    }


    /**需要优化- 引入设计模式 -策略模式*/
    private fun initTab() {
        mFragments.add(StockScanFragment.newInstance(bean, scene)) // 扫描
        titles.add("扫描")
        mFragments.add(
            SummaryListFragment.newInstance()
        )
        titles.add("任务明细")
        mFragments.add(
            QueryInfoListFragment.newInstance(URLPath.Stock.PATH_STOCK_SCAN_TASK_RECORD_CODE, scene))// 扫描记录
        titles.add("扫描记录")
        adapter = ScanPagerAdapter(supportFragmentManager, titles)
        adapter!!.fragments = mFragments
        mBind.mViewPager.adapter = adapter
        mBind.mViewPager.offscreenPageLimit = mFragments.size
        mBind.mTabLayout.setupWithViewPager(mBind.mViewPager)
    }

    override fun initData() {

    }
}

