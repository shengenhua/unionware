package com.unionware.wms.inter.scan;

import com.unionware.wms.URLPath;
import com.unionware.wms.api.PackingApi;
import com.unionware.wms.model.bean.BarcodeDetailsBean;
import com.unionware.wms.model.req.DeleteReq;
import unionware.base.model.req.FiltersReq;
import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

import java.util.Map;

import javax.inject.Inject;

public class RecordPresenter extends BasePresenter<RecordContract.View> implements RecordContract.Persenter {

    PackingApi api;

    @Inject
    public RecordPresenter(PackingApi api) {
        this.api = api;
    }


    @Override
    public void getRecordDetailInfo(String name, FiltersReq filters) {
        NetHelper.request(api.getRecordDetalisInfo(URLPath.Pack.PATH_PACK_SCENE, name, filters), mView, new ICallback<CommonListDataResp<BarcodeDetailsBean>>() {
            @Override
            public void onSuccess(CommonListDataResp<BarcodeDetailsBean> data) {
                if (name.equals(URLPath.Pack.PATH_PACK_SCAN_PACKING_CODE)) { // 表示装箱，反之是明细信息
                    mView.showPackingDetalisInfo(data.getData());
                } else {
                    mView.showDetalsBarcodeInfo(data.getData());
                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void deletaBarcodeInfo(DeleteReq req, int pos) {
        NetHelper.request(api.deletePackingByBarcode(req), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                mView.removeDetalsBarcodeInfo(pos);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }


    @Override
    public void deletaBarcodeInfo(Map<String, Object> map) {
        NetHelper.request(api.deletePackingByBarcode(map), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }
}
