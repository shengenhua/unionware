package com.unionware.wms.inter.unpack;

import android.content.Context;

import com.unionware.wms.inter.scan.ScanContract;
import com.unionware.wms.model.bean.BarcodeDetailsInfoBean;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.req.AnalysisReq;
import com.unionware.wms.model.req.PackingsReq;
import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;
import unionware.base.room.table.UnPackScanInfo;

/**
 * @Author : pangming
 * @Time : On 2023/5/30 11:15
 * @Description : UnPackContract
 */

public interface UnPackContract {
    interface View extends IView {
        void initScanConfigItem(ScanConfigBean data);  // 初始化扫描列表
        void showFailUnPackEvent(String msg);//拆箱失败
        void showSuccessUnPackEvent();//拆箱成功
        void showSuccessconfirmEntry();//录入成功
        void showSuccessClearPackEvent();//清箱成功
        void showFailClearPackEvent(String msg);//清箱失败
        void showFailedView(String msg);
        void showFailAnalysisEvent(int pos, String msg);//条码解析失败
        void showSuccessAnalysisEvent(BarcodeDetailsInfoBean data, int pos);//条码解析成功
        void showLoadingView();

        void hideLoadingView();
        void showPackCode(String packCode);
    }

    interface Persenter extends IPresenter<View> {
        void getScanConfigDetalisInfo(String id); //  获取扫描配置详细信息
        void analysisPackBarcodeByBPUnpacking(AnalysisReq req,int pos); // 更新箱码部分信息
        void clearPack(Context context,String UnPackType,String confirmType, PackingsReq req); //清箱
        void unPack(Context context,String setId);//拆箱

        void confirmEntry(Context context, UnPackScanInfo unPackScanInfo); //确认录入
        void setPackCode(Context context,String setId);//如何有扫描确定过，赋值包装条码
    }
}
