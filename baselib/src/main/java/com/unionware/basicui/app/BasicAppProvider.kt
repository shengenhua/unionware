package com.unionware.basicui.app

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import com.unionware.basicui.login.login.LoginActivity
import com.unionware.basicui.main.home.HomeActivity
import com.unionware.basicui.setting.SettingActivity
import unionware.base.ext.isActivityOnTop
import unionware.base.ext.startActivity
import unionware.base.model.bean.MenuBean
import unionware.base.route.ModuleInitializer
import unionware.base.route.RouteArg

/**
 * Author: sheng
 * Date:2024/12/11
 */
class BasicAppProvider : ModuleInitializer() {
    companion object Path {
        @JvmStatic
        fun getPath(path: String): String {//app://BasicApp/basic_login
            return "app://$BASIC_MODULE/$path"
        }

        private const val BASIC_MODULE = "BasicApp"

        const val LOGIN = "basic_login"
        const val MAIN = "basic_main"
        const val SETTING = "basic_setting"
    }

    override fun module(): Array<String> {
        return arrayOf(BASIC_MODULE)
    }

    @Suppress("DEPRECATION")
    override fun dispose(address: String, arg: RouteArg) {
        val bean = if (Build.VERSION.SDK_INT >= TIRAMISU) {
            arg.bundle?.getSerializable("config", MenuBean::class.java)
        } else {
            arg.bundle?.getSerializable("config")?.let {
                it as MenuBean
            }
        }

        when (address) {
            LOGIN -> {
                if (context.isActivityOnTop(LoginActivity::class.java.name)) {
                    return
                }
                context.startActivity<LoginActivity>(
                    arg.bundle,
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                )
//                ARouter.getInstance().build(RouterPath.Person.PATH_PERSON_LOGIN).navigation()
//                context.startActivity<SettingActivity>()
            }

            MAIN -> {
                if (context.isActivityOnTop(HomeActivity::class.java.name)) {
                    return
                }
                context.startActivity<HomeActivity>(
                    arg.bundle,
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                )
//                ARouter.getInstance().build(RouterPath.Main.PATH_MAIN_HOME).navigation()
            }

            SETTING -> {
                if (context.isActivityOnTop(SettingActivity::class.java.name)) {
                    return
                }
                context.startActivity<SettingActivity>(arg.bundle)
            }
        }
    }
}