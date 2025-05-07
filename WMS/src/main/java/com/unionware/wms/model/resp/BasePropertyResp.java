package com.unionware.wms.model.resp;

import java.io.Serializable;

public class BasePropertyResp implements Serializable {
    private String Id;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
