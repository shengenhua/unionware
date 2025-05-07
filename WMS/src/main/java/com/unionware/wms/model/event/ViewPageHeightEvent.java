package com.unionware.wms.model.event;

import java.io.Serializable;

public class ViewPageHeightEvent implements Serializable {
    private int height;

    public ViewPageHeightEvent(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
