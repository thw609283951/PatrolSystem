package com.supersit.patrol.presenter;

import com.supersit.common.base.BasePresenter;
import com.supersit.common.entity.EventEntity;
import com.supersit.patrol.model.PatrolModel;
import com.supersit.patrol.ui.PatrolUploadItemsActivity;

import java.util.List;

/**
 * Created by 田皓午 on 2018/4/16.
 */

public class PatrolUploadItemsPresenter extends BasePresenter<PatrolUploadItemsActivity>{



    @Override
    public void onCreate() {
        super.onCreate();
    }

    public List<EventEntity> getEventEntity(){
        return PatrolModel.getInstance().getEventEntitys();
    }

}
