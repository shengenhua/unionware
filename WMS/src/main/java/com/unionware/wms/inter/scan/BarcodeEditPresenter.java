package com.unionware.wms.inter.scan;

import com.google.gson.Gson;
import com.unionware.wms.api.PackingApi;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.req.ViewReq;
import unionware.base.model.resp.AnalysisInfoResp;
import unionware.base.model.resp.CommonDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import javax.inject.Inject;

public class BarcodeEditPresenter extends BasePresenter<BarcodeEditContract.View> implements BarcodeEditContract.Presenter {
    PackingApi api;

    @Inject
    public BarcodeEditPresenter(PackingApi api) {
        this.api = api;
    }


    @Override
    public void deleteBarcodeDetails(ViewReq req, int pos) {
        NetHelper.request(api.deleteBarcodeDetails(req), mView, new ICallback<CommonDataResp>() {
            @Override
            public void onSuccess(CommonDataResp data) {
                mView.onSuccess(pos);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void updateScanView(ViewReq req) {
        NetHelper.request(api.updateView(req), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                AnalysisInfoResp analysisInfoResp = new Gson().fromJson(new Gson().toJson(data), AnalysisInfoResp.class);
                EventBus.getDefault().post(analysisInfoResp.getData());
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }


}
