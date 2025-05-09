package com.unionware.basicui.main.home

import android.graphics.Color
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.basicui.main.menu.HomeFragmentConfig
import com.unionware.basicui.main.menu.PersonFragment
import com.unionware.basicui.main.menu.ScanConfigFragment
import com.unionware.path.RouterPath
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.R
import unionware.base.app.utils.LoadingUtil
import unionware.base.app.view.base.BaseMvvmViewBindingActivity
import unionware.base.databinding.HomeActivtiyBinding


@AndroidEntryPoint
@Route(path = RouterPath.Main.PATH_MAIN_HOME)
class HomeActivity : BaseMvvmViewBindingActivity<HomeActivtiyBinding, HomeViewModel>() {

    @JvmField
    @Autowired(name = "isLogin")
    var isLogin: Boolean = false

    private var mExitTime: Long = 0
    private var platformFragment: Fragment? = null
    private var personFragment: Fragment? = null

    override fun initViewObservable() = Unit
    override fun onBindLayout(): Int = R.layout.home_activtiy


    override fun initCommonView() {
        super.initCommonView()
        mViewModel.lifecycle = this
    }

    override fun initView() {
        @Suppress("DEPRECATION")
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
        initBottomNavigation()
        LoadingUtil.init(this)

        mViewModel.mLiveData.observe(this) {
            switchFragment(it)
            binding?.mBottomNavigationView?.selectedItemId.apply {
                when {
                    this == R.id.item_platform && it != 0 -> {
                        binding?.mBottomNavigationView?.selectedItemId = R.id.item_platform
                    }

                    this == R.id.item_person && it != 1 -> {
                        binding?.mBottomNavigationView?.selectedItemId = R.id.item_platform
                    }
                }
            }
        }
    }

    override fun initData() {
        if (HomeFragmentConfig.getFirstFragment() == null) {
            HomeFragmentConfig.setFirstFragment(ScanConfigFragment().apply {
                this.arguments = bundleOf(
                    "isLogin" to isLogin
                )
            })
        }
        if (HomeFragmentConfig.getMeFragment() == null) {
            HomeFragmentConfig.setMeFragment(PersonFragment())
        }
    }

    private fun initBottomNavigation() {
        //去掉底部默认选中背景
        binding?.mBottomNavigationView?.itemIconTintList = null
        binding?.mBottomNavigationView?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_platform -> saveAndSwitch(0)
                R.id.item_person -> saveAndSwitch(1)
            }
            true
        }
    }

    private fun saveAndSwitch(index: Int) {
        mViewModel.saveSelect(index)
    }

    private fun switchFragment(position: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        when (position) {
            0 -> {
                platformFragment?.let {
                    transaction.show(it)
                } ?: HomeFragmentConfig.getFirstFragment()?.let {
                    if (supportFragmentManager.fragments.isNotEmpty()) {
                        supportFragmentManager.fragments.firstOrNull { f ->
                            f::class.java == it::class.java
                        }?.apply {
                            transaction.remove(this)
                        }
                    }
                    platformFragment = it
                    transaction.add(R.id.mContentFL, it, RouterPath.Main.PATH_MENU_HOME)
                }
            }


            1 -> {
                personFragment?.let {
                    transaction.show(it)
                } ?: HomeFragmentConfig.getMeFragment()?.let {
                    if (supportFragmentManager.fragments.isNotEmpty()) {
                        supportFragmentManager.fragments.firstOrNull { f ->
                            f::class.java == it::class.java
                        }?.apply {
                            transaction.remove(this)
                        }
                    }
                    personFragment = it
                    transaction.add(R.id.mContentFL, it, RouterPath.Person.PATH_PERSON_HOME)
                }
            }
        }
        transaction.commitNow()
    }


    //隐藏所有的Fragment
    private fun hideFragments(transaction: FragmentTransaction) {
        platformFragment?.let { transaction.hide(it) }
        personFragment?.let { transaction.hide(it) }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis().minus(mExitTime) <= 2000) {
                finish()
            } else {
                mExitTime = System.currentTimeMillis()
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun enableToolbar(): Boolean {
        return false
    }

    override fun enableEventBus(): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        LoadingUtil.unInit()
    }
}