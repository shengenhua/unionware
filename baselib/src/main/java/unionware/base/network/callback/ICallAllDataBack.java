package unionware.base.network.callback;


import unionware.base.network.exception.ApiException;

/**
 * @Author : pangming
 * @Time : On 2024/8/1 18:31
 * @Description : ICallAllDataBack
 */

public interface ICallAllDataBack<T> {
    void onNoUse(T data);

    void onSuccessAllData(ApiException e);
}
