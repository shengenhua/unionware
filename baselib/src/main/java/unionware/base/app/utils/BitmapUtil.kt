package unionware.base.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File


object BitmapUtil {

    fun getFileNameFromUri(fileUri: Uri?): String? {
        var fileName: String? = null
        if (fileUri != null) {
            val filePath = fileUri.path
            val lastSlashIndex = filePath!!.lastIndexOf(File.separator)
            fileName = if (lastSlashIndex != -1) {
                filePath.substring(lastSlashIndex + 1)
            } else {
                filePath
            }
        }
        return fileName
    }

    fun getImageFromUri(context: Context, imageUri: Uri?): Bitmap? {
        val contentResolver = context.contentResolver
        try {
            contentResolver.openInputStream(imageUri!!).use { inputStream ->
                // 在这里，你可以根据需要调整图片的大小，以减少内存消耗
                return BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 图片压缩
     *
     * @param filepath
     * @param bitmap
     * @return
     */
    fun compress(bitmap: Bitmap?): Bitmap {
        //设置缩放比
        val radio = 6
        val result = Bitmap.createBitmap(
            bitmap!!.width / radio,
            bitmap.height / radio,
            Bitmap.Config.RGB_565
        )
        val canvas = Canvas(result)
        val rectF =
            RectF(0f, 0f, (bitmap.width / radio).toFloat(), (bitmap.height / radio).toFloat())
        //将原图画在缩放之后的矩形上
        canvas.drawBitmap(bitmap, null, rectF, null)
        val bos = ByteArrayOutputStream()
        result.compress(Bitmap.CompressFormat.JPEG, 100, bos)

        return result
    }


    fun bitmapToBase64(bitmap: Bitmap?): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun base64ToBitmap(base64: String?): Bitmap {
        // 将base64字符串取出来，记得只需要后面编码的一段，不需要“base64”，转成Base64的ByteArray
        val dec = Base64.decode(base64, Base64.DEFAULT)
        // 将ByteArray转Bitmap
        val bitmap = BitmapFactory.decodeByteArray(dec, 0, dec.size)
        return bitmap
    }
}