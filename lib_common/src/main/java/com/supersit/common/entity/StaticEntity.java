package com.supersit.common.entity;

import com.blankj.utilcode.util.Utils;
import com.supersit.common.R;
import com.supersit.common.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/3.
 */

public class StaticEntity {

    private static List<BaseEntity> mediaDatas;

    public static List<BaseEntity> getMediaDatas() {
        if (null == mediaDatas) {
            List<BaseEntity> datas = new ArrayList<>();
            datas.add(new BaseEntity(R.mipmap.ic_camera, Utils.getApp().getString(R.string.camera)));
            datas.add(new BaseEntity(R.mipmap.ic_album, Utils.getApp().getString(R.string.album)));
            datas.add(new BaseEntity(R.mipmap.ic_video, Utils.getApp().getString(R.string.video)));
            mediaDatas = datas;
        }
        return mediaDatas;
    }

}
