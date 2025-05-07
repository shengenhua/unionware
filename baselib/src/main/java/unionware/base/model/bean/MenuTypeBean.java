package unionware.base.model.bean;

import java.io.Serializable;
import java.util.List;

public class MenuTypeBean implements Serializable {
    private String color;
    private String link;
    private String icon;
    private String type;
    private String name;
    private List<MenuTypeBean> menu;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<MenuTypeBean> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuTypeBean> menu) {
        this.menu = menu;
    }
}
