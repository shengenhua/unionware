package com.unionware.basicui.setting;

import android.content.Intent;
import android.text.TextUtils;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.QuickAdapterHelper;
import com.lxj.xpopup.XPopup;
import com.tencent.mmkv.MMKV;
import com.unionware.basicui.app.BasicAppProvider;
import com.unionware.basicui.setting.adapter.SettingAdapter;
import com.unionware.basicui.setting.bean.SettingBean;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.R;
import unionware.base.app.utils.LoadingUtil;
import unionware.base.app.utils.ToastUtil;
import unionware.base.app.view.base.viewbinding.BaseBindActivity;
import unionware.base.databinding.SettingActivityBinding;
import unionware.base.route.URouter;
import unionware.base.ui.UpgradeDialog;
import unionware.base.util.AppUpdateUtil;

@AndroidEntryPoint
public class SettingActivity extends BaseBindActivity<SettingActivityBinding> {
    private SettingViewModel viewModel;

    private final MMKV kv = MMKV.mmkvWithID("app");
    private final SettingAdapter settingAdapter = new SettingAdapter();
    private final SettingAdapter bottomSettingAdapter = new SettingAdapter();

    @Override
    public int onBindLayout() {
        return R.layout.setting_activity;
    }

    @Override
    protected void initCommonView() {
        super.initCommonView();
        viewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        getLifecycle().addObserver(viewModel);
        viewModel.getMUIChangeLiveData().getShowToastViewEvent().observe(this, ToastUtil::showToastCenter);
    }

    @Override
    public void initListener() {
        viewModel.getAppVersionLiveData().observe(this, appLastest -> {
            if (appLastest == null) return;
            if (AppUpdateUtil.checkAppVersion(this, appLastest.getVersion())) {
                UpgradeDialog upgradeDialog = new UpgradeDialog(this, appLastest, () -> {
                    if (!TextUtils.isEmpty(appLastest.getFileId())) {
                        LoadingUtil.show("下载中...");
                        viewModel.getAppFile(appLastest.getFileId());
                    }
                });
                new XPopup.Builder(this).dismissOnBackPressed(false).dismissOnTouchOutside(false).asCustom(upgradeDialog).show();
            } else {
                ToastUtil.showToast("已是最新版本");
            }
        });
        viewModel.getAppFileLiveData().observe(this, file -> {
            LoadingUtil.dismiss();
            AppUpdateUtil.installAPK(this, file);
        });
    }

    private final BaseQuickAdapter.OnItemClickListener<SettingBean> settingItemClickListener = (baseQuickAdapter, view, position) -> {
        SettingBean bean = baseQuickAdapter.getItem(position);
        if (bean == null) return;
        switch (bean.getType()) {
            case 1: {
                if (bean.getCls() != null) {
                    startActivity(new Intent(SettingActivity.this, bean.getCls()));
                }
                break;
            }
            case 2: {
                ARouter.getInstance().build(bean.getPath()).navigation();
                break;
            }
            case 3: {
                if (bean.getPath() != null) {
                    URouter.build().action(bean.getPath());
                }
                break;
            }
            case 4: {
                kv.encode(bean.getKey(), bean.getSwitch());
                break;
            }
            case 5: {
                if (bean.getMethod() != null) {
                    runOnUiThread(() -> bean.getMethod().run(this));
                }
                break;
            }
        }
    };

    @Override
    public void initView() {
        LoadingUtil.init(this);
        getMBind().toolbar.setNavigationOnClickListener(view -> finish());
        getMBind().rvSetting.setAdapter(getSettingAdapter());
        getMBind().rvSetting.setLayoutManager(new LinearLayoutManager(this));

        settingAdapter.setOnItemClickListener(settingItemClickListener);
        bottomSettingAdapter.setOnItemClickListener(settingItemClickListener);

        settingAdapter.submitList(getAdapterMenu());
        bottomSettingAdapter.submitList(getBottomMenu());
    }

    protected List<SettingBean> getAdapterMenu() {
       /* List<SettingBean> list = new ArrayList<>();
        list.add(new SettingBean("禁用软键盘", "hideKeyboard", kv.encode("hideKeyboard", false)));
        list.add(new SettingBean("打印设置", RouterPath.Print.PATH_PRINT_SET_MAIN));
//        list.add(new SettingBean("打印页签设置", RouterPath.Print.PATH_PRINT_SET_MAIN));
        list.add(new SettingBean("清除缓存", () -> {
            ToastUtil.showToast("清除缓存成功");
        }));
        list.add(new SettingBean("主题", UnionwareThemeActivity.class));
        list.add(new SettingBean("关于我们", AboutUsActivity.class));
        list.add(new SettingBean("设备授权", AuthConfigActivity.class));
//        list.add(new SettingBean("蓝牙搜索", BluetoothActivity.class));
//        list.add(new SettingBean("帮助中心", RouterPath.Print.PATH_PRINT_SET_MAIN));
        list.add(new SettingBean("检查更新", () -> {
            viewModel.getAppLastest();
        }));
*/
        SettingConfig.addSettingBeans(new SettingBean("检查更新", (activity) -> {
            viewModel.getAppLastest();
        }));

        return SettingConfig.getSettingBeans();
    }

    protected List<SettingBean> getBottomMenu() {
        SettingBean loginOut = new SettingBean("退出登陆", (activity) -> {
            viewModel.loginOut();
            MMKV kv = MMKV.mmkvWithID("app");
            kv.encode("isLogin", false);
            /*Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
            URouter.build().action(BasicAppProvider.getPath(BasicAppProvider.LOGIN));
            finish();
        });
        loginOut.setTextColor(getResources().getColor(unionware.base.R.color.red, this.getTheme()));
        loginOut.setDrawable(0);
        
        SettingConfig.addBottomSettingBeans(loginOut);
        return SettingConfig.getBottomSettingBeans();
    }

    protected RecyclerView.Adapter<?> getSettingAdapter() {
        QuickAdapterHelper helper = new QuickAdapterHelper.Builder(settingAdapter).build();
        helper.addAfterAdapter(bottomSettingAdapter);
        return helper.getAdapter();
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoadingUtil.unInit();
    }
}
