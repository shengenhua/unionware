package unionware.base.model.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class CommonListBean implements Serializable {
    /**
     * 显示的id
     */
    private String id;
    /**
     * 显示的名字
     */
    private String key;
    /**
     * 显示的值
     */
    private String val;

    public CommonListBean(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public CommonListBean(String id, String key, String val) {
        this.id = id;
        this.key = key;
        this.val = val;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        try {
            BigDecimal bigDecimal = new BigDecimal(val.replace(" ", ""));
            if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                return "0";
            }
            return bigDecimal.stripTrailingZeros().toPlainString();
        } catch (Exception e) {
            return val;
        }
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
