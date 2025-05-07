package com.unionware.basicui.main.menu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import unionware.base.app.view.base.viewbinding.BaseBindFragment

/**
 * Author: sheng
 * Date:2025/3/28
 */
abstract class MainBaseFragment<T : ViewBinding> : BaseBindFragment<T>() {
    override fun initObserve() {
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val baseView = super.onCreateView(inflater, container, savedInstanceState)
        baseView?.setPadding(0, topHeight(), 0, 0);
        return baseView
    }

    open fun topHeight(): Int {
        return context?.let { getStatusBarHeight(it) } ?: 0
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    private fun getStatusBarHeight(context: Context): Int {
        // 获得状态栏高度
        val resourceId: Int =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }
}