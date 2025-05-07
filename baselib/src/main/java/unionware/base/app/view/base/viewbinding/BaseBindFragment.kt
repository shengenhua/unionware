package unionware.base.app.view.base.viewbinding

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType

abstract class BaseBindFragment<T : ViewBinding>() : Fragment() {
    protected var mBind: T? = null
    protected lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val type = javaClass.genericSuperclass as ParameterizedType
        val cls = type.actualTypeArguments[0] as Class<*>
        try {
            val inflate = cls.getDeclaredMethod(
                "inflate", LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.javaPrimitiveType
            )
            @Suppress("UNCHECKED_CAST")
            mBind = inflate.invoke(null, inflater, container, false) as T
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mBind!!.root
    }

    /**
     * 是否打开EventBus
     */
    open fun enableEventBus(): Boolean = false

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        mBind = null

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (enableEventBus()) EventBus.getDefault().register(this)
        initObserve()
        initData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    abstract fun initObserve()
    abstract fun initData()

    override fun onResume() {
        super.onResume()

    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.setUserVisibleHint(isVisibleToUser)",
        "androidx.fragment.app.Fragment"
    )
    )
    @Suppress("DEPRECATION")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }
}

