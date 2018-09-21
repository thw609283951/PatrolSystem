package com.supersit.work.adapter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.supersit.common.entity.EventEntity;
import com.supersit.common.entity.EventHandleEntity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.utils.ActionUtil;
import com.supersit.common.utils.SpannableStringUtil;
import com.supersit.work.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 */

public class WorkEventHandleAdapter extends BaseQuickAdapter<EventHandleEntity,BaseViewHolder> {


    public WorkEventHandleAdapter(List<EventHandleEntity> data) {
        super(R.layout.item_event_handle, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EventHandleEntity value) {
        String title;
        String userType;
        String handleType;
        if(EventHandleEntity.UPLIAD == value.getStatus()){
            title = new StringBuffer("1.").append(mContext.getString(R.string.event_source)).toString();
            userType = mContext.getString(R.string.upload_user);
            handleType = mContext.getString(R.string.upload);

            helper.setGone(R.id.ll_handle_suggest,false);
            helper.setVisible(R.id.ll_next_receiver,true);
            TextView tvReceiverUser= helper.getView(R.id.tv_next_receiver);
            tvReceiverUser.setMovementMethod(LinkMovementMethod.getInstance());
            tvReceiverUser.setText(getUserNameAndModePhone(value.getReceiveUserEntity()));
        }else{
            title = new StringBuffer(String.valueOf(helper.getAdapterPosition()+1))
                    .append(mContext.getString(R.string.handle_situation)).toString();
            if(EventHandleEntity.ACCEPT == value.getStatus()){
                handleType = mContext.getString(R.string.accept);
                userType = mContext.getString(R.string.handle_person);
                helper.setText(R.id.tv_handle_suggest,value.getMessage());

                helper.setVisible(R.id.ll_handle_suggest,true);
                helper.setVisible(R.id.ll_next_receiver,true);

                TextView tvReceiverUser= helper.getView(R.id.tv_next_receiver);
                tvReceiverUser.setMovementMethod(LinkMovementMethod.getInstance());
                tvReceiverUser.setText(getUserNameAndModePhone(value.getReceiveUserEntity()));
            }else{
                handleType = mContext.getString(R.string.end);
                userType = mContext.getString(R.string.end_person);

                helper.setGone(R.id.ll_handle_suggest,false);
                helper.setGone(R.id.ll_next_receiver,false);
            }

        }
        helper.setText(R.id.tv_title,title);
        helper.setText(R.id.tv_user_type,userType);

        TextView tvSendUser= helper.getView(R.id.tv_user_name);
        tvSendUser.setMovementMethod(LinkMovementMethod.getInstance());
        tvSendUser.setText(getUserNameAndModePhone(value.getUserEntity()));

        helper.setText(R.id.tv_time,TimeUtils.millis2String(value.getTime()));
        helper.setText(R.id.tv_handle_type,handleType);
    }


    public SpannableStringBuilder getUserNameAndModePhone(UserEntity userEntity){
        SpannableStringUtil result=new SpannableStringUtil();
        result.append(userEntity.getNickName())
                .append("(")
                .appendImage(R.mipmap.ic_call)
                .append(userEntity.getModePhone()).setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        //用intent启动拨打电话
                        if (null != userEntity.getModePhone()) {
                            ActionUtil.call(mContext,userEntity.getModePhone());
                        }
                    }

                }) .append(")");
        return result.create();
    }
}
