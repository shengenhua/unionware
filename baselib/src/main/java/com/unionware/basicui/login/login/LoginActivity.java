package com.unionware.basicui.login.login;

import static unionware.base.ext.StatusBarExtKt.immersionStatusBar;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.tencent.mmkv.MMKV;
import com.unionware.basicui.app.BasicAppProvider;
import com.unionware.basicui.dialog.MoreViewDialog;
import com.unionware.basicui.setting.ConfigNetActivity;
import com.unionware.basicui.setting.acth.AuthConfigActivity;
import com.unionware.path.RouterPath;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.jvm.JvmField;
import unionware.base.R;
import unionware.base.app.ex.DeviceException;
import unionware.base.app.utils.DeviceAuthUtils;
import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;
import unionware.base.databinding.LoginActivityBinding;
import unionware.base.model.bean.ClientInfoBean;
import unionware.base.model.req.LoginReq;
import unionware.base.model.resp.DBCenterResp;
import unionware.base.model.resp.UserInfoResp;
import unionware.base.room.DatabaseProvider;
import unionware.base.room.ThreadTask;
import unionware.base.room.table.User;
import unionware.base.route.URouter;


/**
 *
 */

@AndroidEntryPoint
@Route(path = RouterPath.Person.PATH_PERSON_LOGIN)
public class LoginActivity extends BaseBindActivity<LoginActivityBinding> implements LoginContract.View {
    private LoginReq mLoginReq; // 登录接口参数包装实体
    private LoadingPopupView loading;
    private ActivityResultLauncher<Intent> launcher;
    private MMKV mmkv = MMKV.mmkvWithID("app");
    private String baseUrl;
    private MoreViewDialog<User> moreViewDialog;

    @JvmField
    @Autowired(name = "isAuto")
    boolean isAuto;


