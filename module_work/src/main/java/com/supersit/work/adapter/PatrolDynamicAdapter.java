package com.supersit.work.adapter;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.supersit.common.entity.EventEntity;
import com.supersit.work.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 */

public class PatrolDynamicAdapter extends BaseQuickAdapter<EventEntity,BaseViewHolder> {

    public PatrolDynamicAdapter(List<EventEntity> data) {
        super(R.layout.item_patrol_dynamic, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EventEntity patrolInfo) {
        helper.setText(R.id.tv_title, patrolInfo.getUserEntity().getNickName()+"正在巡查"+patrolInfo.getEventType());
        helper.setText(R.id.tv_time,TimeUtils.millis2String(patrolInfo.getTime(),new SimpleDateFormat("MM-dd HH:mm")));
    }

}
