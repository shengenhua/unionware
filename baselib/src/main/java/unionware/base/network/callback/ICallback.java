package unionware.base.network.callback;


import unionware.base.network.exception.ApiException;

/**
 * 顶层回调接口
 */
public interface ICallback<T> {

    void onSuccess(T data);

    void onFailure(ApiException e);
}
