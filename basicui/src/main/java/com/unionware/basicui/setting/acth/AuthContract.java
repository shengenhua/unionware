package com.unionware.basicui.setting.acth;


import unionware.base.model.req.AuthReq;
import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;

public interface AuthContract {
    interface View extends IView {
        void showFailedView(String msg);

        void onSuccessEvent(String content);
    }

    interface Presenter extends IPresenter<View> {
        void applyAuthInfo(String url, AuthReq req);

        void getAuthInfo(String url);
    }
}
