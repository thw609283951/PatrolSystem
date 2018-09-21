package com.supersit.patrol.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.supersit.common.Constant;
import com.supersit.common.adapter.MediaFileAdapter;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.ui.ShowMediasActivity;
import com.supersit.common.utils.ActionUtil;
import com.supersit.common.utils.MyUtil;
import com.supersit.common.widget.recyclerview.GridSpacingItemDecoration;
import com.supersit.patrol.R;
import com.supersit.patrol.R2;
import com.supersit.common.entity.EventEntity;

import java.util.List;

import butterknife.BindView;

public class EventInfoActivity extends ToolbarActivity {

    @BindView(R2.id.tv_upload_user)
    TextView tvUploadUser;
    @BindView(R2.id.tv_upload_time)
    TextView tvUploadTime;
    @BindView(R2.id.tv_affiliation_project)
    TextView tvAffiliationProject;
    @BindView(R2.id.tv_upload_address)
    TextView tvUploadAddress;
    @BindView(R2.id.tv_event_title)
    TextView tvEventTitle;
    @BindView(R2.id.tv_event_remark)
    TextView tvEventRemark;
    @BindView(R2.id.rv_scene_pictures)
    RecyclerView rvScenePictures;

    private EventEntity mEventEntity;
    private int mSpanCount = 4;

    public static void start(Activity mContext, EventEntity eventEntity) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(mContext, EventInfoActivity.class);
        bundle.putParcelable("eventEntity", eventEntity);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {
        mEventEntity = bundle.getParcelable("eventEntity");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_event_info;
    }


    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.event_info);
        rvScenePictures.setLayoutManager(new GridLayoutManager(mContext, mSpanCount));
        int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.spacing_m);
        rvScenePictures.addItemDecoration(new GridSpacingItemDecoration(mSpanCount, spacingInPixels, true));

        if(null != mEventEntity){
            tvUploadUser.setText(mEventEntity.getUserEntity().getNickName());
            tvUploadTime.setText(TimeUtils.millis2String(mEventEntity.getTime()));
            tvAffiliationProject.setText(mEventEntity.getDeptEntitiy().getName());
            tvUploadAddress.setText(mEventEntity.getAddress());
            tvEventTitle.setText(mEventEntity.getEventTitle()+"("+mEventEntity.getEventType()+")");
            tvEventRemark.setText(mEventEntity.getEventRemark());

            List<String> mediaUrls=mEventEntity.getMediaUrls();
            if(null !=mediaUrls && !mediaUrls.isEmpty()){
                MediaFileAdapter mediaFileAdapter = new MediaFileAdapter(mediaUrls,false);
                mediaFileAdapter.setOnItemClickListener((adapter, view1, position) ->{
                    String filePath = (String) adapter.getItem(position);
                    if(Constant.IMAGE_CODE == MyUtil.getFileType(filePath)){
                        ShowMediasActivity.start( mContext,mEventEntity.getEventTitle(), position, adapter.getData());
                    }else{
                        startActivity(ActionUtil.getVideoUrlIntent(filePath));
                    }
                });
                mediaFileAdapter.bindToRecyclerView(rvScenePictures);
            }
        }
    }
}
