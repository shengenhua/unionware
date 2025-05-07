package com.unionware.wms.inter.box;

import com.unionware.wms.inter.scan.ScanViewContract;

import unionware.base.model.bean.PropertyBean;
import unionware.base.model.req.BarcodePrintExportReq;
import unionware.base.model.req.ViewReq;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.resp.ActionResp;

import java.util.List;

public interface WMSBoxContract {
    interface View extends ScanViewContract.View {

        void initPageId(String pageId);

        void initStateId(String primaryId);

        boolean initScanItem(List<PropertyBean> list);

        void onConfirmBarCode(String tips);
        void showTipsDialog(ViewReq submitReq, ViewReq viewReq, ViewReq enterReq,String tips,int position);//交互提示
        void barcodePrintExportReq(List<ActionResp> actions);//获取打印PDF
        void print(String data);//打印
        void showDialog(String msg);//提示

        void dismissDialog();//关闭提示
        /**
         * 作废清空
         */
        void cancelTask();

        /**
         * 重新开始创建view
         */
        void restartView();

        void closeView();
        void playVoice(int pos,String type);//播放声音 编辑列表用
        void playVoice(String type);//播放声音
    }

    interface Presenter extends ScanViewContract.Presenter<View> {
        /**
         * 获取配置信息
         */
        void getConfig(String scene, String name, FiltersReq req);

        void getBoxStateId(String scene, String name, FiltersReq req); // 获取装箱状态id（）

        void getPageId(ViewReq req); //获取PageId

        /**
         * 关箱
         */
        void closeBoxCode(ViewReq viewReq);

        /**
         * 确认录入
         */
        void confirmBarCode(ViewReq viewReq, int pos);

        /**
         * 作废任务
         */
        void cancelTask(ViewReq viewReq);

        /**
         * 生成包装码
         *
         * @param viewReq
         * @param pos
         */
        void createBoxCode(ViewReq viewReq, int pos);


        void getScanView(ViewReq req, int pos); // 获取视图
        void barcodePrintExportReq(String scene, BarcodePrintExportReq req);//调用打印
        void boxPrintExportReq(String scene,BarcodePrintExportReq req);//拆装箱打印
    }
}
