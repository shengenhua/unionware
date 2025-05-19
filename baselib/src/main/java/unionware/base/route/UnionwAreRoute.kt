package unionware.base.route

import android.content.Context

/**
 * Author: sheng
 * Date:2024/8/27
 * 提供接口给每个模块使用
 */
interface UnionwAreRoute {
    /**
     * 区域，query app url
     */
    fun area(): Array<String> = arrayOf("app")//, "query", "url"

    /**
     *  是否只检查 app 模块  query app url
     */
    fun haveModule(): Boolean = true

    /**
     *  app 模块路由的标识
     */
    fun module(): Array<String>


    /**
     *  app 模块路由的name标识，可以写多个，会遍历name，进行匹配，没有会监听全部
     */
    fun names(): Array<String> = arrayOf()


    /**
     *  当前路由标识，用于区分当前路由
     */
    fun tag(): String

    /**
     * 初始化
     */
    fun init(context: Context)

    /**
     * 详情接口
     */
    fun detailsName(): String = ""

    /**
     * 模块 动作处理
     */
    fun dispose(address: String, arg: RouteArg)
}