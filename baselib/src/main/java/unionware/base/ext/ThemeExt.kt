package unionware.base.ext

import android.content.res.Resources
import unionware.base.R

/**
 * Author: sheng
 * Date:2025/4/22
 */

fun Resources.Theme.getColorPrimary(): Int {
    return getColor(androidx.appcompat.R.attr.colorPrimary)
}

fun Resources.Theme.getColorPrimaryDark(): Int {
    return getColor(androidx.appcompat.R.attr.colorPrimaryDark)
}

fun Resources.Theme.getColorAccent(): Int {
    return getColor(androidx.appcompat.R.attr.colorAccent)
}

/**
 * 获取主题颜色
 */
fun Resources.Theme.getColor(colorId: Int, defaultColor: Int): Int {
    return try {
        getColor(colorId)
    } catch (e: Exception) {
        defaultColor
    }
}

fun Resources.Theme.getColor(colorId: Int): Int {
    val typedValue = android.util.TypedValue()
    this.resolveAttribute(colorId, typedValue, true)
    return typedValue.data
}