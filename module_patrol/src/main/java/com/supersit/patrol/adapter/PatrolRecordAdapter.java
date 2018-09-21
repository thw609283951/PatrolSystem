package com.supersit.patrol.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseViewHolder;
import com.supersit.common.Constant;
import com.supersit.common.adapter.BaseFilterAdapter;
import com.supersit.common.utils.ImageLoader;
import com.supersit.common.utils.MyUtil;
import com.supersit.common.utils.SpannableStringUtil;
import com.supersit.patrol.R;
import com.supersit.common.entity.EventEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 */

public class PatrolRecordAdapter extends BaseFilterAdapter<EventEntity> {


    public PatrolRecordAdapter(@Nullable List<EventEntity> data){
        this(R.layout.item_patrol_event,data);
    }

    public PatrolRecordAdapter(int layoutResId, @Nullable List<EventEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected boolean isContainsData(String prefixString, EventEntity value) {
        String type = value.getEventType();
        String title = value.getEventTitle();
        String remark = value.getEventRemark();
        String address = value.getAddress();
        return type.contains(prefixString) || title.contains(prefixString) || remark.contains(prefixString)
                || address.contains(prefixString);
    }


    @Override
    protected void convert(BaseViewHolder helper, EventEntity patrolInfo) {

        helper.setText(R.id.tv_user, patrolInfo.getUserEntity().getNickName());
        helper.setText(R.id.tv_time,TimeUtils.millis2String(patrolInfo.getTime(),new SimpleDateFormat("MM-dd HH:mm")));

        if(1 == patrolInfo.getNormal()){
            helper.setImageResource(R.id.iv_status,R.mipmap.ic_status_green);
        }else{
            helper.setImageResource(R.id.iv_status,R.mipmap.ic_status_red);
        }

        ImageView imageView = helper.getView(R.id.imageView);
        if(null == patrolInfo.getMediaUrls() || patrolInfo.getMediaUrls().isEmpty()){
            imageView.setImageResource(R.mipmap.ic_patrol_event);
        }else{
            String url = patrolInfo.getMediaUrls().get(0);
            if(Constant.VEDIO_CODE == MyUtil.getFileType(url)){
                imageView.setImageResource(R.mipmap.ic_video);
            }else{
                ImageLoader.display2PlaceholderImage(mContext,url,imageView);
            }

        }

        if(null != mConstraint && !mConstraint.isEmpty()){
            String sTitle = patrolInfo.getEventTitle() + "("+patrolInfo.getEventType()+")";
            if(null != sTitle && sTitle.contains(mConstraint)){
                String[] sTitles = patrolInfo.getEventTitle().split(mConstraint);
                SpannableStringUtil suTitle=new SpannableStringUtil();
                if(0 < sTitles.length)
                    suTitle.append(sTitles[0]);
                if(1 == sTitles.length)
                    suTitle.append(mConstraint).setForegroundColor(Color.RED);
                for (int i = 1; i < sTitles.length; i++) {
                    suTitle.append(mConstraint).setForegroundColor(Color.RED)
                            .append(sTitles[i]);
                }
                helper.setText(R.id.tv_title,suTitle.create());
            }else{
                helper.setText(R.id.tv_title,sTitle);
            }

            String sRemark = patrolInfo.getEventRemark();
            if(null != sRemark && sRemark.contains(mConstraint)){
                String[] sRemarks = patrolInfo.getEventRemark().split(mConstraint);
                SpannableStringUtil suRemark=new SpannableStringUtil();
                if(0 < sRemarks.length)
                    suRemark.append(sRemarks[0]);
                if(1 == sRemarks.length)
                    suRemark.append(mConstraint).setForegroundColor(Color.RED);
                for (int i = 1; i < sRemarks.length; i++) {
                    suRemark.append(mConstraint).setForegroundColor(Color.RED);
                    suRemark.append(sRemarks[i]);
                }
                helper.setText(R.id.tv_remark,suRemark.create());
            }else{
                helper.setText(R.id.tv_remark,sRemark);
            }

            if(null !=patrolInfo.getAddress()){
                String sAddress = patrolInfo.getAddress();
                if(null != sAddress && sAddress.contains(mConstraint)){
                    String[] sAddresss = patrolInfo.getAddress().split(mConstraint);
                    SpannableStringUtil suAddress=new SpannableStringUtil();
                    if(0 < sAddresss.length)
                        suAddress.append(sAddresss[0]);
                    if(1 == sAddresss.length)
                        suAddress.append(mConstraint).setForegroundColor(Color.RED);
                    for (int i = 1; i < sAddresss.length; i++) {
                        suAddress.append(mConstraint).setForegroundColor(Color.RED);
                        suAddress.append(sAddresss[i]);
                    }
                    helper.setText(R.id.tv_address,suAddress.create());
                }else{
                    helper.setText(R.id.tv_address,sAddress);
                }
            }

        }else{
            helper.setText(R.id.tv_title,patrolInfo.getEventTitle() + "("+patrolInfo.getEventType()+")");
            helper.setText(R.id.tv_remark,patrolInfo.getEventRemark());
            helper.setText(R.id.tv_address,patrolInfo.getAddress());
        }


    }

}
