package com.unionware.emes.adapter.barcode

import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.unionware.emes.databinding.AdapterCalibrateBarCodeBinding
import unionware.base.model.bean.barcode.CalibrateBarCodeBean

class CalibrateBarCodeAdapter :
    BaseQuickAdapter<CalibrateBarCodeBean, CalibrateBarCodeAdapter.CalibrateBarCodeDataBinding>() {
    init {
//        setItemAnimation(AnimationType.SlideInRight)
    }

    open class CalibrateBarCodeDataBinding(binding: AdapterCalibrateBarCodeBinding) :
        DataBindingHolder<AdapterCalibrateBarCodeBinding>(binding) {

        var textWatcher: TextWatcher? = null
    }

    override fun onBindViewHolder(
        holder: CalibrateBarCodeDataBinding,
        position: Int,
        item: CalibrateBarCodeBean?,
    ) {
        holder.binding.let { bind ->
            bind.item = item
            bind.clFeature.item = item

            bind.clFeature.also { it ->
                it.etSumInput.apply {
                    holder.textWatcher?.also {
                        removeTextChangedListener(it)
                    }
                    it.ivCheckDelete.setOnClickListener {
                        this.setText("1")
                        item?.qty = "1"
                    }
                    /*if (item?.qty.equals("1")) {
                        this.isEnabled = false
                        it.ivCheckDelete.visibility = View.GONE
                    } else {
                        this.isEnabled = true
                        it.ivCheckDelete.visibility = View.VISIBLE
                    }*/
                    holder.textWatcher = addTextChangedListener {
                        item?.qty = it.toString()
                    }
                    setOnEditorActionListener { v, actionId, event ->
                        return@setOnEditorActionListener true
                    }
                }
            }

            if (item?.calibration4 == null) {
                bind.btnCalibration4.text = "4mA校验值"
//                 == null ?  :
            } else {
                bind.btnCalibration4.text = "4mA校验值:" + item?.calibration4Text
            }
            if (item?.calibration20 == null) {
                bind.btnCalibration20.text = "20mA校验值"
//                 == null ?  :
            } else {
                bind.btnCalibration20.text = "20mA校验值:" + item?.calibration20Text
            }
        }

    }

    override fun onCreateViewHolder(
        context: Context, parent: ViewGroup, viewType: Int,
    ): CalibrateBarCodeDataBinding {
        return CalibrateBarCodeDataBinding(
            AdapterCalibrateBarCodeBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }
}