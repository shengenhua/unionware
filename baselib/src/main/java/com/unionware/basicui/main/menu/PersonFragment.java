package com.unionware.basicui.main.menu;

import android.content.Intent;
import android.os.Build;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.lxj.xpopup.XPopup;
import com.tencent.mmkv.MMKV;
import com.unionware.basicui.app.BasicAppProvider;
import unionware.base.databinding.PersonFragmentBinding;
import com.unionware.basicui.main.menu.adapter.FavoriteAdapter;
import com.unionware.basicui.setting.QRActivity;
import com.unionware.path.RouterPath;

import java.util.Base64;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import unionware.base.app.ex.DeviceException;
import unionware.base.app.utils.DeviceAuthUtils;
import unionware.base.app.utils.ToastUtil;
import unionware.base.model.req.LoginReq;
import unionware.base.model.req.OrgReq;
import unionware.base.model.resp.DBCenterResp;
import unionware.base.room.table.Favourite;
import unionware.base.route.URouter;

@AndroidEntryPoint
@Route(path = RouterPath.Person.PATH_PERSON_HOME)
public class PersonFragment extends MainBaseFragment<PersonFragmentBinding> {
    private final FavoriteAdapter menuAdapter = new FavoriteAdapter();
    private PersonModel viewModel;
    private String tag = "";
    private final MMKV kv = MMKV.mmkvWithID("app");

    @Override
    public void initObserve() {
        super.initObserve();
        viewModel = new ViewModelProvider(this).get(PersonModel.class);
        getLifecycle().addObserver(viewModel);

        viewModel.getMUIChangeLiveData().getShowToastViewEvent().observe(this, ToastUtil::showToastCenter);
        viewModel.getFavouriteLiveData().observe(this, favourites -> {
//            menuAdapter::setNewInstance
            if (menuAdapter != null) {
                menuAdapter.setNewInstance(favourites);
            }
        });
        viewModel.getUserInfoLiveData().observe(this, userInfoResp -> {
            if (userInfoResp.getOrgId() != null && !userInfoResp.getOrgId().isEmpty()) {
                kv.encode("orgId", userInfoResp.getOrgId());
            }
            if(getMBind()!=null){
                getMBind().tvUserNick.setText(userInfoResp.getName());
                getMBind().tvUserDb.setText(userInfoResp.getAccount().getName());
                getMBind().tvUserOrg.setText(userInfoResp.getOrganization().getName());
            }
        });
        viewModel.getUserOrgLiveData().observe(this, dbCenterResps -> {
            String[] orgList = dbCenterResps.stream().map(DBCenterResp::getName).toArray(String[]::new);
            new XPopup.Builder(getActivity())
                    .maxHeight((int) getResources().getDimension(unionware.base.R.dimen.dp_400))
                    .asBottomList("请选择组织", orgList, (position, text) -> {
                        viewModel.setUserOrgInfo(new OrgReq(dbCenterResps.get(position).getId()), text);
                    }).show();
        });
        viewModel.getMenuConfigLiveData().observe(this, config -> {
            //打开界面
            URouter.build().builder(config.getUrl())
                    .init(config.getName(), config.getId(), config.getScene())
                    .withObject("config", config)
                    .navigation();
        });
    }

    @Override
    public void initData() {
        viewModel.getPersonInfo();
        Objects.requireNonNull(getMBind()).tvUserChange.setOnClickListener(view -> viewModel.getOrgList());
        getMBind().ivUserSetting.setOnClickListener(view -> {
            URouter.build().action(BasicAppProvider.getPath(BasicAppProvider.SETTING));
        });

        menuAdapter.setOnItemClickListener((adapter, view, position) -> {
            try {
                DeviceAuthUtils.inspect(getContext(), DeviceAuthUtils.Product.PRODUCT_ID);
            } catch (DeviceException e) {
                ToastUtil.showToastCenter(DeviceAuthUtils.error(e));
                return;
            }
            String path = menuAdapter.getData().get(position).getPath();
            if (path == null) return;
            viewModel.getScanConfigList("MES.Normal", path);
        });
        menuAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            Favourite data = (Favourite) adapter.getItem(position);
            if (data.getPath() == null) return false;
            viewModel.deleteByPath(data.getPath());
            return true;
        });
        Objects.requireNonNull(getMBind()).rvList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        getMBind().rvList.setAdapter(menuAdapter);
        getMBind().ivUserHeadBg.setOnClickListener(v -> {
            LoginReq req = kv.decodeParcelable("loginReq", LoginReq.class);
            if (req == null) {
                return;
            }
            String gson = req.getAcctID() + "unionware" + req.getUsername() + "unionware" + req.getPassword();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                gson = Base64.getEncoder().encodeToString(gson.getBytes());
            } else {
                gson = android.util.Base64.encodeToString(gson.getBytes(), android.util.Base64.DEFAULT);
            }
            Intent intent = new Intent(getActivity(), QRActivity.class);
            intent.putExtra("gson", gson);
            startActivity(intent);
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        viewModel.getFavourites();
        if (!hidden && menuAdapter != null) {
            viewModel.getPersonInfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getFavourites();
    }
}
