package com.unionware.wms.inter.scan;


import unionware.base.model.bean.BillBean;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.ViewReq;
import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;

import java.util.List;

public interface CommonListContract {

    interface View extends IView {
        void showFailedView(String msg);

        void showList(List<BillBean> list);

        void showEmptyView();
    }

    interface Presenter extends IPresenter<View> {
        void requestList(String scene, String name, FiltersReq filters);

        void getSummaryList(ViewReq req);
    }

}
