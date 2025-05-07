@file:Suppress("DEPRECATION")

package unionware.base.app.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.RequiresPermission
import unionware.base.app.BaseApplication
import java.util.regex.Pattern

object NetUtil {

    private const val HTTP_URL_PATTERN: String =
        "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"

    private val HTTP_URL_PATTERN_COMPILED: Pattern = Pattern.compile(HTTP_URL_PATTERN)

    @JvmStatic
    fun isValidUrl(url: String): Boolean {
        return HTTP_URL_PATTERN_COMPILED.matcher(url).matches()
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @JvmStatic
    fun checkNet(): Boolean {
        val context = BaseApplication.instance
        return isWifiConnection(context) || isStationConnection(context)
    }

    @JvmStatic
    fun checkNetToast(): Boolean {
        val isNet = checkNet()
        if (!isNet) {
            ToastUtil.showToast("网络不给力哦！")
        }
        return isNet
    }

    /**
     * 是否使用基站联网
     *
     * @param context
     * @return
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @JvmStatic
    fun isStationConnection(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            ?: return false
        val networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return if (networkInfo != null) {
            networkInfo.isAvailable && networkInfo.isConnected
        } else false
    }

    /**
     * 是否使用WIFI联网
     *
     * @param context
     * @return
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @JvmStatic
    fun isWifiConnection(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            ?: return false
        val networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return if (networkInfo != null) {
            networkInfo.isAvailable && networkInfo.isConnected
        } else false
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @JvmStatic
    fun isNetWorkState(context: Context): NetType {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = manager.activeNetworkInfo
        if (activeNetwork != null) {
            if (activeNetwork.isConnected) {
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                    // Logger.v(TAG, "当前WiFi连接可用 ");
                    return NetType.WIFI
                } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                    // Logger.v(TAG, "当前移动网络连接可用 ");
                    return NetType.NET_4G
                }
            } else {
                // Logger.v(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                return NetType.NO_NET
            }
        } else {
            // Logger.v(TAG, "当前没有网络连接，请确保你已经打开网络 ");
            return NetType.NO_NET
        }
        return NetType.NO_NET
    }

    enum class NetType {
        WIFI, NET_4G, NO_NET
    }
}
