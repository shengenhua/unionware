package com.unionware.basicui.setting.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import unionware.base.databinding.AdapterBluetoothDeviceBinding

/**
 * Author: sheng
 * Date:2025/4/10
 */
class BluetoothDeviceAdapter :
    BaseQuickAdapter<BluetoothDevice, DataBindingHolder<AdapterBluetoothDeviceBinding>>() {
    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(
        holder: DataBindingHolder<AdapterBluetoothDeviceBinding>,
        position: Int,
        item: BluetoothDevice?,
    ) {
        holder.binding.apply {
            btName.text = item?.name ?: "未知设备"
            btAddress.text = item?.address
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): DataBindingHolder<AdapterBluetoothDeviceBinding> {
        return DataBindingHolder(
            AdapterBluetoothDeviceBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    fun addItem(device: BluetoothDevice) {
        items.firstOrNull { it.address == device.address }.let {
            if (it == null) {
                add(device)
            }
        }
    }
}