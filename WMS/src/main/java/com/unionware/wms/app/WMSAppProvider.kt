package com.unionware.wms.app

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
class WMSAppProvider : ModuleInitializer() {
    companion object Path {
        private const val SETNORMALSCAN = "UNW_WMS_APPSETNORMALSCAN"
        private const val SETPACKING = "UNW_WMS_APPSETPACKING"
        private const val SETUNPACK = "UNW_WMS_APPSETUNPACK"
        private const val SETDEPACK = "UNW_WMS_APPSETDEPACK"
    }

    override fun module(): Array<String> {
        return arrayOf(SETNORMALSCAN, SETPACKING, SETUNPACK, SETDEPACK)
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
        arg.apply {
            when (appScene) {
                SETNORMALSCAN -> {
                    ARouter.getInstance().build(RouterWMSPath.WMS.PATH_WMS_SCAN_MAIN)
                        .withSerializable("name", bean?.name)
                        .withSerializable("bean", bean)
                        .navigation()
                }

                SETDEPACK -> {
                    ARouter.getInstance().build(RouterWMSPath.WMS.PATH_WMS_BOX_SPLIT_MAIN)
                        .withSerializable("scene", bean!!.scene)
                        .withSerializable("title", bean.name)
                        .withSerializable("name", getQueryNameByApp(appScene))
                        .withSerializable("primaryId", bean.id)
                        .withSerializable("formId", getFromByApp(appScene))
                        .withBoolean("unpackAndTransfer", bean.isUnpackAndTransfer).navigation()
                }

                else -> {
                    ARouter.getInstance().build(RouterWMSPath.WMS.PATH_WMS_BOX_MAIN)
                        .withSerializable("scene", bean!!.scene)
                        .withSerializable("title", bean.name)
                        .withSerializable("name", getQueryNameByApp(appScene))
                        .withSerializable("primaryId", bean.id)
                        .withSerializable("formId", getFromByApp(appScene))
                        .withSerializable("allowPackCodeCreate", bean.allowPackCodeCreate)
                        .withBoolean("unpackAndTransfer", bean.isUnpackAndTransfer).navigation()

                }
            }
        }

        /*bean?.apply {
            ARouter.getInstance().build(getPath(bean))
                .withSerializable("scene", bean.scene)
                .withSerializable("title", bean.name)
                .withSerializable("primaryId", bean.id)
                .withSerializable("id", bean.id)
                .navigation()
        }*/
    }

    private fun getQueryNameByApp(app: String?): String {
        return when (app) {
            SETPACKING -> "660115AE951CC7"//条码装箱 查询未完成的条码装箱作业
            SETUNPACK -> "6601624F951CCA" //条码拆箱 name code
            SETDEPACK -> "662B1527526C89"//条码拆分 查询未完成的条码拆分作业
            else -> ""
        }
    }

    private fun getFromByApp(app: String?): String {
        return when (app) {
            SETPACKING -> "UNW_WMS_JOB_INPACKING"//条码装箱
            SETUNPACK -> "UNW_WMS_JOB_UNPACKING" //条码拆箱
            SETDEPACK -> "UNW_WMS_JOB_SPLITCODE"//条码拆分
            else -> ""
        }
    }

    private fun getPath(bean: MenuBean): String {
        return when (bean.id) {
            //特殊界面 ，独立功能
            else -> {
                ""
            }
        }
    }
}