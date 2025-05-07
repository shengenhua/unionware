package com.unionware.basicui.login

import android.content.pm.PackageManager
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.tencent.mmkv.MMKV
import com.unionware.basicui.R
import com.unionware.basicui.app.BasicAppProvider
import unionware.base.databinding.ActivityWellcomeBinding
import dalvik.system.DexClassLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import unionware.base.app.view.base.viewbinding.BaseBindActivity
import unionware.base.route.URouter
import java.io.File


class WelcomeActivity : BaseBindActivity<ActivityWellcomeBinding>() {
    override fun onBindLayout(): Int {
        return R.layout.activity_wellcome
    }

    override fun initView() {
        @Suppress("DEPRECATION")
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
//        loadingAar()
        mBind.apply {
            tvAppName.post {
                val spannableString = SpannableString(getAppName())
                val colorSpan = ForegroundColorSpan(Color.YELLOW)
                spannableString.setSpan(colorSpan, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                tvAppName.text = spannableString
            }
            clBg.post {
                clBg.setBackgroundResource(unionware.base.R.mipmap.ic_welcome_bg)
            }
//            imageView2.post {
//                imageView2.setBackgroundResource(unionware.base.R.mipmap.ic_welcome_mes)
//            }
            imageView.post {
                imageView.setBackgroundResource(unionware.base.R.mipmap.ic_welcome_logo)
            }
            /*URouter.build().addIntercept(BasicAppProvider.Login) {
//                startActivity<LoginActivity>()
                true
            }*/
        }
    }

    override fun initData() {
        lifecycleScope.launch {
            delay(400)
            val kv = MMKV.mmkvWithID("app")
            val isLogin = kv.decodeBool("isLogin", false)
            if (isLogin) {
                URouter.build().action(BasicAppProvider.getPath(BasicAppProvider.MAIN))
            } else {
                URouter.build().action(BasicAppProvider.getPath(BasicAppProvider.LOGIN))
            }
            finish()
        }
    }

    private fun getAppName(): String {
        val packageManager = packageManager
        var appName = ""
        try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
//            appName = packageManager.getApplicationLabel(applicationInfo) as String
            (packageManager.getApplicationLabel(applicationInfo) as String).also { appName = it }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appName
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

//                    DexClassLoader(this@apply.absolutePath, this@WelcomeActivity.cacheDir.absolutePath,
//                        null, this@WelcomeActivity.classLoader).also {
//                        it.loadClass("com.unionware.mes.app.MESAppProvider").apply {
////                        it.loadClass("com.unionware.wms.app.WMSAppProvider").apply {
//                            val module = this.getMethod("module")
//                                .invoke(this.getDeclaredConstructor().newInstance())//.
//                            Log.e("welcome", "module: $module")
//                        }
//                    }
                }
            }
        }
    }

}