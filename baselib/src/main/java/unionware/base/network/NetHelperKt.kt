package unionware.base.network

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import unionware.base.network.callback.ICallback
import unionware.base.network.exception.ApiException
import unionware.base.network.response.IResponse
import unionware.base.network.response.ResponseTransformer.analysisClassInfo
import unionware.base.route.URouter
import java.lang.reflect.ParameterizedType

/**
 * Author: sheng
 * Date:2024/11/12
 */
class NetHelperKt {
    companion object {

        @SuppressLint("CheckResult")
        @JvmStatic
        fun <T> request(
            observable: Observable<out IResponse<T>?>,
            lifecycleOwner: LifecycleOwner?,
            callback: ICallback<T>,
        ) {
            lifecycleOwner?.request(observable) {
                success {
                    callback.onSuccess(it)
                }
                failure { callback.onFailure(it) }
            }
        }

        fun <T> request(
            block: () -> IResponse<T>,
            lifecycleOwner: LifecycleOwner?,
            callback: ICallback<T>,
        ) {
            lifecycleOwner?.requestKt({ block() }) {
                onSuccess = {
                    callback.onSuccess(it)
                }
                onFailure = {
                    callback.onFailure(it)
                }
            }
        }
    }
}

fun <T> LifecycleOwner.requestBlock(
    block: () -> IResponse<T>?,
    requestHelper: RequestHelper<T>.() -> Unit,
) {
    requestKt({ block() }, requestHelper)
}

fun <T> LifecycleOwner.request(
    iResponse: Observable<out IResponse<T>>?,
    requestHelper: RequestHelper<T>.() -> Unit,
) {
    request({ iResponse }, requestHelper)
}

fun <T> LifecycleOwner.request(
    block: () -> Observable<out IResponse<T>>?,
    requestHelper: RequestHelper<T>.() -> Unit,
) {
//    requestKt({ block()?.subscribeOn(Schedulers.io())?.blockingSingle()!!/* blockingSingle 这行代码会阻塞，直到异步操作完成并返回结果*/ }, requestHelper)
    lifecycleScope.launch {
        block()?.subscribeOn(Schedulers.io())?.subscribe({
            requestKt({ it }, requestHelper)
        }, {
            lifecycleScope.launch(Dispatchers.Main) {
                RequestHelper<T>().also(requestHelper).onFailure(ApiException.handlerException(it))
            }
        })
    }
}

fun <T> Observable<out IResponse<T>?>.request(
    lifecycleOwner: LifecycleOwner?,
    requestHelper: RequestHelper<T>.() -> Unit,
) {
    lifecycleOwner?.request(this, requestHelper)
}

/**
 * 不建议使用，可能会内存溢出，用于后台一次性调用接口
 */
fun <T> Observable<out IResponse<T>>?.requestNotLifecycle(requestHelper: RequestHelper<T>.() -> Unit) {
    val listener = RequestHelper<T>().also(requestHelper)
    MainScope().launch {
        withContext(Dispatchers.IO) {
            flow {
                emit(this@requestNotLifecycle?.subscribeOn(Schedulers.io())?.blockingSingle()!!)
            }.also {
                withContext(Dispatchers.Main) {
                    it.netManage(listener)
                }
            }
        }
    }
}


fun <T> LifecycleOwner.requestKt(
    block: () -> IResponse<T>?,
    requestHelper: RequestHelper<T>.() -> Unit,
) {
    when {
        Lifecycle.Event.downTo(this.lifecycle.currentState) == Lifecycle.Event.ON_DESTROY -> {
            // 销毁了 不要调用接口 不然会有内存泄漏
            return
        }
    }
    val listener = RequestHelper<T>().also(requestHelper)
    lifecycleScope.launch {
        flow {
            emit(block()!!)
        }.netManage(listener)
    }
}

@Suppress("DEPRECATION", "UNCHECKED_CAST")
suspend fun <T> Flow<IResponse<T>>.netManage(listener: RequestHelper<T>) {
    this.onStart {
        //开始 开启加载进度条
        listener.onStart()
    }.onCompletion {
        //完成 关闭进度条
        listener.onCompletion()
    }.onEach {
        //每次 emit 都回走一次 但是只有一个 emit 所以当作监听全部数据
        listener.onEach(it)
    }.catch {
        //异常 400 等 方法发生错误
        listener.onFailure(ApiException.handlerException(it))
    }.collect {
        // 结果输出
        when {
            it.isSuccess -> {
                if (it.data == null) {
                    val data = try {
                        analysisClassInfo(it).newInstance() as T
                    } catch (e: Exception) {
                        it.msg
                    }
                    listener.onSuccess(
                        try {
                            analysisClassInfo(data).newInstance() as T?
                        } catch (e: Exception) {
                            null
                        }
                    )
                } else {
                    listener.onSuccess(it.data)
                }
            }

            listener.onFailureLogin(it.code, it.data) -> {//"404" == it.code || "504" == it.code
                //调用登陆界面，重新登陆
                listener.onFailure(ApiException(it.code, it.msg))
            }

            it.code.toInt() in 90000..99999 -> {
                listener.onFailure(ApiException(it.code, it.msg).apply {
                    data = it.data.toString()
                })
            }

            else -> {
                listener.onFailure(ApiException(it.code, it.msg).apply {
                    if (it.data != null) {
                        data = Gson().toJson(it.data)
                    }
                })
            }
        }
    }
}

/**
 * 通过该功能得到输入参数的实际类型
 *
 * @param obj
 * @return
 */
fun analysisClassInfo(obj: Any): Class<*> {
    // 在java中T.getClass()或是T.class都是不合法的，因为T是泛型变量
    // 由于一个类的类型在编译器已确定，故不能在运行器得到T的实际类型、
    // getGenericSuperclass：获取当前运行类泛型父类类型，即为参数化类型，有所有类型公用的搞基接口TYPE接收
    // TYPE是 Java 编程语言中所有类型的公共高级接口，它们包含原始类型，参数化，数组，类型变量，基本数据类型
    val getType = obj.javaClass.genericSuperclass
    val params = (getType as ParameterizedType).actualTypeArguments
    return params[0] as Class<*>
}


class RequestHelper<T> {
    /**
     * 每次 emit 都回走一次 但是只有一个 emit 所以当作监听全部数据
     */
    var onEach: (IResponse<T>) -> Unit = {}

    /**
     * 开始 开启加载进度条
     */
    var onStart: () -> Unit = {}

    /**
     * 完成 关闭进度条
     */
    var onCompletion: () -> Unit = {}

    /**
     * 异常 400 等 方法发生错误
     */
    var onFailure: (apiException: ApiException) -> Unit = {}

    /**
     * 成功 输出结果
     */
    var onSuccess: (data: T?) -> Unit = {}


    /**
     * 异常 404,405情况下，调用登陆界面，重新登陆
     */
    var onFailureLogin: (code: String, data: T?) -> Boolean = { code, data ->
        if ("404" == code || "504" == code) {
            //调用登陆界面，重新登陆
            URouter.build().action("app://BasicApp/basic_login")
            true
        } else {
            false
        }
    }

    fun success(unit: ((data: T?) -> Unit)) {
        onSuccess = unit
    }

    fun failure(unit: ((apiException: ApiException) -> Unit)) {
        onFailure = unit
    }
}