package com.unionware.query.app

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
class QueryAppProvider : ModuleInitializer() {
    override fun haveModule(): Boolean = false

    override fun area(): Array<String> = arrayOf("query")

    override fun dispose(address: String, arg: RouteArg) {
        @Suppress("DEPRECATION") val bean = if (Build.VERSION.SDK_INT >= TIRAMISU) {
            arg.bundle?.getSerializable("config", MenuBean::class.java)
        } else {
            arg.bundle?.getSerializable("config")?.let {
                it as MenuBean
            }
        }
        bean?.apply {
            ARouter.getInstance().build("/query/dyamic")
                .withSerializable("scene", bean.scene)
                .withSerializable("title", bean.name)
                .withSerializable("reportFormId", bean.reportFormId)
                .withSerializable("primaryId", bean.id)
                .navigation()
            /*ARouter.getInstance().build(getPath(bean))
                .withSerializable("scene", bean.scene)
                .withSerializable("title", bean.name)
                .withSerializable("primaryId", bean.id)
                .withSerializable("id", bean.id)
                .navigation()*/
        }
    }
}