package unionware.base.util

import android.content.Context
import unionware.base.ext.getColor
import unionware.base.ext.getColorAccent
import unionware.base.ext.getColorPrimary
import unionware.base.ext.getColorPrimaryDark

/**
 * Author: sheng
 * Date:2025/4/22
 */
class ThemeUtil {
    companion object {
        /**
         * 获取主题颜色
         */
        @JvmStatic
        fun getColor(context: Context, colorId: Int, defaultColor: Int): Int {
            context.theme.let {
                return it.getColor(colorId, defaultColor)
            }
        }

        @JvmStatic
        fun getColor(context: Context, colorId: Int): Int {
            context.theme.let {
                return it.getColor(colorId)
            }
        }

        @JvmStatic
        fun getColorPrimary(context: Context): Int {
            context.theme.let {
                return it.getColorPrimary()
            }
        }

        @JvmStatic
        fun getColorPrimaryDark(context: Context): Int {
            context.theme.let {
                return it.getColorPrimaryDark()
            }
        }

        @JvmStatic
        fun getColorAccent(context: Context): Int {
            context.theme.let {
                return it.getColorAccent()
            }
        }
    }
}