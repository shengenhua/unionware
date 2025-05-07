package com.unionware.wms.inter.scan;

import com.unionware.wms.model.bean.BarcodeDetailsBean;
import com.unionware.wms.model.req.DeleteReq;
import unionware.base.model.req.FiltersReq;
import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;

import java.util.List;
import java.util.Map;

public interface RecordContract {
    interface View extends IView {
        void showFailedView(String msg);

        void removeDetalsBarcodeInfo(int pos); // 删除成功后删除本地记录

        void showPackingDetalisInfo(List<BarcodeDetailsBean> list); // 显示包装条码信息

        void showDetalsBarcodeInfo(List<BarcodeDetailsBean> list); // 显示子项条码信息
    }

    interface Persenter extends IPresenter<View> {
        void getRecordDetailInfo(String name, FiltersReq filters); // 获取包装箱码详细信息

        void deletaBarcodeInfo(DeleteReq req, int pos); // 删除条码信息

        void deletaBarcodeInfo(Map<String, Object> map); // 删除条码信息

    }
}
