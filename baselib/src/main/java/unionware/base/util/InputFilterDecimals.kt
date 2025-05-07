package unionware.base.util

import android.text.InputFilter
import android.text.Spanned

/**
 * Author: sheng
 * Date:2024/9/25
 */
class InputFilterDecimals(private var newScale: Int) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int,
    ): CharSequence? {
        try {
//            val input = (dest.toString() + source.toString()).toDouble()
            val input = dest.toString() + source.toString()
            if (isInRange(newScale, input))
                return null
        } catch (_: NumberFormatException) {
        }
        return ""
    }

    private fun isInRange(a: Int, numberAsString: String): Boolean {
//        val df = DecimalFormat("#.#####")
//        val numberAsString = df.format(c)
        val decimalPlaces = numberAsString.split(".").let {
            if(it.size > 1) it[1].length else 0
        }
        return decimalPlaces <= a
    }
}