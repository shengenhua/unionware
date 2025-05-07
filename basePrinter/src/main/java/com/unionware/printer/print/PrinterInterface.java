package com.unionware.printer.print;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Handler;

import java.io.File;
import java.util.List;

public interface PrinterInterface {
    void startHeartBeat() throws Exception;//原因：针对可以支持心跳的打印机，可以使打印机快速打印,功能：用心跳初始化连接打印机，开始心跳

    void stopHeartBeat();

    void connect(Context context, UsbDevice device);

    boolean isConnect();

    boolean checkPrintState(Handler handler);

    void sendString(String message) throws Exception;//通用发送消息

    void print(List<File> files, int num) throws Exception;//num份数

    void setCallBack(PrintCallBack callBack);

    void disconnect() throws Exception;

    String getTcpPortNumber();

    public interface PrintCallBack {
        void showToast(String msg, int type);////0连接(黄色)1心跳包2连接成功（绿绿色）3连接失败（红色）
    }

    //    void sendFileContents(String filePath) throws ConnectionException;//斑马打印机使用
    //    void printImage(Bitmap bitmap, int x, int y, int width, int height) throws Exception;//斑马打印机使用
}
