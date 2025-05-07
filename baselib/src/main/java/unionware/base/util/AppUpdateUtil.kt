package unionware.base.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Base64
import androidx.core.content.FileProvider.getUriForFile
import java.io.File
import java.io.FileOutputStream

/**
 * Author: sheng
 * Date:2025/3/31
 */
class AppUpdateUtil {
    companion object {

        @JvmStatic
        fun checkAppVersion(context: Context, version: String?): Boolean {
//        获取当前app版本
            val currentVersion = getVersionName(context)
            // 比较版本号
            return compareVersion(version, currentVersion) > 0
        }

        private fun compareVersion(ver: String?, currentVer: String): Int {
            if (TextUtils.isEmpty(ver) || TextUtils.isEmpty(currentVer)) {
                return 0
            }
            val version = ver?.replace(".", "")
            val currentVersion = currentVer.replace(".", "")
            val versionInt = version?.toInt() ?: 0
            val currentVersionInt = currentVersion.toInt()
            return versionInt - currentVersionInt
        }


        /**
         * 获取指定包名的版本号
         *
         * @param context 本应用程序上下文
         * @return
         * @throws Exception
         */
        @JvmStatic
        fun getVersionName(context: Context): String {
            // 获取packagemanager的实例
            val packageManager = context.packageManager
            try {
                val packInfo = packageManager.getPackageInfo(context.packageName, 0)
                val version = packInfo.versionName
                return version
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return ""
        }

        @JvmStatic
        fun base64ToFile(base64: String?, fileName: String): File {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            // 将ByteArray转Bitmap
            val file = File(dir, fileName)//File("输出路径\\123.png")
            //选择文件保存的位置
            val fileOutputStream = FileOutputStream(file)
            //写入
            fileOutputStream.write(Base64.decode(base64, Base64.DEFAULT))
            //关闭流
            fileOutputStream.close()
            return file
        }

        /**
         * 调用系统安装apk
         */
        @JvmStatic
        fun installAPK(context: Context, apkFile: File?) {
            apkFile?.absolutePath?.let {
                context.startActivity(getInstallAppIntent(context, it))
            }
        }


        private fun getInstallAppIntent(context: Context?, filePath: String): Intent? {
            //apk文件的本地路径
            val apkFile = File(filePath)
            if (!apkFile.exists()) {
                return null
            }
            return getInstallAppIntent(context, apkFile)
        }

        private fun getInstallAppIntent(context: Context?, apkFile: File?): Intent {
            val intent = Intent(Intent.ACTION_VIEW)
            val contentUri = getUriForFile(context!!, apkFile)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
            return intent
        }

        /**
         * 将文件转换成uri(支持7.0)
         *
         * @param mContext
         * @param file
         * @return
         */
        @JvmStatic
        fun getUriForFile(mContext: Context, file: File?): Uri? {
            val fileUri: Uri? =
                getUriForFile(mContext, mContext.packageName + ".unionware.fileprovider", file!!)
            return fileUri
        }
    }
}