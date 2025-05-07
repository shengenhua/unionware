package com.unionware.wms.inter.wms.scan;

import com.unionware.wms.model.req.DeleteReq;

import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;
import unionware.base.model.req.FiltersReq;

/**
 * @Author : pangming
 * @Time : On 2024/7/17 13:58
 * @Description : NormalRecordContract
 */

public interface NormalRecordContract {
    interface View extends IView {
        void showFailedView(String msg);

    }

    interface Presenter extends IPresenter<View> {
        void getRecordDetailInfo(String name, FiltersReq filters); // 获取扫描记录

        void deletaBarcodeInfo(DeleteReq req, int pos); // 删除条码信息

    }
}
