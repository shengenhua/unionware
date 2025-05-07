package com.unionware.app.activity

import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.tencent.mmkv.MMKV
import com.unionware.path.RouterPath
import com.unionware.printer.BluetoothUtil
import com.unionware.printer.PrintUtils
import com.unionware.printer.R
import com.unionware.printer.databinding.PrinterSettingActivityBinding
import com.unionware.printer.print.PrintKey
import com.unionware.printer.print.PrinterInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import unionware.base.app.utils.ToastUtil
import unionware.base.app.view.base.viewbinding.BaseBindActivity

@AndroidEntryPoint
@Route(path = RouterPath.Print.PATH_PRINT_SET_MAIN)
class PrinterSettingActivity : BaseBindActivity<PrinterSettingActivityBinding>(),
    AdapterView.OnItemSelectedListener {
    lateinit var kv: MMKV;
    private var printerInterface: PrinterInterface? = null
    private val callBack: PrinterInterface.PrintCallBack =
        PrinterInterface.PrintCallBack { msg: String?, type: Int ->
            when (type) {
                0 -> {
                    setState(msg, Color.YELLOW)
                }

                2 -> {
                    setState(msg, Color.GREEN)
                }

                3 -> {
                    setState(msg, Color.RED)
                }
            }
        }

    fun setState(msg: String?, color: Int) {
        lifecycleScope.launch {
            mBind.tvPrintState.text = msg
            mBind.tvPrintState.setBackgroundColor(color)
        }
    }

    override fun onBindLayout(): Int {
        return R.layout.printer_setting_activity
    }

    override fun initView() {
        mBind.toolbar.setNavigationOnClickListener { view -> finish() }
        mBind.btnTest.setOnClickListener {
            try {
                if (isBluetoothType() && !BluetoothUtil.getBlueToothStatus(this)) {
                    BluetoothUtil.setBlueToothStatus(this, true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            save()
            connectPrint()
        }
        mBind.btnCommit.setOnClickListener {
            save()
        }
    }

    override fun initData() {
        kv = MMKV.mmkvWithID("app")

        initConnectionMethod();
        initPrinterTypes()
        initPrintMode()
        initPrintDirection()
        initEditView(kv.decodeInt(PrintKey.COMMON_PRINTER_TYPE, 0))
        mBind.serverIp.setText(kv.decodeString(PrintKey.BLUETOOTH_ADDRESS, ""))
    }

    private fun connectPrint() {
        try {
            if (printerInterface != null && printerInterface!!.isConnect()) {
                setState("连接成功", Color.GREEN)
            } else {
                printerInterface = PrintUtils.connectPrintTest(this, callBack)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        printerInterface?.apply {
//            this.print()
        }
    }

    private fun save() {
        val type: Int = kv.decodeInt(PrintKey.COMMON_PRINTER_TYPE, 0)
        when (type) {
            0 -> {//斑马打印机
                if (mBind.serverIp.text.isEmpty()) {
                    ToastUtil.showToast("请输入打印机IP及端口")
                    return
                }
                if (kv.decodeInt(PrintKey.IS_Bluetooth, 0) == 0) {
                    kv.putString(PrintKey.BLUETOOTH_ADDRESS, mBind.serverIp.text.toString())
                } else {
                    kv.putString(PrintKey.TCP_ADDRESS, getTcpAddress())
                    kv.putString(PrintKey.TCP_PORT, getTcpPortNumber())
                }
            }
        }
        ToastUtil.showToast("保存成功")
    }

    private fun test() {

    }

    private fun isBluetoothType(): Boolean {
        if (0 == kv.decodeInt(
                PrintKey.IS_Bluetooth,
                0
            ) && 0 == kv.decodeInt(PrintKey.COMMON_PRINTER_TYPE, 0)
        ) {
            return true
        }
        return false
    }

    private fun initConnectionMethod() {
        val types = resources.getStringArray(R.array.connection_method)
        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this@PrinterSettingActivity,
            android.R.layout.simple_spinner_dropdown_item,
            android.R.id.text1,
            types
        )
        mBind.spnConnectionMethod.setAdapter(adapter)
        mBind.spnConnectionMethod.setSelection(kv.decodeInt(PrintKey.IS_Bluetooth, 0))
        mBind.spnConnectionMethod.setOnItemSelectedListener(this)
    }

    private fun initPrinterTypes() {
        val types = resources.getStringArray(R.array.printer_type)
        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this@PrinterSettingActivity,
            android.R.layout.simple_spinner_dropdown_item,
            android.R.id.text1,
            types
        )
        mBind.spnPrinterType.setAdapter(adapter)
        mBind.spnPrinterType.setSelection(kv.decodeInt(PrintKey.COMMON_PRINTER_TYPE, 0))
        mBind.spnPrinterType.setOnItemSelectedListener(this)
    }

    private fun initPrintMode() {
        val types = resources.getStringArray(R.array.print_mode)
        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this@PrinterSettingActivity,
            android.R.layout.simple_spinner_dropdown_item,
            android.R.id.text1,
            types
        )
        mBind.spnPrintType.setAdapter(adapter)
        mBind.spnPrintType.setSelection(kv.decodeInt(PrintKey.COMMON_PRINTER_MODE, 0))
        mBind.spnPrintType.setOnItemSelectedListener(this)
    }

    private fun initPrintDirection() {
        val types = resources.getStringArray(R.array.paper_direction)
        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this@PrinterSettingActivity,
            android.R.layout.simple_spinner_dropdown_item,
            android.R.id.text1,
            types
        )
        mBind.spnDirectionType.setAdapter(adapter)
        mBind.spnDirectionType.setSelection(kv.decodeInt(PrintKey.PRINT_SELECTION_ANGLE, 0))
        mBind.spnDirectionType.setOnItemSelectedListener(this)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spn_connection_method -> {
                kv.putInt(PrintKey.IS_Bluetooth, position)
            }

            R.id.spn_printer_type -> {
                kv.putInt(PrintKey.COMMON_PRINTER_TYPE, position)
                initEditView(position)
            }

            R.id.spn_direction_type -> {
                kv.putInt(PrintKey.PRINT_SELECTION_ANGLE, position)
            }

            R.id.spn_print_type -> {
                kv.putInt(PrintKey.COMMON_PRINTER_MODE, position)
                if (position == 0) {
                    mBind.llPrintDirection.visibility = View.VISIBLE
                } else {
                    mBind.llPrintDirection.visibility = View.GONE
                }
            }
        }
    }

    fun initEditView(position: Int) {
        when (position) {
            0 -> {
                mBind.llConnectionMethod.visibility = View.GONE
                mBind.llPrintDirection.visibility = View.VISIBLE
                mBind.llPrintType.visibility = View.VISIBLE
            }

            1 -> {
                mBind.llConnectionMethod.visibility = View.GONE
                mBind.llPrintDirection.visibility = View.GONE
                mBind.llPrintType.visibility = View.GONE
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    fun getTcpAddress(): String? {
        val strings = mBind.serverIp.text.toString().split(":")
        return strings[0]
    }

    fun getTcpPortNumber(): String? {
        val strings = mBind.serverIp.text.toString().split(":")
        if (strings.size > 1) {
            return strings[1]
        } else {
            if (printerInterface == null) {
                return ""
            }
            return printerInterface!!.tcpPortNumber //默认端口
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        printerInterface?.let {
            it.stopHeartBeat()
        }
    }
}