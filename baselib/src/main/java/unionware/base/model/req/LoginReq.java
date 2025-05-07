package unionware.base.model.req;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import unionware.base.model.bean.ClientInfoBean;

public class LoginReq implements Parcelable {
    private String acctID;
    private String username;
    private String password;
    @SerializedName("lcid")
    private String languageId;
    @SerializedName("PasswordIsEncrypted")
    private boolean isEncrypted;
    @SerializedName("AuthenticateType")
    private Integer authType;
    @SerializedName("KickoutFlag")
    private Integer kickType;
    @SerializedName("CustomizationParameter")
    private String customizationParameter;
    @SerializedName("ClientInfo")
    private ClientInfoBean clientInfo;

    public LoginReq() {

    }

    public LoginReq(boolean isEncrypted, Integer authType, Integer kickType, String customizationParameter, ClientInfoBean clientInfo) {
        this.isEncrypted = isEncrypted;
        this.authType = authType;
        this.kickType = kickType;
        this.customizationParameter = customizationParameter;
        this.clientInfo = clientInfo;
    }


    public LoginReq(Parcel in) {
        acctID = in.readString();
        username = in.readString();
        password = in.readString();
        languageId = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isEncrypted = in.readBoolean();
        }
        authType = in.readInt();
        kickType = in.readInt();
        customizationParameter =in.readString();
        clientInfo = (ClientInfoBean) in.readSerializable();
    }

    public static final Creator<LoginReq> CREATOR = new Creator<LoginReq>() {
        @Override
        public LoginReq createFromParcel(Parcel in) {
            return new LoginReq(in);
        }

        @Override
        public LoginReq[] newArray(int size) {
            return new LoginReq[size];
        }
    };


    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public Integer getAuthType() {
        return authType;
    }

    public void setAuthType(Integer authType) {
        this.authType = authType;
    }

    public Integer getKickType() {
        return kickType;
    }

    public void setKickType(Integer kickType) {
        this.kickType = kickType;
    }

    public String getCustomizationParameter() {
        return customizationParameter;
    }

    public void setCustomizationParameter(String customizationParameter) {
        this.customizationParameter = customizationParameter;
    }

    public ClientInfoBean getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfoBean clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getAcctID() {
        return acctID;
    }

    public void setAcctID(String acctID) {
        this.acctID = acctID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(acctID);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(languageId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(isEncrypted);
        }
        parcel.writeInt(authType);
        parcel.writeInt(kickType);
        parcel.writeString(customizationParameter);
        parcel.writeSerializable(clientInfo);
    }
}
