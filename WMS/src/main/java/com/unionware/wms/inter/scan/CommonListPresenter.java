package com.unionware.wms.inter.scan;

import com.unionware.wms.api.PackingApi;

import unionware.base.api.util.ConvertUtils;
import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.ViewReq;
import unionware.base.model.resp.CommonDataResp;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

import java.util.Map;

import javax.inject.Inject;

public class CommonListPresenter extends BasePresenter<CommonListContract.View> implements CommonListContract.Presenter {
    PackingApi api;

    @Inject
    public CommonListPresenter(PackingApi api) {
        this.api = api;
    }


    @Override
    public void requestList(String scene, String name, FiltersReq filters) {
        NetHelper.request(api.getCommonList(scene, name, filters), mView, new ICallback<CommonListDataResp<Map<String, Object>>>() {
            @Override
            public void onSuccess(CommonListDataResp<Map<String, Object>> data) {
                if (filters.getPageIndex() == 1 && (null == data.getData() || data.getData().isEmpty())) {
                    mView.showEmptyView();
                } else {
                    mView.showList(ConvertUtils.convertViewToList(data.getView(), data.getData()));
                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void getSummaryList(ViewReq req) {
        NetHelper.request(api.getSummaryList(req), mView, new ICallback<CommonDataResp>() {
            @Override
            public void onSuccess(CommonDataResp data) {
                if (null == data.getData().getData() || data.getData().getData().isEmpty()) {
                    mView.showEmptyView();
                } else {
                    mView.showList(ConvertUtils.convertViewToList(data.getData().getView(), data.getData().getData()));
                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }


}
