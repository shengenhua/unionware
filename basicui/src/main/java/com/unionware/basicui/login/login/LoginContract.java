package com.unionware.basicui.login.login;


import unionware.base.model.req.LoginReq;
import unionware.base.model.resp.DBCenterResp;
import unionware.base.model.resp.UserInfoResp;
import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;

import java.util.List;


public interface LoginContract {

    interface View extends IView {

        String getUserName();

        String getPassword();

        void showDBCenterView(List<DBCenterResp> data, boolean showDialog);

        void showFailedView(String msg);

        void showLoadingView(String tips);

        void hideLoadingView();

        void saveUserInfo(UserInfoResp req);

        void jumpToMain();

    }

    interface Presenter extends IPresenter<View> {
        void getDBCenterList(boolean showDialog); // 获取账套中心列表

        void getUserInfo(); // 获取用户信息

        void login(LoginReq model); // 登录
    }
}
