package com.unionware.wms.ui.activity

import android.content.Intent
import android.graphics.Color
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.lxj.xpopup.XPopup
import com.unionware.wms.R
import com.unionware.wms.databinding.ActivityBillListBinding
import com.unionware.wms.inter.bill.BillListContract
import com.unionware.wms.inter.bill.BillListPresenter
import unionware.base.model.req.ViewReq
import com.unionware.wms.ui.adapter.BillAdapter
import unionware.base.model.bean.BillBean
import unionware.base.model.req.FiltersReq
import com.yanzhenjie.recyclerview.*
import dagger.hilt.android.AndroidEntryPoint
import unionware.base.app.view.base.viewbinding.BaseBindActivity
import unionware.base.ext.showToast
import javax.inject.Inject


@AndroidEntryPoint
class DetailListActivity : BaseBindActivity<ActivityBillListBinding>(), BillListContract.View,
    SwipeMenuCreator, OnItemMenuClickListener {

    @Inject
    @JvmField
    var presenter: BillListPresenter? = null

    @JvmField
    @Autowired(name = "scene")
    var scene: String? = null

    @JvmField
    @Autowired(name = "name")
    var name: String? = null

    @JvmField
    @Autowired(name = "primaryId")
    var primaryId: String? = ""

    @JvmField
    @Autowired(name = "schemaId")
    var schemaId: String? = ""

    @JvmField
    @Autowired(name = "pageId")
    var pageId: String? = null

    @JvmField
    @Autowired(name = "title")
    var title: String? = null

    private var filtersReq: FiltersReq? = null

    private var billAdapter: BillAdapter? = null

    override fun onBindLayout(): Int {
        return R.layout.activity_bill_list
    }

    override fun initView() {
        presenter?.attach(this)

        mBind.icWmsToolbar.tbTitle.text = title
        mBind.icWmsToolbar.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        } //拆分
        //全部删除
        mBind.icWmsToolbar.tbSubmit.visibility = View.VISIBLE
        mBind.icWmsToolbar.tbSubmit.text = "全部删除"
        mBind.icWmsToolbar.tbSubmit.setOnClickListener {
            XPopup.Builder(this).asConfirm("删除", "确认删除全部条码？") {
                deleteAll()
            }.show()
        }

        billAdapter = BillAdapter(this)
        layoutInflater.inflate(unionware.base.R.layout.view_empty, null).also {
            it.findViewById<ImageView>(unionware.base.R.id.iv_empty_icon)
                .setImageResource(unionware.base.R.mipmap.ic_empty_bill)
            it.findViewById<TextView>(unionware.base.R.id.tv_empty_tips).text = "空白数据"
            billAdapter?.setEmptyView(it)
            billAdapter?.isUseEmpty = false
        }

        mBind.srvBill.also {
            it.setSwipeMenuCreator(this)
            it.setOnItemMenuClickListener(this)
            it.adapter = billAdapter
            it.layoutManager = LinearLayoutManager(this)
        }

        mBind.icScanView.etInProgressSearch.also {
            it.setOnKeyListener { v, keyCode, event ->
                if (event != null &&
                    event.keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_DOWN
                ) {
                    delete("ByCode", it.text.toString())
                    return@setOnKeyListener true
                }
                false

            }
        }

        onBackPressed(true) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun initData() {
        val params: MutableMap<String, Any> = HashMap()
        primaryId?.also { params["primaryId"] = it }
//        schemaId?.also { params["schemaId"] = it }
        filtersReq = FiltersReq(params)
        presenter?.requestList(scene, name, filtersReq)
    }

    override fun showFailedView(msg: String) {
        msg?.showToast()

        mBind.icScanView.etInProgressSearch.run {
            isFocusable = true
            isFocusableInTouchMode = true
            setSelection(0, text?.length ?: 0)
            requestFocus()
        }
    }

    override fun showList(list: List<BillBean>) {
        /*if (1 == filtersReq?.pageIndex) {
            billAdapter?.setNewInstance(list as MutableList<BillBean>)
        } else {
            billAdapter?.addData(list)
        }*/
        mBind.srvBill.setSwipeItemMenuEnabled(true)
        billAdapter?.setNewInstance(list as MutableList<BillBean>)
    }

    override fun showEmptyView() {
        billAdapter?.data?.clear()
        billAdapter?.notifyDataSetChanged()
        mBind.srvBill.setSwipeItemMenuEnabled(false)
        billAdapter?.isUseEmpty = true
    }

    override fun deleteItem(pos: Int) {
        if (pos == -1) {
            initData()
            return
        }
        billAdapter?.removeAt(pos)
        if (billAdapter?.data!!.isEmpty()) {
            mBind.srvBill.setSwipeItemMenuEnabled(false)
        }
        billAdapter?.isUseEmpty = billAdapter?.data!!.isEmpty()

        mBind.icScanView.etInProgressSearch.run {
            isFocusable = true
            isFocusableInTouchMode = true
            setSelection(0, text?.length ?: 0)
            requestFocus()
        }
    }

    override fun onCreateMenu(leftMenu: SwipeMenu?, rightMenu: SwipeMenu?, position: Int) {
        val deleteItem = SwipeMenuItem(mContext)
            .setText("删除")
            .setTextColor(Color.WHITE)
            .setBackgroundColor(
                resources.getColor(
                    unionware.base.R.color.red,
                    resources.newTheme()
                )
            )
            .setWidth(resources.getDimensionPixelSize(unionware.base.R.dimen.dp_80))
            .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
        rightMenu?.addMenuItem(deleteItem)
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?, adapterPosition: Int) {
        menuBridge?.closeMenu()
        XPopup.Builder(this).asConfirm("删除", "确认删除条码？") {
            delete("ByRowId", billAdapter!!.data[adapterPosition].code, adapterPosition)
        }.show()
    }

    private fun delete(type: String, target: String, pos: Int = -1) {
        val req = ViewReq("INVOKE_DELETEBARCODE", pageId)
        val map: MutableMap<String, Any> = HashMap()
        //ByCode (根据条码删除)   ByRowId（根据行id删除 )
        map["type"] = type
        map["target"] = target

        req.params = map

        presenter?.delete(req, if (pos == -1) billAdapter!!.getPosByCode(target) else pos)
    }

    private fun deleteAll() {
        val req = ViewReq("INVOKE_DELETEBARCODE", pageId)
        val map: MutableMap<String, Any> = HashMap()
        //ByCode (根据条码删除)   ByRowId（根据行id删除 )
        map["type"] = "All"
        req.params = map

        presenter?.delete(req, -1)
    }

    private fun onBackPressed(isEnabled: Boolean, callback: () -> Unit) {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(isEnabled) {
            override fun handleOnBackPressed() {
                callback()
            }
        })
    }
}