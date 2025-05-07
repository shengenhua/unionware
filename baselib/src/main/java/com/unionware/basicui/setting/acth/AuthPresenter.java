package com.unionware.basicui.setting.acth;


import javax.inject.Inject;

import unionware.base.api.UserApi;
import unionware.base.model.req.AuthReq;
import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;


public class AuthPresenter extends BasePresenter<AuthContract.View> implements AuthContract.Presenter {
    @Inject
    UserApi userApi;

    @Inject
    public AuthPresenter() {
    }

    @Override
    public void applyAuthInfo(String url, AuthReq req) {
        NetHelper.request(userApi.applyAuthInfo(url, req), mView, new ICallback<>() {
            @Override
            public void onSuccess(Object data) {
                mView.showFailedView("申请成功！");
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void getAuthInfo(String url) {
        NetHelper.request(userApi.getAuthInfo(url), mView, new ICallback<>() {
            @Override
            public void onSuccess(Object data) {
                mView.onSuccessEvent(data.toString());
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView("URL FAILURE:" + e.getErrorMsg());
            }
        });
    }
}
