package com.unionware.wms.inter.scan;

import unionware.base.model.req.ViewReq;
import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;

public interface BarcodeEditContract {

    interface View extends IView {
        void showFailedView(String msg);

        void onSuccess(int pos);

        void onUpdateSuccessEvent();
    }

    interface Presenter extends IPresenter<View> {
        void deleteBarcodeDetails(ViewReq req, int pos);

        void updateScanView(ViewReq req); // 更新视图
    }

}
