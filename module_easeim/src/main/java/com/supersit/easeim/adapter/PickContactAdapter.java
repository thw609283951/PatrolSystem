package com.supersit.easeim.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.widget.EaseImageView;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.utils.ImageLoader;
import com.supersit.common.widget.sidelist.BaseSideListAdapter;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 田皓午 on 2018/4/3.
 */

public class PickContactAdapter extends BaseSideListAdapter<UserEntity>{

    private List<String> mExistMembers;
    private List<UserEntity> mSelectUsers;

    public PickContactAdapter(@NonNull Context context,List<String> existMembers,List objects) {
        super(context,  objects);
        mExistMembers = existMembers;
        mSelectUsers = new ArrayList<>();
    }

    static class ViewHolder {
        @BindView(R2.id.tv_header) TextView tvHead;
        @BindView(R2.id.iv_avatar) EaseImageView ivAvatar;
        @BindView(R2.id.tv_name) TextView tvName;
        @BindView(R2.id.tv_remark) TextView tvRemark;
        @BindView(R2.id.checkbox) CheckBox checkBox;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.item_contact_with_checkbox, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        UserEntity user = (UserEntity) getItem(position);
        if(user == null)
            Log.d("ContactAdapter", position + "");
        String header = user.getInitialLetter();

        if (position == 0 || header != null && !header.equals(((UserEntity)getItem(position - 1)).getInitialLetter())) {
            if (TextUtils.isEmpty(header)) {
                holder.tvHead.setVisibility(View.GONE);
            } else {
                holder.tvHead.setVisibility(View.VISIBLE);
                holder.tvHead.setText(header);
            }
        } else {
            holder.tvHead.setVisibility(View.GONE);
        }

        holder.tvName.setText( null == user.getNickName() ? user.getUserName() : user.getNickName() );
        holder.tvRemark.setText( user.getModePhone());
        ImageLoader.displayImage(context,user.getAvatarUrl(),R.drawable.ic_default_avatar,holder.ivAvatar);
        if (holder.checkBox != null) {
            if (mExistMembers != null && mExistMembers.contains(user.getUserName())) {
                holder.checkBox.setButtonDrawable(R.mipmap.ic_checkbox_gray_on);
            } else {
                holder.checkBox.setButtonDrawable(R.drawable.bg_checkbox_selector);
                holder.checkBox.setChecked(mSelectUsers.contains(user));

            }

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // check the exist members
                    if (mExistMembers != null && mExistMembers.contains(user.getUserName())) {
                        return;
                    }
                    if (isChecked) {
                        mSelectUsers.add(user);
                    } else {
                        mSelectUsers.remove(user);
                    }
                }
            });
        }

        return convertView;
    }

    public List<UserEntity> getSelectUsers(){
        return mSelectUsers;
    }

    @Override
    protected boolean isContains(String prefix, UserEntity item) {
        if(item.getUserName().contains(prefix) || item.getNickName().contains(prefix) )
            return true;
        return false;
    }
}
