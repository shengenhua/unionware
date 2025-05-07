package unionware.base.route

import android.content.Context
import androidx.startup.Initializer

/**
 *<provider
 *     android:name="androidx.startup.InitializationProvider"
 *     android:authorities="${applicationId}.androidx-startup"
 *     android:exported="false">
 *
 *     <!-- 自动初始化 -->
 *     <meta-data
 *         android:name=".xxxx"
 *         android:value="androidx.startup" />
 * </provider>
 *
 * Author: sheng
 * Date:2024/12/11
 */
abstract class ModuleInitializer : Initializer<String>, UnionwAreRoute {

    lateinit var context: Context
    override fun create(context: Context): String {
        this.context = context
        URouter.build().register(context, this)
        return ""
    }

    override fun init(context: Context) = Unit

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
    override fun area(): Array<String> = super.area()
    override fun module(): Array<String> = arrayOf()
    override fun haveModule(): Boolean = super.haveModule()
}