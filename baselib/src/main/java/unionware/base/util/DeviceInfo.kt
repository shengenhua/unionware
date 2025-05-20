package unionware.base.util

import android.content.Context
import android.os.Build
import unionware.base.app.utils.DeviceUtils
import java.util.Locale

/**
 * Author: sheng
 * Date:2025/5/13
 */
class DeviceInfo(context: Context) {
    private val appContext = context.applicationContext

    // 设备基础信息
    /**设备序列号、唯一码 */
    fun getSERIAL(): String? {
        val serial = Build.getSerial()
        if (serial.isNotEmpty() && Build.UNKNOWN != serial) {
            return serial
        }
        return DeviceUtils.getDeviceId(appContext)
    }

    /**手机型号 */
    fun getModel() = Build.MODEL

    /**设备名称 */
    fun getDeviceName() = Build.DEVICE

    /**设备品牌 */
    fun getBrand() = Build.BRAND

    /**制造商 */
    fun getManufacturer() = Build.MANUFACTURER

    // 系统语言信息
    /**系统语言简写 */
    fun getSystemLanguage() = Locale.getDefault().language

    /**地区代码 */
    fun getSystemCountry() = Locale.getDefault().country

    /**获取当前SDK 版本 */
    fun getSDK() = Build.VERSION.SDK_INT

    /**获取当前系统 版本 */
    fun getRELEASE() = Build.VERSION.RELEASE

    /**
     * Build.ID的用途和功能
     * ‌设备识别‌：通过Build.ID可以获取设备的制造商和型号信息，这对于应用程序的设备适配和测试非常有用，可以根据Build.ID来确定特定设备的特殊配置和行为‌
     *
     * ‌版本兼容性‌：Build.ID可以帮助获取设备的固件版本，从而在应用程序中进行版本兼容性的处理。不同版本的固件可能有不同的API支持和行为表现，通过判断Build.ID可以针对不同版本进行不同的处理逻辑‌
     *
     * ‌功能支持‌：通过Build.ID可以判断设备上是否支持某些功能或硬件。例如，某些旧版本的固件可能不支持照相机或蓝牙功能，可以通过Build.ID来判断设备是否支持这些功能，并在应用程序中进行相应的处理‌
     *
     * */
    fun getID() = Build.ID
}