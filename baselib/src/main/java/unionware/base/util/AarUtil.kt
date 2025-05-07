package unionware.base.util

import android.content.Context
import dalvik.system.DexClassLoader
import java.io.File

/**
 * Author: sheng
 * Date:2025/4/3
 */
class AarUtil {
    companion object {

        @JvmStatic
        fun loadAarFileToClass(context: Context): DexClassLoader? {
            return context.assets?.let {
                File(context.cacheDir, "MES.aar").apply {
                    if (!exists()) {
                        createNewFile()
                        it.open("MES.aar").copyTo(this.outputStream())
                    }
                }.let {
                    DexClassLoader(
                        it.absolutePath,
                        context.cacheDir.absolutePath,
                        null,
                        context.classLoader
                    )
                }
            }
        }

        @JvmStatic
        fun loadAarFile(context: Context) {
            context.assets?.apply {
                this.open("MES.aar").also {
                    File(context.cacheDir, "MES.aar").apply {
                        if (!exists()) {
                            createNewFile()
                            it.copyTo(this.outputStream())
                        }

                        DexClassLoader(
                            absolutePath,
                            context.cacheDir.absolutePath,
                            null,
                            context.classLoader
                        ).also {
                            it.loadClass("com.unionware.mes.app.MESAppProvider").apply {
                                this.getMethod("")
                                    .invoke(this.getDeclaredConstructor().newInstance())
                            }
                        }
                    }
                }
            }
        }
    }
}