package com.unionware.wms.inter.scan;

import com.unionware.wms.model.req.PageIdReq;
import unionware.base.model.req.ViewReq;
import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;
import unionware.base.model.resp.AnalysisResp;

public class ScanViewContract {
    public interface View extends IView {
        void showFailedView(String msg);

        void onCompleteUpdateView(AnalysisResp data, int pos);

        void onSuccessSubmit(String tips, boolean isContainer);

        void errorUpdate(String tips, int pos);
    }


    public interface Presenter<T> extends IPresenter<T> {
        void createScanView(ViewReq req); // 创建视图

        void updateScanView(ViewReq req, int pos); // 更新视图  pos -200(确定前更新文本和数量字段) -100 （普通扫描更新默认值回来处理）

        void submitScanView(ViewReq req); // 提交视图

        void destroyScanView(PageIdReq req); // 销毁视图

        void getScanView(ViewReq req); // 获取视图

        /**
         * 销毁视图 销毁后重新创建视图
         * @param req
         */
        void closeOpenScanView(PageIdReq req); //
    }
}
