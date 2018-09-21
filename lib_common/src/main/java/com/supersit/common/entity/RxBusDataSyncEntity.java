package com.supersit.common.entity;

import java.io.Serializable;

/** 数据同步监听
 * Created by 田皓午 on 2018/4/11.
 */

public class RxBusDataSyncEntity implements Serializable{
    private int id;
    private boolean isSuccess;

    public RxBusDataSyncEntity(int id, boolean isSuccess) {
        this.id = id;
        this.isSuccess = isSuccess;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
