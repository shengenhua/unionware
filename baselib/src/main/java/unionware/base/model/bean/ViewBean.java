package unionware.base.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ViewBean implements Serializable {
    public ViewBean(String key, String name, boolean visible) {
        this.key = key;
        this.name = name;
        this.visible = visible;
    }
    private String key;

    private String name;

    @SerializedName("isVisible")
    private boolean visible;

    @SerializedName("isEditor")
    private boolean editor;

    @SerializedName("isFilter")
    private boolean filter;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isEditor() {
        return editor;
    }

    public void setEditor(boolean editor) {
        this.editor = editor;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }
}
