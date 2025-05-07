package com.unionware.wms.model.event;

public class RefreshTaskIdEvent {
    String id;

    public RefreshTaskIdEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
