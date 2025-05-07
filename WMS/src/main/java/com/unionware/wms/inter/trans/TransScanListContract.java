package com.unionware.wms.inter.trans;

import android.content.Context;

import java.util.List;

import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;
import unionware.base.room.table.TransScanInfo;

/**
 * @Author : pangming
 * @Time : On 2023/6/6 16:31
 * @Description : TransScanListContract
 */

public interface TransScanListContract {
    interface View extends IView {
        void showScanList(List<TransScanInfo> scanInfoList);

        void showDetailsScanList(List<TransScanInfo> scanInfoList);

        void showPackScanList(List<TransScanInfo> scanInfoList);

        void upDataByDeleteDetail(TransScanInfo transScanInfo);

        void upDataByDeleteAll();

        void upDataByDeleteDetailsAll();
    }

    interface Persenter extends IPresenter<View> {
        void getAllScanListByInternalCodeId(Context context, String id);

        void deleteByEntity(Context context, int position, TransScanInfo transScanInfo);

        void deleteByInternalCodeId(Context context, String id);

        void getDetailsScanListByInternalCodeId(Context context, String id);

        void getInBarcodeScanListByInternalCodeId(Context context, String id);

        void deleteAllDetailByInternalCodeId(Context context, String id);
    }
}
