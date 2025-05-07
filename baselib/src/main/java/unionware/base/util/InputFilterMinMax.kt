package unionware.base.util

import android.text.InputFilter
import android.text.Spanned

/**
 * Author: sheng
 * Date:2024/9/25
 */
class InputFilterMinMax(private var min: Int, private var max: Int) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int,
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toInt()
            if (isInRange(min, max, input))
                return null
        } catch (_: NumberFormatException) {
        }
        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) {
            c in a..b
        } else {
            c in b..a
        }
    }
}