package com.unionware.basicui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.ListPopupWindow
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * Author: sheng
 * Date:2024/11/4
 */
class MoreViewDialog<T>(
    private val context: Context,
    /**显示的view*/
    private val anchorView: View,
    val adapter: BaseEditSpinnerAdapter<T>,
) {
    private var popupWindow: ListPopupWindow = ListPopupWindow(context).apply {
        setBackgroundDrawable(
            context.resources.getDrawable(
                unionware.base.R.drawable.common_border_white,
                context.theme
            )
        )

        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
        promptPosition = ListPopupWindow.POSITION_PROMPT_BELOW
        isModal = true
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = 300
        anchorView = this@MoreViewDialog.anchorView
        setAdapter(adapter)
    }

    fun setOnItemClickListener(unit: (T) -> Unit) =
        popupWindow.setOnItemClickListener { p, view, position, id ->
            unit.invoke(this@MoreViewDialog.adapter.getItem(position))
        }

    fun isShowing() = popupWindow.isShowing
    fun dismiss() = popupWindow.dismiss()
    fun show() = popupWindow.show()

    abstract class BaseEditSpinnerAdapter<T>(
        private val context: Context,
        @LayoutRes val layout: Int,
        var items: List<T> = emptyList(),
    ) : BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): T = items[position]
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(layout, null)
            convert(BaseViewHolder(view), position, getItem(position))
            return view
        }

        abstract fun convert(holder: BaseViewHolder, position: Int, t: T)
    }
}