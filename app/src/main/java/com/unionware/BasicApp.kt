package com.unionware

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import dalvik.system.DexClassLoader
import java.io.File

@HiltAndroidApp
class BasicApp : Application() {

    override fun onCreate() {
        super.onCreate()
//        loadingAar()
//        DatabaseProvider.initialize(this,)
        // Hilt Activity must be attached to an @HiltAndroidApp Application. Found: class com.unionware.BasicApp
    }

    private fun loadingAar() {
//        UAarRouter.registerAar(this)
        assets?.apply {
            this.open("MES.aar").also {
                File(cacheDir, "MES.aar").apply {
                    if (exists()) {
                        delete()
                    }
                    createNewFile()
                    it.copyTo(this.outputStream())

                    DexClassLoader(absolutePath, cacheDir.absolutePath, null, classLoader).also {
                        it.loadClass("com.unionware.mes.app.MESAppProvider").apply {
//                        it.loadClass("com.unionware.wms.app.WMSAppProvider").apply {
                            val module = this.getMethod("module")
                                .invoke(this.getDeclaredConstructor().newInstance())//.
                            Log.e("welcome", "module: $module")
                        }
                    }
                }
            }
        }
    }
}