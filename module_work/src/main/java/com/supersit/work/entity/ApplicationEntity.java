package com.supersit.work.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.supersit.work.db.WorkDatabase;

/**
 * Created by 田皓午 on 2018/4/21.
 */

@Table(database = WorkDatabase.class)
public class ApplicationEntity extends BaseModel implements Parcelable{

    @PrimaryKey(autoincrement = true)
    @SerializedName("applicationId")
    private int id;

    @Column
    @SerializedName("applicationFlag")
    private int tag;

    @Column
    @SerializedName("applicationName")
    private String name;

    @Column
    @SerializedName("applicationLogoPath")
    private String iconUrl;

    @Column
    @SerializedName("url")
    private String url;

    @Column
    private int unReadCount;

    public ApplicationEntity() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.tag);
        dest.writeString(this.name);
        dest.writeString(this.iconUrl);
        dest.writeString(this.url);
        dest.writeInt(this.unReadCount);
    }

    protected ApplicationEntity(Parcel in) {
        this.id = in.readInt();
        this.tag = in.readInt();
        this.name = in.readString();
        this.iconUrl = in.readString();
        this.url = in.readString();
        this.unReadCount = in.readInt();
    }

    public static final Creator<ApplicationEntity> CREATOR = new Creator<ApplicationEntity>() {
        @Override
        public ApplicationEntity createFromParcel(Parcel source) {
            return new ApplicationEntity(source);
        }

        @Override
        public ApplicationEntity[] newArray(int size) {
            return new ApplicationEntity[size];
        }
    };
}
