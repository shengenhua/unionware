package com.unionware.wms.inter.wms.scan;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.unionware.base.lib_ui.utils.SoundType;
import com.unionware.wms.api.PackingApi;
import com.unionware.wms.model.req.PageIdReq;
import com.unionware.wms.model.resp.AnalysisInfoBySubmitResp;

import unionware.base.model.bean.SimpleViewAndModelBean;
import unionware.base.model.bean.TaskIdBean;
import unionware.base.model.req.BarcodePrintExportReq;
import unionware.base.model.req.TaskIdReq;
import unionware.base.model.req.TaskReq;
import unionware.base.model.req.ViewReq;
import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.req.FiltersReq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.http.Body;
import unionware.base.model.resp.AnalysisInfoResp;
import unionware.base.model.resp.CommonDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallAllDataBack;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;
import unionware.base.network.response.BaseResponse;

/**
 * @Author : pangming
 * @Time : On 2024/7/11 16:11
 * @Description : NormalScanPresenter
 */
public class NormalScanPresenter extends BasePresenter<NorMalScanContract.View> implements NorMalScanContract.Presenter {
    PackingApi api;

    @Inject
    public NormalScanPresenter(PackingApi api) {
        this.api = api;
    }

    @Override
    public void getScanConfigData(String scene, FiltersReq req) {

    }

