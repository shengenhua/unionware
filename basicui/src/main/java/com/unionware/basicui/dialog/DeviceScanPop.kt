package com.unionware.basicui.dialog

import android.content.Context
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import com.lxj.xpopup.impl.ConfirmPopupView
import com.unionware.basicui.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DeviceScanPop(
    context: Context, val title: String? = "设备申请", val callback: ScanCallBack? = null,
) :
    ConfirmPopupView(context, R.layout.pop_device_scan) {

    override fun onCreate() {
        super.onCreate()
        initView()
        initData()
    }

    private fun initView() {
        findViewById<TextView>(R.id.tvTitle).text = title ?: "设备申请"
        findViewById<AppCompatImageView>(R.id.aivQrScan).setOnClickListener {
            callback?.onQrScanClick()
        }
        findViewById<AppCompatEditText>(R.id.aetQr).setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                if (!v.text.isNullOrEmpty()) {
                    lifecycleScope.launch {
                        delay(500)
                        val text = findViewById<AppCompatEditText>(R.id.aetQr).text.toString()
                        if (text.isNotEmpty()) {
                            callback?.onEdit(text)
                            findViewById<AppCompatEditText>(R.id.aetQr).setText("")
                            findViewById<AppCompatEditText>(R.id.aetQr).requestFocus()
                        }
                    }
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun initData() = Unit

    interface ScanCallBack {
        fun onEdit(text: String)
        fun onQrScanClick()
    }
}