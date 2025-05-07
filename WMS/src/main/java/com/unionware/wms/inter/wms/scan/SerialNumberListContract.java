package com.unionware.wms.inter.wms.scan;

import java.util.List;

import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;
import unionware.base.model.bean.SerialNumberInfoBean;
import unionware.base.model.req.ViewReq;

/**
 * @Author : pangming
 * @Time : On 2024/8/28 15:07
 * @Description : SerialNumberListContract
 */

public interface SerialNumberListContract {
    interface View extends IView {
        void showFailedView(String msg);

        void showList(List<SerialNumberInfoBean.SerialNumberDetailBean> list);

        void showEmptyView();
    }

    interface Presenter extends IPresenter<View> {
        void getSerialNumberList(ViewReq req);
    }
}
