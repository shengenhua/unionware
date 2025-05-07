package unionware.base.model.req;

import java.io.Serializable;

public class ItemBean implements Serializable {
    private String key;
    private String val;
    private Integer row;

    public ItemBean(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public ItemBean(String key, String val, Integer row) {
        this.key = key;
        this.val = val;
        this.row = row;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }
}
