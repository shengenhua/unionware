package com.unionware.wms.inter.wms.scan;

import com.unionware.wms.model.bean.NormalScanConfigBean;

import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;
import unionware.base.model.req.FiltersReq;

/**
 * @Author : pangming
 * @Time : On 2024/7/9 15:25
 * @Description : WMSRegularScanContract
 */

public interface WMSRegularScanContract{
  interface View extends IView {
      void setSrcFormId(NormalScanConfigBean normalScanConfigBean);
      void showFailedView(String msg);
    }
    interface Presenter extends IPresenter<View>{
        void requestConfigurationList(String scene, String name, FiltersReq filters);//获取配置列表
        void requestConfigurationDetails(String scene, String name, FiltersReq filters);//获取配置详情
    }
}
