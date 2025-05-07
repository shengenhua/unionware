package com.unionware.wms.inter.unpack;

import android.content.Context;
import android.text.TextUtils;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.room.DatabaseProvider;
import unionware.base.room.ThreadTask;
import unionware.base.room.table.UnPackScanInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @Author : pangming
 * @Time : On 2023/6/1 15:23
 * @Description : UnPackScanListPresenter
 */

public class UnPackScanListPresenter extends BasePresenter<UnPackScanListContract.View> implements UnPackScanListContract.Persenter {
    @Inject
    public UnPackScanListPresenter() {

    }

    @Override
    public void getAllScanListByInternalCodeId(Context context, String id) {
//        List<UnPackScanInfo> list =ManagerFactory.getInstance(context).getUnPackScanInfoManager().queryBuilder().where(UnPackScanInfoDao.Properties.InternalCodeId.eq(id)).list();
//        boolean isSubItem = true;
//        if(list.size()>0){
//            UnPackScanInfo unPackScanInfo = new UnPackScanInfo();
//            if(list.get(0).getPackCode()!=null && !TextUtils.isEmpty(list.get(0).getPackCode())){
//                unPackScanInfo.setPackCode(list.get(0).getPackCode());
//                unPackScanInfo.setId(0L);
//                list.add(0,unPackScanInfo);
//                isSubItem = false;
//            }
//        }
//        mView.showScanList(list,isSubItem);
    }

    @Override
    public void getDetailsScanListByInternalCodeId(Context context, String id) {
        //判断是否有包装条码


        List<UnPackScanInfo> unPackScanInfos = ThreadTask.getTwo(() -> DatabaseProvider.getInstance()
                .getUnPackScanInfoDao().queryByCodeAPcADc(id, "", ""));
        int size = unPackScanInfos.size();
        List<UnPackScanInfo> list = new ArrayList<>();
        if (size > 0) {
            list = ThreadTask.getTwo(() -> DatabaseProvider.getInstance()
                    .getUnPackScanInfoDao().queryByCodeADc(id, ""));
        } else {
            list = ThreadTask.getTwo(() -> DatabaseProvider.getInstance()
                    .getUnPackScanInfoDao().queryByCodeAPc(id, ""));
        }
        mView.showDetailsScanList(list);
    }

    @Override
    public void getPackScanListByInternalCodeId(Context context, String id) {

        List<UnPackScanInfo> list = ThreadTask.getTwo(() -> DatabaseProvider.getInstance()
                .getUnPackScanInfoDao().queryByCodeAPcADc(id, "", ""));
        mView.showPackScanList(list);
    }

    @Override
    public void deleteByEntity(Context context, int position, UnPackScanInfo unPackScanInfo) {
        ThreadTask.start(() -> DatabaseProvider.getInstance()
                .getUnPackScanInfoDao().delete(unPackScanInfo));
        mView.upDataByDeleteDetail(unPackScanInfo);
//        if(!"".equals(unPackScanInfo.getPackCode()) && position == 1){
//            mView.upDataByDeleteAll();
//        }

    }

    @Override
    public void deleteByInternalCodeId(Context context, String id) {
        ThreadTask.start(() -> DatabaseProvider.getInstance()
                .getUnPackScanInfoDao().deleteList(id));
        mView.upDataByDeleteAll();
    }

    @Override
    public void deleteAllDetailByInternalCodeId(Context context, String id, String packCode) {
        ThreadTask.start(() -> DatabaseProvider.getInstance()
                .getUnPackScanInfoDao().deleteListByDetailNot(id,""));
        mView.upDataByDeleteDetailsAll();
    }


}
