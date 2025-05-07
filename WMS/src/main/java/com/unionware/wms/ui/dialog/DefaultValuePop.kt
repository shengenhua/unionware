package com.unionware.wms.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.impl.ConfirmPopupView
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.tencent.mmkv.MMKV
import com.unionware.wms.R
import com.unionware.wms.ui.adapter.DefaultValueAdapter
import unionware.base.app.utils.ToastUtil
import unionware.base.model.bean.PropertyBean
import unionware.base.room.DatabaseProvider
import unionware.base.room.ThreadTask
import unionware.base.room.table.DefaultKey
import unionware.base.room.table.DefaultValueInfo


@SuppressLint("ViewConstructor")
open class DefaultValuePop(var list: MutableList<PropertyBean>, var app: String, context: Context) :
    ConfirmPopupView(context, R.layout.dialog_default_value) {
    private var mConfirmListener: OnConfirmListener? = null
    private var defaultValueAdapter: DefaultValueAdapter? = null
    private var cbDefaultView: CheckBox? = null

    private var defaultKey: DefaultKey? = null
    private var isFirst: Boolean? = true

    override fun onCreate() {
        super.onCreate()
        setTitleContent("默认值界面", "", "")
        hostWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        initView()
        initData()
    }

    fun setConfirmListener(confirmListener: OnConfirmListener) {
        this.mConfirmListener = confirmListener
    }

    fun initView() {
        defaultValueAdapter = DefaultValueAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.rv_default_value)
        cbDefaultView = findViewById(R.id.cb_default)
        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            adapter = defaultValueAdapter
        }

        findViewById<TextView>(R.id.tv_confirm).setOnClickListener { //保存

            val defaultValueInfos: MutableList<DefaultValueInfo> = ArrayList()
            var isEmpty: Boolean = false
            defaultValueAdapter?.data?.forEach {
                if (defaultValueAdapter?.getEditValue(it.key) != null && defaultValueAdapter!!.getEditValue(
                        it.key
                    ).isNotEmpty()
                ) {
                    isEmpty = false
                }
                val defaultValueInfo = DefaultValueInfo()
                defaultValueInfo.id = defaultKey?.defaultKey!!
                defaultValueInfo.key = it.key
                defaultValueInfo.value = defaultValueAdapter?.getEditValue(it.key)
                defaultValueInfos.add(defaultValueInfo)
            }
            if (isEmpty) {
                ToastUtil.showToastCenter("请设置默认值");
                return@setOnClickListener
            }

            defaultKey?.isDefault = cbDefaultView!!.isChecked

            ThreadTask.start {
                DatabaseProvider.getInstance().getDefaultValueInfoDao()
                    .deleteList(defaultKey?.defaultKey.toString())
                DatabaseProvider.getInstance().getDefaultValueInfoDao()
                    .inserts(defaultValueInfos)
                defaultKey?.let { default ->
                    DatabaseProvider.getInstance().getDefaultKeyDao().insert(default)
                }
            }
//            defaultKey?.resetDefaultValueInfo()
            /*defaultValueManager?.saveOrUpdate(defaultValueInfos)

            defaultManager?.save(defaultKey)*/

            if (mConfirmListener != null) {
                mConfirmListener!!.onConfirm()
            }
            dismiss()
        }
    }


    fun initData() {
        val kv = MMKV.mmkvWithID("app")
        val userId: String? = kv.decodeString("userId", "")
        val dbId: String? = kv.decodeString("dbId", "")
        Log.e(
            "默认值1",
            "userId = " + userId + "dbId = " + dbId + "app = " + app
        ) //        var all = defaultValueManager?.queryBuilder()?.build()?.list()
        ThreadTask.getTwo {
            DatabaseProvider.getInstance().getDefaultKeyDao()
                .queryKey(userId.toString(), dbId.toString(), app)
        }.also {
            if (it.isNullOrEmpty()) {
                Log.e("默认值1", "没值")
                defaultKey = DefaultKey()
                defaultKey?.dbId = kv.decodeString("dbId", "")
                defaultKey?.userID = kv.decodeString("userId", "")
                defaultKey?.app = app
                defaultKey?.isDefault = false
                ThreadTask.start {
                    DatabaseProvider.getInstance().getDefaultKeyDao().insert(defaultKey!!)
                }
//                defaultManager?.save(defaultKey) //                defaultKey = defaultManager?.queryKey(userId.toString(), dbId.toString(), app)
            } else {
                defaultKey = it.first()
            }
        } //只有勾选了默认值保存至下次作业，第一次进来才赋值数据
        val defaultValueInfo = ThreadTask.getTwo {
            DatabaseProvider.getInstance().getDefaultValueInfoDao()
                .queryByKey(defaultKey?.defaultKey.toString())
        }
        if (defaultKey?.isDefault == true) {
            val map = defaultValueInfo.let {
                it?.associate { it.key to it.value }
            }
            /*val map = defaultKey?.defaultValueInfo?.associate {
                it.key to it.value
            } as HashMap<String, String>*/
            list.forEach {
                if (defaultValueInfo?.isNotEmpty()!!) {
                    it.value = map?.get(it.key) ?: ""
                }
            }
            defaultValueAdapter?.setList(list)
        } else {
            defaultValueAdapter?.setList(list)
            //没有勾选了默认值保存至下次作业，第一次进来初始化,前面刚进界面已清除
//            if (defaultKey?.defaultValueInfo?.isNotEmpty()!!) {
//                //defaultKey?.resetDefaultValueInfo()
//                defaultValueManager?.delete(defaultKey?.defaultValueInfo)
////                defaultKey?.resetDefaultValueInfo()
////                defaultManager?.saveOrUpdate(defaultKey)
//            }
        }
        isFirst = true;
        cbDefaultView?.isChecked = defaultKey?.isDefault!!
    }

    open fun setData() {
        if (isFirst == false) {
            val kv = MMKV.mmkvWithID("app")
            val userId: String? = kv.decodeString("userId", "")
            val dbId: String? = kv.decodeString("dbId", "")
            Log.e("默认值", "userId = " + userId + "dbId = " + dbId + "app = " + app)
            ThreadTask.getTwo {
                DatabaseProvider.getInstance().getDefaultKeyDao()
                    .queryKey(userId.toString(), dbId.toString(), app)
            }.also {
                if (it.isNullOrEmpty()) {
                    Log.e("默认值1", "没值")
                    defaultKey = DefaultKey()
                    defaultKey?.dbId = kv.decodeString("dbId", "")
                    defaultKey?.userID = kv.decodeString("userId", "")
                    defaultKey?.app = app
                    defaultKey?.isDefault = false
                    DatabaseProvider.getInstance().getDefaultKeyDao().insert(defaultKey!!)
//                defaultManager?.save(defaultKey) //                defaultKey = defaultManager?.queryKey(userId.toString(), dbId.toString(), app)
                } else {
                    defaultKey = it.first()
                }
            }
            val defaultValueInfo = ThreadTask.getTwo {
                DatabaseProvider.getInstance().getDefaultValueInfoDao()
                    .queryByKey(defaultKey?.defaultKey.toString())
            }
            val map = defaultValueInfo?.associate {
                it.key.toString() to it.value.toString()
            }
            list.forEach {
                if (defaultValueInfo?.isNotEmpty()!!) {
                    it.value = map?.get(it.key) ?: ""
                }
            }
            defaultValueAdapter?.setNewInstance(list)
            cbDefaultView?.isChecked = defaultKey?.isDefault!!
        } else {
            isFirst = false
        }

    }
}