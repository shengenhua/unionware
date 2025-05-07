package com.unionware.wms.inter.trans;

import android.content.Context;

import com.unionware.wms.model.bean.BarcodeDetailsInfoBean;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.req.AnalysisReq;
import unionware.base.app.view.base.mvp.IPresenter;
import unionware.base.app.view.base.mvp.IView;

/**
 * @Author : pangming
 * @Time : On 2023/6/5 15:25
 * @Description : RePackContract
 */

public interface TransContract {
    interface View extends IView {
        void initScanConfigItem(ScanConfigBean data);  // 初始化扫描列表
        void showFailedView(String msg);//初始化扫描列表失败
        void showSuccessAnalysisEvent(BarcodeDetailsInfoBean data, int pos);//条码解析成功
        void showFailAnalysisEvent(int pos, String msg);//条码解析失败
        void showSuccessConfirmEntry();//录入成功
        void showFailConfirmEntry(String msg);//录入失败
        void setInBarcode(String inBarcode);//设置转入箱码
        void setFocusable(int position);//设置焦点
        void showSuccessSubmit(String msg);//转箱成功
        //void showFailSubmit(String msg);//转箱失败
        void showLoadingView();

        void hideLoadingView();
    }
    interface Persenter extends IPresenter<View> {
        void getScanConfigDetalisInfo(String id); //  获取扫描配置详细信息
        void analysisPackBarcodeByTransfer(Context context,AnalysisReq req, int pos);//扫描解析
        void getPrintTemplate(String formId, int pos);//获取套打模板
        void confirmEntry(Context context,String inBarcode,String outBarcode,String internalCodeId);//确认录入
        void setInBarcode(Context context,String internalCodeId);//已扫描入数据库过，设置转入箱码显示
        void rePacking(Context context,String setId,String tempId);//转箱
        void printExport(Context context,String scene,String billId,String tempId);//获取套打导出
    }
}
