package com.supersit.common.net.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/17.
 */

public class MyResponse<T> implements Serializable {

    public static final int CODE_REQUEST_FAIL = 0;
    public static final int CODE_REQUEST_SUCCESS = 1;


    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String msg;

    @SerializedName("data")
    private T resultData;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResultData() {
        return resultData;
    }

    public void setResultData(T resultData) {
        this.resultData = resultData;
    }
}
