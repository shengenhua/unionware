package com.unionware.wms.inter.unpack;

import android.content.Context;

import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;
import unionware.base.room.table.UnPackScanInfo;

import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2023/6/1 14:03
 * @Description : UnPackScanListContract
 */

public interface UnPackScanListContract {
    interface View extends IView {
        void showDetailsScanList(List<UnPackScanInfo> scanInfoList);
        void showPackScanList(List<UnPackScanInfo> scanInfoList);
        void upDataByDeleteDetail(UnPackScanInfo unPackScanInfo);
        void upDataByDeleteAll();
        void upDataByDeleteDetailsAll();
    }
    interface Persenter extends IPresenter<View> {
        void getAllScanListByInternalCodeId(Context context,String id);
        void getDetailsScanListByInternalCodeId(Context context,String id);
        void getPackScanListByInternalCodeId(Context context,String id);
        void deleteByEntity(Context context,int position,UnPackScanInfo unPackScanInfo);
        void deleteByInternalCodeId(Context context,String id);
        void deleteAllDetailByInternalCodeId(Context context,String id,String packCode);

    }
}