    @Override
    public void createScanTask(TaskReq req) {
        mView.showDialog("初始化配置中...");
        NetHelper.request(api.createNormalScanTask(req), mView, new ICallback<TaskIdBean>() {
            @Override
            public void onSuccess(TaskIdBean data) {
                mView.dismissDialog();
                mView.initConfigInfo(data);
                mView.refreshSourceOrTaskList();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.dismissDialog();
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void commandViewData(ViewReq req) {
        NetHelper.request(api.commandViewData(req), mView, new ICallback<AnalysisInfoResp>() {
            @Override
            public void onSuccess(AnalysisInfoResp data) {
                mView.dismissDialog();
                analysisAction(data);
                //
            }

            @Override
            public void onFailure(ApiException e) {
                mView.dismissDialog();
//                mView.openImageViewer("测试 https", "https://q2.itc.cn/images01/20240207/e24b767153b246b684203d55ac4e534c.jpeg");
//                mView.openImageViewer("测试 http", "http://q2.itc.cn/images01/20240207/e24b767153b246b684203d55ac4e534c.jpeg");
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void commandQueryViewData(ViewReq req) {
        NetHelper.request(api.commandViewData(req), mView, new ICallback<AnalysisInfoResp>() {
            @Override
            public void onSuccess(AnalysisInfoResp resp) {
                mView.dismissDialog();
                if (resp == null || resp.getAction() == null || resp.getAction().isEmpty()) {
                    return;
                }
                resp.getAction().stream().findFirst().ifPresent(actionResp -> {
                    if (actionResp.getName().equals("WMS_QUERYVIEW")) {//点击行 事件
                        ARouter.getInstance().build("/query/dyamic")
                                .withSerializable("scene", "WMS.Normal")
                                .withSerializable("title", "库存查询")
                                .withSerializable("reportFormId", "UNW_WMS_MOBI_INVERTOY_REPORT")
                                .withSerializable("primaryId", "67185f13c5221e")
                                .withSerializable("clickData", actionResp)
                                .withSerializable("showFilter", false)
                                .navigation();
                    }
                });
            }

            @Override
            public void onFailure(ApiException e) {
                mView.dismissDialog();
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void commandSubmitViewData(ViewReq req) {
        NetHelper.request(api.commandViewData(req), mView, new ICallback<AnalysisInfoResp>() {
            @Override
            public void onSuccess(AnalysisInfoResp data) {
                mView.dismissDialog();
                analysisAction(data);
                TaskIdReq taskIdReq = new TaskIdReq("INVOKE_SUBMITTASK", req.getPageId());
                flowTaskSubmit(taskIdReq);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.dismissDialog();
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    private void analysisAction(AnalysisInfoResp resp) {
        if (resp == null || resp.getAction() == null || resp.getAction().isEmpty()) {
            return;
        }
        resp.getAction().forEach(actionResp -> {
            switch (actionResp.getName()) {
                case "TOAST":
                    showToast(actionResp.getActionDetailResp().getType(), actionResp.getActionDetailResp().getMessage());
                    break;
                case "WMS_OPENIMAGEVIEWER":
                    //打开图片地址
                    mView.openImageViewer(actionResp.getActionDetailResp().getName(), actionResp.getActionDetailResp().getUri());
                    break;
                default:
                    break;
            }
        });
    }

    /**
     *后台返回提示
     */
    private void showToast(int type, String message) {
        switch (type) {
            case 1:
                mView.playVoice(SoundType.Default.SUCCESS);
                break;
            case 2:
            case 4:
                mView.playVoice(SoundType.Default.ERROR);
                break;
            case 3:
                mView.playVoice(SoundType.Default.SUBMIT_SUCCESS);
                break;
        }
        mView.showFailedView(message);
    }

    @Override
    public void closeContainer(ViewReq req) {

    }

    @Override
    public void createViewGetMore(ViewReq req) {
        mView.showDialog("初始化配置中...");
        NetHelper.request(api.createViewGetMore(req), mView, new ICallback<SimpleViewAndModelBean>() {
            @Override
            public void onSuccess(SimpleViewAndModelBean bean) {
                mView.onCompleteView(bean);
                mView.dismissDialog();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.dismissDialog();
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void flowTaskSubmit(@Body TaskIdReq req) {
        mView.showDialog("正在提交...");
        NetHelper.requestByAllData(api.flowTaskSubmit(req), mView, new ICallAllDataBack<Object>() {
            @Override
            public void onNoUse(Object id) {
                mView.onSuccessSubmit("提交成功", true);
                mView.refreshSourceOrTaskList();
                mView.dismissDialog();
            }

            @Override
            public void onSuccessAllData(ApiException e) {
                mView.dismissDialog();
                Integer code = Integer.valueOf(e.getCode());
                if (code >= 90000 && code <= 99999) {
                    if (req.getSponsors() != null) {
                        List<String> codes = new ArrayList<>(Arrays.asList(req.getSponsors()));
                        codes.add(e.getData());
                        req.setSponsors(codes.toArray(new String[0]));
                    } else {
                        List<String> codes = new ArrayList<>();
                        codes.add(e.getData());
                        req.setSponsors(codes.toArray(new String[0]));
                    }

                    mView.showTipsDialog(req, null, e.getErrorMsg());
                } else if (code == 200) {
                    mView.showFailedView(e.getErrorMsg());
                    //mView.onSuccessSubmit(e.getErrorMsg(), true);
                    mView.playVoice(SoundType.Default.SUCCESS);
                    mView.refreshSourceOrTaskList();
                    //判断是否有单据打印
                    BaseResponse baseResponse = new Gson().fromJson(e.getData(), BaseResponse.class);
                    AnalysisInfoBySubmitResp analysisInfoResp = new Gson().fromJson(new Gson().toJson(baseResponse.getData()), AnalysisInfoBySubmitResp.class);
                    if (analysisInfoResp.getAction() != null && analysisInfoResp.getAction().size() > 0) {
                        boolean flag = false;
                        for (int i = 0; i < analysisInfoResp.getAction().size(); i++) {
                            if (analysisInfoResp.getAction().get(i).getName().equals("WMS_FORMPRINT")) {
                                flag = true;
                                mView.printExportReq(analysisInfoResp.getAction(), true);
                            }
                        }
                        if (!flag) mView.onSuccessSubmit(e.getErrorMsg(), true);
                    } else {
                        mView.onSuccessSubmit(e.getErrorMsg(), true);
                    }

                } else {
                    mView.showFailedView(e.getErrorMsg());
                }
            }
        });
    }

//    @Override
//    public void setClientDefines(ClientCustomParametersReq req) {
//        mView.showDialog("默认值保存中...");
//        NetHelper.request(api.setClientDefines(req), mView, new ICallback<String>() {
//            @Override
//            public void onSuccess(String bean) {
//                mView.showFailedView("保存成功");
//                mView.getClientDefines(req.getParams(),false);
//                mView.dismissDialog();
//            }
//
//            @Override
//            public void onFailure(ApiException e) {
//                mView.dismissDialog();
//                mView.showFailedView(e.getErrorMsg());
//            }
//        });
//    }
//
//    @Override
//    public void getClientDefines(ClientCustomParametersReq req) {
//        mView.showDialog("初始化配置中...");
//        NetHelper.request(api.getClientDefines(req), mView, new ICallback<List<ClientCustomParametersReq.Param>>() {
//            @Override
//            public void onSuccess(List<ClientCustomParametersReq.Param> bean) {
//                mView.getClientDefines(bean,true);
//                mView.dismissDialog();
//            }
//
//            @Override
//            public void onFailure(ApiException e) {
//                mView.dismissDialog();
//                mView.showFailedView(e.getErrorMsg());
//            }
//        });
//    }

    @Override
    public void barcodePrintExportReq(String scene, BarcodePrintExportReq req) {
        mView.showDialog("正在打印中...");
        NetHelper.request(api.barcodePrintExportReq(scene, req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String bean) {
                if (!"!PrinterRemoteService".equals(bean)) {
                    mView.print(bean);
                } else {
                    mView.showFailedView("已发送打印指令");
                }
                // mView.showFailedView("打印成功");
                mView.dismissDialog();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.dismissDialog();
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void printExportReq(String scene, BarcodePrintExportReq req, boolean isFinish) {
        mView.showDialog("正在打印中...");
        NetHelper.request(api.printExportReq(scene, req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String bean) {
                if (!"!PrinterRemoteService".equals(bean)) {
                    if (isFinish)
                        mView.printEndToFinish(bean);
                    else mView.print(bean);
                } else {
                    mView.showFailedView("已发送打印指令");
                    mView.onSuccessSubmit("", true);
                }
                // mView.showFailedView("打印成功");
                mView.dismissDialog();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.dismissDialog();
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void clientRowDoubleClick(ViewReq req, boolean isHaveScanInput) {
        NetHelper.request(api.clientRowDoubleClick(req), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                try {
                    AnalysisInfoResp analysisInfoResp = new Gson().fromJson(new Gson().toJson(data), AnalysisInfoResp.class);
                    analysisAction(analysisInfoResp);
                    mView.onCompleteUpdateView(analysisInfoResp.getData(), -300);
                } catch (Throwable e) {

                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.errorUpdate(e.getErrorMsg(), -300);
                if (isHaveScanInput) {
                    mView.playVoice(SoundType.Default.ERROR);
                    mView.requestFocus();
                }
            }
        });
    }

    @Override
    public void createScanView(ViewReq req) {
        mView.showDialog("初始化配置中...");
        NetHelper.request(api.createView(req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String id) {
                mView.onCompleteView(id);
                mView.dismissDialog();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.dismissDialog();
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void updateScanView(ViewReq req, int pos) {
        NetHelper.request(api.updateView(req), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                AnalysisInfoResp analysisInfoResp = new Gson().fromJson(new Gson().toJson(data), AnalysisInfoResp.class);
                mView.onCompleteUpdateView(analysisInfoResp.getData(), pos);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.errorUpdate(e.getErrorMsg(), pos);
                if (pos == -1) {
                    mView.playVoice(SoundType.Default.ERROR);
                    mView.requestFocus();
                }
            }
        });
    }

    @Override
    public void submitScanView(ViewReq req) {
        NetHelper.request(api.confirmViewData(req), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                mView.onSuccessSubmit("录入成功", false);
                mView.playVoice(SoundType.Default.SUCCESS);
                CommonDataResp commonDataResp = new Gson().fromJson(new Gson().toJson(data), CommonDataResp.class);
                mView.barcodePrintExportReq(commonDataResp.getAction());
            }

            @Override
            public void onFailure(ApiException e) {
                Integer code = Integer.valueOf(e.getCode());
                if (code >= 90000 && code <= 99999) {
                    if (req.getSponsors() != null) {
                        List<String> codes = new ArrayList<>(Arrays.asList(req.getSponsors()));
                        codes.add(e.getData());
                        req.setSponsors(codes.toArray(new String[0]));
                    } else {
                        List<String> codes = new ArrayList<>();
                        codes.add(e.getData());
                        req.setSponsors(codes.toArray(new String[0]));
                    }

                    mView.showTipsDialog(null, req, e.getErrorMsg());

                } else {
                    mView.playVoice(SoundType.Default.ERROR);
                    mView.errorUpdate(e.getErrorMsg(), -1);
                    mView.requestFocus();
                }
            }
        });
    }

    @Override
    public void destroyScanView(PageIdReq req) {
        NetHelper.request(api.closeView(req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String data) {
                mView.finishUI();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.finishUI();
            }
        });
    }

    @Override
    public void getScanView(ViewReq req) {

    }

    @Override
    public void closeOpenScanView(PageIdReq req) {
        NetHelper.request(api.closeView(req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String data) {
                mView.restartUI();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.restartUI();
            }
        });
    }

    @Override
    public void taskCancel(Map map) {
        NetHelper.request(api.taskCancel(map), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String data) {

            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }
}
