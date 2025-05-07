package com.unionware.wms.model.req;

import java.io.Serializable;

public class PageIdReq implements Serializable {
    private String pageId;

    public PageIdReq(String pageId) {
        this.pageId = pageId;
    }
}
