package com.supersit.common.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.supersit.common.R;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.model.UserModel;
import com.supersit.common.utils.ImageLoader;
import com.supersit.common.widget.MyImageView;

import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */

public class GridUserAdapter extends BaseQuickAdapter<UserEntity, BaseViewHolder> {

    public GridUserAdapter(@Nullable List<UserEntity> data) {
        super(R.layout.item_grid_user, data);
    }

    public GridUserAdapter(@Nullable List<UserEntity> data,int showCount) {
        super(R.layout.item_grid_user, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, UserEntity userEntity) {
        MyImageView ivAvatar = helper.getView(R.id.iv_avatar);
        ImageLoader.displayImage(mContext, userEntity.getAvatarUrl(),R.mipmap.ic_default_avatar, ivAvatar);
        helper.setText(R.id.tv_username,userEntity.getNickName());
    }


}
