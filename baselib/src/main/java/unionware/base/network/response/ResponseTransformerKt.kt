package unionware.base.network.response

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import unionware.base.network.exception.ApiException

/**
 * Author: sheng
 * Date:2025/3/31
 */
class ResponseTransformerKt<T>(private val check404: Boolean = true) :
    ObservableTransformer<IResponse<T>, T>,
    LifecycleEventObserver {

    companion object {
        @JvmStatic
        fun <T> obtain(lifecycleOwner: LifecycleOwner): ResponseTransformerKt<T> {
            val transformer: ResponseTransformerKt<T> = ResponseTransformerKt()
            lifecycleOwner.lifecycle.addObserver(transformer)
            return transformer
        }

        @JvmStatic
        fun <T> obtain(
            lifecycleOwner: LifecycleOwner,
            check404: Boolean,
        ): ResponseTransformerKt<T> {
            val transformer: ResponseTransformerKt<T> = ResponseTransformerKt(check404)
            lifecycleOwner.lifecycle.addObserver(transformer)
            return transformer
        }
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    @Suppress("UNCHECKED_CAST")
    override fun apply(upstream: Observable<IResponse<T>>): ObservableSource<T> {
        return upstream.doOnSubscribe { compositeDisposable.add(it) }
            .doOnDispose { compositeDisposable.clear() }
            .onErrorResumeNext(Function<Throwable, ObservableSource<out IResponse<T>?>> {
                Observable.error(ApiException.handlerException(it))
            }).flatMap(
                Function<IResponse<T>, ObservableSource<T>> { response: IResponse<T> ->
                    if (response.isSuccess) {
                        if (response.data != null) {
                            return@Function Observable.just<T?>(response.data)
                        } else {
                            // 业务请求成功，但是返回的data为空
                            //通过反射手动创建data，这个data 一般没有实际用途
                            /*Gson().let { gson ->
                                val clz = ResponseTransformer.analysisClassInfo(response)
                                if (response.isSuccess) {
                                    gson.toJson(response.msg)?.let {
                                        gson.fromJson(it, clz)
                                    }
                                } else {
                                    gson.toJson(response)?.let {
                                        gson.fromJson(it, clz)
                                    }
                                }
                            }.also {
                                Observable.just(it as T)
                            }
                            return@Function Observable.just<T?>(obj)*/
                            // 业务请求成功，但是返回的data为空
                            //通过反射手动创建data，这个data 一般没有实际用途
                            val obj: T
                            if (response.isSuccess) {
                                obj = response.msg as T
                            } else {
                                val clz = ResponseTransformer.analysisClassInfo(response)
                                @Suppress("DEPRECATION")
                                obj = clz.newInstance() as T
                            }
                            return@Function Observable.just(obj)
                        }
                    } else if (check404 && ("404" == response.code || "504" == response.code)) { // 登录用户信息过期404
//                        ARouter.getInstance().build(RouterPath.Person.PATH_PERSON_LOGIN).withBoolean("isAuto", true).navigation();
                    } else if (response.code.toInt() in 90000..99999) {
                        val e = ApiException(response.code, response.msg)
                        e.data = response.data.toString()
                        return@Function Observable.error<T?>(e)
                    }
                    val apiException = ApiException(response.code, response.msg)
                    if (response.data != null) {
                        apiException.data = Gson().toJson(response.data)
                    }
                    Observable.error(apiException)
                }).subscribeOn(Schedulers.io()) // 指定时间产生的线程（请求的线程）
            .observeOn(AndroidSchedulers.mainThread()) // 指定事件处理的线程（响应的线程）
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            source.lifecycle.removeObserver(this)
            if (!compositeDisposable.isDisposed) {
                compositeDisposable.dispose()
            }
            compositeDisposable.clear()
        }
    }
}