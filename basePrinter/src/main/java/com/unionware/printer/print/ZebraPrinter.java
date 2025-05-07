package com.unionware.printer.print;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mmkv.MMKV;
import com.unionware.printer.ImageUtil;
import com.unionware.printer.R;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Description:斑马打印机
 * Data：2020-09-18-13:56
 * Author: hyg
 */
public class ZebraPrinter extends BasePrinter {
    private Connection printerConnection;
    private com.zebra.sdk.printer.ZebraPrinter printer;
    private Object printing = new Object();
    private boolean printStop = false;
    private int isbluetooth = 0;//0 蓝牙打印，1无线打印
    private String blueToothAddress;
    private String tcpAddress;
    private String tcpPort;
    private int printerMode;
    private int angle;
    private Queue<String> queue = new PriorityQueue<>();
    private MMKV kv;

    public boolean isStartHeart() {
        if (queue == null) return false;
        synchronized (queue) {
            return queue.size() > 0;
        }
    }

    public ZebraPrinter(Context context) {
        this.context = context;
        kv = MMKV.mmkvWithID("app");
        initData();
    }

    private void initData() {
        isbluetooth = kv.decodeInt(PrintKey.IS_Bluetooth, 0);
        blueToothAddress = kv.decodeString(PrintKey.BLUETOOTH_ADDRESS, "");
        tcpPort = kv.decodeString(PrintKey.TCP_PORT, "");
        tcpAddress = kv.decodeString(PrintKey.TCP_ADDRESS, "");
        printerMode = kv.decodeInt(PrintKey.COMMON_PRINTER_MODE, 0);
        angle = kv.decodeInt(PrintKey.PRINT_SELECTION_ANGLE, 0);
    }

