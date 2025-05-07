package com.unionware.wms.inter.wms.scan;

import com.unionware.wms.api.PackingApi;

import javax.inject.Inject;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.bean.SerialNumberInfoBean;
import unionware.base.model.req.ViewReq;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

/**
 * @Author : pangming
 * @Time : On 2024/8/28 15:08
 * @Description : SerialNumberListPresenter
 */

public class SerialNumberListPresenter extends BasePresenter<SerialNumberListContract.View> implements SerialNumberListContract.Presenter {
    PackingApi api;

    @Inject
    public SerialNumberListPresenter(PackingApi api) {
        this.api = api;
    }

    @Override
    public void getSerialNumberList(ViewReq req) {
        NetHelper.request(api.getSerialNumber(req), mView, new ICallback<SerialNumberInfoBean>() {
            @Override
            public void onSuccess(SerialNumberInfoBean data) {
                if (null == data.getData() || data.getData().isEmpty()) {
                    mView.showEmptyView();
                } else {
                    mView.showList(data.getData());
                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }
}
