package com.unionware.wms.ui.activity

import android.view.View
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.path.RouterPath
import com.unionware.wms.R
import com.unionware.wms.URLPath
import com.unionware.wms.databinding.CommonViewpageActivityBinding
import com.unionware.wms.ui.adapter.ScanPagerAdapter
import com.unionware.wms.ui.fragment.*
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.app.view.base.viewbinding.BaseBindActivity
import unionware.base.model.bean.MenuBean


@AndroidEntryPoint
@Route(path = RouterPath.Pack.PATH_PACK_SCAN_MAIN)
class ScanActivity : BaseBindActivity<CommonViewpageActivityBinding>() {
    @JvmField
    @Autowired(name = "bean")
    var bean: MenuBean? = null
    private val mFragments: MutableList<Fragment> = ArrayList()
    private var adapter: ScanPagerAdapter? = null
    private var operType: String = ""

    override fun onBindLayout(): Int {
        return R.layout.common_viewpage_activity
    }


    override fun initView() {
        setSupportActionBar(mToolbar)
        mBind.tvTabTitle.text = bean!!.name
        mBind.toolbar.setNavigationOnClickListener { finish() }
        operType = bean?.type.toString()
        if ("1" == operType) {
            mBind.tvScanInProgress.visibility = View.GONE
        }
        initTab()
    }


    /**需要优化- 引入设计模式 -策略模式*/
    private fun initTab() {
        when (operType) {
            "1" -> {
                mFragments.add(CommonScanFragment.newInstance(intent.getStringExtra("id"),
                    intent.getStringExtra("taskId")))
                mFragments.add(PackingScanRecordFragment.newInstance(intent.getStringExtra("taskId")))
            }
            "2" -> {
                mFragments.add(UnpackingFragment.newInstance(intent.getStringExtra("id"),
                    intent.getStringExtra("taskId")))
                mFragments.add(UnPackScanListFragment.newInstance(intent.getStringExtra("id")))
            }
            "3" -> {
                when (bean!!.transType) {
                    "1" -> {
                        mFragments.add(BTransFragment.newInstance(intent.getStringExtra("id"),
                            intent.getStringExtra("taskId")))
                        mFragments.add(TransScanListFragment.newInstance(intent.getStringExtra("id")))
                    }
                    "2" -> {
                        mFragments.add(BBDTransFragment.newInstance(intent.getStringExtra("id"),
                            intent.getStringExtra("taskId")))
                        mFragments.add(TransScanListFragment.newInstance(intent.getStringExtra("id")))
                    }
                    "3" -> {
                        mFragments.add(BDTransFragment.newInstance(intent.getStringExtra("id"),
                            intent.getStringExtra("taskId")))
                        mFragments.add(TransScanListFragment.newInstance(intent.getStringExtra("id")))
                    }
                }
            }
        }



        if (bean!!.scene.contains("WMS.Stock")) {
            mFragments.add(CommonListFragment.newInstance(bean, bean!!.srcFormId))
            mFragments.add(CommonListFragment.newInstance(bean,
                URLPath.Stock.PATH_STOCK_IN_PROGRESS_TASK_CODE))
        }
        var titles: MutableList<String> = arrayListOf("单据列表", "任务列表")
        adapter = ScanPagerAdapter(supportFragmentManager, titles)
        adapter!!.fragments = mFragments
        mBind.mViewPager.adapter = adapter
        mBind.mViewPager.offscreenPageLimit = 3
        mBind.mTabLayout.setupWithViewPager(mBind.mViewPager)
    }

    override fun initData() {
    }
}

