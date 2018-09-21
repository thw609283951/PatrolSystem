package com.supersit.easeim.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.widget.EaseImageView;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.utils.ImageLoader;
import com.supersit.common.widget.sidelist.BaseSideListAdapter;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 田皓午 on 2018/4/3.
 */

public class ContactAdapter extends BaseSideListAdapter<UserEntity>{
    public ContactAdapter(@NonNull Context context, List objects) {
        super(context,  objects);
    }


    static class ViewHolder {
        @BindView(R2.id.tv_header) TextView tvHead;
        @BindView(R2.id.iv_avatar) EaseImageView ivAvatar;
        @BindView(R2.id.tv_name) TextView tvName;
        @BindView(R2.id.tv_remark) TextView tvRemark;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.item_contact, parent, false);
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

        EaseAvatarOptions avatarOptions = EaseUI.getInstance().getAvatarOptions();
        if(avatarOptions != null && holder.ivAvatar instanceof EaseImageView) {
            EaseImageView avatarView = ((EaseImageView) holder.ivAvatar);
            if (avatarOptions.getAvatarShape() != 0)
                avatarView.setShapeType(avatarOptions.getAvatarShape());
            if (avatarOptions.getAvatarBorderWidth() != 0)
                avatarView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
            if (avatarOptions.getAvatarBorderColor() != 0)
                avatarView.setBorderColor(avatarOptions.getAvatarBorderColor());
            if (avatarOptions.getAvatarRadius() != 0)
                avatarView.setRadius(avatarOptions.getAvatarRadius());
        }
        StringBuffer stringBuffer = new StringBuffer(user.getNickName());
        String Positions= user.getPositions();
        if(null != Positions){
            stringBuffer.append("(");
            stringBuffer.append(Positions);
            stringBuffer.append(")");
        }

        holder.tvName.setText(stringBuffer.toString());
        holder.tvRemark.setText( user.getModePhone());
        ImageLoader.displayImage(context,user.getAvatarUrl(),R.drawable.ic_default_avatar,holder.ivAvatar);
        return convertView;
    }

    @Override
    protected boolean isContains(String prefix, UserEntity item) {
        if(item.getUserName().contains(prefix) || item.getNickName().contains(prefix) )
            return true;
        return false;
    }
}
