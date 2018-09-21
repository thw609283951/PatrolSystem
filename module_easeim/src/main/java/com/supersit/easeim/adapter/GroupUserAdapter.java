package com.supersit.easeim.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.easeui.widget.EaseImageView;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.model.UserModel;
import com.supersit.common.utils.ImageLoader;
import com.supersit.easeim.R;

import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */

public class GroupUserAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private boolean mIsShow;
    public GroupUserAdapter(@Nullable List<String> data,boolean isShow) {
        super(R.layout.item_group_user, data);
        mIsShow = isShow;
    }

    @Override
    protected void convert(BaseViewHolder helper, String username) {
        EaseImageView ivAvatar = helper.getView(R.id.iv_avatar);
        if(mIsShow){
            if( helper.getPosition() == getData().size()-2){
                ivAvatar.setShapeType(2);
                helper.setText(R.id.tv_username,"");
                ivAvatar.setImageResource(R.drawable.em_smiley_add_btn);
                return;
            }else if(helper.getPosition() == getData().size()-1 ){
                ivAvatar.setShapeType(2);
                helper.setText(R.id.tv_username,"");
                ivAvatar.setImageResource(R.drawable.em_smiley_minus_btn);
                return;
            }
        }

        UserEntity userEntity = UserModel.getInstance().getUserByUserName(username);
        if(null == userEntity){
            ivAvatar.setImageResource(R.drawable.ic_default_avatar);
            helper.setText(R.id.tv_username,username);
        }else{
            ImageLoader.displayImage(mContext, userEntity.getAvatarUrl(), ivAvatar);
            helper.setText(R.id.tv_username,userEntity.getNickName());
        }
    }

}
