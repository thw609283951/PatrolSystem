package com.supersit.work.adapter;

import android.support.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.supersit.work.R;
import com.supersit.work.entity.ApplicationEntity;
import com.supersit.work.entity.NotificationEntity;

import java.util.List;

/**
 * Created by 田皓午 on 2018/5/22.
 */

public class NotifyAdapter extends BaseQuickAdapter<NotificationEntity,BaseViewHolder>{
    public NotifyAdapter(@Nullable List<NotificationEntity> data) {
        super(R.layout.item_notice, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NotificationEntity entity) {
        helper.setText(R.id.tv_time, TimeUtils.getFriendlyTimeSpanByNow(entity.getTime()));
        helper.setText(R.id.tv_title, entity.getTitle());
        helper.setText(R.id.tv_remark, entity.getRemark());
    }
}
