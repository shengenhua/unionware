package com.unionware.wms.inter.wms.scan;

import com.unionware.wms.inter.scan.ScanViewContract;

import unionware.base.model.bean.EntityBean;
import unionware.base.model.bean.SimpleViewAndModelBean;
import unionware.base.model.bean.TaskIdBean;
import unionware.base.model.req.BarcodePrintExportReq;
import unionware.base.model.req.TaskIdReq;
import unionware.base.model.req.TaskReq;
import unionware.base.model.req.ViewReq;
import unionware.base.model.req.FiltersReq;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import unionware.base.model.resp.ActionResp;

/**
 * @Author : pangming
 * @Time : On 2024/7/11 16:06
 * @Description : NorMalScanContract
 */
public interface NorMalScanContract {
    public interface View extends ScanViewContract.View {
        void initScanItem(List<EntityBean> list, boolean isContainer);

        void initConfigInfo(TaskIdBean bean); // 初始化配置信息

        void onCompleteView(String id); // 完成初始化配置

        void onCompleteView(SimpleViewAndModelBean bean);//完成初始化配置,并返回View pageId Data

        void showDialog(String msg);//提示

        void dismissDialog();//关闭提示

        void showTipsDialog(TaskIdReq req, ViewReq viewReq, String tips);//交互提示

        void finishUI();//关闭提示

        /**
         * 重新创建视图
         */
        void restartUI();//

        void requestFocus();//获取焦点

        //void getClientDefines(List<ClientCustomParametersReq.Param> params,boolean isGet);//获取默认值
        void barcodePrintExportReq(List<ActionResp> actions);//获取打印PDF
        void printExportReq(List<ActionResp> actions,boolean isFinish);//获取单据打印

        void print(String data);//打印
        void printEndToFinish(String data);//打印

        void playVoice(String type);//播放声音

        void refreshSourceOrTaskList();//刷新源单或任务列表

        /**
         * 打开 图片查看器
         * @param name
         * @param uri
         */
        void openImageViewer(String name, String uri);
    }

    interface Presenter extends ScanViewContract.Presenter<View> {
        void getScanConfigData(String scene, FiltersReq req); // 查询作业流程配置

        void createScanTask(TaskReq req); // 创建扫描单据

        /**
         * //操作动作
         */
        void commandViewData(ViewReq req);
        /**
         * //操作动作
         */
        void commandQueryViewData(ViewReq req);

        void closeContainer(ViewReq req); // 关闭容器

        void createViewGetMore(ViewReq req);//创建视图返回更多

        void flowTaskSubmit(@Body TaskIdReq req);//提交作业

        //        void setClientDefines(ClientCustomParametersReq req);//批量设置客户端自定义参数
//        void getClientDefines(ClientCustomParametersReq req);//批量获取客户端自定义参数
        void barcodePrintExportReq(String scene, BarcodePrintExportReq req);//调用条码打印
        void printExportReq(String scene, BarcodePrintExportReq req,boolean isFinish);//调用单据打印
        void clientRowDoubleClick(ViewReq req, boolean isHaveScanInput);
        /**
         * //操作动作 操作之后提交
         */
        void commandSubmitViewData(ViewReq req);

        void taskCancel(Map map);//删除任务
    }
}
