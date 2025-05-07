package com.unionware.wms.inter.box;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unionware.base.lib_common.model.resp.ListDataViewResp;
import com.unionware.wms.api.PackingApi;
import com.unionware.wms.model.bean.ScanConfigBean;
import com.unionware.wms.model.req.PageIdReq;
import com.unionware.wms.model.resp.AnalysisTResp;
import com.unionware.wms.model.resp.BarcodesResp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import unionware.base.app.view.base.mvp.BasePresenter;
import unionware.base.model.bean.PropertyBean;
import unionware.base.model.req.BarcodePrintExportReq;
import unionware.base.model.req.FiltersReq;
import unionware.base.model.req.ViewReq;
import unionware.base.model.resp.AnalysisInfoResp;
import unionware.base.model.resp.AnalysisResp;
import unionware.base.model.resp.CommonListDataResp;
import unionware.base.network.NetHelper;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

/**
 * @Author : pangming
 * @Time : On 2024/8/14 11:24
 * @Description : SplitPresenter
 */
public class SplitPresenter extends BasePresenter<SplitContract.View> implements SplitContract.Presenter {
    PackingApi api;
    // 定义需要存储和管理的数据
    private final MutableLiveData<AnalysisResp> analysisLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> printData = new MutableLiveData<>();

    public MutableLiveData<AnalysisResp> getAnalysisLiveData() {
        return analysisLiveData;
    }

    public MutableLiveData<String> getPrintData() {
        return printData;
    }

    @Inject
    public SplitPresenter(PackingApi api) {
        this.api = api;
    }

    @Override
    public void getPageId(ViewReq req) {
        NetHelper.request(api.createViewGetData(req), mView, new ICallback<ListDataViewResp<AnalysisResp, PropertyBean>>() {
            @Override
            public void onSuccess(ListDataViewResp<AnalysisResp, PropertyBean> data) {
                mView.initPageId(data.getPageId());
                if (mView.initScanItem(getEditList(data.getView(), data.getData()), getSHowList(data.getView(), data.getData())) && data.getData() != null) {
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
    public void getScanView(ViewReq req, int pos) {

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
    public void splitQuantity(ViewReq req) {
        NetHelper.request(api.splitQuantity(req), mView, new ICallback<AnalysisInfoResp>() {
            @Override
            public void onSuccess(AnalysisInfoResp data) {
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
    }

    @Override
    public void commandScanView(ViewReq req) {
        mView.showLoading("提交中...");
        NetHelper.request(api.commandViewData(req), mView, new ICallback<AnalysisInfoResp>() {
            @Override
            public void onSuccess(AnalysisInfoResp data) {
                updateView(data.getData(), -1);
                switch (req.getCommand()) {
                    case "INVOKE_SETSPLITQTY":
//                        ToastUtil.showToast("拆分数量成功");
                        ViewReq viewReq = new ViewReq(req.getPageId());
                        submitScanView(viewReq);
                        break;
                    default:
                        mView.dismissLoading();
                        break;
                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
                mView.dismissLoading();
            }
        });
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
//                if(pos == 0){
//                    //标准数量
//                   // splitQuantity();
//                }else {
//                    //非标准数量
//                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.errorUpdate(e.getErrorMsg(), pos);
            }
        });
    }

    @Override
    public void submitScanView(ViewReq req) {
        mView.showLoading("提交中...");
        req.setCommand("INVOKE_SUBMITTASK");
        NetHelper.request(api.commandView(req), mView, new ICallback<Object>() {
            @Override
            public void onSuccess(Object data) {
//                updateView(data.getData(), -1);
                mView.dismissLoading();
                if (req.getCommand().equals("INVOKE_SUBMITTASK")) {
                    //打印 打印后重新创建虚拟视图
                    Gson gson = new Gson();
                    AnalysisTResp<List<BarcodesResp>> analysisTResp = gson.fromJson(gson.toJson(data), new TypeToken<>() {
                    });
                    if (analysisTResp.getData() != null && !analysisTResp.getData().isEmpty()) {
                        mView.submitView(analysisTResp.getData());
                    } else {
                        mView.showFailedView("打印数据为空");
                        restartView(req.getPageId());
                    }
                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
                mView.dismissLoading();
            }
        });
    }

    @Override
    public void barcodePrintExportReq(String scene, BarcodePrintExportReq req, String pageId) {
        mView.showLoading("打印中...");
        NetHelper.request(api.barcodePrintExportReq(scene, req), mView, new ICallback<>() {
            @Override
            public void onSuccess(String data) {
                if (data != null && data.equals("!PrinterRemoteService")) {
                    mView.showFailedView("已发送打印指令");
                    restartView(pageId);
                } else {
                    printData.setValue(data);
                    mView.showFailedView("打印成功");
                }
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
                restartView(pageId);
//                printData.setValue("");
            }
        });
    }

    @Override
    public void restartView(String pageId) {
        NetHelper.request(api.closeView(new PageIdReq(pageId)), mView, new ICallback<>() {
            @Override
            public void onSuccess(String data) {
                mView.initStateId(null);
            }

            @Override
            public void onFailure(ApiException e) {
                mView.showFailedView(e.getErrorMsg());
            }
        });
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
    public void getScanView(ViewReq req) {

    }

    @Override
    public void closeOpenScanView(PageIdReq req) {

    }

    private void updateView(AnalysisResp data, int pos) {
        analysisLiveData.setValue(data);
        mView.onCompleteUpdateView(data, pos);
    }

    private List<PropertyBean> getEditList(List<PropertyBean> list, AnalysisResp data) {
        List<PropertyBean> editList = new ArrayList<>();
        if (list != null && data != null) {
            // editList = list.stream().filter(s -> data.getFBillHead().get(0).get(s.getKey()).isEnabled()).collect(Collectors.toList());
            //暂时用这个，等接口调整
            editList = list.stream().filter(s -> s.getKey().equals("FBarCodeId_Proxy") || s.getKey().equals("FSplitCount_Proxy"))
//                    .peek(propertyBean -> propertyBean.setValue(tryBigDecimal(propertyBean.getValue())))
                    .collect(Collectors.toList());
            editList.forEach(propertyBean -> {
                propertyBean.setValue(tryBigDecimal(propertyBean.getValue()));
            });
        }
        return editList;
    }

    private List<PropertyBean> getSHowList(List<PropertyBean> list, AnalysisResp data) {
        List<PropertyBean> editList = new ArrayList<>();
        if (list != null && data != null) {
            // editList = list.stream().filter(s -> data.getFBillHead().get(0).get(s.getKey()).isEnabled()).collect(Collectors.toList());
            //暂时用这个，等接口调整
            editList = list.stream().filter(s -> !s.getKey().equals("FBarCodeId_Proxy") && !s.getKey().equals("FSplitCount_Proxy"))
//                    .peek(propertyBean -> propertyBean.setValue(tryBigDecimal(propertyBean.getValue())))
                    .collect(Collectors.toList());
        }
        return editList;
    }

    public String tryBigDecimal(String value) {
        try {
            if (TextUtils.isEmpty(value)) {
                return "";
            }
            BigDecimal bigDecimal = new BigDecimal(value.trim());
            if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                return "0";
            }
            return bigDecimal.stripTrailingZeros().toPlainString();
        } catch (Exception ignored) {
        }
        return value;
    }
}
