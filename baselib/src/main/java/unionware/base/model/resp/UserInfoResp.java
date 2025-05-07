package unionware.base.model.resp;

import com.google.gson.annotations.SerializedName;
import unionware.base.model.bean.BaseProperty;

import java.io.Serializable;

public class UserInfoResp implements Serializable {
    private String id;
    private String name;
    private String email;
    private String version;
    @SerializedName(value = "userId", alternate = "UserId")
    private String userId;
    @SerializedName("DBid")
    private String dbId; // 服务器标识
    private BaseProperty account;
    @SerializedName(value = "organization", alternate = "CurrentOrganizationInfo")
    private BaseProperty organization;
    private BaseProperty locale;
    private String orgId;

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public BaseProperty getAccount() {
        return account;
    }

    public void setAccount(BaseProperty account) {
        this.account = account;
    }

    public BaseProperty getOrganization() {
        return organization;
    }

    public void setOrganization(BaseProperty organization) {
        this.organization = organization;
    }

    public BaseProperty getLocale() {
        return locale;
    }

    public void setLocale(BaseProperty locale) {
        this.locale = locale;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
