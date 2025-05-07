package com.unionware.basicui.setting.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import unionware.base.R
import unionware.base.databinding.ActivityBluetoothBinding
import unionware.base.app.view.base.viewbinding.BaseBindActivity


class BluetoothActivity : BaseBindActivity<ActivityBluetoothBinding>() {
    override fun onBindLayout(): Int = R.layout.activity_bluetooth

    //    val bluetoothAdapter =
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothDeviceAdapter = BluetoothDeviceAdapter()

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        bluetoothManager?.apply {
            adapter.cancelDiscovery()
            adapter.bluetoothLeScanner.stopScan(scanCallback)
        }
    }

    @SuppressLint("MissingPermission")
    override fun initView() {
        // 获取BluetoothManager
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        // 获取BluetoothAdapter
        bluetoothManager?.adapter?.apply {
            /* if (isDiscovering) {
                 cancelDiscovery()
             }
             startDiscovery().apply {
                 Log.d("BluetoothActivity", "startDiscovery: $this")
             }*/
        }
        mBind.apply {
            btRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@BluetoothActivity).apply {
                    setReverseLayout(true)
                }
                adapter = bluetoothDeviceAdapter
            }
            btnScan.setOnClickListener {
                bluetoothManager?.adapter?.apply {
                    val settings =
                        ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .build()
                    bluetoothLeScanner.startScan(null, settings, scanCallback)
                }
            }
        }
    }

    private val scanCallback = object : ScanCallback() {

        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let {
                bluetoothDeviceAdapter.addItem(it)
                /*val name = it.name
                val address = it.address
                val rssi = result.rssi
                val nameOrAddress = if (name.isNullOrEmpty()) address else name
                Log.d("BluetoothActivity", "startDiscovery: $nameOrAddress")*/
            }
        }
    }

    override fun initData() {
    }
}