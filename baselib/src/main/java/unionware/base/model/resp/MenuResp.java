package unionware.base.model.resp;


import unionware.base.model.bean.MenuBean;

import java.io.Serializable;
import java.util.List;

public class MenuResp implements Serializable {
    private List<MenuBean> data;

    public List<MenuBean> getData() {
        return data;
    }

    public void setData(List<MenuBean> data) {
        this.data = data;
    }
}
