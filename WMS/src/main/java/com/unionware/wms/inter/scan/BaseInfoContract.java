package com.unionware.wms.inter.scan;

import unionware.base.model.bean.BaseInfoBean;
import unionware.base.model.req.FiltersReq;
import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;

import java.util.List;

public interface BaseInfoContract {
    interface View extends IView {
        void showFailedView(String msg);

        void showBaseInfoList(List<BaseInfoBean> list);
    }

    interface Presenter extends IPresenter<View> {
        void getBaseInfoList(String name, FiltersReq req); // 获取包装箱码详细信息
    }
}
