package com.unionware.wms.inter.scan;

import unionware.base.model.bean.EntityBean;
import unionware.base.model.bean.TaskIdBean;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.TaskIdReq;
import unionware.base.model.req.TaskReq;
import unionware.base.model.req.ViewReq;

import java.util.List;

public class StockScanContract {
    public interface View extends ScanViewContract.View {
        void initScanItem(List<EntityBean> list, boolean isContainer);

        void initConfigInfo(TaskIdBean bean); // 初始化配置信息

        void onCompleteView(String id); // 完成初始化配置

        void showDialog(String msg);

        void dismissDialog();

        void showTipsDialog(TaskIdReq req, String tips);

        void finishUI();
    }

    interface Presenter extends ScanViewContract.Presenter<View> {
        void getScanConfigData(String scene, FiltersReq req); // 查询作业流程配置

        void createScanTask(TaskReq req); // 创建扫描单据

        void submitTask(TaskIdReq req);

        void closeContainer(ViewReq req); // 关闭容器

    }
}
