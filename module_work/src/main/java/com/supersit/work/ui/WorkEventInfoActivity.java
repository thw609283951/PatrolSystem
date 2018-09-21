package com.supersit.work.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.supersit.common.adapter.MediaFileAdapter;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.EventEntity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.ui.ShowMediasActivity;
import com.supersit.common.widget.recyclerview.GridSpacingItemDecoration;
import com.supersit.common.widget.recyclerview.SpaceItemDecoration;
import com.supersit.mvp.presenter.RequirePresenter;
import com.supersit.work.R;
import com.supersit.work.R2;
import com.supersit.work.adapter.WorkEventHandleAdapter;
import com.supersit.work.presenter.WorkEventInfoPresenter;
import com.supersit.work.widget.EventHandleDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@RequirePresenter(WorkEventInfoPresenter.class)
public class WorkEventInfoActivity extends ToolbarActivity<WorkEventInfoPresenter> {

    public static final int mRequestCode = 1;

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
    @BindView(R2.id.rv_event_handle)
    RecyclerView rvEventHandle;
    @BindView(R2.id.btn_edit_suggest)
    Button btnEditSuggest;
    @BindView(R2.id.btn_event_end)
    Button btnEventEnd;


    public EventEntity mEventEntity;
    private int mSpanCount = 4;

    public static void start(Activity mContext, EventEntity eventEntity) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(mContext, WorkEventInfoActivity.class);
        bundle.putParcelable("eventEntity", eventEntity);
        intent.putExtras(bundle);
        mContext.startActivityForResult(intent,mRequestCode);
    }

    @Override
    public void initData(Bundle bundle) {
        mEventEntity = bundle.getParcelable("eventEntity");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_work_event_info;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.event_info);
        rvScenePictures.setLayoutManager(new GridLayoutManager(mContext, mSpanCount));
        int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.spacing_m);
        rvScenePictures.addItemDecoration(new GridSpacingItemDecoration(mSpanCount, spacingInPixels, true));

        rvEventHandle.setLayoutManager(new LinearLayoutManager(mContext));
        rvEventHandle.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        if (null != mEventEntity) {
            int status = mEventEntity.getStatus();
            if(1 != status){
                btnEditSuggest.setVisibility(View.GONE);
                btnEventEnd.setVisibility(View.GONE);
            }
            tvUploadUser.setText(mEventEntity.getUserEntity().getNickName());
            tvUploadTime.setText(TimeUtils.millis2String(mEventEntity.getTime()));
            tvAffiliationProject.setText(mEventEntity.getDeptEntitiy().getName());
            tvUploadAddress.setText(mEventEntity.getAddress());
            tvEventTitle.setText(mEventEntity.getEventTitle() + "(" + mEventEntity.getEventType() + ")");
            tvEventRemark.setText(mEventEntity.getEventRemark());

            List<String> mediaUrls = mEventEntity.getMediaUrls();
            if (null != mediaUrls && !mediaUrls.isEmpty()) {
                MediaFileAdapter mediaFileAdapter = new MediaFileAdapter(mediaUrls, false);
                mediaFileAdapter.setOnItemClickListener((adapter, view1, position) -> ShowMediasActivity.start(mContext, mEventEntity.getEventTitle(),position, adapter.getData()));
                mediaFileAdapter.bindToRecyclerView(rvScenePictures);
            }


            WorkEventHandleAdapter workEventHandleAdapter = new WorkEventHandleAdapter(mEventEntity.getEventHandleEntitys());
            workEventHandleAdapter.bindToRecyclerView(rvEventHandle);
        }
    }


    @OnClick({R2.id.btn_edit_suggest, R2.id.btn_event_end})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.btn_edit_suggest) {
            showEventHandleDialog();
        } else if (i == R.id.btn_event_end) {
            getPresenter().upLoadEventHandleEntity(3);
        }
    }

    private EventHandleDialog dialogFragment;
    public void showEventHandleDialog(){
        if(null == dialogFragment){
            dialogFragment = new EventHandleDialog(new EventHandleDialog.CommentListener() {
                @Override
                public void sendComment(String inputText, UserEntity receiverUser) {
                    ToastUtils.showShort(inputText,receiverUser.getNickName());
                    getPresenter().mContent = inputText;
                    getPresenter().mReceiveUser = receiverUser;
                    getPresenter().upLoadEventHandleEntity(2);
                }

                @Override
                public void saveComment(String inputText, UserEntity receiverUser) {
                    getPresenter().mContent = inputText;
                    getPresenter().mReceiveUser = receiverUser;
                }
            });
        }else{
            dialogFragment.setText(getPresenter().mContent);
            dialogFragment.setSelectUser(getPresenter().mReceiveUser);
        }
        dialogFragment.show(getSupportFragmentManager(),"aaa");
    }

    public void dismissEventHandleDialog(){
        if(null != dialogFragment)
            dialogFragment.dismiss();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK )
            return;
    }
}
