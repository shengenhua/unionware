package unionware.base.network.response;

/**
 * 通过接口的形式来转换兼容
 * @param <T>
 */
public interface IResponse<T> {
    T getData();

    String getMsg();

    String getCode();

    boolean isSuccess();
}
