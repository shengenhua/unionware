package unionware.base.network.exception;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 通用请求异常封装类
 */
public class ApiException extends Exception {

    public static final int UNKNOWN_ERROR = 10;
    public static final int PARSE_ERROR = 11;
    public static final int NETWORK_ERROR = 12;

    private final String code;
    private final String errorMsg;
    private final AtomicReference<String> data;

    public ApiException(String code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
        data = new AtomicReference<>();
    }


    public static ApiException handlerException(Throwable e) {
        if (e instanceof ApiException) {
            return (ApiException) e;
        }
        ApiException exception;
        if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            exception = new ApiException(String.valueOf(PARSE_ERROR), "数据解析异常");
            return exception;
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException
                || e instanceof SocketTimeoutException) {
            exception = new ApiException(String.valueOf(NETWORK_ERROR), "网络请求异常");
            return exception;
        } else {
            exception = new ApiException(String.valueOf(UNKNOWN_ERROR), "其他异常：" + e.getMessage());
            return exception;
        }

    }


    public String getErrorMsg() {
        return errorMsg;
    }

    public String getCode() {
        return code;
    }

    public String getData() {
        return data.get();
    }

    public void setData(String data) {
        this.data.set(data);
    }
}
