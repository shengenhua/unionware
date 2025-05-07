package com.unionware.wms.inter.scan;

import com.unionware.wms.URLPath;
import com.unionware.wms.api.PackingApi;

import javax.inject.Inject;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.bean.BaseInfoBean;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

public class BaseInfoPresenter extends BasePresenter<BaseInfoContract.View> implements BaseInfoContract.Presenter {
    PackingApi api;

    @Inject
    public BaseInfoPresenter(PackingApi api) {
        this.api = api;
    }

    @Override
    public void getBaseInfoList(String key, FiltersReq req) {
        NetHelper.request(api.getBaseInfoList(URLPath.Pack.PATH_PACK_SCENE, key, req), mView, new ICallback<CommonListDataResp<BaseInfoBean>>() {
            @Override
            public void onSuccess(CommonListDataResp<BaseInfoBean> data) {
                mView.showBaseInfoList(data.getData());
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }
}
