package com.supersit.work.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.supersit.common.utils.ImageLoader;
import com.supersit.work.R;
import com.supersit.work.entity.ApplicationEntity;

import java.util.List;

/**
 * Created by 田皓午 on 2018/4/21.
 */

public class AppsAdapter extends BaseQuickAdapter<ApplicationEntity,BaseViewHolder>{


    public AppsAdapter( @Nullable List data) {
        super(R.layout.item_app, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ApplicationEntity item) {
        ImageView imageView = helper.getView(R.id.imageView);
        ImageLoader.displayImage(mContext,item.getIconUrl(),imageView);
        helper.setText(R.id.tv_name,item.getName());
    }



}
