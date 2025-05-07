package com.unionware.wms.inter.wms.scan;

import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;
import unionware.base.model.bean.BillBean;
import unionware.base.model.req.FiltersReq;

import java.util.List;
import java.util.Map;

/**
 * @Author : pangming
 * @Time : On 2024/6/26 11:08
 * @Description : MMSRegularScanCommonListContract
 */

public interface WMSRegularScanCommonListContract {
    interface View extends IView {
        void showFailedView(String msg);

        void showList(List<BillBean> list);

        void showEmptyView();
        void upDataByTaskCancel(int position);
    }

    interface Presenter extends IPresenter<View> {
        void requestList(String scene, String name, FiltersReq filters);//获取源单
        //void createNormalScanTask(TaskReq req);//创建扫描任务
        void taskCancel(Map map, int position);//删除任务
    }
}
