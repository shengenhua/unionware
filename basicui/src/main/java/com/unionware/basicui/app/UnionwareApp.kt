package com.unionware.basicui.app

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.tencent.mmkv.MMKV
import unionware.base.app.utils.DeviceAuthUtils
import unionware.base.app.utils.ToastUtil
import unionware.base.room.DatabaseProvider

/**
 * Author: sheng
 * Date:2024/12/30
 */
class UnionwareApp {
    companion object {
        @JvmStatic
        fun init(application: Application) {
            ARouter.openDebug()
            ARouter.openLog();
            ARouter.init(application)
            MMKV.initialize(application)
            //初始化土司工具类
            ToastUtil.init(application)
            DatabaseProvider.initialize(application)
//            DeviceAuthUtils.setDebug(true)
            //初始化腾讯bugly
//            CrashReport.initCrashReport(application, "22cff55565", false)
        }
    }
}