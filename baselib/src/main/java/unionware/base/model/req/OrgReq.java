package unionware.base.model.req;

import java.io.Serializable;

public class OrgReq implements Serializable {
    private String orgId;

    public OrgReq(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
