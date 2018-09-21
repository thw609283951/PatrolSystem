package com.supersit.patrol.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.supersit.common.ARouterConstant;
import com.supersit.common.Constant;
import com.supersit.common.adapter.MediaFileAdapter;
import com.supersit.common.base.BaseEntity;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.EventEntity;
import com.supersit.common.entity.StaticEntity;
import com.supersit.common.interfaces.IFlowTagName;
import com.supersit.common.ui.ShowMediasActivity;
import com.supersit.common.utils.ActionUtil;
import com.supersit.common.utils.MyUtil;
import com.supersit.common.widget.GridRadioDialog;
import com.supersit.common.widget.edittext.AutoCheckEditText;
import com.supersit.common.widget.edittext.AutoCheckEditTextClass;
import com.supersit.common.widget.edittext.EditTextType;
import com.supersit.common.widget.recyclerview.SpaceItemDecoration;
import com.supersit.mvp.presenter.RequirePresenter;
import com.supersit.patrol.R;
import com.supersit.patrol.R2;
import com.supersit.patrol.entity.EventTitleEntity;
import com.supersit.patrol.entity.EventTypeEntity;
import com.supersit.patrol.entity.LocationEntity;
import com.supersit.patrol.presenter.PatrolUploadPresenter;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@RequirePresenter(PatrolUploadPresenter.class)
public class PatrolUploadActivity extends ToolbarActivity<PatrolUploadPresenter> {


    @BindView(R2.id.et_event_type)
    public EditText etEventType;
    @BindView(R2.id.tfl_event_type)
    TagFlowLayout tflEventType;
    @BindView(R2.id.et_event_title)
    public EditText etEventTitle;
    @BindView(R2.id.tfl_event_title)
    TagFlowLayout tflEventTitle;
    @BindView(R2.id.cb_normal)
    public CheckBox cbNormal;
    @BindView(R2.id.et_event_remark)
    public EditText etEventRemark;
    @BindView(R2.id.tv_text_count_200)
    TextView tvTextCount200;
    @BindView(R2.id.et_commit_object)
    EditText etCommitObject;
    @BindView(R2.id.et_location)
    EditText etLocation;
    @BindView(R2.id.rv_photo_or_vedio)
    RecyclerView rvPhotoOrVedio;

    @BindView(R2.id.btn_delete)
    Button btnDelete;
    @BindView(R2.id.et_belong_project)
    EditText etBelongProject;
    @BindView(R2.id.tfl_belong_project)
    TagFlowLayout tflBelongProject;

    private String currCameraImgPath = null;
    public MediaFileAdapter mMediaFileAdapter;

    private AutoCheckEditTextClass mProjectEditTextClass,mTypeEditTextClass
            ,mTitleEditTextClass,mRemarkEditTextClass,mReceiveUserEditTextClass;

    public static void start(Activity mContext) {
        start(mContext, null);
    }

