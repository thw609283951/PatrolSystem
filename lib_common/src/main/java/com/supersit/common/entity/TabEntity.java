package com.supersit.common.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.flyco.tablayout.listener.CustomTabEntity;

/**
 * Created by Administrator on 2018/3/28.
 */

public class TabEntity implements CustomTabEntity,Parcelable {
    public String title;
    public int selectedIcon;
    public int unSelectedIcon;

    public TabEntity(String title, int selectedIcon, int unSelectedIcon) {
        this.title = title;
        this.selectedIcon = selectedIcon;
        this.unSelectedIcon = unSelectedIcon;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return unSelectedIcon;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeInt(this.selectedIcon);
        dest.writeInt(this.unSelectedIcon);
    }

    protected TabEntity(Parcel in) {
        this.title = in.readString();
        this.selectedIcon = in.readInt();
        this.unSelectedIcon = in.readInt();
    }

    public static final Creator<TabEntity> CREATOR = new Creator<TabEntity>() {
        @Override
        public TabEntity createFromParcel(Parcel source) {
            return new TabEntity(source);
        }

        @Override
        public TabEntity[] newArray(int size) {
            return new TabEntity[size];
        }
    };
}