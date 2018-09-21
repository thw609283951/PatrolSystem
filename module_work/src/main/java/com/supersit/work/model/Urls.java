package com.supersit.work.model;

import com.supersit.common.base.BaseUrls;

/**
 * Created by 田皓午 on 2018/3/31.
 */

public class Urls extends BaseUrls{


    /** 同步我的应用列表 **/
    public static final String URL_APP_LIST = HOSTHTTP + "application/";

    /**  获取工作事件列表 **/
    public static final String URL_WORK_EVENTS = HOSTHTTP + "workpast/status/";

    /** 获取巡查动态 **/
    public static final String URL_PATROL_DYNAMIC = HOSTHTTP + "workpast/permissions/";

    /** 发送事件处理信息 **/
    public static final String URL_EVENT_HANDLE = HOSTHTTP + "workpast/flow/";

    /**  获取通知公告列表 **/
    public static final String URL_WORK_NOTIFYS = HOSTHTTP + "notify/";

    /**天气详情**/
    public static final String URL_WEATHER= "http://wthrcdn.etouch.cn/weather_mini";
}
