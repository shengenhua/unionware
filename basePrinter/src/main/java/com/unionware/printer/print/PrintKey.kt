package com.unionware.printer.print

class PrintKey {
    companion object {
        const val COMMON_PRINTER_TYPE = "printer_type" //打印机类型：type=0、默认：斑马打印机（ZR668/ZR638/ZR520/ZR328）、1 Dt630
        const val COMMON_PRINTER_MODE = "printer_mode" //打印方式  默认0 表示图片打印，1表示PDF打印
        const val PRINT_SELECTION_ANGLE = "Print_selection_angle"//打印角度  原来角度，90°，180°
        const val BLUETOOTH_ADDRESS = "BLUETOOTH_ADDRESS"// 蓝牙地址
        const val TCP_ADDRESS = "TCP_ADDRESS"// IP 地址
        const val TCP_PORT = "TCP_PORT"//端口
        const val IS_Bluetooth = "IS_Bluetooth"//是否是蓝牙  0 是蓝牙，1是无线
    }
}