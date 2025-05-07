package unionware.base.network;

import android.annotation.SuppressLint;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import unionware.base.network.callback.ICallAllDataBack;
import unionware.base.network.callback.ICallback;
import unionware.base.network.exception.ApiException;

import io.reactivex.Observable;
import unionware.base.network.response.AllResponseTransformer;
import unionware.base.network.response.IResponse;
import unionware.base.network.response.ResponseTransformer;


@SuppressLint("CheckResult")
public class NetHelper {
    public static <T> void request(Observable<? extends IResponse<T>> o, LifecycleOwner lifecycleOwner, ICallback<T> callback) {
        o.compose(ResponseTransformer.obtain(lifecycleOwner)).subscribe(t -> {
            if (isNotExist(lifecycleOwner)) {
                return;
            }
            callback.onSuccess(t);
        }, throwable -> {
            if (isNotExist(lifecycleOwner)) {
                return;
            }
            callback.onFailure(ApiException.handlerException(throwable));
        });
    }

    public static boolean isNotExist(LifecycleOwner lifecycleOwner) {
        return lifecycleOwner == null || Lifecycle.Event.downTo(lifecycleOwner.getLifecycle().getCurrentState()) == Lifecycle.Event.ON_DESTROY;
    }


    public static <T> void requestByAllData(Observable<? extends IResponse<T>> o, LifecycleOwner lifecycleOwner, ICallAllDataBack<T> callback) {
        o.compose(AllResponseTransformer.obtain(lifecycleOwner)).subscribe(t -> callback.onNoUse((T) t), throwable -> callback.onSuccessAllData(ApiException.handlerException(throwable)));
    }
}
