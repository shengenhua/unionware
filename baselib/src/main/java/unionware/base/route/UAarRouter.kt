package unionware.base.route

import android.content.Context
import android.os.Bundle
import dalvik.system.DexClassLoader
import java.io.File
import java.io.Serializable

/**
 * Author: sheng
 * Date:2024/8/27
 */
open class UAarRouter {

    companion object {
        private val appRoute = mutableMapOf<String, MutableMap<String, UnionwAreRoute>>()
        private val appNoFixedRoute = mutableMapOf<String, UnionwAreRoute>()
        private val interceptUnit = mutableMapOf<String, (RouteArg) -> Boolean>()

        /**
         * app 对应的 详情接口
         */
        private val appDetails = mutableMapOf<String, String>()

        private val uRoute by lazy { UAarRouter() }

        @JvmStatic
        fun build(): UAarRouter = uRoute

        fun registerAar(context: Context) {
            context.assets?.apply {
                this.list("")?.filter { it.contains(".aar") }?.forEach {
//                    Log.e("welcome", "aar: $it")
                    this.open(it).apply {

                    }
                }
                this.open("MES.aar").also {
                    File(context.cacheDir, "MES.aar").apply {
                        if (exists()) {
                            delete()
                        }
                        createNewFile()
                        it.copyTo(this.outputStream())
                        DexClassLoader(
                            absolutePath,
                            context.cacheDir.absolutePath,
                            null,
                            context.classLoader
                        ).also {

                            try {
                                val itClass = it.loadClass("com.unionware.mes.app.MESAppProvider")
                                val route = itClass.getDeclaredConstructor().newInstance()
                                /*it.loadClass("com.unionware.mes.app.MESAppProvider").apply {
                                    val unionwAreRoute = this as Class<UnionwAreRoute>
                                    URouter.build().register(
                                        context,
                                        unionwAreRoute.getDeclaredConstructor().newInstance()
                                    )
                                }*/
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    /*open fun putAppDetails(url: String, details: String = "") {
        appDetails[url] = details
    }*/

    fun getAppDetails(url: String): String {
        Builder(url).let {
            appRoute[it.area]?.get(it.module)?.detailsName()
        }?.also {
            return it
        }
//        if (appDetails.containsKey(url)) return "65A7B6C3866971"
        return ""
    }

    /**
     * 注册
     */
    open fun register(context: Context, route: UnionwAreRoute) {
        val area = route.area()
        area.forEach {
            if (it.contains("://")) {
                it.replace("://", "")
            }
            if (!appRoute.containsKey(it)) {
                appRoute[it] = mutableMapOf()
            }
            if (route.haveModule()) {
                route.module().forEach { module ->
                    appRoute[it]?.put(module, route)
                }
            } else {
                appNoFixedRoute[it] = route
            }
        }
        route.init(context)
    }

    /**
     * 注销
     */

    open fun unRegister(module: String) {
        appRoute.forEach {
            it.value.remove(module)
        }
    }

    open fun unRegister(app: String, module: String) {
        appRoute[app]?.remove(module)
    }

    open fun unRegister(route: UnionwAreRoute) {
        route.area().forEach {
            route.module().forEach { module ->
                appRoute[it]?.remove(module)
            }
        }
    }

    fun addIntercept(app: String, unit: (RouteArg) -> Boolean) {
        interceptUnit[app] = unit
    }

    fun action(module: String, address: String, bundle: Bundle? = null) {
        Builder(module, address).with(bundle).navigation()
    }

    /**
     * 行为
     */
    fun action(url: String, bundle: Bundle? = null) {
        Builder(url).with(bundle).navigation()
    }

    /**
     * 行为
     */
    fun action(url: String) {
        Builder(url).navigation()
    }

    fun builder(url: String): Builder {
        return Builder(url)
    }

    class Builder(
        /**模块*/
        val area: String,
        val module: String,
        val address: String,
    ) {
        companion object {
            fun salvageUrl(urlRoute: String, pos: Int): String {
                try {
                    return when {
                        !urlRoute.contains("/") -> {
                            urlRoute
                        }

                        pos == 0 -> {
                            urlRoute.substring(0, urlRoute.indexOf("://"))
                        }

                        pos == 1 -> {
                            val one = urlRoute.substring(0, urlRoute.indexOf("://") + 3)
                            val two = urlRoute.replace(one, "")
                            val three = two.substring(0, two.indexOf("/"))
                            return three
                        }

                        pos == 2 -> {
                            urlRoute.substring(urlRoute.lastIndexOf("/") + 1)
                        }

                        else -> ""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return ""
                }
            }
        }

        constructor(urlRoute: String) : this(
            salvageUrl(urlRoute, 0),
            salvageUrl(urlRoute, 1),
            salvageUrl(urlRoute, 2),
        )


        constructor(area: String, module: String) : this(area, module, "")

        private var bundle = Bundle()

        fun with(bundle: Bundle?): Builder {
            bundle?.apply {
                this@Builder.bundle = this
            }
            return this
        }

        fun withString(key: String, value: String): Builder {
            bundle.putString(key, value)
            return this
        }

        fun withInt(key: String, value: Int): Builder {
            bundle.putInt(key, value)
            return this
        }

        fun withSerializable(key: String, value: Serializable): Builder {
            bundle.putSerializable(key, value)
            return this
        }

        fun withObject(key: String, value: Any): Builder {
            when (value) {
                is String -> {
                    bundle.putString(key, value)
                }

                is Boolean -> {
                    bundle.putBoolean(key, value)
                }

                is Int -> {
                    bundle.putInt(key, value)
                }

                is Float -> {
                    bundle.putFloat(key, value)
                }

                is Long -> {
                    bundle.putLong(key, value)
                }

                is Double -> {
                    bundle.putDouble(key, value)
                }

                is Serializable -> {
                    bundle.putSerializable(key, value)
                }

                else -> {
                    bundle.putString(key, value.toString())
                }
            }

            return this
        }

        fun init(title: String = "", primaryId: String = "", scene: String = ""): Builder {
            bundle.putString("title", title)
            bundle.putString("primaryId", primaryId)
            bundle.putString("scene", scene)
            return this
        }

        fun navigation() {
            this.navigation(RouteArg())
        }

        /**
         * 界面跳转
         */
        fun navigation(arg: RouteArg = RouteArg()) {
            arg.apply {
                this@Builder.bundle.also {
                    title = it.getString("title").toString()
                    primaryId = it.getString("primaryId").toString()
                    scene = it.getString("scene").toString()
                    appScene = module
                    path = "$area://$module/$address"
                }
                bundle = this@Builder.bundle
            }
            //拦截
            when {
                /*interceptUnit.containsKey("$area://$module/$address") -> {
                    interceptUnit["$area://$module/$address"]?.invoke(arg) ?: false
                }*/
                interceptUnit.containsKey(arg.path) -> {
                    interceptUnit[arg.path]?.invoke(arg) ?: false
                }

                interceptUnit.containsKey(module) -> {
                    interceptUnit[module]?.invoke(arg) ?: false
                }

                appNoFixedRoute.containsKey(area) -> {
                    appNoFixedRoute[area]?.dispose(address, arg)
                    true
                }

                else -> {
                    false
                }
            }.also {
                if (!it) {
                    appRoute[area]?.get(module)?.dispose(address, arg)
                }
            }
        }
    }
}





















