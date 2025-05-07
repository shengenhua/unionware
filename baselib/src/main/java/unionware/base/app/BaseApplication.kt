package unionware.base.app

import android.app.Application
import android.content.Context
import unionware.base.app.utils.ProcessUtils
import unionware.base.app.utils.ToastUtil


open class BaseApplication : Application() {

    companion object {
        const val TAG = "BaseApplication"

        lateinit var instance: BaseApplication
            private set

        @JvmStatic
        fun getContext(): Context = instance.applicationContext
    }

    override fun attachBaseContext(base: Context?) {
        instance = this
//        MultiDex.install(this)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        if (ProcessUtils.isMainProcess == true) {
            initOnlyMainProcess()
        }

    }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onTrimMemory(level: Int) {
        // EventBus.getDefault().post(LowMemeryEvent(level))
        super.onTrimMemory(level)
    }


    /**
     * 主线程中初始化内容
     */
    protected open fun initOnlyMainProcess() {
        ToastUtil.init(this)

//        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
//            ARouter.openLog()     // 打印日志
//            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
//        }
//        ARouter.init(this) // 尽可能早，推荐在Application中初始化
    }
}