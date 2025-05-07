package com.unionware.virtual.view

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.google.zxing.integration.android.IntentIntegrator
import unionware.base.model.bean.PropertyBean
import com.unionware.virtual.view.basics.BasicVirProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import unionware.base.model.bean.BaseInfoBean
import unionware.base.app.utils.sound.SoundUtils
import unionware.base.app.view.base.BaseMvvmToolbarActivity
import unionware.base.app.viewmodel.BaseViewModel

abstract class BaseVirtualActivity<V : ViewDataBinding, VM : BaseViewModel> :
    BaseMvvmToolbarActivity<V, VM>() {

    @JvmField
    @Autowired(name = "scene")
    var scene: String = ""

    @JvmField
    @Autowired(name = "primaryId")
    var primaryId: String = ""

    @JvmField
    @Autowired(name = "id")
    var id: String = ""

    @JvmField
    @Autowired(name = "appSetId")
    var appSetId: String = ""

    @JvmField
    @Autowired(name = "itemSearchId")
    var itemSearchId: String = ""

    @JvmField
    @Autowired(name = "listSearchId")
    var listSearchId: String = ""

    @JvmField
    @Autowired(name = "title")
    var title: String = ""


    override fun initBaseViewObservable() {
        super.initBaseViewObservable()
        mViewModel.mUIChangeLiveData.getTTSSucOrFailEvent()
            .observe(this) {
                SoundUtils.playVoice(this, it)
            }
        mViewModel.mUIChangeLiveData.getTTSEvent()
            .observe(this) {
                SoundUtils.playVoice(this, it)
            }
    }

    override fun initView() {
        setTitle(title)
    }


    protected fun queryBasic(
        position: Int,
        bean: PropertyBean? = null,
        parentId: String? = null,
        parentName: String? = null,
        queryUnit: ((Int?, BaseInfoBean?) -> Unit),
    ) {
        queryFlow.value = null
        val queryIntent = Intent(this, BasicVirProfileActivity::class.java)
        queryIntent.putExtra("scene", scene)
        queryIntent.putExtra("position", position)
        queryIntent.putExtra("key", bean?.tag)
        parentId?.apply {
            queryIntent.putExtra("parentId", this)
        }
        parentName?.apply {
            queryIntent.putExtra("parentName", this)
        }
        if ("FLEXVALUE" == bean?.type) {
            bean.apply {
                queryIntent.putExtra("parentName", "parentId")
                queryIntent.putExtra("parentId", this.related)
                queryIntent.putExtra("flexId", this.flexId)
            }
        }

        queryLauncher.launch(queryIntent)
        lifecycleScope.launch(Dispatchers.Main) {
            queryFlow.collectLatest { infoBean ->
                infoBean?.also {
                    queryUnit.invoke(position, it)
                    cancel()
                }
            }
        }
    }

    private var queryFlow = MutableStateFlow<BaseInfoBean?>(null)

    /**
     * 基础资料 数据回调回来
     */
    @Suppress("DEPRECATION")
    private val queryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data ?: return@registerForActivityResult
            runBlocking {
                val pos = result.resultCode // 更新位置
                val infoBean = if (Build.VERSION.SDK_INT >= TIRAMISU) data.getSerializableExtra(
                    "baseInfo", BaseInfoBean::class.java
                ) else data.getSerializableExtra("baseInfo") as BaseInfoBean?
                infoBean?.let {
                    queryFlow.value = infoBean
                }
            }
        }


    protected fun zxingBasic(
        position: Int,
        queryUnit: ((Int?, String?) -> Unit),
    ) {
        zxingFlow.value = null
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("请对准二维码进行扫描")
        integrator.setCameraId(0)
        integrator.setOrientationLocked(true)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(false)
        val zxingIntent = integrator.createScanIntent()
        zxingLauncher.launch(zxingIntent)
        lifecycleScope.launch(Dispatchers.Main) {
            zxingFlow.collectLatest { infoBean ->
                infoBean?.also {
                    queryUnit.invoke(position, it)
                    cancel()
                }
            }
        }
    }

    private var zxingFlow = MutableStateFlow<String?>(null)
    private val zxingLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data ?: return@registerForActivityResult
            val zxingResult = IntentIntegrator.parseActivityResult(result.resultCode, data)
            if (zxingResult.contents != null) {
                // 获取扫描结果
                val content = zxingResult.contents.trim { it <= ' ' }
                zxingFlow.value = content
            }
        }
}