    @Override
    public void startHeartBeat() throws Exception {
        stopHeartBeat();//启动前关闭一次，防止多个线程同时存在
        new Thread(() -> {
            printStop = false;
            queue.offer("start");
            while (!printStop) {
                int interval = 15;//休眠15秒，心跳一次
                if (isConnect()) {
                    if (isStartHeart()) {
                        try {
                            sendCallBack(1);
                            Log.d("斑马打印机", "HeartBeat: ");
                            sendString("~HS");
                        } catch (Throwable e) {
                            Log.d("斑马打印机", "HeartBeat Err: " + ((e.getCause() == null) ? "" : e.getCause().getMessage()));
                            setPrintConnectionNull();
                            sendCallBack(3);
                            continue;
                        }
                    }
                } else {
                    Log.d("斑马打印机", "HeartBeat:Connecting ");
                    try {
                        sendCallBack(0);
                        disconnect();
                        connect();
                        sendCallBack(2);
                    } catch (ConnectionException e) {
                        interval = 5;
                        Log.e("斑马打印机", "重新连接，蓝牙未启用或没有权限");
                    } catch (Exception e) {
                        interval = 3;
                        sendCallBack(3);
                        Log.d("斑马打印机", "连接失败");
                    }
                }

                try {
                    int cnt = 0;
                    while (!printStop) {
                        if (cnt >= interval) break;
                        Thread.sleep(1000);
                        ++cnt;
                    }
                } catch (Exception e) {
                    Log.d("斑马打印机", "间隔出现异常:" + e.getMessage());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void stopHeartBeat() {
        printStop = true;
    }

    @Override
    public void sendString(String message) throws Exception {
        try {
            doSendString(message);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    void doSendString(String message) throws Exception {
        if (printerConnection != null) {
            synchronized (printerConnection) {
                synchronized (printing) {
                    printerConnection.sendAndWaitForResponse(message.getBytes(), 1000, 1000, null);
                }
            }
        }
    }

    @Override
    public void print(List<File> files, int num) throws Exception {
        initData();
        if (isConnect()) {
            Log.e("斑马打印机", "已连接打印机111");
            printf(files, num);
        } else {
            Log.e("斑马打印机", "连接打印机222");
            connectAndPrint(files, num);
        }
    }

    private void connectAndPrint(List<File> files, int num) throws Exception {
        try {
            connect();
        } catch (Exception e) {
            if (e.getMessage().equals("连接失败，请重试。")) {
                connectAndPrint(files, num);
            }
            throw new Exception(e.getMessage());
        }
        if (isConnect()) {
            Log.e("斑马打印机", "开始打印222");
            printf(files, num);
        } else {
            Thread.sleep(1000);
            Log.e("斑马打印机", "开始打印333");
            connectAndPrint(files, num);
        }
    }

    private void printf(List<File> files, int num) throws Exception {
        synchronized (queue) {
            queue.clear();
        }
        sendCallBack(4);
        int printNum = 0;
        if (printerMode == 0) {
            try {
                for (File file : files) {
                    ArrayList<Bitmap> bitmaps = ImageUtil.pdfToBitmap(file, angle);
                    for (Bitmap bitmap : bitmaps) {
                        for (int i = 0; i < num; i++) {
                            Log.e("斑马打印机", "bitmap.getWidth() = " + bitmap.getWidth() + "bitmap.getHeight() = " + bitmap.getHeight());
                            printImage(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
                            printNum++;
                        }
                    }
                }
            } catch (Exception e) {
                if (e.getMessage().equals("Error writing to connection: Broken pipe") && printNum == 0) {
                    connectAndPrint(files, num);
                }
                throw new Exception(e.getMessage());
            }
        } else {
            List<File> newList = new ArrayList<>();
            for (File file : files) {
                for (int i = 0; i < num; i++)
                    newList.add(file);
            }
            try {
                for (File file : newList) {
                    sendFileContents(file.getAbsolutePath());
                    printNum++;
                }
            } catch (Exception e) {
                if (e.getMessage().equals("Error writing to connection: Broken pipe") && printNum == 0) {
                    connectAndPrint(files, num);
                }
                throw new Exception(e.getMessage());

            }

        }
        sendCallBack(5);
        if (!printStop) {
            synchronized (queue) {
                queue.offer("start");
            }
        }
    }

    public void printImage(Bitmap bitmap, int x, int y, int width, int height) throws Exception {
        try {
            doSendBitmap(bitmap, x, y, width, height);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    void doSendBitmap(Bitmap bitmap, int x, int y, int width, int height) throws Exception {
        if (printerConnection != null) {
            synchronized (printerConnection) {
                synchronized (printing) {
                    printer.printImage(new ZebraImageAndroid(bitmap), x, y, width, height, false);
                }
            }
        }
    }

    public void sendFileContents(String filePath) throws ConnectionException {
        if (printerConnection != null) {
            synchronized (printerConnection) {
                synchronized (printing) {
                    printer.sendFileContents(filePath);
                }
            }
        }
    }

    @Override
    public void connect(Context context, UsbDevice device) {
        new Thread(() -> {
            try {
                connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void connect() throws Exception {
        initData();
        sendCallBack(0);
//        if (isConnect()) {
//            sendCallBack( 2);
//            return;
//        }
        disconnect();

        if (isbluetooth == 0) {
            printerConnection = new BluetoothConnection(blueToothAddress);
        } else {
            int port = Integer.parseInt(TextUtils.isEmpty(tcpPort) ? "9100" : tcpPort);
            printerConnection = new TcpConnection(tcpAddress, port);
        }

        try {
            printerConnection.open();
            Log.e("斑马打印机", "连接打印机");
        } catch (ConnectionException e) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                Log.e("斑马打印机", "蓝牙未启用");
                sendCallBack(6);
                throw new ConnectionException("斑马打印机:检测到蓝牙未启用或没有权限");
            } else {
                Log.e("斑马打印机", "连接失败，请重试1-1");
                sendCallBack(3);
                throw new Exception(context.getResources().getString(R.string.server_linkerr));
            }
        } catch (Exception e) {
            disconnect();
            sendCallBack(3);
            Log.e("斑马打印机", "连接失败，请重试1");
            throw new Exception(context.getResources().getString(R.string.server_linkerr));
        }

        if (isConnect()) {
            try {
                printer = ZebraPrinterFactory.getInstance(printerConnection);
                sendCallBack(2);
                Log.e("斑马打印机", "连接打印机成功  打印机语音=" + getL());
            } catch (ZebraPrinterLanguageUnknownException e) {
                setPrinterNull();
                disconnect();
                Log.e("斑马打印机", "未知的语言");
                sendCallBack(3);
                throw new Exception(context.getResources().getString(R.string.server_unknowlanguage));
            } catch (Exception e) {
                setPrinterNull();
                disconnect();
                Log.e("斑马打印机", "连接失败，请重试2");
                sendCallBack(3);
                throw new Exception(context.getResources().getString(R.string.server_linkerr));
            }
        }
    }

    @Override
    public boolean isConnect() {
        if (printerConnection != null && printerConnection.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public void disconnect() throws Exception {
        try {
            if (printerConnection != null) {
                synchronized (printerConnection) {
                    Log.e("斑马打印机", "取消断开连接");
                    printerConnection.close();
                }
            }
            setPrintConnectionNull();
        } catch (Exception e) {
            throw new Exception(context.getResources().getString(R.string.server_linkerr));
        }
    }

    /**
     * Description:获取友好名字
     */
    public String getFriendlyName() {
        return ((BluetoothConnection) printer.getConnection()).getFriendlyName();
    }

    /**
     * Description:默认端口
     */
    @Override
    public String getTcpPortNumber() {
        return "9100";
    }

    private void sendCallBack(int code) {
        //0连接1心跳包2连接成功3连接失败4正在打印5打印成功
        if (getCallBack() == null) return;
        String msg = "";
        switch (code) {
            case 0:
                msg = "连接打印机...";
                break;
            case 2:
                msg = "连接成功";
                break;
            case 3:
                msg = "连接失败，请重试";
                break;
            case 4:
                msg = "正在打印...";
                break;
            case 5:
                msg = "打印成功";
                break;
            case 6:
                msg = "蓝牙未启用";
                code = 3;
                break;
        }

        try {
            showToast(msg, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setPrintConnectionNull() {
        if (printerConnection != null) {
            synchronized (printerConnection) {
                printerConnection = null;
            }
        }
    }

    void setPrinterNull() {
        if (printer != null) {
            synchronized (printer) {
                printer = null;
            }
        }
    }

    public String getL() {
        PrinterLanguage printerLanguage = printer.getPrinterControlLanguage();
        if (printerLanguage == PrinterLanguage.ZPL) {
            return "ZPL";
        }
        if (printerLanguage == PrinterLanguage.CPCL) {
            return "CPCL";
        }
        if (printerLanguage == PrinterLanguage.LINE_PRINT) {
            return "LINE_PRINT";
        }
        return "未知语言";
    }
}
