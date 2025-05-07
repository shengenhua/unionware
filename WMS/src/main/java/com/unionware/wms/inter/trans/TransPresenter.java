package com.unionware.wms.inter.trans;

import android.content.Context;
import android.text.TextUtils;

import com.unionware.wms.URLPath;
import com.unionware.wms.api.PackingApi;
import com.unionware.wms.model.bean.BarcodeDetailsInfoBean;
import com.unionware.wms.model.bean.BoxPackingsBean;
import com.unionware.wms.model.bean.RePackBean;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.req.AnalysisReq;
import com.unionware.wms.model.req.RePackReq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.bean.PrintTemplateBean;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.PrintExportReq;
import unionware.base.model.req.PrintTemplateReq;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;
import unionware.base.room.DatabaseProvider;
import unionware.base.room.ThreadTask;
import unionware.base.room.table.TransScanInfo;

/**
 * @Author : pangming
 * @Time : On 2023/6/5 15:28
 * @Description : RePackPresenter
 */

public class TransPresenter extends BasePresenter<TransContract.View> implements TransContract.Persenter {
    PackingApi api;

    //private boolean isLoading = true;
    @Inject
    public TransPresenter(PackingApi api) {
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
    public void analysisPackBarcodeByTransfer(Context context, AnalysisReq req, int pos) {
        //如果是转入箱码，判断是否已扫入，扫入不能再扫入其它箱码
        if ("in".equals(req.getPackCodeType())) {
            List<TransScanInfo> list =
                    ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao().queryByCodeAndType(req.getSetId(), "1"));

            if (list.size() > 0 && !list.get(0).getInBarcode().equals(req.getCode())) {
                mView.showFailAnalysisEvent(pos, "已存在转入箱码，不能再扫描其它转入箱码");
                mView.setInBarcode(list.get(0).getInBarcode());
                return;
            }
        }
        NetHelper.request(api.analysisPackBarcodeByTransfer(req), mView, new ICallback<List<BarcodeDetailsInfoBean>>() {
            @Override
            public void onSuccess(List<BarcodeDetailsInfoBean> data) {
                if (data.size() > 0) {
                    mView.showSuccessAnalysisEvent(data.get(0), pos);
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
    public void getPrintTemplate(String formId, int pos) {
        NetHelper.request(api.getPrintTemplate(new PrintTemplateReq(URLPath.Trans.PATH_TRANS_FORM_ID)), mView, new ICallback<List<PrintTemplateBean>>() {
            @Override
            public void onSuccess(List<PrintTemplateBean> data) {

            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailAnalysisEvent(pos, e.getErrorMsg());
            }
        });
    }

    @Override
    public void confirmEntry(Context context, String inBarcode, String outBarcode, String internalCodeId) {
        //判断是否保存转入箱
        if (!TextUtils.isEmpty(inBarcode)) {

            List<TransScanInfo> list =
                    ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao().queryByCodeAndType(internalCodeId, "1"));
            int size = list.size();
            if (size == 0) {
                TransScanInfo transScanInfo = new TransScanInfo();
                transScanInfo.setInternalCodeId(internalCodeId);
                transScanInfo.setInBarcode(inBarcode);
                transScanInfo.setOutBarcode("");
                transScanInfo.setTransType("1");
                ThreadTask.start(() -> DatabaseProvider.getInstance().getTransScanInfoDao().insert(transScanInfo));
            }
        }
        //控制转出箱码不允许重复扫描
        List<TransScanInfo> list =
                ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao().queryByCodeAndOutBar(internalCodeId, outBarcode));
        if (list.size() == 0) {
            TransScanInfo transScanInfo = new TransScanInfo();
            transScanInfo.setInternalCodeId(internalCodeId);
            transScanInfo.setInBarcode(inBarcode);
            transScanInfo.setOutBarcode(outBarcode);
            ThreadTask.start(() -> DatabaseProvider.getInstance().getTransScanInfoDao().insert(transScanInfo));
            mView.showSuccessConfirmEntry();
        } else {
            mView.showFailConfirmEntry("转出箱码不允许重复扫描");
        }
//
    }

    @Override
    public void setInBarcode(Context context, String internalCodeId) {
        List<TransScanInfo> list =
                ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao().queryByCodeAndType(internalCodeId, "1"));
        if (list.size() > 0) {
            mView.setInBarcode(list.get(0).getInBarcode());
        } else {
            mView.setFocusable(0);
        }

    }

    @Override
    public void rePacking(Context context, String setId, String templateId) {

//        if(isLoading){
//            mView.showLoadingView();
//            return;
//        }
        List<TransScanInfo> list =
                ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao().queryByCodeAndTypeIsNull(setId));
        if (list.size() == 0) {
            mView.showFailedView("还未有扫描记录");
            return;
        }
        RePackReq req = new RePackReq();
        req.setSetId(setId);
        req.setInPackCode(list.get(0).getInBarcode());
        List<BoxPackingsBean> boxPackingsBeanList = new ArrayList<>();
        for (TransScanInfo transScanInfo : list) {
            BoxPackingsBean boxPackingsBean = new BoxPackingsBean();
            boxPackingsBean.setPackCode(transScanInfo.getOutBarcode());
            boxPackingsBeanList.add(boxPackingsBean);
        }
        req.setPackings(boxPackingsBeanList);
        mView.showLoadingView();
        NetHelper.request(api.rePacking(req), mView, new ICallback<RePackBean>() {
            @Override
            public void onSuccess(RePackBean data) {
                mView.hideLoadingView();
                mView.showSuccessSubmit("转箱成功");

                ThreadTask.start(() -> DatabaseProvider.getInstance().getTransScanInfoDao().deleteList(setId));
//                if(!TextUtils.isEmpty(templateId)){
//                    printExport(context,data.getId(),templateId);
//                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.hideLoadingView();
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void printExport(Context context, String scene, String billId, String tempId) {
        PrintExportReq req = new PrintExportReq();
        req.setFormId(URLPath.Trans.PATH_TRANS_FORM_ID);
        List<String> billIds = new ArrayList<>();
        billIds.add(billId);
        req.setBillIds(billIds);
        List<String> templateIds = new ArrayList<>();
        templateIds.add(tempId);
        req.setTemplateIds(templateIds);
        mView.showLoadingView();
        NetHelper.request(api.getPrintExport(scene, req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String data) {

                if (!"!PrinterRemoteService".equals(data)) {
                    //需要需要打印
                }
                mView.hideLoadingView();
                mView.showSuccessSubmit("获取套打导出成功");
                //Log.e("printExport","获取套打导出成功");

            }

            @Override
            public void onFailure(ApiException e) {
                mView.hideLoadingView();
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }


}
