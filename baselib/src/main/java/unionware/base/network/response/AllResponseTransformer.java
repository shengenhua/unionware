package unionware.base.network.response;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import unionware.base.network.exception.ApiException;

/**
 * @Author : pangming
 * @Time : On 2024/8/1 16:33
 * @Description : AllResponseTransformer
 */

public class AllResponseTransformer<T> implements ObservableTransformer<IResponse<T>, T>, LifecycleObserver {

        final CompositeDisposable compositeDisposable = new CompositeDisposable();

        @Override
        public @NonNull
        ObservableSource<T> apply(@NonNull Observable<IResponse<T>> upstream) {
            return upstream.doOnSubscribe(new Consumer<>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            compositeDisposable.add(disposable);
                        }
                    }).onErrorResumeNext(throwable -> {
                        //出现异常统一处理（非业务性的异常）
                        return Observable.error(ApiException.handlerException(throwable));
                    }).flatMap((Function<IResponse<T>, ObservableSource<T>>) response -> {
                        if ("404".equals(response.getCode()) || "504".equals(response.getCode())) { // 登录用户信息过期404
                            ARouter.getInstance().build("/person/login").withBoolean("isAuto", true).navigation();
                        } else if (Integer.parseInt(response.getCode()) >= 90000 && Integer.parseInt(response.getCode()) <= 99999) {
                            ApiException e = new ApiException(response.getCode(), response.getMsg());
                            e.setData(response.getData().toString());
                            return Observable.error(e);
                        }else {
                            //返回所有信息
                            ApiException e = new ApiException(response.getCode(), response.getMsg());
                            BaseResponse baseResponse = new BaseResponse(Integer.valueOf(response.getCode()));
                            baseResponse.setMessage(response.getMsg());
                            baseResponse.setData(response.getData());
                            e.setData(new Gson().toJson(baseResponse));
                            return Observable.error(e);
                        }
                        return Observable.error(new ApiException(response.getCode(), response.getMsg()));
                    }).subscribeOn(Schedulers.io())// 指定时间产生的线程（请求的线程）
                    .observeOn(AndroidSchedulers.mainThread()); // 指定事件处理的线程（响应的线程）
        }


        public static <T> AllResponseTransformer<T> obtain(LifecycleOwner lifecycleOwner) {
            AllResponseTransformer<T> transformer = new AllResponseTransformer<>();
            lifecycleOwner.getLifecycle().addObserver(transformer);
            return transformer;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        void onDestroy() {
            if (!compositeDisposable.isDisposed()) {
                compositeDisposable.dispose();
            }
        }

    }
