package com.unionware.wms.inter.box;

import com.unionware.wms.inter.scan.ScanViewContract;
import com.unionware.wms.model.resp.BarcodesResp;

import unionware.base.model.bean.PropertyBean;
import unionware.base.model.req.BarcodePrintExportReq;
import unionware.base.model.req.ViewReq;
import unionware.base.model.req.FiltersReq;

import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2024/8/14 11:17
 * @Description : SplitContract
 */

public interface SplitContract {
    interface View extends ScanViewContract.View {
        void initPageId(String pageId);

        void initStateId(String primaryId);

        boolean initScanItem(List<PropertyBean> editList, List<PropertyBean> showList);

        void closeView();

        void submitView(List<BarcodesResp> barcodes); //

        void showLoading(String msg);

        void dismissLoading();
    }

    interface Presenter extends ScanViewContract.Presenter<View> {
        void getPageId(ViewReq req); //获取PageId

        void getScanView(ViewReq req, int pos); // 获取视图

        void getBoxStateId(String scene, String name, FiltersReq req);

        void splitQuantity(ViewReq req);//拆分数量

        /**
         * 动作处理
         * @param req
         */
        void commandScanView(ViewReq req); // 更新视图  pos -200(确定前更新文本和数量字段) -100 （普通扫描更新默认值回来处理）

        /**
         * 条码打印导出
         *
         * @param scene
         * @param req
         * @param pageId
         */
        void barcodePrintExportReq(String scene, BarcodePrintExportReq req, String pageId);

        /**
         * 重新创建视图
         */
        void restartView(String pageId);
    }
}
