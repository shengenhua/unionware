package com.unionware.basicui.view.spinner

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ListPopupWindow
import androidx.viewbinding.ViewBinding

/**
 * Author: sheng
 * Date:2024/12/23
 */
class SpinnerPop(context: Context, view: View, spinnerBackground: Drawable? = null) {
    var popupWindow: ListPopupWindow = object : ListPopupWindow(context) {
    }

    init {
        popupWindow.apply {
            spinnerBackground?.also {
                setBackgroundDrawable(it)
            }
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
            promptPosition = ListPopupWindow.POSITION_PROMPT_BELOW
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            isModal = true
            anchorView = view
            setOnDismissListener {
            }
        }
    }

    fun show() {
        popupWindow.show()
    }

    fun dismiss() {
        popupWindow.dismiss()
    }

    fun isShowing(): Boolean {
         return popupWindow.isShowing
    }

    fun <T, VB : ViewBinding> setBaseAdapter(
        adapter: SpinnerPopAdapter<T, VB>,
        itemClick: ((position: Int, t: T?) -> Unit)? = null,
    ) {
        /*adapter.setOnItemClickListener(object : SpinnerPopAdapter.OnItemClickListener<T> {
            override fun onItemClick(view: View?, position: Int, t: T?) {
                itemClick.invoke(position, t)
                popupWindow.dismiss()
            }
        })*/
        popupWindow.setOnItemClickListener { parent, view, position, id ->
            itemClick?.invoke(position, adapter.getItem(position))
            popupWindow.dismiss()
        }
        popupWindow.setAdapter(adapter)
    }

}