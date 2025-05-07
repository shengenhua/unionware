package com.unionware.mes.app

import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import com.alibaba.android.arouter.launcher.ARouter
import unionware.base.model.bean.MenuBean
import unionware.base.route.ModuleInitializer
import unionware.base.route.RouteArg

/**
 * Author: sheng
 * Date:2024/12/11
 */
class MESAppProvider : ModuleInitializer() {
    companion object Path {
        private const val BASIC_MODULE = "UNW_XMES_APPSETPROCESS"
    }

    override fun detailsName(): String {
        return "65A7B6C3866971"
    }

    override fun module(): Array<String> {
        return arrayOf(BASIC_MODULE)
    }

    override fun dispose(address: String, arg: RouteArg) {
        val bean = if (Build.VERSION.SDK_INT >= TIRAMISU) {
            arg.bundle?.getSerializable("config", MenuBean::class.java)
        } else {
            @Suppress("DEPRECATION")
            arg.bundle?.getSerializable("config")?.let {
                it as MenuBean
            }
        }
        bean?.apply {
            ARouter.getInstance().build(openPath(arg.primaryId ?: "", bean))
                .withSerializable("scene", bean.scene)
                .withSerializable("title", bean.name)
                .withSerializable("primaryId", bean.id)
                .withSerializable("id", bean.id)
                .withSerializable("itemSearchId", bean.itemSearchId)
                .withSerializable("listSearchId", bean.listSearchId)
                .withSerializable("orderSearchId", bean.orderSearchId)
                .withSerializable("searchId", bean.searchId)
                .withSerializable("useStyleId", bean.useStyleId)
                .navigation()
        }
    }

    private fun openPath(app: String, bean: MenuBean): String {
        val path =
            when (bean.typeId ?: "") {
                //公共列表
                else -> {
                    if (bean.useStyleId == "2") {
                        RouterMESPath.MES.PATH_MES_PRDLIST
                    } else {
                        RouterMESPath.MES.PATH_MES_LIST
                    }
                }
            }
        return path
    }
}