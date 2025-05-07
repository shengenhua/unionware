package com.unionware.mes.view.dynamic

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.unionware.mes.app.RouterMESPath
import com.unionware.mes.viewmodel.dynamic.DynamicViewModel
import com.unionware.path.RouterPath
import dagger.hilt.android.AndroidEntryPoint

/**
 * 动态装配
 * Author: sheng
 * Date:2024/11/19
 */
@AndroidEntryPoint
@Route(path = RouterMESPath.MES.PATH_MES_DYNAMIC)
open class OriginalDynamicActivity : DynamicActivity<DynamicViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        super.initView()
        /*binding?.apply {
            val viewTreeObserver = root.viewTreeObserver
            viewTreeObserver.addOnGlobalLayoutListener {
                val rect = Rect()
                root.getWindowVisibleDisplayFrame(rect)
                val screenHeight = root.rootView.height
                val keyboardHeight = screenHeight - rect.bottom
                if (keyboardHeight > screenHeight * 0.15) {
                    // 软键盘显示，调整布局
                    // 例如，将布局向上推移 keyboardHeight 像素
                    root.translationY = -keyboardHeight.toFloat()
                } else {
                    // 软键盘隐藏，恢复布局
                    root.translationY = 0f
                }
            }
        }*/
    }
}