package com.unionware.basicui.setting

import com.tencent.mmkv.MMKV
import com.unionware.basicui.setting.acth.AuthConfigActivity
import com.unionware.basicui.setting.apptheme.ThemeTextActivity
import com.unionware.basicui.setting.apptheme.UnionwareThemeActivity
import com.unionware.basicui.setting.bean.SettingBean
import com.unionware.path.RouterPath
import unionware.base.app.utils.ToastUtil.showToast
import kotlin.jvm.java

/**
 * Author: sheng
 * Date:2025/5/9
 */
class SettingConfig {

    companion object {
        private val settingBeans: MutableList<SettingBean> = ArrayList()
        private val bottomSettingBeans: MutableList<SettingBean> = ArrayList()

        init {
            settingBeans.add(
                SettingBean(
                    "禁用软键盘",
                    "hideKeyboard",
                    MMKV.mmkvWithID("app").encode("hideKeyboard", false)
                )
            )
            settingBeans.add(SettingBean("打印设置", RouterPath.Print.PATH_PRINT_SET_MAIN))
            settingBeans.add(SettingBean("清除缓存") {
                it.cacheDir?.apply {
                    if (isDirectory) {
                        this.listFiles()?.forEach {
                            it.delete()
                        }
                    }
                }
                showToast("清除缓存成功")
            })
            settingBeans.add(SettingBean("主题", UnionwareThemeActivity::class.java))
            settingBeans.add(SettingBean("字体调整", ThemeTextActivity::class.java))
            settingBeans.add(SettingBean("关于我们", AboutUsActivity::class.java))
            settingBeans.add(SettingBean("设备授权", AuthConfigActivity::class.java))
        }

        @JvmStatic
        fun getSettingBeans(): MutableList<SettingBean> {
            return settingBeans
        }

        @JvmStatic
        fun addSettingBeans(vararg beans: SettingBean) {
            beans.forEach {
                val oldBean = settingBeans.firstOrNull { bean -> bean.name == it.name }
                if (oldBean != null) {
                    oldBean.apply {
                        it.key = this.key
                        it.switch = this.switch
                        it.path = this.path
                        it.uPath = this.uPath
                        it.cls = this.cls
                        it.method = this.method
                    }
                } else {
                    settingBeans.add(it)
                }
            }
        }

        @JvmStatic
        fun getBottomSettingBeans(): MutableList<SettingBean> {
            return bottomSettingBeans
        }

        @JvmStatic
        fun addBottomSettingBeans(vararg beans: SettingBean) {
            beans.forEach {
                val oldBean = bottomSettingBeans.firstOrNull { bean -> bean.name == it.name }
                if (oldBean != null) {
                    oldBean.apply {
                        it.key = this.key
                        it.switch = this.switch
                        it.path = this.path
                        it.uPath = this.uPath
                        it.cls = this.cls
                        it.method = this.method
                    }
                } else {
                    bottomSettingBeans.add(it)
                }
            }
            /*beans.forEach {
                bottomSettingBeans.add(it)
            }*/
        }
    }
}