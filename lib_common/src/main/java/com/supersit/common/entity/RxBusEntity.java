package com.supersit.common.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by 田皓午 on 2018/4/11.
 */

public class RxBusEntity implements Serializable{


    private int id;
    private String message;

    public RxBusEntity(int id, String message){
        this.id=id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
