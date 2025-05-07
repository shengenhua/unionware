package com.unionware.lib_base.utils.ext

import android.os.Build
import android.provider.ContactsContract.Data
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


/*
fun Any.formatter(pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault(Locale.Category.FORMAT))
    return sdf.format(System.currentTimeMillis())
}*/

fun Data.formatter(pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault(Locale.Category.FORMAT))
    return sdf.format(this)
}

fun Long.formatter(pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault(Locale.Category.FORMAT))
    return sdf.format(this)
}

fun String.formatter(pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault(Locale.Category.FORMAT))
    return try {
        sdf.format(this.yMdHmsToDate())
    } catch (e: Exception) {
        this
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.formatter(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return formatter.format(this)
}

fun Data.formatter(): String {
    return formatter("yyyy-MM-dd HH:mm:ss")
}

fun Long.formatter(): String {
    return formatter("yyyy-MM-dd HH:mm:ss")
}

fun Data.formatterYMD(): String {
    return formatter("yyyy-MM-dd")
}

fun Long.formatterYMD(): String {
    return formatter("yyyy-MM-dd")
}

fun String.yMdHmsToDate(): Date {
    val dataSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT))
    return dataSdf.parse(this) as Date
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.formatter(): String {
    return formatter("yyyy-MM-dd HH:mm:ss")
}