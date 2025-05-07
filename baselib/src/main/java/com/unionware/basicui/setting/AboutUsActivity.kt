package com.unionware.basicui.setting

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import unionware.base.R
import unionware.base.databinding.AboutUsActivityBinding
import unionware.base.app.view.base.viewbinding.BaseBindActivity

/**
 * Author: sheng
 * Date:2025/3/26
 */
class AboutUsActivity : BaseBindActivity<AboutUsActivityBinding>() {
    override fun onBindLayout(): Int = R.layout.about_us_activity

    @SuppressLint("SetTextI18n")
    override fun initView() {
        mBind.apply {
            icAutoToolbar.tbTitle.text = "关于我们"
            icAutoToolbar.toolbar.setNavigationOnClickListener { finish() }
            tvAppName.text = getAppName()
            tvAppVersion.text = "v${getVersionName()}"
        }
    }

    override fun initData() {
    }

    private fun getAppName(): String {
        var appName = ""
        try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            appName = packageManager.getApplicationLabel(applicationInfo) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appName
    }

    /**
     * 获取指定包名的版本号
     *
     * @param context 本应用程序上下文
     * @return
     * @throws Exception
     */
    fun getVersionName(): String {
        try {
            val packInfo = packageManager.getPackageInfo(packageName, 0)
            val version = packInfo.versionName
            return version
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }
}