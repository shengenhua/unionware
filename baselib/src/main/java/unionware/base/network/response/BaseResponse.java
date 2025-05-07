package unionware.base.network.response;

import com.google.gson.annotations.SerializedName;

public class BaseResponse<T> implements IResponse<T> {
    @SerializedName(value = "code", alternate = "LoginResultType")
    int code;
    @SerializedName(value = "message", alternate = "Message")
    String message;
    @SerializedName(value = "result", alternate = {"Successful"})
    boolean result;
    @SerializedName(value = "data", alternate = {"Result", "Context"})
    T data;


    public BaseResponse(int code) {
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public String getMsg() {
        return message;
    }

    @Override
    public String getCode() {
        return String.valueOf(code);
    }

    @Override
    public boolean isSuccess() {
        return (code == 200 || isResult() || code == 1 || code == 0) && (data != null || message.contains("请求成功")); // 判断网络回调
    }
}
