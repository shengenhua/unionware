package unionware.base.ext

import java.math.BigDecimal
import java.math.RoundingMode


fun String.strToNumber(): Double {
    if (this.isEmpty()) {
        return 0.0
    }
    return this.bigDecimalToZeros().toDouble()
}

fun String.strToInt(default: Int = 0): Int {
    if (this.isEmpty()) {
        return default
    }
    return this.bigDecimalToZeros().toInt()
}

/**
 *  去掉多余的0
 */
fun Any?.bigDecimalToZeros(): String {
    return this.toString().tryBigDecimalToZeros() ?: this.toString()
}


/**
 *  去掉多余的0
 */
fun String?.tryBigDecimalToZeros(): String? {
    try {
        return this?.bigDecimalToZeros() ?: this
    } catch (_: Exception) {
    }
    return this
}

/**
 *  去掉多余的0
 */
fun String.bigDecimalToZeros(): String {
    if (this.isEmpty()) {
        return ""
    }
    val bigDecimal = BigDecimal(this.trim())
    if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
        return "0"
    }
    return bigDecimal.stripTrailingZeros().toPlainString()
}

/**
 *  除以  /
 */
fun String?.bigDecDivideToZeros(str: String?, scale: Int = 4): String {
    if (this.isNullOrEmpty()) {
        return ""
    }
    if (str.isNullOrEmpty()) {
        return ""
    }
    if (BigDecimal(str).compareTo(BigDecimal.ZERO) == 0) {
        return ""
    }
    return bigDecimalDivide(str, scale).let {
        if (it.compareTo(BigDecimal.ZERO) == 0) {
            return@let ""
        }
        it.stripTrailingZeros().toPlainString()
    }
}

/**
 *  除以  /
 */
fun String.bigDecimalDivide(str: String): BigDecimal {
    return bigDecimalDivide(str, 4)
}

/**
 *  除以  /
 */
fun String.bigDecimalDivide(str: String, scale: Int): BigDecimal {
    return BigDecimal(this.replace(" ", "")).divide(
        BigDecimal(str), scale, RoundingMode.HALF_UP
    )
}

/**
 *  减法 -
 */
fun String?.bigDecSubtractToZeros(str: String?): String {
    if (this.isNullOrEmpty()) {
        return str ?: ""
    }
    if (str.isNullOrEmpty()) {
        return this
    }
    return bigDecimalSubtract(str).let {
        if (it.compareTo(BigDecimal.ZERO) == 0) {
            return@let ""
        }
        it.stripTrailingZeros().toPlainString()
    }
}

/**
 *  减法 -
 */
fun String.bigDecimalSubtract(str: String): BigDecimal {
    return BigDecimal(this).subtract(BigDecimal(str))
}

/**
 *  加法 +
 */
fun String?.bigDecAddToZeros(str: String?): String {
    if (this.isNullOrEmpty()) {
        return str ?: ""
    }
    if (str.isNullOrEmpty()) {
        return this
    }
    return bigDecimalAdd(str).let {
        if (it.compareTo(BigDecimal.ZERO) == 0) {
            return@let "0"
        }
        it.stripTrailingZeros().toPlainString()
    }
}

/**
 *  加法 +
 */
fun String.bigDecimalAdd(str: String): BigDecimal {
    return BigDecimal(this).add(BigDecimal(str));
}


/**
 *  乘法 *
 */
fun String?.bigDecMultiplyToZeros(str: String?): String {
    if (this.isNullOrEmpty()) {
        return ""
    }
    if (str.isNullOrEmpty()) {
        return ""
    }
    return bigDecimalMultiply(str).let {
        if (it.compareTo(BigDecimal.ZERO) == 0) {
            return@let "0"
        }
        it.stripTrailingZeros().toPlainString()
    }
}

/**
 *  乘法 +
 */
fun String.bigDecimalMultiply(str: String): BigDecimal {
    return BigDecimal(this).multiply(BigDecimal(str));
}

/**
 *  控制不保留了小数
 */
fun String.bigDecimalScale(): String {
    return this.bigDecimalScale(0)
}

/**
 *  控制保留后几位小数
 */
fun String.bigDecimalScale(newScale: Int): String {
    return this.bigDecimalScale(newScale, RoundingMode.HALF_UP)
}

fun String.bigDecimalScale(newScale: Int, mode: RoundingMode): String {
    if (this.isEmpty()) {
        return ""
    }
    return BigDecimal(this).setScale(newScale, mode).toPlainString()
}