package unionware.base.model.bean;

import java.io.Serializable;

public class BarcodeBean implements Serializable {
    private String Key;
    private String Value;
    private Object Id;//可能有些接口用string,有些用Id
    private String Number;
    private String Name;
    private String Display;
    private boolean Enabled;
    private boolean Visible;

    public boolean isVisible() {
        return Visible;
    }

    public void setVisible(boolean visible) {
        Visible = visible;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public Object getId() {
        if(Id != null && (Id instanceof Double || Id instanceof Integer)){
            if(Id.toString().contains(".0")){
                Id = Id.toString().replace(".0","");
            }
        }
        return Id;
    }

    public void setId(Object id) {
        Id = id;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isEnabled() {
        return Enabled;
    }

    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public String getDisplay() {
        return Display;
    }

    public void setDisplay(String display) {
        Display = display;
    }
}
