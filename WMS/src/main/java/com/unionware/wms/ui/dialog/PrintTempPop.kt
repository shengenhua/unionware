package com.unionware.wms.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.lxj.xpopup.core.BottomPopupView
import com.tencent.mmkv.MMKV
import com.unionware.wms.R
import com.unionware.wms.databinding.PrintTempPopBinding
import com.unionware.wms.ui.adapter.NorMalScanAdapter
import unionware.base.model.bean.PropertyBean

/**
 * Author: sheng
 * Date:2025/3/26
 */
@SuppressLint("ViewConstructor")
class PrintTempPop(
    context: Context,
    mmkvId: String,
    private val confirm: OnBtnListener,
) : BottomPopupView(context) {
    lateinit var binding: PrintTempPopBinding
    /* override fun getInnerLayoutId(): Int {
         return R.layout.print_temp_pop
     }*/

    override fun addInnerContent() {
        binding = PrintTempPopBinding.inflate(
            LayoutInflater.from(context), bottomPopupContainer, false
        )
        bottomPopupContainer.addView(binding.root)

//        binding = DataBindingUtil.bind(findViewById(R.id.clRoot))
    }

    private var mmkv: MMKV = MMKV.mmkvWithID(mmkvId)
    private var adapter: NorMalScanAdapter = NorMalScanAdapter()

    interface OnBtnListener {
        fun onConfirm()
        fun onQuery(key: String)
    }

    override fun onCreate() {
        super.onCreate()

        binding?.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnConfirm.setOnClickListener {
                /*if (cbSave.isChecked) {
                } else {
                    mmkv.clearAll()
                }*/
                adapter.data.forEach {
                    mmkv.encode(getMMkvKeyName(it?.key ?: ""), it?.value)
                    mmkv.encode(it?.key ?: "", it?.id)
                }
                confirm.onConfirm()
            }
            cbSave.isChecked = mmkv.decodeBool("isSave", false)
            cbSave.setOnCheckedChangeListener { _, isChecked ->
                mmkv.encode("isSave", isChecked)
            }
            rvContent.layoutManager = LinearLayoutManager(context)
            rvContent.adapter = adapter
            adapter.isLockShow = false
            adapter.setNewInstance(mutableListOf<PropertyBean?>().apply {
                add(PropertyBean("printTemp", "打印模板").apply {
                    value = mmkv.decodeString(getMMkvKeyName(key), "")
                    id = mmkv.decodeString(key, "")
                    isEnable = true
                    type = "BASEDATA"
                })
            })
            adapter.addChildClickViewIds(R.id.iv_base_info_query, R.id.tv_scan_lock)
            adapter.setOnItemChildClickListener { _, view, position ->
                if (view.id == R.id.iv_base_info_query) {
                    confirm.onQuery(adapter.getKey(position))
                }
            }
        }
    }

    fun setData(key: String, value: String?, id: String?) {
        adapter.data.withIndex().firstOrNull { it.value?.key == key }?.apply {
            this.value?.value = value
            this.value?.id = id
            adapter.notifyItemChanged(index)
//            mmkv.encode(getMMkvKeyName(key), value)
//            mmkv.encode(key, id)
        }
    }

    private fun getMMkvKeyName(key: String): String {
        return "${key}Name"
    }
}