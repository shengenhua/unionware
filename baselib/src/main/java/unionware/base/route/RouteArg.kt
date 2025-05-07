package unionware.base.route

import android.os.Bundle
import java.io.Serializable

/**
 * Author: sheng
 * Date:2024/8/29
 */
class RouteArg : Serializable {
    /**地址*/
    var path: String = ""

    /**app*/
    var appScene: String = ""

    /**
     * 标题
     */
    var title: String = ""

    /**
     * 场景
     */
    var scene: String? = null

    /**
     * id
     */
    var primaryId: String? = null

    /**
     * id
     */
    var bundle: Bundle? = null

    /**
     * 回调
     */
    var callback: RouteCallback? = null

    interface RouteCallback {
        fun onResult()
    }
}
