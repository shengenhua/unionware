package com.unionware.wms.inter.scan;

import com.unionware.wms.URLPath;
import com.unionware.wms.api.PackingApi;
import com.unionware.wms.model.bean.ProgressInfoBean;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

import com.unionware.wms.model.req.IdReq;

import javax.inject.Inject;

public class InProgressPresenter extends BasePresenter<InProgressContract.View> implements InProgressContract.Presenter {

    PackingApi api;

    @Inject
    public InProgressPresenter(PackingApi api) {
        this.api = api;
    }

    @Override
    public void getInProgressList(FiltersReq filters) {
        NetHelper.request(api.getInProgressList(URLPath.Pack.PATH_PACK_SCENE, URLPath.Pack.PATH_PACK_SCAN_TASK_LIST, filters), mView, new ICallback<CommonListDataResp<ProgressInfoBean>>() {
            @Override
            public void onSuccess(CommonListDataResp<ProgressInfoBean> data) {
                mView.showInProgressList(data.getData());
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void deleteTempPackingList(IdReq req, int pos) {
        NetHelper.request(api.deleteTempPackingList(req), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                mView.removeInProgressItem(pos);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }
}
