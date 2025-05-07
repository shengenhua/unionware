package unionware.base.model.bean;

import java.util.List;

/**
 * @Author : pangming
 * @Time : On 2024/8/28 15:12
 * @Description : SerialNumberBean 源单序列号
 */

public class SerialNumberInfoBean {
    private List<Object> action;
    private List<SerialNumberDetailBean> data;

    public List<Object> getAction() {
        return action;
    }

    public void setAction(List<Object> action) {
        this.action = action;
    }

    public List<SerialNumberDetailBean> getData() {
        return data;
    }

    public void setData(List<SerialNumberDetailBean> data) {
        this.data = data;
    }

    public static class SerialNumberDetailBean{
        private String SerialNo;
        private int Status;//0-未扫，1-已扫，2-新增

        public String getSerialNo() {
            return SerialNo;
        }

        public void setSerialNo(String serialNo) {
            SerialNo = serialNo;
        }

        public int getStatus() {
            return Status;
        }

        public void setStatus(int status) {
            Status = status;
        }
    }
}
