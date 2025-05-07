package com.unionware.basicui.login.login;


import java.util.List;

import javax.inject.Inject;

import unionware.base.api.UserApi;
import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.req.LoginReq;
import unionware.base.model.resp.DBCenterResp;
import unionware.base.model.resp.UserInfoResp;
import unionware.base.network.NetHelper;
import unionware.base.network.NetHelperKt;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;


/**
 * 逻辑梳理：
 */
public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter {
    UserApi userApi;

    @Inject
    public LoginPresenter(UserApi userApi) {
        this.userApi = userApi;//ApiHelper.getInstance().userApi;
    }

    @Override
    public void getDBCenterList(boolean showDialog) {
        NetHelper.request(userApi.getKingDeeDataCenterList(), mView, new ICallback<>() {
            @Override
            public void onSuccess(List<DBCenterResp> data) {
                mView.hideLoadingView();
                mView.showDBCenterView(data, showDialog);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.hideLoadingView();
                mView.showFailedView(e.getErrorMsg());
            }
        });

    }

    @Override
    public void getUserInfo() {
        NetHelper.request(userApi.getUserInfo(), mView, new ICallback<>() {
            @Override
            public void onSuccess(UserInfoResp data) {
                mView.saveUserInfo(data);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void login(LoginReq req) {
        mView.showLoadingView("登录中");
        NetHelperKt.request(userApi.login(req), mView, new ICallback<>() {
            @Override
            public void onSuccess(UserInfoResp data) {
                mView.hideLoadingView();
                mView.saveUserInfo(data);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.hideLoadingView();
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }
}
