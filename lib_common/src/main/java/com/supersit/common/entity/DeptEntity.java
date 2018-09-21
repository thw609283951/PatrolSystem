package com.supersit.common.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.supersit.common.db.CommonDatabase;
import com.supersit.common.interfaces.IFlowTagName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 田皓午 on 2018/4/3.
 */
@Table(database = CommonDatabase.class)
public class DeptEntity extends BaseModel implements Parcelable,IFlowTagName {

    @PrimaryKey
    @SerializedName("deptId")
    private long id;

    @Column
    @SerializedName("deptParentId")
    private long parentId;

    @Column
    @SerializedName("deptName")
    private String name;

    @Column
    @SerializedName("deptRemark")
    private String remark;

    @ForeignKey
    @SerializedName("deptAddress")
    private DeptLocationEntity deptLocationEntity;

    private List<UserEntity> userEntitys;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeptLocationEntity getDeptLocationEntity() {
        if(null == deptLocationEntity)
            deptLocationEntity = SQLite.select()
                .from(DeptLocationEntity.class)
                .where(DeptLocationEntity_Table.id.eq(id))
                .querySingle();
        return deptLocationEntity;
    }

    public void setDeptLocationEntity(DeptLocationEntity deptLocationEntity) {
        this.deptLocationEntity = deptLocationEntity;
    }

    public List<UserEntity> getUserEntitys() {
        if (userEntitys == null) {
            List<UserEntity_DeptEntity> userEntity_deptEntitys = SQLite.select()
                    .from(UserEntity_DeptEntity.class)
                    .where(UserEntity_DeptEntity_Table.deptEntity_id.eq(id))
                    .queryList();
            if(null != userEntity_deptEntitys && !userEntity_deptEntitys.isEmpty()){
                userEntitys = new ArrayList<>();
                for (UserEntity_DeptEntity userEntity_deptEntitiy : userEntity_deptEntitys) {
                    userEntitys.add(userEntity_deptEntitiy.getUserEntity());
                }
            }
        }
        return userEntitys;
    }

    public void setUserEntitys(List<UserEntity> userEntitys) {
        this.userEntitys = userEntitys;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }



    @Override
    public String getFlowTagName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.parentId);
        dest.writeString(this.name);
        dest.writeString(this.remark);
        dest.writeParcelable(this.deptLocationEntity, flags);
        dest.writeTypedList(this.userEntitys);
    }

    public DeptEntity() {
    }

    protected DeptEntity(Parcel in) {
        this.id = in.readLong();
        this.parentId = in.readLong();
        this.name = in.readString();
        this.remark = in.readString();
        this.deptLocationEntity = in.readParcelable(DeptLocationEntity.class.getClassLoader());
        this.userEntitys = in.createTypedArrayList(UserEntity.CREATOR);
    }

    public static final Creator<DeptEntity> CREATOR = new Creator<DeptEntity>() {
        @Override
        public DeptEntity createFromParcel(Parcel source) {
            return new DeptEntity(source);
        }

        @Override
        public DeptEntity[] newArray(int size) {
            return new DeptEntity[size];
        }
    };
}
