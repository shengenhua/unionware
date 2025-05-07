package com.unionware.wms.inter.scan;

import com.google.gson.Gson;
import com.unionware.wms.URLPath;
import com.unionware.wms.api.PackingApi;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.req.PageIdReq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.bean.TaskIdBean;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.TaskIdReq;
import unionware.base.model.req.TaskReq;
import unionware.base.model.req.ViewReq;
import unionware.base.model.resp.AnalysisInfoResp;
import unionware.base.model.resp.AnalysisResp;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

public class StockScanPresenter extends BasePresenter<StockScanContract.View> implements StockScanContract.Presenter {

    PackingApi api;


    @Inject
    public StockScanPresenter(PackingApi api) {
        this.api = api;
    }

    @Override
    public void createScanView(ViewReq req) {
        mView.showDialog("初始化配置中...");
        NetHelper.request(api.createView(req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String id) {
                mView.onCompleteView(id);
                mView.dismissDialog();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.dismissDialog();
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void updateScanView(ViewReq req, int pos) {
        NetHelper.request(api.updateView(req), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                AnalysisInfoResp analysisInfoResp = new Gson().fromJson(new Gson().toJson(data), AnalysisInfoResp.class);
                mView.onCompleteUpdateView(analysisInfoResp.getData(), pos);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.errorUpdate(e.getErrorMsg(), pos);
            }
        });
    }

    @Override
    public void submitScanView(ViewReq req) {
        NetHelper.request(api.confirmViewData(req), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                mView.onSuccessSubmit("录入成功", false);

            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void destroyScanView(PageIdReq req) {
        NetHelper.request(api.closeView(req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String data) {
                mView.finishUI();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.finishUI();
            }
        });
    }

    @Override
    public void getScanView(ViewReq req) {
        NetHelper.request(api.getViewData(req), mView, new ICallback<AnalysisResp>() {
            @Override
            public void onSuccess(AnalysisResp data) {
                mView.onCompleteUpdateView(data, 0);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void closeOpenScanView(PageIdReq req) {

    }

    @Override
    public void getScanConfigData(String scene, FiltersReq req) {
        NetHelper.request(api.getScanConfigDetails(scene, URLPath.Stock.PATH_STOCK_CONFIG_DETAILS, req), mView, new ICallback<CommonListDataResp<ScanConfigBean>>() {
            @Override
            public void onSuccess(CommonListDataResp<ScanConfigBean> data) {
                mView.initScanItem(data.getData().get(0).getEntity(), data.getData().get(0).isContainer());
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void createScanTask(TaskReq req) {
        mView.showDialog("初始化配置中...");
        NetHelper.request(api.createScanTask(req), mView, new ICallback<TaskIdBean>() {
            @Override
            public void onSuccess(TaskIdBean data) {
                mView.initConfigInfo(data);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.dismissDialog();
                mView.showFailedView(e.getErrorMsg());
            }
        });

    }

    @Override
    public void submitTask(TaskIdReq req) {
        mView.showDialog("提交中...");
        NetHelper.request(api.submitTask(req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String data) {
                mView.onSuccessSubmit("提交成功", true);
                mView.dismissDialog();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.dismissDialog();
                Integer code = Integer.valueOf(e.getCode());
                if (code >= 90000 && code <= 99999) {
                    if (req.getSponsors() != null) {
                        List<String> codes = new ArrayList<>(Arrays.asList(e.getData()));
                        codes.add(e.getCode());
                        req.setSponsors(codes.toArray(new String[0]));
                    } else {
                        List<String> codes = new ArrayList<>();
                        codes.add(e.getData());
                        req.setSponsors(codes.toArray(new String[0]));
                    }

                    mView.showTipsDialog(req, e.getErrorMsg());

                } else {
                    mView.showFailedView(e.getErrorMsg());
                }

            }
        });
    }

    @Override
    public void closeContainer(ViewReq req) {
        NetHelper.request(api.closeContainer(req), mView, new ICallback<AnalysisInfoResp>() {
            @Override
            public void onSuccess(AnalysisInfoResp data) {

            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }
}
