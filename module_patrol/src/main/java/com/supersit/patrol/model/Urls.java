package com.supersit.patrol.model;

import com.supersit.common.base.BaseUrls;

/**
 * Created by 田皓午 on 2018/3/31.
 */

public class Urls extends BaseUrls{


    /** 事件基础信息同步 **/
    public static final String URL_EVENTS_BASE = HOSTHTTP + "workpast/type/";

    /**
     * 上传事件信息
     */
    public static final String URL_UPLOAD_EVENT = HOSTHTTP + "workpast/";

    /** 历史上报记录 **/
    public static final String URL_PATROL_RECOR = HOSTHTTP + "workpast/";

    /**
     * 工程列表
     */
    public static final String URL_PROJECT_LIST = HOSTHTTP + "dept/project/";
}
