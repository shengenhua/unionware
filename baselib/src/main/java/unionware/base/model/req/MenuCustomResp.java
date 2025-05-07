package unionware.base.model.req;

import unionware.base.model.bean.MenuTypeBean;

import java.io.Serializable;
import java.util.List;

public class MenuCustomResp implements Serializable {
    private List<MenuTypeBean> menu;


    public List<MenuTypeBean> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuTypeBean> menu) {
        this.menu = menu;
    }
}
