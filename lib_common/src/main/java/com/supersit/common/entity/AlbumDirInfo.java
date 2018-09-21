package com.supersit.common.entity;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 一个目录的相册对象
 *
 * @author Administrator
 */
public class AlbumDirInfo implements Parcelable {

    private int count;
    private String dirName;
    private List<String> imageList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
        dest.writeString(this.dirName);
        dest.writeStringList(this.imageList);
    }

    public AlbumDirInfo() {
    }

    protected AlbumDirInfo(Parcel in) {
        this.count = in.readInt();
        this.dirName = in.readString();
        this.imageList = in.createStringArrayList();
    }

    public static final Creator<AlbumDirInfo> CREATOR = new Creator<AlbumDirInfo>() {
        @Override
        public AlbumDirInfo createFromParcel(Parcel source) {
            return new AlbumDirInfo(source);
        }

        @Override
        public AlbumDirInfo[] newArray(int size) {
            return new AlbumDirInfo[size];
        }
    };
}