    public static void start(Activity mContext, EventEntity eventEntity) {

        Intent intent = new Intent(mContext, PatrolUploadActivity.class);

        if (null != eventEntity) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("eventEntity", eventEntity);
            intent.putExtras(bundle);
            mContext.startActivityForResult(intent, 1);
        } else
            mContext.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {

        if (null != bundle)
            getPresenter().mCurrEventEntity = bundle.getParcelable("eventEntity");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_patrol_upload;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.event_up);
        if (null != getPresenter().mCurrEventEntity) {
            btnDelete.setVisibility(View.VISIBLE);
        }
        cbNormal.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                etEventRemark.setText(R.string.everything_normal);
            }else{
                etEventRemark.setText("");
            }
        });

        mProjectEditTextClass = new AutoCheckEditTextClass((AutoCheckEditText) etBelongProject).checkType(EditTextType.TYPE_OF_NOT_EMPTY);
        mTypeEditTextClass = new AutoCheckEditTextClass((AutoCheckEditText) etEventType).checkType(EditTextType.TYPE_OF_NOT_EMPTY);
        mTitleEditTextClass = new AutoCheckEditTextClass((AutoCheckEditText) etEventTitle).checkType(EditTextType.TYPE_OF_NOT_EMPTY);
        mRemarkEditTextClass = new AutoCheckEditTextClass((AutoCheckEditText) etEventRemark).checkType(EditTextType.TYPE_OF_NOT_EMPTY);
        mReceiveUserEditTextClass = new AutoCheckEditTextClass((AutoCheckEditText) etCommitObject).checkType(EditTextType.TYPE_OF_NOT_EMPTY);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPhotoOrVedio.setLayoutManager(linearLayoutManager);
        rvPhotoOrVedio.addItemDecoration(new SpaceItemDecoration(3));
        mMediaFileAdapter = new MediaFileAdapter(null, true);
        mMediaFileAdapter.setOnItemClickListener((adapter, view1, position) -> {
            String filePath = (String) adapter.getItem(position);
            if(Constant.IMAGE_CODE == MyUtil.getFileType(filePath)){
                ShowMediasActivity.start((Activity) mContext, position, mMediaFileAdapter.getData());
            }else{
                ActionUtil.openFile(mContext,filePath);
            }

        });
        mMediaFileAdapter.setOnItemChildClickListener((adapter, view12, position) -> {
            if (view12.getId() == R.id.iv_badge_delete) {
                mMediaFileAdapter.remove(position);
            }
        });
        mMediaFileAdapter.bindToRecyclerView(rvPhotoOrVedio);
    }

    @OnClick({R2.id.btn_delete, R2.id.et_commit_object, R2.id.tv_refresh_location, R2.id.ibtn_media, R2.id.btn_upload, R2.id.btn_save})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_refresh_location) {
            getPresenter().getCurrLocationInfo(true);
        } else if (i == R.id.et_commit_object) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isSelect",true);
            ARouter.getInstance().build(ARouterConstant.ContactListActivity).with(bundle)
                    .navigation((Activity) mContext, Constant.CODE_SELECT_USER_REQUEST);
        } else if (i == R.id.ibtn_media) {
            showMediaDialog();
        } else if (i == R.id.btn_upload || i == R.id.btn_save) {
            trimData(i);
        } else if (i == R.id.btn_delete) {

            if (getPresenter().mCurrEventEntity.delete()) {
                showSuccessToast(R.string.delete_success);
                setResult(RESULT_OK);
                finish();
            } else {
                showErrorToast(R.string.delete_fail);
            }
        }
    }


    public void showEventEntity(EventEntity currEventEntity) {
        etBelongProject.setText(currEventEntity.getDeptEntitiy().getName());
        etEventType.setText(currEventEntity.getEventType());
        etEventTitle.setText(currEventEntity.getEventTitle());
        cbNormal.setChecked(currEventEntity.getNormal() == 1 ? true : false);
        etEventRemark.setText(currEventEntity.getEventRemark());
        if (null != getPresenter().mReceiverUser)
            etCommitObject.setText(getPresenter().mReceiverUser.getNickName());
        if (null != getPresenter().mCurrLocationInfo)
            etLocation.setText(getPresenter().mCurrLocationInfo.getAddress());
        if (null != currEventEntity.getMediaUrls())
            mMediaFileAdapter.setNewData(currEventEntity.getMediaUrls());
    }

    public void showFLBelongProjects(List<DeptEntity> entitiys){
        if(1 == entitiys.size()){
            getPresenter().mCurrDeptEntitiy = entitiys.get(0);
            etBelongProject.setText(getPresenter().mCurrDeptEntitiy.getName());

            tflBelongProject.setVisibility(View.GONE);
            return;
        }

        tflBelongProject.setAdapter(new TagAdapter(entitiys) {
            @Override
            public View getView(FlowLayout parent, int position, Object o) {
                TextView tvFlow = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_flow_tag,
                        tflBelongProject, false);
                IFlowTagName flowTagName = (IFlowTagName) o;
                tvFlow.setText(flowTagName.getFlowTagName());
                return tvFlow;
            }
        });
        tflBelongProject.setOnTagClickListener((view, position, parent) -> {
            getPresenter().mCurrDeptEntitiy = entitiys.get(position);
            etBelongProject.setText(getPresenter().mCurrDeptEntitiy .getFlowTagName());
            return true;
        });
    }

    public void showFLEventTypes(List<EventTypeEntity> eventTypes) {
        tflEventType.setAdapter(new TagAdapter(eventTypes) {
            @Override
            public View getView(FlowLayout parent, int position, Object o) {
                TextView tvFlow = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_flow_tag,
                        tflEventType, false);
                IFlowTagName flowTagName = (IFlowTagName) o;
                tvFlow.setText(flowTagName.getFlowTagName());
                return tvFlow;
            }
        });
        tflEventType.setOnTagClickListener((view, position, parent) -> {
            if (etEventType.getText().toString().equals(eventTypes.get(position).getFlowTagName())) {
                etEventType.setText("");
                etEventTitle.setText("");
                showFLEventTitles(null);
            } else {
                etEventType.setText(eventTypes.get(position).getFlowTagName());
                showFLEventTitles(eventTypes.get(position).getEventTitleEntities());
            }

            return true;
        });
    }

    public void showFLEventTitles(List<EventTitleEntity> eventTitles) {
        tflEventTitle.setAdapter(new TagAdapter(eventTitles) {
            @Override
            public View getView(FlowLayout parent, int position, Object o) {
                TextView tvFlow = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_flow_tag,
                        tflEventTitle, false);
                IFlowTagName flowTagName = (IFlowTagName) o;
                tvFlow.setText(flowTagName.getFlowTagName());
                return tvFlow;
            }
        });
        tflEventTitle.setOnTagClickListener((view, position, parent) -> {
            etEventTitle.setText(eventTitles.get(position).getFlowTagName());
            return true;
        });
    }

    public void showLoaction(String address) {
        etLocation.setText(address == null ? getString(R.string.gps_location) : address);
    }

    public void showMediaDialog() {
        GridRadioDialog radioDialog = new GridRadioDialog(mContext, 3);
        radioDialog.setTitle("");
        radioDialog.setDatas(StaticEntity.getMediaDatas());
        Window window = radioDialog.getWindow();
        window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.AnimBottom);  //添加动画
        radioDialog.setOnItemClickListener((dialog, data) -> {
            dialog.dismiss();
            BaseEntity baseInfo = (BaseEntity) data;
            if (baseInfo.getId() == R.mipmap.ic_camera) {
                currCameraImgPath = MyUtil.getNewFilePath(mContext, Constant.IMAGE_CODE);
                ActionUtil.openCamera((Activity) mContext, currCameraImgPath);

            } else if (baseInfo.getId() == R.mipmap.ic_album) {
                ActionUtil.openAblum((Activity) mContext, mMediaFileAdapter.getData());

            } else if (baseInfo.getId() == R.mipmap.ic_video) {
                ActionUtil.recorderVideo((Activity) mContext);

            } else {
            }
        });
        radioDialog.show();
    }

    public void trimData(int onClickViewId) {
        if(mProjectEditTextClass.isException() ||  mTypeEditTextClass.isException() || mTitleEditTextClass.isException()
                || mRemarkEditTextClass.isException() || mReceiveUserEditTextClass.isException()){
            return;
        }

        if (null == getPresenter().mCurrLocationInfo) {
            etLocation.setError(getResources().getString(R.string.location_error));
            etLocation.requestFocus();
            return;
        }

        if (0 == mMediaFileAdapter.getItemCount()) {
            showInfoToast(R.string.please_upload_at_least_one_picture);
            return;
        }

        getPresenter().trimData(onClickViewId);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        Bundle bundle = null;
        if (data != null)
            bundle = data.getExtras();
        switch (requestCode) {
            case Constant.CODE_SELECT_USER_REQUEST:
                getPresenter().mReceiverUser = bundle.getParcelable("user");
                etCommitObject.setText(getPresenter().mReceiverUser.getNickName());
                break;

            case Constant.CODE_CAMERA_REQUEST:
                if (null != currCameraImgPath) {
                    mMediaFileAdapter.addData(currCameraImgPath);
                }
                break;
            case Constant.CODE_MULTI_ALBUM_REQUEST:
                mMediaFileAdapter.setNewData(bundle.getStringArrayList("selectedImages"));
                break;

            case Constant.CODE_VIDEO_REQUEST:
                String videoPath = bundle.getString("path");
                if (null != videoPath) {
                    mMediaFileAdapter.addData(videoPath);
                }
                break;

            case Constant.CODE_LOCATION_REQUEST:
                LocationEntity LocationEntity = new LocationEntity();
                LocationEntity.setLatitude(bundle.getDouble("latitude"));
                LocationEntity.setLongitude(bundle.getDouble("longitude"));

                String address = bundle.getString("address");
                LocationEntity.setAddress(address);
                getPresenter().mCurrLocationInfo = LocationEntity;
                showLoaction(address);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
