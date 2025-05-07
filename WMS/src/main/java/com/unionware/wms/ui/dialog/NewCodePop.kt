package com.unionware.wms.ui.dialog

import android.content.Context
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.MutableLiveData
import com.lxj.xpopup.impl.ConfirmPopupView
import com.unionware.wms.R
import unionware.base.app.utils.ToastUtil
import unionware.base.model.bean.BarcodeBean
import unionware.base.model.resp.AnalysisResp

class NewCodePop(
    private var newCodeTitle: String,
    private var analysisLiveData: MutableLiveData<AnalysisResp>,
    private var codeData: MutableLiveData<String>,
    context: Context,
) :
    ConfirmPopupView(context, R.layout.dialog_new_code) {

    var onNewCodeListener: ((code: String) -> Unit)? = null
    var onAddNewCodeListener: (() -> String?)? = null
    var onConfirmListener: (() -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        initView()
        initData()
    }

    private fun initView() {
        val newCode = findViewById<AppCompatEditText>(R.id.et_new_code)
        val newCodeAdd = findViewById<ImageView>(R.id.iv_new_code_add)
        findViewById<TextView>(R.id.tv_new_code_title).text = newCodeTitle
        newCodeAdd.setOnClickListener {
            onAddNewCodeListener?.invoke()?.let {
                newCode.setText(it)
            }
        }
        newCode.setOnKeyListener { v, keyCode, event ->
            if (event != null &&
                event.keyCode == KeyEvent.KEYCODE_ENTER &&
                event.action == KeyEvent.ACTION_DOWN
            ) {
                onNewCodeListener?.invoke(newCode.text.toString())
                return@setOnKeyListener true
            }
            false
        }

        findViewById<TextView>(R.id.tv_confirm).setOnClickListener {
            analysisLiveData.getValue()?.let {
                it.getFBillHead().get(0).get("FNewCodeId")?.let {
                    if (it.number == null || it.number.isEmpty()) {
                        if (newCode.text!!.isEmpty()) {
                            ToastUtil.showToastCenter("转入箱不允许为空!")
                        } else {
                            ToastUtil.showToastCenter("请正确输入转入箱码!")
                        }
                    } else {
                        //保存
                        onConfirmListener?.invoke()
                        dismiss()
                    }
                }
            }

        }
        analysisLiveData.observe(this) { analysis ->
            // 数据更新渲染
            analysis.fBillHead[0]
                .filter { it.key == "FNewCodeId" }
                .forEach { (key: String?, value: BarcodeBean?) ->
                    if (key == "FNewCodeId") {
//                        findViewById<TextView>(R.id.tv_content)?.let {
//                            it.visibility = VISIBLE
//                            it.text = value.number
//                        }
                        newCode?.setText(value?.number ?: "")
                        newCode?.requestFocus()
                        newCode?.setSelection(0, newCode.text.toString().length)
                    }
                }

        }
        codeData.observe(this) {
            if (it != "200") {
                newCode?.requestFocus()
                newCode?.setSelection(0, newCode.text.toString().length)
            } else {
                //转入项目扫描成功，自动加入成功，自动确认
                onConfirmListener?.invoke()
                dismiss()
            }
        }
    }

    private fun initData() {
//        newCode
    }
}