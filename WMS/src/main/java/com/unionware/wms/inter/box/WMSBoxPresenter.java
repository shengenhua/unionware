package com.unionware.wms.inter.box;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.unionware.base.lib_common.model.resp.ListDataViewResp;
import com.unionware.base.lib_ui.utils.SoundType;
import com.unionware.wms.api.PackingApi;
import com.unionware.wms.command.CommandInvoke;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.req.PageIdReq;

import unionware.base.model.bean.PropertyBean;
import unionware.base.model.req.BarcodePrintExportReq;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.ViewReq;
import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.resp.AnalysisInfoResp;
import unionware.base.model.resp.AnalysisResp;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallAllDataBack;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;
import unionware.base.network.response.BaseResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class WMSBoxPresenter extends BasePresenter<WMSBoxContract.View> implements WMSBoxContract.Presenter {
    PackingApi api;

    // 定义需要存储和管理的数据
    private final MutableLiveData<AnalysisResp> analysisLiveData = new MutableLiveData<>();
    //拆箱转入箱码，监听扫描返回是否成功
    private final MutableLiveData<String> codeData = new MutableLiveData<>();

    public MutableLiveData<String> getCodeData() {
        return codeData;
    }

    public MutableLiveData<AnalysisResp> getAnalysisLiveData() {
        return analysisLiveData;
    }

    @Inject
    public WMSBoxPresenter(PackingApi api) {
        this.api = api;
    }

    @Override
    public void getConfig(String scene, String name, FiltersReq req) {
        NetHelper.request(api.getCommonList(scene, name, req), mView, new ICallback<CommonListDataResp<Map<String, Object>>>() {
            @Override
            public void onSuccess(CommonListDataResp<Map<String, Object>> data) {

                data.getData();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void getBoxStateId(String scene, String name, FiltersReq req) {
        NetHelper.request(api.getBoxStateId(scene, name, req), mView, new ICallback<CommonListDataResp<ScanConfigBean>>() {
            @Override
            public void onSuccess(CommonListDataResp<ScanConfigBean> data) {
                mView.initStateId(data.getData().isEmpty() ? null : data.getData().get(0).getId());
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void getPageId(ViewReq req) {
        NetHelper.request(api.createViewGetData(req), mView, new ICallback<ListDataViewResp<AnalysisResp, PropertyBean>>() {
            @Override
            public void onSuccess(ListDataViewResp<AnalysisResp, PropertyBean> data) {
                mView.initPageId(data.getPageId());
                if (mView.initScanItem(data.getView()) && data.getData() != null) {
                    updateView(data.getData(), -1);
                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void closeBoxCode(ViewReq viewReq) {
//        NetHelper.request(api.commandViewDataToView(viewReq), mView, new ICallback<Object>() {
//            @Override
//            public void onSuccess(Object data) {
        //关箱后,直接提交就好不用2步了
        viewReq.setCommand(CommandInvoke.Pack.WMS_SUBMIT_TASK);
        submitScanView(viewReq, new ICallAllDataBack<Object>() {
            @Override
            public void onNoUse(Object data) {
                mView.onSuccessSubmit("关箱成功", false);
                mView.playVoice(SoundType.Default.SUCCESS);
                mView.restartView();
                AnalysisInfoResp analysisInfoResp = new Gson().fromJson(new Gson().toJson(data), AnalysisInfoResp.class);
                mView.barcodePrintExportReq(analysisInfoResp.getAction());
            }

            @Override
            public void onSuccessAllData(ApiException e) {
                Integer code = Integer.valueOf(e.getCode());
                if (code >= 90000 && code <= 99999) {
                    if (viewReq.getSponsors() != null) {
                        List<String> codes = new ArrayList<>(Arrays.asList(e.getData()));
                        codes.add(e.getCode());
                        viewReq.setSponsors(codes.toArray(new String[0]));
                    } else {
                        List<String> codes = new ArrayList<>();
                        codes.add(e.getData());
                        viewReq.setSponsors(codes.toArray(new String[0]));
                    }

                    mView.showTipsDialog(viewReq, null, null, e.getErrorMsg(), 0);

                } else if (code == 200) {
                    mView.onSuccessSubmit(e.getErrorMsg(), false);
                    mView.restartView();
                    BaseResponse baseResponse = new Gson().fromJson(e.getData(), BaseResponse.class);
                    AnalysisInfoResp analysisInfoResp = new Gson().fromJson(new Gson().toJson(baseResponse.getData()), AnalysisInfoResp.class);
                    mView.barcodePrintExportReq(analysisInfoResp.getAction());
                    mView.playVoice(SoundType.Default.SUCCESS);
                } else {
                    mView.showFailedView(e.getErrorMsg());
                    mView.playVoice(SoundType.Default.ERROR);
                }
            }
        });
    }

//            @Override
//            public void onFailure(ApiException e) {
//                Integer code = Integer.valueOf(e.getCode());
//                if (code >= 90000 && code <= 99999) {
//                    if (viewReq.getSponsors() != null) {
//                        List<String> codes = new ArrayList<>(Arrays.asList(e.getData()));
//                        codes.add(e.getCode());
//                        viewReq.setSponsors(codes.toArray(new String[0]));
//                    } else {
//                        List<String> codes = new ArrayList<>();
//                        codes.add(e.getData());
//                        viewReq.setSponsors(codes.toArray(new String[0]));
//                    }
//
//                    mView.showTipsDialog(viewReq, null,null,e.getErrorMsg(),0);
//
//                }else {
//                    mView.showFailedView(e.getErrorMsg());
//                }
//            }
//        });
//    }

    @Override
    public void confirmBarCode(ViewReq viewReq, int pos) {
        NetHelper.request(api.commandViewDataToView(viewReq), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                mView.onConfirmBarCode("录入成功");
                mView.playVoice(SoundType.Default.SUCCESS);
                AnalysisInfoResp analysisInfoResp = new Gson().fromJson(new Gson().toJson(data), AnalysisInfoResp.class);
                updateView(analysisInfoResp.getData(), pos - 1);
            }

            @Override
            public void onFailure(ApiException e) {
                Integer code = Integer.valueOf(e.getCode());
                if (code >= 90000 && code <= 99999) {
                    if (viewReq.getSponsors() != null) {
                        List<String> codes = new ArrayList<>(Arrays.asList(e.getData()));
                        codes.add(e.getCode());
                        viewReq.setSponsors(codes.toArray(new String[0]));
                    } else {
                        List<String> codes = new ArrayList<>();
                        codes.add(e.getData());
                        viewReq.setSponsors(codes.toArray(new String[0]));
                    }

                    mView.showTipsDialog(null, null, viewReq, e.getErrorMsg(), pos);
                } else {
                    mView.showFailedView(e.getErrorMsg());
                    mView.playVoice(SoundType.Default.ERROR);
                }
            }
        });
    }

    @Override
    public void cancelTask(ViewReq viewReq) {
        NetHelper.request(api.commandViewData(viewReq), mView, new ICallback<AnalysisInfoResp>() {
            @Override
            public void onSuccess(AnalysisInfoResp data) {
                mView.onSuccessSubmit("作废成功", false);
                //重新创建 视图
                mView.restartView();
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void createBoxCode(ViewReq viewReq, int pos) {
        NetHelper.request(api.commandViewData(viewReq), mView, new ICallback<AnalysisInfoResp>() {
            @Override
            public void onSuccess(AnalysisInfoResp data) {
                updateView(data.getData(), pos);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    private void updateView(AnalysisResp data, int pos) {
        analysisLiveData.setValue(data);
        mView.onCompleteUpdateView(data, pos);
    }

    @Override
    public void createScanView(ViewReq req) {
    }

    @Override
    public void updateScanView(ViewReq req, int pos) {
        NetHelper.request(api.updateView(req), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                AnalysisInfoResp analysisInfoResp = new Gson().fromJson(new Gson().toJson(data), AnalysisInfoResp.class);
                updateView(analysisInfoResp.getData(), pos);
                mView.playVoice(pos, SoundType.Default.SUCCESS);
                //根据返回转入箱码，更新
                for (String key : analysisInfoResp.getData().getFBillHead().get(0).keySet()) {
                    if (key.equals("FNewCodeId") && analysisInfoResp.getData().getFBillHead().get(0).get("FNewCodeId").getNumber() != null && !analysisInfoResp.getData().getFBillHead().get(0).get("FNewCodeId").getNumber().isEmpty()) {
                        codeData.setValue("200");
                    }
                }
            }

            @Override
            public void onFailure(ApiException e) {
                Integer code = Integer.valueOf(e.getCode());
                ViewReq newReq = new ViewReq(req.getPageId());
                newReq.setItems(req.getItems());
                newReq.setParams(req.getParams());
                codeData.setValue(code.toString());
                if (code >= 90000 && code <= 99999) {
                    if (newReq.getSponsors() != null) {
                        List<String> codes = new ArrayList<>(Arrays.asList(e.getData()));
                        codes.add(e.getCode());
                        newReq.setSponsors(codes.toArray(new String[0]));
                    } else {
                        List<String> codes = new ArrayList<>();
                        codes.add(e.getData());
                        newReq.setSponsors(codes.toArray(new String[0]));
                    }

                    mView.showTipsDialog(null, newReq, null, e.getErrorMsg(), pos);
                } else {
                    mView.errorUpdate(e.getErrorMsg(), pos);
                    mView.playVoice(pos, SoundType.Default.ERROR);
                }

            }
        });
    }

    @Override
    public void submitScanView(ViewReq req) {
        submitScanView(req, new ICallAllDataBack<Object>() {
            @Override
            public void onNoUse(Object data) {
                mView.onSuccessSubmit("提交成功", false);
                mView.playVoice(SoundType.Default.SUCCESS);
                mView.restartView();
                AnalysisInfoResp analysisInfoResp = new Gson().fromJson(new Gson().toJson(data), AnalysisInfoResp.class);
                mView.barcodePrintExportReq(analysisInfoResp.getAction());
            }

            @Override
            public void onSuccessAllData(ApiException e) {
                Integer code = Integer.valueOf(e.getCode());
                if (code >= 90000 && code <= 99999) {
                    if (req.getSponsors() != null) {
                        List<String> codes = new ArrayList<>(Arrays.asList(e.getData()));
                        codes.add(e.getCode());
                        req.setSponsors(codes.toArray(new String[0]));
                    } else {
                        List<String> codes = new ArrayList<>();
                        codes.add(e.getData());
                        req.setSponsors(codes.toArray(new String[0]));
                    }

                    mView.showTipsDialog(req, null, null, e.getErrorMsg(), 0);

                } else if (code == 200) {
                    mView.onSuccessSubmit(e.getErrorMsg(), false);
                    mView.restartView();
                    BaseResponse baseResponse = new Gson().fromJson(e.getData(), BaseResponse.class);
                    AnalysisInfoResp analysisInfoResp = new Gson().fromJson(new Gson().toJson(baseResponse.getData()), AnalysisInfoResp.class);
                    mView.barcodePrintExportReq(analysisInfoResp.getAction());
                    mView.playVoice(SoundType.Default.SUCCESS);
                } else {
                    mView.showFailedView(e.getErrorMsg());
                    mView.playVoice(SoundType.Default.ERROR);
                }
            }
        });
    }

    private void submitScanView(ViewReq req, ICallAllDataBack<Object> callback) {
        NetHelper.requestByAllData(api.commandViewDataToView(req), mView, callback);
    }

    @Override
    public void destroyScanView(PageIdReq req) {
        NetHelper.request(api.closeView(req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String data) {
                mView.closeView();
            }

            @Override
            public void onFailure(ApiException e) {

            }
        });
    }

    @Override
    public void getScanView(ViewReq req, int pos) {
        NetHelper.request(api.getViewData(req), mView, new ICallback<AnalysisResp>() {
            @Override
            public void onSuccess(AnalysisResp data) {
                updateView(data, pos);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void barcodePrintExportReq(String scene, BarcodePrintExportReq req) {
        mView.showDialog("正在打印中...");
        NetHelper.request(api.barcodePrintExportReq(scene,req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String bean) {
                if(!"!PrinterRemoteService".equals(bean)){
                    mView.print(bean);
                    mView.showFailedView("打印成功");
                }else {
                    mView.showFailedView("已发送打印指令");
                }
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
    public void boxPrintExportReq(String scene,BarcodePrintExportReq req) {
        mView.showDialog("正在打印中...");
        NetHelper.request(api.boxPrintExportReq(scene,req), mView, new ICallback<String>() {
            @Override
            public void onSuccess(String bean) {
                if (!"!PrinterRemoteService".equals(bean)) {
                    mView.print(bean);
                    mView.showFailedView("打印成功");
                }else {
                    mView.showFailedView("已发送打印指令");
                }

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
    public void getScanView(ViewReq req) {
        getScanView(req, -1);
    }

    @Override
    public void closeOpenScanView(PageIdReq req) {

    }
}
