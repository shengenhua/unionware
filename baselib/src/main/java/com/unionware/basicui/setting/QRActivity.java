package com.unionware.basicui.setting;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import unionware.base.R;
import unionware.base.databinding.QrActivityBinding;

import kotlin.jvm.JvmField;
import unionware.base.app.utils.QRUtils;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;

public class QRActivity extends BaseBindActivity<QrActivityBinding> {

    @JvmField
    @Autowired(name = "title")
    String title = "我的二维码";

    @Override
    public int onBindLayout() {
        return R.layout.qr_activity;
    }

    @Override
    public void initView() {
        getMBind().ivQrTitle.setText(title);
        String content = getIntent().getStringExtra("gson");
        getMBind().ivImage.setImageBitmap(QRUtils.createQRCodeBitmap(content));
        getMBind().ivBack.setOnClickListener(v -> finish());
    }

    @Override
    public void initData() {

    }
}
