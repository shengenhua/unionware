package com.unionware.wms.inter.wms.scan;

import com.unionware.wms.api.PackingApi;
import com.unionware.wms.model.bean.BarcodeDetailsBean;
import com.unionware.wms.model.req.DeleteReq;

import javax.inject.Inject;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

/**
 * @Author : pangming
 * @Time : On 2024/7/17 13:56
 * @Description : NormalRecordPresenter
 */

public class NormalRecordPresenter extends BasePresenter<NormalRecordContract.View> implements NormalRecordContract.Presenter {
    PackingApi api;

    @Inject
    public NormalRecordPresenter(PackingApi api) {
        this.api = api;
    }

    @Override
    public void getRecordDetailInfo(String name, FiltersReq filters) {
        NetHelper.request(api.getRecordDetalisInfo("WMS.Normal", "663DE5F52B90F6", filters), mView, new ICallback<CommonListDataResp<BarcodeDetailsBean>>() {
            @Override
            public void onSuccess(CommonListDataResp<BarcodeDetailsBean> data) {

            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void deletaBarcodeInfo(DeleteReq req, int pos) {

    }

}
