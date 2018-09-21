/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.supersit.common;

public class Constant {

    public static final String ACCOUNT_REMOVED = "account_removed";
    public static final String ACCOUNT_CONFLICT = "conflict";
    public static final String ACCOUNT_FORBIDDEN = "user_forbidden";
    public static final String ACCOUNT_KICKED_BY_CHANGE_PASSWORD = "kicked_by_change_password";
    public static final String ACCOUNT_KICKED_BY_OTHER_DEVICE = "kicked_by_another_device";
    public static final String ACCOUNT_IS_CONFLICT = "isConflict";

    /**
     * 文件类型
     */
    public static final int IMAGE_CODE = 0;
    public static final int VEDIO_CODE = 1;
    public static final int WORD_CODE = 2;
    public static final int EXCEL_CODE = 3;
    public static final int PPT_CODE = 4;
    public static final int PDF_CODE = 5;
    public static final int OTHER_CODE = -1;

    /**
     * Activity 之间跳转常量
     */
    public static final int CODE_CLIP_REQUEST = 100; // 裁剪
    public static final int CODE_RADIO_ALBUM_REQUEST = 110; // 单选相册
    public static final int CODE_MULTI_ALBUM_REQUEST = 111; // 多选相册
    public static final int CODE_CAMERA_REQUEST = 112; // 拍照
    public static final int CODE_VIDEO_REQUEST = 113; // 录像
    public static final int CODE_LOCATION_REQUEST = 114; // 位置纠正

    public static final int CODE_SELECT_USER_REQUEST = 115; // 位置纠正

    /**
     *  RxBus 常量定义
     */
    public static final int RXBUS_LOGIN_SUCEESS=10001;
    public static final int RXBUS_UPIM_UNREAD_COUNT=10002;
    public static final int RXBUS_UPIM_CONTACTS=10003;

    public static final int RXBUS_UPWORK_APPS=10004;
    public static final int RXBUS_UPPATROL_BASEEVENTS=10005;
    public static final int RXBUS_UPWORK_NOTIFYS=10006;

}
