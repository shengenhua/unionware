package unionware.base.app.ex;

/**
 * Author: sheng
 * Date:2024/8/30
 */
public class DeviceException extends Exception{
    private String error = null;
    private String msg = null;

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getError() {
        return error;
    }

    public DeviceException(String error) {
        this.error = error;
    }
}
