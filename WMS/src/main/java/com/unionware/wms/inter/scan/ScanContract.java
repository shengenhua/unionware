package com.unionware.wms.inter.scan;

import com.unionware.wms.model.bean.BarcodeDetailsInfoBean;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.req.AnalysisReq;
import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;

import java.util.Map;

public interface ScanContract {
    interface View extends IView {
        void showFailedView(String msg);

        void showFailAnalysisEvent(int pos, String msg);

        void showTipsAnalysisEvent(int pos, AnalysisReq req, String msg); // 预警提示

        void showSuccessAnalysisEvent(BarcodeDetailsInfoBean data, int pos);

        void showSuccessDetailsEvent(BarcodeDetailsInfoBean data, int pos);

        void showSuccessUpdateEvent();

        void submitSuccessEvent();

        void initScanConfigItem(ScanConfigBean data);  // 初始化扫描列表

        void jumpToProgressList(); // 跳转【进行中列表】

        void requestFocus(int pos);

        void resetBill(String id); // 初始化单据
    }

    interface Presenter extends IPresenter<View> {
        void getScanConfigDetalisInfo(String id); //  获取扫描配置详细信息

        void confirmInfo(AnalysisReq req, int pos); // 确定录入

        void changeBoxActivity(); // 暂存（换箱）

        void submitInfo(Map<String, String> map);

        void closeBoxInfo(); //关箱

        void analysisBarcode(AnalysisReq req, int pos); // 解析条码

        void updateBarcodeInfo(AnalysisReq req, int pos); // 更新箱码部分信息

        void createTempPackingList(String id); // 创建临时装箱单（扫描配置id）

    }
}
