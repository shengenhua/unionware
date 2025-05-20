package com.unionware.basicui.main.menu;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.lxj.xpopup.XPopup;
import com.tencent.mmkv.MMKV;
import com.unionware.basicui.app.BasicAppProvider;
import com.unionware.basicui.main.menu.adapter.MenuAdapter;
import com.unionware.basicui.main.menu.adapter.MenuGridAdapter;
import com.unionware.basicui.main.service.DeviceHeartService;
import com.unionware.path.RouterPath;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.app.ex.DeviceException;
import unionware.base.app.utils.DeviceAuthUtils;
import unionware.base.app.utils.LoadingUtil;
import unionware.base.app.utils.ToastUtil;
import unionware.base.databinding.ScanConfigActivityBinding;
import unionware.base.model.bean.MenuTypeBean;
import unionware.base.model.req.LoginReq;
import unionware.base.room.DatabaseProvider;
import unionware.base.room.ThreadTask;
import unionware.base.room.table.Favourite;
import unionware.base.route.URouter;
import unionware.base.ui.UpgradeDialog;
import unionware.base.util.AppUpdateUtil;

@Route(path = RouterPath.Main.PATH_MENU_HOME)
@AndroidEntryPoint
public class ScanConfigFragment extends MainBaseFragment<ScanConfigActivityBinding> {

    private Boolean isLogin = false;
    private ScanConfigModel viewModel;

    private final MenuAdapter parentAdapter = new MenuAdapter();
    private final MenuGridAdapter menuAdapter = new MenuGridAdapter();
    private ServiceConnection serviceConnection = null;


