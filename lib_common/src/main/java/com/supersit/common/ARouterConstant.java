
package com.supersit.common;

/**
 * 路由路径管理
 */
public class ARouterConstant {


    /*** 主模块 start ***/
    public static final String MainUIGroup = "main_ui";
    public static final String MainActivity = "/"+MainUIGroup+"/MainActivity";
    /*** 主模块 end ***/


    /*** 通讯模块 start***/
    public static final String IMUIGroup = "im_ui";
    public static final String IMFragment = "/"+IMUIGroup+"/IMFragment";
    public static final String ContactListActivity = "/"+IMUIGroup+"/ContactListActivity";
    public static final String MsgSetActivity = "/"+IMUIGroup+"/MsgSetActivity";
    public static final String UserProfileActivity = "/"+IMUIGroup+"/UserProfileActivity";

    public static final String IMServiceGroup = "im_services";
    public static final String IIMService = "/"+IMServiceGroup+"/IIMService";
    /*** 通讯模块 end***/

    /*** 巡查模块 start ***/
    public static final String PatrolUIGroup = "patrol";
    public static final String PatrolFragment = "/"+PatrolUIGroup+"/PatrolFragment";
    public static final String PersonnelFragment = "/"+PatrolUIGroup+"/PersonnelFragment";
    public static final String PatrolLocationActivity = "/"+PatrolUIGroup+"/PatrolLocationActivity";
    public static final String ProjectListActivity = "/"+PatrolUIGroup+"/ProjectListActivity";
    /*** 巡查 end ***/

    /*** 日常工作模块 start ***/
    public static final String WorkUIGroup = "work";
    public static final String WorkFragment = "/"+WorkUIGroup+"/WorkFragment";
    /*** 巡查 end ***/
}
