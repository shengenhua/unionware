package com.unionware

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dalvik.system.DexClassLoader
import java.io.File

/**
 * Author: sheng
 * Date:2025/4/3
 */
class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingAar()
    }

    private fun loadingAar() {
        assets?.apply {
            this.open("MES.aar").also {
                File(cacheDir, "MES.aar").apply {
                    if (!exists()) {
                        createNewFile()
                        it.copyTo(this.outputStream())
                    }

                    DexClassLoader(absolutePath, cacheDir.absolutePath, null, classLoader).also {
                        it.loadClass("com.unionware.mes.app.MESAppProvider").apply {
                            val module = this.getMethod("module")
                                .invoke(this.getDeclaredConstructor().newInstance())//.
                            Log.e("welcome", "module: $module")
//                            this.getMethod("").invoke(this.getDeclaredConstructor().newInstance())
                        }
                    }
                }
            }
        }
    }
}