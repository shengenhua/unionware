package unionware.base.network.response;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import unionware.base.network.exception.ApiException;
import unionware.base.route.URouter;

/**
 * 实现：
 * 1.对线程进行切换，达到代码复用的目标
 * 2.对RxJava生命周期管理，防止内存泄漏
 * 3.对响应数据统一处理，获取到真正相拥的data，进行业务处理
 */
public class ResponseTransformer<T> implements ObservableTransformer<IResponse<T>, T>, LifecycleEventObserver {

    final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    @Override
    public ObservableSource<T> apply(@NonNull Observable<IResponse<T>> upstream) {
        return upstream
                .doOnSubscribe(compositeDisposable::add)
                .doOnDispose(compositeDisposable::clear)
                .onErrorResumeNext(throwable -> {
                    return Observable.error(ApiException.handlerException(throwable));
                }).flatMap((Function<IResponse<T>, ObservableSource<T>>) response -> {
                    if (response.isSuccess()) {
                        if (response.getData() != null) {
                            return Observable.just(response.getData());
                        } else {
                            // 业务请求成功，但是返回的data为空
                            //通过反射手动创建data，这个data 一般没有实际用途
                            T obj;
                            if (response.isSuccess()) {
                                obj = (T) response.getMsg();
                            } else {
                                Class<?> clz = analysisClassInfo(response);
                                obj = (T) clz.newInstance();
                            }

                            return Observable.just(obj);
                        }

                    } else if ("404".equals(response.getCode()) || "504".equals(response.getCode())) { // 登录用户信息过期404
//                        ARouter.getInstance().build(RouterPath.Person.PATH_PERSON_LOGIN).withBoolean("isAuto", true).navigation();
                        URouter.build().action("app://BasicApp/basic_login");
                    } else if (Integer.parseInt(response.getCode()) >= 90000 && Integer.parseInt(response.getCode()) <= 99999) {
                        ApiException e = new ApiException(response.getCode(), response.getMsg());
                        e.setData(response.getData().toString());
                        return Observable.error(e);
                    }
                    ApiException apiException = new ApiException(response.getCode(), response.getMsg());
                    if (response.getData() != null) {
                        apiException.setData(new Gson().toJson(response.getData()));
                    }
                    return Observable.error(apiException);
                }).subscribeOn(Schedulers.io())// 指定时间产生的线程（请求的线程）
                .observeOn(AndroidSchedulers.mainThread()); // 指定事件处理的线程（响应的线程）
    }


    public static <T> ResponseTransformer<T> obtain(LifecycleOwner lifecycleOwner) {
        ResponseTransformer<T> transformer = new ResponseTransformer<>();
        lifecycleOwner.getLifecycle().addObserver(transformer);
        return transformer;
    }

    /**
     * 通过该功能得到输入参数的实际类型
     *
     * @param obj
     * @return
     */
    public static Class<?> analysisClassInfo(Object obj) {
        // 在java中T.getClass()或是T.class都是不合法的，因为T是泛型变量
        // 由于一个类的类型在编译器已确定，故不能在运行器得到T的实际类型、
        // getGenericSuperclass：获取当前运行类泛型父类类型，即为参数化类型，有所有类型公用的搞基接口TYPE接收
        // TYPE是 Java 编程语言中所有类型的公共高级接口，它们包含原始类型，参数化，数组，类型变量，基本数据类型
        Type getType = obj.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) getType).getActualTypeArguments();
        return (Class<?>) params[0];
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            source.getLifecycle().removeObserver(this);
            if (!compositeDisposable.isDisposed()) {
                compositeDisposable.dispose();
            }
            compositeDisposable.clear();
        }
    }
}
