package com.unionware.once.app

import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import com.alibaba.android.arouter.launcher.ARouter
import com.unionware.path.RouterPath
import unionware.base.ext.showToast
import unionware.base.model.bean.MenuBean
import unionware.base.route.ModuleInitializer
import unionware.base.route.RouteArg
import unionware.base.route.URouter

/**
 * Author: sheng
 * Date:2024/12/11
 */
class OnceAppProvider : ModuleInitializer() {
    companion object Path {
        private const val BASIC_MODULE = "UNW_WMS_APPSET_ONCE"
    }

    /*override fun detailsName(): String {
        return "65A7B6C3866971"
    }*/

    override fun init(context: Context) {
        super.init(context)
        /*URouter.build().addIntercept("app://BasicApp/basic_login") {
            "监听登陆,未处理".showToast()
            true
        }*/
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
        bean?.apply {
            ARouter.getInstance().build(getPath(bean))
                .withSerializable("scene", bean.scene)
                .withSerializable("title", bean.name)
                .withSerializable("primaryId", bean.id)
                .withSerializable("id", bean.id)
                .navigation()
        }
    }

    private fun getPath(bean: MenuBean): String {
        return when (bean.id) {
            //特殊界面 ，独立功能
            "669a31965a3747" -> RouterOncePath.ONCE.PATH_ONCE_AGEING_RACK_TRANSFER
            "669a31bd5a3749" -> RouterOncePath.ONCE.PATH_ONCE_INSPECTION

            "66ed542fb3faba" -> RouterOncePath.ONCE.PATH_ONCE_ZXHD
            "66ed542fbua58" -> RouterOncePath.ONCE.PATH_ONCE_GDCPZY
            "66ed542fbwe86" -> RouterOncePath.ONCE.PATH_ONCE_GDJD

            "67edef0e48abbd" -> RouterOncePath.ONCE.PATH_ONCE_LIST

            "670e04aeab8045" -> RouterOncePath.ONCE.PATH_ONCE_BARCODE_REPRINTING
            "6784e03ab0501a" -> RouterOncePath.ONCE.PATH_ONCE_MJBCPHWBD
            "67b43d6bbc713b" -> RouterOncePath.ONCE.PATH_ONCE_LGSC

            "2aagv37z71r17" -> RouterOncePath.ONCE.PATH_ONCE_GDSCGZJL
            "2aagv37z71r89" -> RouterOncePath.ONCE.PATH_ONCE_ZHOYU_ORDER_PRINT
            "2aagv37z71r90" -> RouterOncePath.ONCE.PATH_ONCE_ZHOYU_PIECE_RATE

            "2d26x658z5w3k" -> RouterOncePath.ONCE.PATH_ONCE_BLJXPD
            else -> {
                ""
            }
        }
    }
}