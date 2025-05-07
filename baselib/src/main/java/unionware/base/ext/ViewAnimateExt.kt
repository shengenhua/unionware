package com.unionware.lib_base.utils.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View

/**
 * Author: sheng
 * Date:2024/9/12
 */

fun View.leftToRightAnimate(
    viewVis: Int = View.VISIBLE, duration: Long = 150, action: (() -> Unit)? = null,
) {
    animate().translationX(-width.toFloat()) // 开始时的X坐标，0表示视图在其父视图的左侧
        .setDuration(duration) // 动画持续时间，单位毫秒
        .alpha(0f) // 开始时的透明度，1表示不透明
        .withEndAction {
            action?.invoke()
            if (viewVis == View.GONE) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                animate().translationX(0f).alpha(1f)
            }
        }
}

fun View.rightToLeftAnimate(
    viewVis: Int = View.VISIBLE, duration: Long = 150, action: (() -> Unit)? = null,
) {
    animate().translationX(width.toFloat()) // 开始时的X坐标，0表示视图在其父视图的左侧
        .setDuration(duration) // 动画持续时间，单位毫秒
        .alpha(0f) // 开始时的透明度，1表示不透明
        .withEndAction {
            action?.invoke()
            if (viewVis == View.GONE) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                animate().translationX(0f).alpha(1f)
            }
        }
}

fun View.topToBotAnimate(
    viewVis: Int = View.VISIBLE, duration: Long = 150, action: (() -> Unit)? = null,
) {
    animate().translationY(-height.toFloat()).setDuration(duration).alpha(0f).withEndAction {
        action?.invoke()
        if (viewVis == View.GONE) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
            animate().translationY(0f).alpha(1f)
        }
    }
}

fun View.botToTopAnimate(
    viewVis: Int = View.VISIBLE, duration: Long = 150, action: (() -> Unit)? = null,
) {
// 关闭（下上）动画
    animate().translationY(height.toFloat()).setDuration(duration).alpha(0f).withEndAction {
        action?.invoke()
        if (viewVis == View.GONE) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
            animate().translationY(0f).alpha(1f).withEndAction {}
        }
    }
}

fun View.expandAnimate(expand: Boolean = true, duration: Long = 150, height: Int = -1) {
    if (expand) {
        visibility = View.VISIBLE
    }
    measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    val animator = ValueAnimator.ofInt(
        if (expand) 0 else measuredHeight,
        if (expand) measuredHeight else 0
    )
    animator.addUpdateListener {
        if ((animator.animatedValue as Int) != 1 && (animator.animatedValue as Int) != 0) {
            val params = layoutParams
            params?.height = animator.animatedValue as Int
            layoutParams = params
        }
    }
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            visibility = if (expand) View.VISIBLE else View.GONE
            if (expand && height != -1) {
                val params = layoutParams
                params?.height = height
                layoutParams = params
            }
        }
    })
    animator.duration = duration
    animator.start()
}

fun View.scaleAnimate(
    viewVis: Int = View.VISIBLE, duration: Long = 150, action: (() -> Unit)? = null,
) {
    // 缩放隐藏动画
    animate().scaleX(0f).scaleY(0f).setDuration(duration).alpha(0f).withEndAction {
        action?.invoke()
        if (viewVis == View.GONE) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
            animate().scaleX(1f).scaleY(1f).alpha(1f)
        }
    }
}