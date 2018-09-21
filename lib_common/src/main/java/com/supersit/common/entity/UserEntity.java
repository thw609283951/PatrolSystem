package com.supersit.common.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.supersit.common.db.CommonDatabase;
import com.supersit.common.interfaces.IBaseInitialLetter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/31.
 */

@Table(database = CommonDatabase.class)
@ManyToMany(referencedTable = DeptEntity.class)
public class UserEntity extends BaseModel implements Parcelable,IBaseInitialLetter {

    @PrimaryKey
    @SerializedName("userId")
    private Long userId;

    @Column
    @SerializedName("userAccount")
    private String userName;

    @Column
    @SerializedName("userName")
    private String nickName;
    @Column
    private String initialLetter;

    @Column
    @SerializedName("userHeadPath")
    private String avatarUrl;

    @Column
    @SerializedName("userMobileTel")
    private String modePhone;

    @SerializedName("depts")
    private List<DeptEntity> depts;

    @Column
    @SerializedName("userPosition")
    private String positions;

    @Override
    public String getInitialLetter() {
        return initialLetter;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getModePhone() {
        return modePhone;
    }

    public void setModePhone(String modePhone) {
        this.modePhone = modePhone;
    }



    public List<DeptEntity> getDepts() {
        if (depts == null || depts.isEmpty()) {
            List<UserEntity_DeptEntity> userEntity_deptEntitiys = SQLite.select()
                    .from(UserEntity_DeptEntity.class)
                    .where(UserEntity_DeptEntity_Table.userEntity_userId.eq(userId))
                    .queryList();
            if(null != userEntity_deptEntitiys && !userEntity_deptEntitiys.isEmpty()){
                depts = new ArrayList<>();
                for (UserEntity_DeptEntity userEntity_deptEntitiy : userEntity_deptEntitiys) {
                    if(null != userEntity_deptEntitiy.getDeptEntity())
                        depts.add(userEntity_deptEntitiy.getDeptEntity());
                }
            }
        }
        return depts;
    }

    public void setDepts(List<DeptEntity> depts) {
        this.depts = depts;
    }

    public String getPositions() {
        return positions;
    }

    public void setPositions(String positions) {
        this.positions = positions;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.nickName);
        dest.writeString(this.initialLetter);
        dest.writeString(this.avatarUrl);
        dest.writeString(this.modePhone);
        dest.writeList(this.depts);
        dest.writeString(this.positions);
    }

    public UserEntity() {
    }

    protected UserEntity(Parcel in) {
        this.userId = (Long) in.readValue(Long.class.getClassLoader());
        this.userName = in.readString();
        this.nickName = in.readString();
        this.initialLetter = in.readString();
        this.avatarUrl = in.readString();
        this.modePhone = in.readString();
        this.depts = new ArrayList<DeptEntity>();
        in.readList(this.depts, DeptEntity.class.getClassLoader());
        this.positions = in.readString();
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel source) {
            return new UserEntity(source);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };
}
