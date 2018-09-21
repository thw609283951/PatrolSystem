package com.supersit.common.provider;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.net.model.MyResponse;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/3/31.
 */

public interface IPatrolService extends IProvider {
    /**
     * 获取即时通讯未读信息的数量
     * @return
     */
    int getUnreadIMMsgCountTotal();
}
