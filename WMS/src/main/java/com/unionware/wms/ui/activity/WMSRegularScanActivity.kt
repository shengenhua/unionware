package com.unionware.wms.ui.activity

import android.view.View
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.wms.R
import com.unionware.wms.URLPath
import com.unionware.wms.app.RouterWMSPath
import com.unionware.wms.databinding.CommonViewpageActivityBinding
import com.unionware.wms.inter.wms.scan.WMSRegularScanContract
import com.unionware.wms.inter.wms.scan.WMSRegularScanPresenter
import com.unionware.wms.model.bean.NormalScanConfigBean
import com.unionware.wms.model.event.NormalScanCreateEvent
import com.unionware.wms.ui.adapter.ScanPagerAdapter
import com.unionware.wms.ui.fragment.WMSRegularScanCommonListFragment
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import unionware.base.app.view.base.viewbinding.BaseBindActivity
import unionware.base.ext.showToast
import unionware.base.model.bean.MenuBean
import unionware.base.model.req.FiltersReq
import javax.inject.Inject

/**
 * @Author : pangming
 * @Time : On 2024/6/25 19:40
 * @Description : WMSRegularScanActvity
 * 普通扫描列表
 */

@AndroidEntryPoint
@Route(path = RouterWMSPath.WMS.PATH_WMS_SCAN_MAIN)
class WMSRegularScanActivity : BaseBindActivity<CommonViewpageActivityBinding>(),
    WMSRegularScanContract.View {
    @Inject
    @JvmField
    var presenter: WMSRegularScanPresenter? = null

    @JvmField
    @Autowired(name = "bean")
    var bean: MenuBean? = null

    @JvmField
    @Autowired(name = "name")
    var name: String? = null

    private val mFragments: MutableList<Fragment> = ArrayList()
    private var adapter: ScanPagerAdapter? = null
    private var operType: String = ""

    override fun onBindLayout(): Int {
        return R.layout.common_viewpage_activity
    }


    override fun initView() {
        presenter!!.attach(this)
        setSupportActionBar(mToolbar)
        mBind.tvTabTitle.text = name
        mBind.toolbar.setNavigationOnClickListener { finish() }
        operType = bean?.type.toString()
        //initTab();
        val params: MutableMap<String, Any> = HashMap()
        params["primaryId"] = bean!!.id
        val req = FiltersReq(params)
        presenter!!.requestConfigurationList(
            bean!!.scene,
            URLPath.WMS.MENU_WMS_APP_NORMALSCAN_CONFIGURATION_LIST, req
        );
    }


    /**需要优化- 引入设计模式 -策略模式*/
    private fun initTab(normalScanConfigBean: NormalScanConfigBean?) {
        var titles: MutableList<String> = arrayListOf("单据列表", "进行中")
        //作业流程启用多单合并扫描，且多单模式为按明细条码获取源单信息或按包装条码获取源单信息时，则不显示单据列表界面，仅显示进行中列表界面。
        //无源单：Mode=3
        if ((normalScanConfigBean!!.multiCombineScan && !normalScanConfigBean.multiMode.equals("1")) || normalScanConfigBean.mode.equals(
                "3"
            )
        ) {
            titles = arrayListOf("进行中")
            mFragments.add(
                WMSRegularScanCommonListFragment.newInstance(
                    bean,
                    URLPath.WMS.MENU_WMS_APP_SCAN_TASK, normalScanConfigBean
                )
            )

        } else {
            when (normalScanConfigBean.mode) {
                "4" -> {
                    mFragments.add(
                        WMSRegularScanCommonListFragment.newInstance(
                            bean,
                            normalScanConfigBean!!.tarFormId,
                            normalScanConfigBean
                        )
                    )
                }

                "1" -> {
                    mFragments.add(
                        WMSRegularScanCommonListFragment.newInstance(
                            bean,
                            normalScanConfigBean!!.srcFormId,
                            normalScanConfigBean
                        )
                    )
                }
            }

            mFragments.add(
                WMSRegularScanCommonListFragment.newInstance(
                    bean,
                    URLPath.WMS.MENU_WMS_APP_SCAN_TASK, normalScanConfigBean
                )
            )
        }

        adapter = ScanPagerAdapter(supportFragmentManager, titles)
        adapter!!.fragments = mFragments
        mBind.mViewPager.adapter = adapter
        mBind.mViewPager.offscreenPageLimit = 3
        mBind.mTabLayout.setupWithViewPager(mBind.mViewPager)
    }

    private fun initTitle(normalScanConfigBean: NormalScanConfigBean?) {
        if (normalScanConfigBean!!.getMode().equals("3")) {
            //无源单
            mBind.tvScanInProgress.visibility = View.VISIBLE
            mBind.tvScanInProgress.text = "新任务扫描"

        } else if (normalScanConfigBean!!.multiCombineScan && normalScanConfigBean.multiMode.equals(
                "1"
            )
        ) {
            //有源单和进行中
            //作业流程配置启用多单合并扫描，且多单模式为手工选择源单时，单据列表支持多选，且显示【合并扫描】按钮，
            // 点击时按列表界面选择单据调用相关接口创建任务及打开扫描界面。
            mBind.tvScanInProgress.visibility = View.VISIBLE
            mBind.tvScanInProgress.text = "合并扫描"
        } else if (normalScanConfigBean!!.multiCombineScan && normalScanConfigBean.multiMode.equals(
                "2"
            )
        ) {
            //只有进行中
            //若作业流程配置启用多单合并扫描，
            // 且多单模式为按条码获取源单信息，则进行中列表显示【新任务扫描】按钮，点击按钮时自动打开空扫描界面。
            mBind.tvScanInProgress.visibility = View.VISIBLE
            mBind.tvScanInProgress.text = "新任务扫描"
        }
        if (mBind.tvScanInProgress.visibility == View.VISIBLE) {
            mBind.tvScanInProgress.setOnClickListener {
                // if(mBind.tvScanInProgress.text.equals("合并扫描")){
                EventBus.getDefault().post(NormalScanCreateEvent())
                //}
            }
        }
    }

    override fun initData() {
    }

    override fun setSrcFormId(normalScanConfigBean: NormalScanConfigBean?) {
        initTitle(normalScanConfigBean)
        initTab(normalScanConfigBean);
    }


    override fun showFailedView(msg: String?) {
        msg?.showToast()
    }
}