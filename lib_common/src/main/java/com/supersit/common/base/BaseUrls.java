package com.supersit.common.base;

/**
 * Created by 田皓午 on 2018/3/31.
 */

public class BaseUrls {

    //巡查系统
//    public static final String HOSTHTTP = "http://gxt.supersit.com:8063/xct/app/";
    public static final String HOSTHTTP = "http://gxt.supersit.com:8061/xct/app/";
    public static final String HOSTHTTP2= "http://gxt.supersit.com:8062/";

    //天气预报网
    public static final String URL_WEATHER_FORECAST = "http://wx.weather.com.cn/mweather/101280101.shtml#1";

    /** 好友列表同步 **/
    public static final String URL_FRIEND_LIST = HOSTHTTP + "user/";
    /** 部门列表同步 **/
    public static final String URL_DEPT_LIST = HOSTHTTP + "dept/";
}
