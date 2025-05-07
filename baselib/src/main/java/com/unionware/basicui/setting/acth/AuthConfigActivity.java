package com.unionware.basicui.setting.acth;

import android.Manifest;
import android.content.Intent;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import unionware.base.R;
import unionware.base.databinding.SettingAuthConfigActivtiyBinding;
import com.unionware.basicui.dialog.DeviceScanPop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.model.req.AuthReq;
import unionware.base.app.ex.DeviceException;
import unionware.base.app.utils.DeviceAuthUtils;
import unionware.base.app.utils.DeviceUtils;
import unionware.base.app.utils.NetUtil;
import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;

@AndroidEntryPoint
public class AuthConfigActivity extends BaseBindActivity<SettingAuthConfigActivtiyBinding> implements AuthContract.View {

    @Inject
    AuthPresenter presenter;//AboutUs

    private ActivityResultLauncher<Intent> launcher;
    private CameraCallback callback;

    @Override
    public int onBindLayout() {
        return R.layout.setting_auth_config_activtiy;
    }

    @Override
    public void initView() {
        presenter.attach(this);

        getMBind().layoutToolbar.toolbar.setNavigationOnClickListener(v -> finish());
        getMBind().layoutToolbar.tbTitle.setText("设备授权");

        registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            getMBind().tvAuthCode.setText(DeviceUtils.getDeviceId(this));
            try {
                boolean isAccredit = DeviceAuthUtils.inspect(this, DeviceAuthUtils.Product.PRODUCT_ID);
                getMBind().tvAuthState.setText(isAccredit ? "已授权" : "未授权");
                getMBind().tvAuthState.setTextColor(getResources().getColor(isAccredit ? unionware.base.R.color.black : unionware.base.R.color.red, this.getTheme()));
            } catch (DeviceException e) {
                getMBind().tvAuthState.setText("未授权");
                getMBind().tvAuthState.setTextColor(getResources().getColor(unionware.base.R.color.red, this.getTheme()));
                ToastUtil.showToastCenter(DeviceAuthUtils.error(e));
            }
        }).launch(DeviceAuthUtils.requiresPermission());
        getMBind().tvAuthCode.setText(DeviceUtils.getDeviceId(this));

        getMBind().btnAuth.setOnClickListener(v -> /*授权*/showScan(false));
        getMBind().ftbApply.setOnClickListener(v -> /*申请*/showScan(true));

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            if (data == null) return;
            IntentResult zxingResult = IntentIntegrator.parseActivityResult(result.getResultCode(), data);
            if (zxingResult.getContents() != null) {
                // 获取扫描结果
                String content = zxingResult.getContents().trim();
                if (callback != null) {
                    callback.onZxingResult(content);
                }
            }
        });

        try {
            DeviceAuthUtils.inspect(this, DeviceAuthUtils.Product.PRODUCT_ID);
        } catch (DeviceException e) {
            ToastUtil.showToastCenter(DeviceAuthUtils.error(e));
        }
    }

    private BasePopupView basePopupView;

    public void showScan(boolean isApply) {
        basePopupView = new XPopup.Builder(this).maxWidth(2000)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asCustom(new DeviceScanPop(this, isApply ? "设备授权申请" : "设备授权", new DeviceScanPop.ScanCallBack() {
                    @Override
                    public void onEdit(@NonNull String text) {
                        authInfo(isApply, text);
                    }

                    @Override
                    public void onQrScanClick() {
                        openCamera(content -> authInfo(isApply, content));
                    }
                }))
                .show();
    }

    private void authInfo(boolean isApply, String content) {
        if (isApply) {
            if (NetUtil.isValidUrl(content)) {
                presenter.applyAuthInfo(content, new AuthReq(DeviceUtils.getDeviceId(AuthConfigActivity.this)));
            } else {
                showFailedView("申请失败(0x0000)");
            }
        } else {
            if (NetUtil.isValidUrl(content)) {
                presenter.getAuthInfo(content);
            } else {
                onSuccessEvent(content);
            }
        }
    }

    @Override
    public void initData() {
    }

    private void openCamera(CameraCallback callback) {
        this.callback = callback;
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("请对准二维码进行扫描");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        Intent zxingIntent = integrator.createScanIntent();
        launcher.launch(zxingIntent);
    }


    /**
     * 需要的权限
     * @return
     */
    private String[] requiresPermission() {
        List<String> permission = new ArrayList<>(
                Arrays.asList(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE));
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permission.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
        return permission.toArray(new String[0]);
    }


    @Override
    public void showFailedView(String msg) {
        if ("申请成功！".equals(msg)) {
            basePopupView.dismiss();
        }
        ToastUtil.showToastCenter(msg);
    }

    @Override
    public void onSuccessEvent(String content) {
        try {
            DeviceAuthUtils.inspect(this, content, DeviceAuthUtils.Product.PRODUCT_ID);
            showFailedView("激活成功");
            basePopupView.dismiss();
            getMBind().tvAuthState.setText("已授权");
            getMBind().tvAuthState.setTextColor(getResources().getColor(android.R.color.black));
        } catch (DeviceException e) {
            showFailedView(String.format("激活失败(%s)", e.getError()));
        } catch (Exception e) {
            showFailedView(String.format("激活失败(%s)", e.getMessage()));
        }
    }

    public interface CameraCallback {
        void onZxingResult(String content);
    }
}