    @Override
    public void initObserve() {
        super.initObserve();
        viewModel = new ViewModelProvider(this).get(ScanConfigModel.class);
        getLifecycle().addObserver(viewModel);

        viewModel.getMUIChangeLiveData().getShowToastViewEvent().observe(this, s -> {
            if (s != null) {
                ToastUtil.showToastCenter(s);
            }
        });
        viewModel.getMUIChangeLiveData().getFinishActivityEvent().observe(this, s -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
        viewModel.getLoginUserLiveData().observe(this, s -> {
            viewModel.getMenuList(getActivity());
        });

        viewModel.getMenuConfigLiveData().observe(this, config -> {
            //打开界面
            URouter.build().builder(config.getUrl()).init(config.getName(), config.getId(), config.getScene()).withObject("config", config).navigation();
        });
        viewModel.getMenuLiveData().observe(this, menuBean -> {
            if (getMBind() == null) return;
            if (menuBean == null || menuBean.getMenu() == null || menuBean.getMenu().isEmpty()) {
                //显示空
                getMBind().rvScanConfigParent.setVisibility(View.GONE);
                getMBind().rvScanConfigChild.setVisibility(View.GONE);
            } else {
                getMBind().rvScanConfigParent.setVisibility(View.VISIBLE);
                getMBind().rvScanConfigChild.setVisibility(View.VISIBLE);
                parentAdapter.setNewInstance(menuBean.getMenu());
                menuAdapter.setList(menuBean.getMenu().get(0).getMenu());
            }
        });
        viewModel.getAppVersionLiveData().observe(this, appLastest -> {
            if (getActivity() == null) return;
            if (AppUpdateUtil.checkAppVersion(getActivity(), appLastest.getVersion())) {
                UpgradeDialog upgradeDialog = new UpgradeDialog(getActivity(), appLastest, () -> {
                    if (appLastest.getFileId() != null) {
                        LoadingUtil.show("下载中...");
                        viewModel.getAppFile(appLastest.getFileId());
                    }
                });
                new XPopup.Builder(getActivity())
                        .dismissOnBackPressed(false)
                        .dismissOnTouchOutside(false)
                        .asCustom(upgradeDialog)
                        .show();
            }
        });

        viewModel.getAppFileLiveData().observe(this, file -> {
            LoadingUtil.dismiss();
            if (getActivity() == null) return;
            AppUpdateUtil.installAPK(getActivity(), file);
        });
        viewModel.getConnectFailure().observe(this, apiException -> {
            new XPopup.Builder(getActivity())
                    .dismissOnBackPressed(false)
                    .dismissOnTouchOutside(false)
                    .asConfirm("提示",
                            "登陆异常:" + apiException.getErrorMsg(),
                            () -> {
                                URouter.build().action(BasicAppProvider.getPath(BasicAppProvider.LOGIN));
                                getActivity().finish();
                            }, () -> {
                                URouter.build().action(BasicAppProvider.getPath(BasicAppProvider.LOGIN));
                                getActivity().finish();
                            })
                    .show();
        });
        viewModel.getConnectSuccess().observe(this, s -> {
            //开启服务
            Intent intent = new Intent(getActivity(), DeviceHeartService.class);
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    // 当服务连接成功时调用
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    // 当服务连接断开时调用
                }
            };
            getActivity().bindService(intent, serviceConnection, getActivity().BIND_AUTO_CREATE);
        });
    }

    @Override
    public void initData() {
        if (getArguments() != null) {
            isLogin = getArguments().getBoolean("isLogin", false);
        }
        MMKV kv = MMKV.mmkvWithID("app");
        menuAdapter.setOnItemClickListener((adapter, view, position) -> {
            try {
                DeviceAuthUtils.inspect(getContext(), DeviceAuthUtils.Product.PRODUCT_ID);
            } catch (DeviceException e) {
                ToastUtil.showToastCenter(DeviceAuthUtils.error(e));
                return;
            }
            viewModel.getScanConfigList("MES.Normal", menuAdapter.getData().get(position).getLink());
        });


        menuAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            String tag = kv.decodeString("userId") + kv.decodeString("dbId") + kv.decodeString("orgId");
            ThreadTask.start(() -> {
                MenuTypeBean data = (MenuTypeBean) adapter.getItem(position);
                if (DatabaseProvider.getInstance().getFavouriteDao().countBypathAndTag(data.getLink(), tag) > 0) {
                    DatabaseProvider.getInstance().getFavouriteDao().deleteByPathAndTag(data.getLink(), tag);
                    ToastUtil.showToastCenter("取消收藏");
                } else {
                    // constructor(name: String?, color: String?, path: String?, icon: String?, tag: String?) {
                    Favourite favourite = new Favourite(data.getName(), data.getColor(), data.getLink(), data.getIcon(), tag);
                    DatabaseProvider.getInstance().getFavouriteDao().insert(favourite);
                    ToastUtil.showToastCenter("收藏成功");
                }
            });
            return true;
        });

        parentAdapter.setOnItemClickListener((adapter, view, position) -> {
            parentAdapter.setSelectedPosition(position);
            menuAdapter.setNewInstance(parentAdapter.getData().get(position).getMenu());
        });
        if (getMBind() != null) {
            getMBind().rvScanConfigParent.setLayoutManager(new LinearLayoutManager(getActivity()));
            getMBind().rvScanConfigParent.setAdapter(parentAdapter);
            getMBind().rvScanConfigChild.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            getMBind().rvScanConfigChild.setAdapter(menuAdapter);
        }
        if (isLogin) {
            viewModel.getLoginInfo();
            viewModel.getAppLastest();
        } else {
            LoginReq mLoginReq = kv.decodeParcelable("loginReq", LoginReq.class);
            viewModel.login(mLoginReq);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (viewModel.getLoginUserLiveData().getValue() == null) {
            parentAdapter.getData().clear();
            menuAdapter.getData().clear();
            viewModel.getLoginInfo();
        } else {
            MMKV kv = MMKV.mmkvWithID("app");
            String userId = kv.decodeString("userId");
            String dbId = kv.decodeString("dbId");
            if (!viewModel.getLoginUserLiveData().getValue().getDbId().equals(dbId) ||
                    !viewModel.getLoginUserLiveData().getValue().getUserId().equals(userId)) {
                parentAdapter.setNewInstance(new ArrayList<>());
                menuAdapter.setNewInstance(new ArrayList<>());
                viewModel.getLoginInfo();
            }
        }
        /*if (!hidden && (viewModel.getMenuLiveData().getValue() == null ||
                viewModel.getMenuLiveData().getValue().getMenu() == null ||
                viewModel.getMenuLiveData().getValue().getMenu().isEmpty())) {
            viewModel.getMenuList();
        }*/
    }

    /** @noinspection deprecation*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceConnection != null) {
            getActivity().unbindService(serviceConnection);
        }
    }
}
