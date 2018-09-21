package com.supersit.work.entity;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.supersit.common.db.CommonDatabase;
import com.supersit.work.db.WorkDatabase;

import java.io.Serializable;

/**
 * Created by 田皓午 on 2018/5/21.
 */
@Table(database = WorkDatabase.class)
public class NotificationEntity extends BaseModel implements Serializable{

    @PrimaryKey
    @SerializedName("notifyId")
    private int id;

    @Column
    @SerializedName("notifyCreateTime")
    private long time;

    @Column
    @SerializedName("notifyTitle")
    private String title;

    @Column
    @SerializedName("notifyContent")
    private String remark;

    private String detailsUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }
}
