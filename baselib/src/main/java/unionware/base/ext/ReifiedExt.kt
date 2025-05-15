package unionware.base.ext

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle

inline fun <reified T : Activity> Activity.startActivity() {
    startActivity(Intent(this, T::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    })
}

inline fun <reified T : Activity> Context.startActivity() {
    startActivity(Intent(this, T::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    })
}

inline fun <reified T : Activity> Context.startActivity(bundle: Bundle?) {
    startActivity(Intent(this, T::class.java).apply {
        bundle?.let { this.putExtras(it) }
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    })
}

inline fun <reified T : Activity> Context.startActivity(bundle: Bundle?, flags: Int) {
    startActivity(Intent(this, T::class.java).apply {
        bundle?.let { this.putExtras(it) }
        addFlags(flags);
    })

}
fun Context.getTopActivity(className: String): Activity? {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    if (activityManager.appTasks.isNotEmpty()) {
        return activityManager.appTasks[0].taskInfo.topActivity as Activity
    }
    return null
}

fun Context.isActivityOnTop(className: String): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    if (activityManager.appTasks.isNotEmpty()) {
        val topActivity = activityManager.appTasks[0].taskInfo.topActivity
        if (topActivity != null && topActivity.className == className) {
            return true
        }
    }
    return false
}
