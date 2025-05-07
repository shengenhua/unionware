package com.unionware.wms.inter.trans;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.room.DatabaseProvider;
import unionware.base.room.ThreadTask;
import unionware.base.room.table.TransScanInfo;

/**
 * @Author : pangming
 * @Time : On 2023/6/6 16:32
 * @Description : TransScanListPresenter
 */

public class TransScanListPresenter extends BasePresenter<TransScanListContract.View> implements TransScanListContract.Persenter {
    @Inject
    public TransScanListPresenter() {

    }


    @Override
    public void getAllScanListByInternalCodeId(Context context, String id) {
        List<TransScanInfo> list =
                ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao().queryByCode(id));
        if (list.size() > 0) {
            TransScanInfo transScanInfo = new TransScanInfo();
            transScanInfo.setInBarcode(list.get(0).getInBarcode());
            transScanInfo.setId(0L);
            list.add(0, transScanInfo);
        }
        mView.showScanList(list);
    }

    @Override
    public void deleteByEntity(Context context, int position, TransScanInfo transScanInfo) {
        ThreadTask.start(() -> DatabaseProvider.getInstance().getTransScanInfoDao().delete(transScanInfo));
        mView.upDataByDeleteDetail(transScanInfo);
    }

    @Override
    public void deleteAllDetailByInternalCodeId(Context context, String id) {
        ThreadTask.start(() -> DatabaseProvider.getInstance().getTransScanInfoDao().deleteListTypeIsNull(id));
        mView.upDataByDeleteDetailsAll();
    }

    @Override
    public void deleteByInternalCodeId(Context context, String id) {

        ThreadTask.start(() -> DatabaseProvider.getInstance().getTransScanInfoDao().deleteList(id));
        mView.upDataByDeleteAll();
    }

    @Override
    public void getDetailsScanListByInternalCodeId(Context context, String id) {
        List<TransScanInfo> list = ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao().queryByCodeAndTypeIsNull(id));
        mView.showDetailsScanList(list);
    }

    @Override
    public void getInBarcodeScanListByInternalCodeId(Context context, String id) {
        List<TransScanInfo> list = ThreadTask.getTwo(() -> DatabaseProvider.getInstance().getTransScanInfoDao().queryByCodeAndTypeIsNotNull(id));
        mView.showPackScanList(list);
    }


}
