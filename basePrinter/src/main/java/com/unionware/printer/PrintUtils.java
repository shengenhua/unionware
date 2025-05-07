package com.unionware.printer;

import android.content.Context;

import com.tencent.mmkv.MMKV;
import com.unionware.printer.print.BasePrinter;
import com.unionware.printer.print.PrintKey;
import com.unionware.printer.print.PrinterInterface;
import com.unionware.printer.print.ZebraPrinter;

public class PrintUtils {
    public static PrinterInterface printerInterface;

    /**
     *  打印界面用
     * @param context
     * @param callBack
     * @return
     */
    public static PrinterInterface connectPrint(Context context, PrinterInterface.PrintCallBack callBack) {
        MMKV kv = MMKV.mmkvWithID("app");
        int type = kv.decodeInt(PrintKey.COMMON_PRINTER_TYPE, 0);
        String blueToothAddress = kv.decodeString(PrintKey.BLUETOOTH_ADDRESS,"");
        if(printerInterface == null) {
            if (type == 0 && !blueToothAddress.isEmpty()) {
                printerInterface = new ZebraPrinter(context);
            } else {
                printerInterface = new BasePrinter();
            }
        }
        printerInterface.setCallBack(callBack);
        if (type == 0 && !blueToothAddress.isEmpty()) {
            PrinterInterface finalPrinterInterface = printerInterface;
            try {
                finalPrinterInterface.startHeartBeat();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            try {
                printerInterface.connect(context, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return printerInterface;
    }

    /**
     *  打印设置界面，测试连接用
     * @param context
     * @param callBack
     * @return
     */
    public static PrinterInterface connectPrintTest(Context context,PrinterInterface.PrintCallBack callBack) {
        MMKV kv = MMKV.mmkvWithID("app");
        int type = kv.decodeInt(PrintKey.COMMON_PRINTER_TYPE, 0);
        if(printerInterface == null) {
            if (type == 0) {
                printerInterface = new ZebraPrinter(context);
            } else {
                printerInterface = new BasePrinter();
            }
        }
        printerInterface.setCallBack(callBack);
        try {
            printerInterface.connect(context, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return printerInterface;
    }
    public static boolean isConnectPrint(PrinterInterface printerInterface){
        if(printerInterface instanceof ZebraPrinter){
            return true;
        }
        return false;
    }
}
