package com.unionware.wms.inter.unpack;

import android.content.Context;
import android.text.TextUtils;

import com.unionware.wms.URLPath;
import com.unionware.wms.api.PackingApi;
import com.unionware.wms.model.bean.BarcodeDetailsInfoBean;
import com.unionware.wms.model.bean.BoxDetailsBean;
import com.unionware.wms.model.bean.BoxPackingsBean;
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
import unionware.base.room.table.UnPackScanInfo;

import com.unionware.wms.model.req.PackingsReq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * @Author : pangming
 * @Time : On 2023/5/30 11:21
 * @Description : UnPackingPresenter
 */

public class UnPackingPresenter extends BasePresenter<UnPackContract.View> implements UnPackContract.Persenter {
    PackingApi api;

    @Inject
    public UnPackingPresenter(PackingApi api) {
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
    public void analysisPackBarcodeByBPUnpacking(AnalysisReq req, int pos) {
        NetHelper.request(api.analysisPackBarcodeByBPUnpacking(req), mView, new ICallback<List<BarcodeDetailsInfoBean>>() {
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
    public void clearPack(Context context, String UnPackType, String confirmType, PackingsReq req) {
        NetHelper.request(api.unPacking(req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String data) {
                //清箱成功后删除扫描记录
                ThreadTask.start(() -> DatabaseProvider.getInstance().getUnPackScanInfoDao().deleteList(req.getSetId()));
                mView.showSuccessClearPackEvent();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailClearPackEvent(e.getErrorMsg());
            }
        });
    }

    @Override
    public void unPack(Context context, String setId) {
        PackingsReq req = new PackingsReq();
        List<UnPackScanInfo> list = ThreadTask.getTwo(() -> DatabaseProvider.getInstance()
                .getUnPackScanInfoDao().queryByCode(setId));
        if (list.size() > 0) {
            List<BoxPackingsBean> packingsList = new ArrayList<>();
            BoxPackingsBean packings = new BoxPackingsBean();
            packings.setPackCode(list.get(0).getPackCode());
            List<BoxDetailsBean> boxDetailsBeanList = new ArrayList<>();
            for (UnPackScanInfo u : list) {
                BoxDetailsBean boxDetailsBean = new BoxDetailsBean();
                boxDetailsBean.setBarCode(u.getDetailCode());
                boxDetailsBean.setQty(u.getDetailQty());
                boxDetailsBean.setEntryid("0");
                boxDetailsBeanList.add(boxDetailsBean);
            }
            packings.setDetails(boxDetailsBeanList);
            packingsList.add(packings);
            req.setPackings(packingsList);
            req.setSetId(setId);
            req.setType("unPack");
            mView.showLoadingView();
            NetHelper.request(api.unPacking(req), mView, new ICallback<String>() {
                @Override
                public void onSuccess(String data) {
                    ThreadTask.start(() -> DatabaseProvider.getInstance()
                            .getUnPackScanInfoDao().deleteList(setId));
                    mView.hideLoadingView();
                    mView.showSuccessUnPackEvent();
                }

                @Override
                public void onFailure(ApiException e) {
                    mView.hideLoadingView();
                    mView.showFailUnPackEvent(e.getErrorMsg());
                }
            });

        } else {
            mView.showFailUnPackEvent("已扫列表还没数据，请先扫描");
        }

    }

    @Override
    public void confirmEntry(Context context, UnPackScanInfo unPackScanInfo) {
        //判断包装条码是否保存


        List<UnPackScanInfo> list = ThreadTask.getTwo(() -> DatabaseProvider.getInstance()
                .getUnPackScanInfoDao().queryByCodeAPcADc(unPackScanInfo.getInternalCodeId(),"",""));
        int size = list.size();
        /*int size = ManagerFactory.getInstance(context).getUnPackScanInfoManager().
                queryBuilder().where(UnPackScanInfoDao.Properties.
                        InternalCodeId.eq(unPackScanInfo.getInternalCodeId()),
                        UnPackScanInfoDao.Properties.PackCode.notEq(""),
                        UnPackScanInfoDao.Properties.DetailCode.eq("")).list().size();*/
        if (size == 0 && !TextUtils.isEmpty(unPackScanInfo.getPackCode())) {
            UnPackScanInfo packScanInfo = new UnPackScanInfo();
            packScanInfo.setPackCode(unPackScanInfo.getPackCode());
            packScanInfo.setDetailCode("");
            packScanInfo.setInternalCodeId(unPackScanInfo.getInternalCodeId());
            ThreadTask.start(() -> DatabaseProvider.getInstance().getUnPackScanInfoDao().insert(packScanInfo));
        }
        ThreadTask.start(() -> DatabaseProvider.getInstance().getUnPackScanInfoDao().insert(unPackScanInfo));
        mView.showSuccessconfirmEntry();
    }

    @Override
    public void setPackCode(Context context, String setId) {
        List<UnPackScanInfo> list = ThreadTask.getTwo(() -> DatabaseProvider.getInstance()
                .getUnPackScanInfoDao().queryByCodeAPcADc(setId,"",""));
        if (list.size() > 0) {
            mView.showPackCode(list.get(0).getPackCode());
        }
    }
}
