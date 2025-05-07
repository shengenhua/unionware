package com.unionware.printer.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.unionware.printer.ImageUtil;
import com.unionware.printer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import unionware.base.app.utils.BitmapUtil;

public class BasePrinter implements PrinterInterface {
    private PrintCallBack callBack;
    protected Context context;
    protected Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 测试使用
     */
    protected boolean isDebug = false;

    @Override
    public void startHeartBeat() throws Exception {

    }

    @Override
    public void stopHeartBeat() {

    }

    @Override
    public void connect(Context context, UsbDevice device) {
        if (context != null) {
            this.context = context;
        }
    }

    @Override
    public boolean isConnect() {
        if (isDebug) {
            return true;
        }
        showToast(context != null ? context.getString(R.string.conn_first) : "请先连接打印机", 3);
        return false;
    }

    @Override
    public boolean checkPrintState(Handler handler) {
        if (isDebug) {
            return true;
        }
        showToast(context != null ? context.getString(R.string.conn_first) : "请先连接打印机", 3);
        return false;
    }

    @Override
    public void sendString(String message) throws Exception {

    }

    @Override
    public void print(List<File> files, int num) throws Exception {
        for (File file : files) {
            ArrayList<Bitmap> bitmaps = ImageUtil.pdfToBitmap(file, 0);
            for (Bitmap bitmap : bitmaps) {
                Log.d("BasePrinter", "print: " + BitmapUtil.INSTANCE.bitmapToBase64(bitmap));
            }
        }
    }

    @Override
    public void setCallBack(PrintCallBack callBack) {
        this.callBack = callBack;
//        callBack.showToast("请先连接打印机",3);
    }

    public PrintCallBack getCallBack() {
        return callBack;
    }

    @Override
    public void disconnect() throws Exception {

    }

    @Override
    public String getTcpPortNumber() {
        return "";
    }

    protected void showToast(String msg, int type) {
        mainHandler.post(() -> {
            if (callBack != null)
                callBack.showToast(msg, type);
        });
    }
}
