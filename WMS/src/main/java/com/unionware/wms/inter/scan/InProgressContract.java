package com.unionware.wms.inter.scan;

import com.unionware.wms.model.bean.ProgressInfoBean;
import unionware.base.model.req.FiltersReq;
import com.unionware.wms.model.req.IdReq;
import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;

import java.util.List;

public interface InProgressContract {

    interface View extends IView  {
        void showFailedView(String msg);

        void showInProgressList(List<ProgressInfoBean> list);

        void removeInProgressItem(int pos);
    }

    interface Presenter extends IPresenter<View> {
        void getInProgressList(FiltersReq filters); // 获取进行中列表

        void deleteTempPackingList(IdReq req, int pos);
    }

}
