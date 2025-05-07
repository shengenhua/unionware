package com.unionware.wms.inter.scan;

import com.unionware.wms.URLPath;
import com.unionware.wms.api.PackingApi;
import com.unionware.wms.model.bean.BarcodeDetailsInfoBean;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.req.AnalysisReq;
import unionware.base.model.req.FiltersReq;
import com.unionware.wms.model.req.IdReq;
import com.unionware.wms.model.resp.BasePropertyResp;
import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class ScanPresenter extends BasePresenter<ScanContract.View> implements ScanContract.Presenter {

    PackingApi api;

    @Inject
    public ScanPresenter(PackingApi api) {
        this.api = api;
    }

    @Override
    public void getScanConfigDetalisInfo(String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("primaryId", id);
        NetHelper.request(api.getScanConfigDetails(URLPath.Pack.PATH_PACK_SCENE, URLPath.Pack.PATH_PACK_SCAN_CONFIG_CODE, new FiltersReq(map)), mView, new ICallback<CommonListDataResp<ScanConfigBean>>() {
            @Override
            public void onSuccess(CommonListDataResp<ScanConfigBean> data) {
                mView.initScanConfigItem(data.getData().get(0));
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }


    @Override
    public void analysisBarcode(AnalysisReq req, int pos) {
        NetHelper.request(api.analysisPackBarcode(req), mView, new ICallback<List<BarcodeDetailsInfoBean>>() {
            @Override
            public void onSuccess(List<BarcodeDetailsInfoBean> data) {
                if (data.size() > 0) {
                    if ("Pack".equals(req.getType())) {
                        mView.showSuccessAnalysisEvent(data.get(0), pos);
                    } else {
                        mView.showSuccessDetailsEvent(data.get(0), pos);
                    }
                } else {
                    mView.showFailAnalysisEvent(pos, "暂无数据");
                }

            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailAnalysisEvent(pos, e.getErrorMsg());
            }
        });

    }

    @Override
    public void updateBarcodeInfo(AnalysisReq req, int pos) {
        NetHelper.request(api.updateBarcodeInfo(req), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                mView.showSuccessUpdateEvent();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailAnalysisEvent(pos, e.getErrorMsg());
            }
        });
    }

    @Override
    public void createTempPackingList(String id) {
        IdReq req = new IdReq(id);
        NetHelper.request(api.createTempPackingList(req), mView, new ICallback<BasePropertyResp>() {
            @Override
            public void onSuccess(BasePropertyResp data) {
                mView.resetBill(data.getId());
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }


    @Override
    public void confirmInfo(AnalysisReq req, int pos) {
        NetHelper.request(api.comfirmBarcodeInfo(req), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                mView.requestFocus(pos);
            }

            @Override
            public void onFailure(ApiException e) {
                // 预警提示
                if (e.getCode().contains("9")) {
                    String[] interactions = new String[]{e.getCode()};
                    req.setInteractions(interactions);
                    mView.showTipsAnalysisEvent(pos, req, e.getErrorMsg());
                } else {
                    mView.showFailAnalysisEvent(pos, e.getErrorMsg());
                }

            }
        });
    }

    @Override
    public void changeBoxActivity() {

    }

    @Override
    public void submitInfo(Map<String, String> map) {
        NetHelper.request(api.submitBarcodeInfo(map), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                mView.submitSuccessEvent();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void closeBoxInfo() {

    }


}
