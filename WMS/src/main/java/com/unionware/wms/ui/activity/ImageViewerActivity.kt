package com.unionware.wms.ui.activity

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.unionware.wms.R
import com.unionware.wms.databinding.ActivityWmsImageViewerBinding
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.app.view.base.viewbinding.BaseBindActivity
import unionware.base.ext.loadImage

/**
 * Author: sheng
 * Date:2024/12/16
 */
@AndroidEntryPoint
class ImageViewerActivity : BaseBindActivity<ActivityWmsImageViewerBinding>() {
    override fun onBindLayout(): Int = R.layout.activity_wms_image_viewer

    @JvmField
    @Autowired(name = "imageUrl")
    var imageUrl: String? = null

    @JvmField
    @Autowired(name = "imageName")
    var imageName: String? = null

    override fun initView() {
        mBind.icWmsToolbar.tbTitle.text = imageName
        mBind.icWmsToolbar.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        imageUrl?.let { mBind.ivImage.loadImage(it) }
    }

    override fun initData() = Unit
}