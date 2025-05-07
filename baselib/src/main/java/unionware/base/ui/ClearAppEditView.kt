package unionware.base.ui

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import unionware.base.R

class ClearAppEditView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {
    private val mClearDrawable = DrawableCompat.wrap(
        ContextCompat.getDrawable(context, R.drawable.input_delete_ic)!!
    )
    private var onClearListener: ((view: TextView) -> Unit)? = null

    fun setOnClearListener(unit: (view: TextView) -> Unit) {
        onClearListener = unit
    }

    init {
        mClearDrawable.setBounds(
            0,
            0,
            mClearDrawable.intrinsicWidth,
            mClearDrawable.intrinsicHeight
        )
        setDrawableVisible(false)
    }

    private fun setDrawableVisible(visible: Boolean) {
        if (mClearDrawable.isVisible == visible) {
            return
        }

        mClearDrawable.setVisible(visible, false)
        val drawables = compoundDrawablesRelative
        setCompoundDrawablesRelative(
            drawables[0],
            drawables[1],
            if (visible) mClearDrawable else null,
            drawables[3]
        )
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (isFocused) {
            setDrawableVisible(text?.isNotEmpty() ?: false)
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused && text != null) {
            setDrawableVisible(this.text?.isNotEmpty() ?: false)
        } else {
            setDrawableVisible(false)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()

        // 是否触摸了 Drawable
        var touchDrawable = false
        // 获取布局方向
        val layoutDirection = layoutDirection
        if (layoutDirection == LAYOUT_DIRECTION_LTR) {
            // 从左往右
            touchDrawable = x > width - mClearDrawable.intrinsicWidth - paddingEnd &&
                    x < width - paddingEnd
        } else if (layoutDirection == LAYOUT_DIRECTION_RTL) {
            // 从右往左
            touchDrawable = x > paddingStart &&
                    x < paddingStart + mClearDrawable.intrinsicWidth
        }

        if (mClearDrawable.isVisible && touchDrawable) {
            if (event.action == MotionEvent.ACTION_UP) {
                setText("")
                requestFocus()
                onClearListener?.invoke(this)
            }
            return true
        }
        return super.onTouchEvent(event)
    }

}
