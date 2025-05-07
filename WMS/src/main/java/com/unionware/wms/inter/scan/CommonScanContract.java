package com.unionware.wms.inter.scan;

import com.unionware.wms.model.bean.ScanConfigBean;

import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;

public interface CommonScanContract {
    interface View extends IView {
        void showFailedView(String msg);

        void initScanConfigItem(ScanConfigBean data);  // 初始化扫描列表

    }

    interface Presenter extends IPresenter<View> {
        void getScanConfigDetalisInfo(String id); //  获取扫描配置详细信息
    }
}
