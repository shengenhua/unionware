package com.unionware.wms.inter.trans;

import android.content.Context;
import android.text.TextUtils;

import com.unionware.wms.URLPath;
import com.unionware.wms.api.PackingApi;
import com.unionware.wms.model.bean.BarcodeDetailsInfoBean;
import com.unionware.wms.model.bean.BoxDetailsBean;
import com.unionware.wms.model.bean.BoxPackingsBean;
import com.unionware.wms.model.bean.RePackBean;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.req.AnalysisReq;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;
import unionware.base.room.DatabaseProvider;
import unionware.base.room.ThreadTask;
import unionware.base.room.table.TransScanInfo;

import com.unionware.wms.model.req.RePackReq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * @Author : pangming
 * @Time : On 2023/6/8 19:42
 * @Description : BDTransPresenter
 */

public class BDTransPresenter extends BasePresenter<BDTransContract.View> implements BDTransContract.Persenter {
    PackingApi api;

    @Inject
    public BDTransPresenter(PackingApi api) {
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
                    ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao().queryByCodeAndType(req.getSetId(), "3"));
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

    }

    @Override
    public void confirmEntry(Context context, TransScanInfo transScanInfo) {
        //判断是否保存转入箱
        if (!TextUtils.isEmpty(transScanInfo.getInBarcode())) {
            List<TransScanInfo> list =
                    ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao()
                            .queryByCodeAndType(transScanInfo.getInternalCodeId(), "3"));
            int size = list.size();
            if (size == 0) {
                TransScanInfo transScanInfo2 = new TransScanInfo();
                transScanInfo2.setInternalCodeId(transScanInfo.getInternalCodeId());
                transScanInfo2.setInBarcode(transScanInfo.getInBarcode());
                transScanInfo2.setOutBarcode("");
                transScanInfo2.setTransType("3");
                ThreadTask.start(() -> DatabaseProvider.getInstance().getTransScanInfoDao()
                        .insert(transScanInfo2));
            }
        }
        //控制当前子项条码必需与已扫描的子项条码属于同一类条码，例如，若首个扫描录入的子项条码为包装条码，
        // 则后续扫描的子项条码也必需为包装条码；若首个扫描录入的子项条码为明细条码，则后续扫描的子项条码也必需为明细条码。

        List<TransScanInfo> list =
                ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao()
                        .queryByCodeAndTypeIsNull(transScanInfo.getInternalCodeId()));
        if (list.size() > 0 && !list.get(0).getDetailCodeType().equals(transScanInfo.getDetailCodeType())) {
            mView.showFailConfirmEntry("当前子项条码必需与已扫描的子项条码属于同一类条码(包装条码或明细条码)");
            return;
        }
        ThreadTask.start(() -> DatabaseProvider.getInstance().getTransScanInfoDao()
                .insert(transScanInfo));
        mView.showSuccessConfirmEntry();
    }

    @Override
    public void setInBarcode(Context context, String internalCodeId) {
        List<TransScanInfo> list =
                ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao()
                        .queryByCodeAndType(internalCodeId, "3"));
        if (list.size() > 0) {
            mView.setInBarcode(list.get(0).getInBarcode());
        } else {
            mView.setFocusable(0);
        }
    }

    @Override
    public void rePacking(Context context, String setId, String tempId) {
        List<TransScanInfo> list =
                ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao()
                        .queryByCodeAndTypeIsNull(setId));
        if (list.size() == 0) {
            mView.showFailedView("还未有扫描记录");
            return;
        }
        try {
            mView.showLoadingView();
            RePackReq req = new RePackReq();
            req.setSetId(setId);
            req.setInPackCode(list.get(0).getInBarcode());
            List<BoxPackingsBean> boxPackingsBeanList = new ArrayList<>();
            BoxPackingsBean boxPackingsBean = new BoxPackingsBean();
            boxPackingsBean.setPackCode("");
            List<BoxDetailsBean> boxDetailsBeanList = new ArrayList<>();
            for (TransScanInfo transScanInfo : list) {
                BoxDetailsBean boxDetailsBean = new BoxDetailsBean();
                boxDetailsBean.setBarCode(transScanInfo.getDetailCode());
                boxDetailsBean.setQty(transScanInfo.getDetailQty());
                boxDetailsBean.setEntryid("0");
                boxDetailsBeanList.add(boxDetailsBean);
            }
            boxPackingsBean.setDetails(boxDetailsBeanList);
            boxPackingsBeanList.add(boxPackingsBean);
            req.setPackings(boxPackingsBeanList);
            NetHelper.request(api.rePacking(req), mView, new ICallback<RePackBean>() {
                @Override
                public void onSuccess(RePackBean data) {
                    mView.hideLoadingView();
                    mView.showSuccessSubmit("转箱成功");

                    ThreadTask.start(() -> DatabaseProvider.getInstance().getTransScanInfoDao()
                            .deleteList(setId));
                }

                @Override
                public void onFailure(ApiException e) {
                    mView.hideLoadingView();
                    mView.showFailedView(e.getErrorMsg());
                }
            });
        } catch (Exception e) {
            mView.hideLoadingView();
            mView.showFailedView(e.getMessage());
        }

    }
}
