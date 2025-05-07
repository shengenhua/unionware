package unionware.base.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


/**
 * @Author : pangming
 * @Time : On 2024/7/3 17:31
 * @Description : BaseDataBean 保存基础资料相关的值
 */



public class BaseDataBean implements Parcelable{
    private Integer id;
    private String code;
    private String name;
    private String tare;//克重
    private String tag;//标识

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTare() {
        return tare;
    }

    public void setTare(String tare) {
        this.tare = tare;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public BaseDataBean(){

    }
    public BaseDataBean(Parcel in) {
        id = in.readInt();
        code = in.readString();
        name = in.readString();
        tare = in.readString();
        tag = in.readString();
    }
    public static final Creator<BaseDataBean> CREATOR = new Creator<BaseDataBean>() {
        @Override
        public BaseDataBean createFromParcel(Parcel in) {
            return new BaseDataBean(in);
        }

        @Override
        public BaseDataBean[] newArray(int size) {
            return new BaseDataBean[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(code);
        parcel.writeString(name);
        parcel.writeString(tare);
        parcel.writeString(tag);
    }
}