    @Inject
    LoginPresenter presenter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerActivityResult();
    }

    @Override
    public int onBindLayout() {
        return R.layout.login_activity;
    }

    @Override
    public void initView() {
        immersionStatusBar(this, true, android.R.color.transparent, true, 0.2f);
        presenter.attach(this);
        // 先判断有没有权限
        requertPermissions();
        baseUrl = mmkv.decodeString("url", "");

        loading = new XPopup.Builder(this).dismissOnTouchOutside(false).asLoading();
        moreViewDialog = getUserMoreViewDialog();
        mBind.etLoginUser.setOnEditorActionListener((v, actionId, event) -> {
            autoLogin(v.getText().toString());
            getMBind().etLoginPwd.postDelayed(() -> getMBind().etLoginPwd.requestFocus(), 50);
            return true;
        });

        mBind.etLoginPwd.setOnEditorActionListener((v, actionId, event) -> {
            autoLogin(v.getText().toString());
            return true;
        });

        mBind.ivUserMore.setOnClickListener(v -> {
            if (!moreViewDialog.isShowing() && !moreViewDialog.getAdapter().getItems().isEmpty()) {
                moreViewDialog.show();
            }
        });
        mBind.tvLoginAcSet.setOnClickListener(view -> {
            if ("".equals(baseUrl)) {//
                ToastUtil.showToast(R.string.server_address_is_null);
                return;
            }
            presenter.getDBCenterList(true);
        });
        mBind.btnLogin.setOnClickListener(view -> {
            if ("".equals(baseUrl)) {
                ToastUtil.showToast(R.string.server_address_is_null);
                return;
            }
            getUserName();
            getPassword();
            presenter.login(mLoginReq);
        });
        mBind.tvLoginLanguage.setOnClickListener(view -> showLanguageView());
        mBind.tvLoginConfig.setOnClickListener(view -> launcher.launch(new Intent(this, ConfigNetActivity.class)));
        mBind.tvLoginAuth.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, AuthConfigActivity.class)));
        View.OnClickListener openQr = v -> openQrScan();
        mBind.ivLoginUserLogo.setOnClickListener(openQr);
        mBind.ivLoginPwdLogo.setOnClickListener(openQr);
    }

    @NonNull
    private MoreViewDialog<User> getUserMoreViewDialog() throws RuntimeException {
        List<User> users = ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getUserDao().queryByTag(baseUrl));
        if (users == null) {
            users = new ArrayList<>();
        }
        MoreViewDialog<User> moreViewDialog = new MoreViewDialog<>(this, getMBind().clUserName,
                new MoreViewDialog.BaseEditSpinnerAdapter<>(this, R.layout.item_user_name_text, users) {
                    @Override
                    public void convert(@NonNull BaseViewHolder holder, int position, User user) {
                        holder.<TextView>getView(R.id.tvName).setText(user.getUserName());
                    }
                });
        moreViewDialog.setOnItemClickListener(user -> {
            mBind.etLoginUser.setText(user.getUserName());
            setLoginAc(user.getDbId(), user.getDbName());
            moreViewDialog.dismiss();
            return null;
        });
        return moreViewDialog;
    }

    private void requertPermissions() {
        //申请权限获取
        registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            //每次进登录，都要校验一次
            MMKV mmkv = MMKV.defaultMMKV();
            mmkv.encode("inspect_time", 0L);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                exterPermissions.launch(intent);
                return;
            }
            inspectDevice();
        }).launch(DeviceAuthUtils.requiresPermission());
    }

    private final ActivityResultLauncher<Intent> exterPermissions = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        inspectDevice();
    });

    /**
     * 检查当前设备是否注册校验
     */
    private void inspectDevice() {
        try {
            DeviceAuthUtils.inspectByDateTips(this, DeviceAuthUtils.Product.PRODUCT_ID);
        } catch (DeviceException e) {
            if (e.getError().equals("xxxxxx15")) {
                new XPopup.Builder(this).asConfirm("提示", e.getMsg(), null).show();
            } else {
                ToastUtil.showToastCenter(DeviceAuthUtils.error(e));
            }
        }
    }

    @Override
    public void initData() {
        mLoginReq = new LoginReq(false, 1, 1, "{\"KickoutControlMode\": \"UNIONWARE_WMS_PDA\"}", new ClientInfoBean(8, "{\"KickoutControlMode\": \"UNIONWARE_WMS_PDA\"}"));
        mmkv.encode("isLogin", false);
        mLoginReq.setLanguageId("2052");

//        mBind.etLoginUser.setText(mmkv.decodeString("userName", ""));//保存登陆账号

        if (!TextUtils.isEmpty(baseUrl)) {
            String userName = MMKV.mmkvWithID("app").decodeString("userName", "");
            List<User> localUser = ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getUserDao().queryByTag(baseUrl));
            if (localUser == null) {
                localUser = new ArrayList<>();
            }
            if (localUser.isEmpty()) {
                mBind.etLoginUser.setText(mmkv.decodeString("userName", ""));
                presenter.getDBCenterList(false);
            } else {
                User oneUser = localUser.get(0);
                localUser.stream().filter(user -> Objects.equals(userName, user.getUserName())).findFirst().ifPresentOrElse(user -> {
                    mBind.etLoginUser.setText(user.getUserName());
                    setLoginAc(user.getDbId(), user.getDbName());
                }, () -> {
                    mBind.etLoginUser.setText(oneUser.getUserName());
                    setLoginAc(oneUser.getDbId(), oneUser.getDbName());
                });
            }
        }
        moreViewDialog = getUserMoreViewDialog();

        if (!TextUtils.isEmpty(mBind.etLoginUser.getText())) {
            getMBind().etLoginPwd.postDelayed(() -> getMBind().etLoginPwd.requestFocus(), 50);
        }
    }

    @Override
    public String getUserName() {
        mLoginReq.setUsername(String.valueOf(mBind.etLoginUser.getText()));
        return mLoginReq.getUsername();
    }

    @Override
    public String getPassword() {
        mLoginReq.setPassword(String.valueOf(mBind.etLoginPwd.getText()));
        return mLoginReq.getPassword();
    }

    @Override
    public void showDBCenterView(List<DBCenterResp> data, boolean showDialog) {
        String[] dbs = data.stream().map(DBCenterResp::getName).toArray(String[]::new);
        if (showDialog) {
            new XPopup.Builder(this).asBottomList("请选择账套", dbs, (position, text) -> {
                setLoginAc(data.get(position).getId(), text);
            }).show();
        } else {
            setLoginAc(data.get(0).getId(), data.get(0).getName());
        }
    }

    private void setLoginAc(String data, String text) {
        mLoginReq.setAcctID(data);
        mBind.tvLoginAcSet.setText(text);
    }

    private void showLanguageView() {
        String[] languages = new String[]{"简体中文", "繁体中文", "英文"};
        new XPopup.Builder(this).asBottomList("请选择语言", languages, (position, text) -> {
            mBind.tvLoginLanguage.setText(text);
        }).show();
    }

    @Override
    public void showFailedView(String msg) {
        ToastUtil.showToastCenter(msg);
//        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingView(String tips) {
        runOnUiThread(() -> {
            loading.setTitle(tips);
            loading.show();
        });
    }

    @Override
    public void hideLoadingView() {
        try {
            if (loading != null && loading.isShow()) {
                loading.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveUserInfo(UserInfoResp userInfo) {
//        MMKV kv = MMKV.mmkvWithID("app");
        mmkv.encode("userName", mLoginReq.getUsername());//保存登陆的账号
        mmkv.encode("userId", userInfo.getUserId()); // 用户id
        mmkv.encode("dbId", userInfo.getDbId());
        mmkv.encode("orgId", userInfo.getOrganization().getId());
        mmkv.encode("isLogin", true);
        mmkv.encode("loginReq", mLoginReq);

        User user = new User();
        user.setDbId(mLoginReq.getAcctID());
        user.setDbName(getMBind().tvLoginAcSet.getText().toString());
        user.setTag(baseUrl);
        user.setUserName(mLoginReq.getUsername());
        ThreadTask.start(() -> {
            DatabaseProvider.getInstance().getUserDao().deleteList(baseUrl, mLoginReq.getUsername());
            DatabaseProvider.getInstance().getUserDao().insert(user);
        });
        jumpToMain();
    }

    @Override
    public void jumpToMain() {
        if (!isAuto) {
            URouter.build().builder(BasicAppProvider.getPath(BasicAppProvider.MAIN))
                    .withObject("isLogin", true)
                    .navigation();
        }
        finish();
    }

    private void registerActivityResult() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            if (data != null) {
                if (300 == result.getResultCode()) {
                    baseUrl = data.getStringExtra("url");
                } else {
                    IntentResult zxingResult = IntentIntegrator.parseActivityResult(result.getResultCode(), data);
                    if (zxingResult.getContents() != null) {
                        // 获取扫描结果
                        String content = zxingResult.getContents().trim();
                        autoLogin(content);
                    }
                }
            }
        });
    }

    public void autoLogin(String content) {
        try {
            if (!"unionware".contains(content)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    content = new String(Base64.getDecoder().decode(content.getBytes()));
                } else {
                    content = new String(android.util.Base64.decode(content.getBytes(), android.util.Base64.DEFAULT));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (content.contains("unionware")) {
            if ("".equals(baseUrl)) {
                Toast.makeText(mContext, "请先配置服务器地址！", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                String[] loginContent = content.split("unionware");
                mLoginReq.setAcctID(loginContent[0]);
                mLoginReq.setUsername(loginContent[1]);
                mLoginReq.setPassword(loginContent[2]);
                presenter.login(mLoginReq);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openQrScan() {
        // 启动二维码扫描界面
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
}
