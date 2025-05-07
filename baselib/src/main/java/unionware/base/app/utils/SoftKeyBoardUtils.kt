package unionware.base.app.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * @Author : pangming
 * @Time : On 2023/6/21 16:31
 * @Description : SoftKeyBoardUtils
 */

object SoftKeyBoardUtils {
    @JvmStatic
    fun hideSoftKeyBoard(context: Context, view: View) {
        val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }
}