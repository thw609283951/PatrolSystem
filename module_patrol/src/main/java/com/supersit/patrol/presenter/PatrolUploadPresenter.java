package com.supersit.patrol.presenter;

import android.app.Activity;

import com.blankj.utilcode.util.LogUtils;
import com.supersit.common.Constant;
import com.supersit.common.base.BasePresenter;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.RxBusDataSyncEntity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.model.UserModel;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.common.ui.LocationCorrectionActivity;
import com.supersit.common.utils.CompressPhotoUtil;
import com.supersit.patrol.R;
import com.supersit.common.entity.EventEntity;
import com.supersit.patrol.entity.EventTypeEntity;
import com.supersit.patrol.entity.LocationEntity;
import com.supersit.patrol.model.PatrolModel;
import com.supersit.patrol.ui.PatrolUploadActivity;
import com.threshold.rxbus2.annotation.RxSubscribe;
import com.threshold.rxbus2.util.EventThread;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PatrolUploadPresenter extends BasePresenter<PatrolUploadActivity> {

    public EventEntity mCurrEventEntity;

    public LocationEntity mCurrLocationInfo;
    public UserEntity mReceiverUser,mCurrUser;
    public DeptEntity mCurrDeptEntitiy;

    private List<EventTypeEntity> mEventTypeEntities;

    @Override
    public void onCreate() {
        super.onCreate();

        mCurrUser = UserModel.getInstance().getCurrUser();
        getView().showFLBelongProjects(mCurrUser.getDepts());

        mEventTypeEntities = PatrolModel.getInstance().getEventTypeEntitys();
        getView().showFLEventTypes(mEventTypeEntities);

        if(null == mCurrEventEntity){
            getCurrLocationInfo(false);
        }else{
            mReceiverUser = UserModel.getInstance().getUserByUserId(mCurrEventEntity.getReceiveUserId());
            if( 0!=mCurrEventEntity.getLatitude() &&  0!=mCurrEventEntity.getLongitude()){
                mCurrLocationInfo = new LocationEntity();
                mCurrLocationInfo.setLatitude(mCurrEventEntity.getLatitude());
                mCurrLocationInfo.setLongitude(mCurrEventEntity.getLongitude());
                mCurrLocationInfo.setAddress(mCurrEventEntity.getAddress());
            }
            getView().showEventEntity(mCurrEventEntity);
        }
    }

    @RxSubscribe(observeOnThread = EventThread.MAIN)
    public void RecRxBusDataSync(RxBusDataSyncEntity syncEntity) {
        if(Constant.RXBUS_UPPATROL_BASEEVENTS == syncEntity.getId()){
            if(syncEntity.isSuccess()){
                mEventTypeEntities = PatrolModel.getInstance().getEventTypeEntitys();
                getView().showFLEventTypes(mEventTypeEntities);
            }
        }
    }

    public void getCurrLocationInfo(boolean isShowDialog){
        AndPermission.with(getView())
                .runtime()
                .permission(
                        Permission.Group.LOCATION,
                        Permission.Group.SENSORS)
                .onGranted(data -> {
                    PatrolModel.getInstance().getCurrLocationInfo()
                            .compose(RxSchedulers.io_main())
                            .doOnSubscribe(disposable -> putDisposable(disposable))
                            .subscribe(new Observer<MyResponse<LocationEntity>>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    if(isShowDialog)
                                        getView().showProgressDialog(R.string.Is_get_location,d);
                                }

                                @Override
                                public void onNext(MyResponse<LocationEntity> value) {
                                    if(value.isSuccess()){
                                        mCurrLocationInfo = value.getResultData();
                                        getView().showLoaction(mCurrLocationInfo.getAddress());
                                    }else{
                                        getView().showSuccessToast(value.getMsg());
                                        getView().showLoaction(value.getMsg());
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    getView().showErrorToast(e.getMessage());
                                    getView().dismissProgressDialog();
                                }

                                @Override
                                public void onComplete() {
                                    getView().dismissProgressDialog();
                                }
                            });
                }).onDenied(data -> {
                    getView().showErrorToast(getView().getString(R.string.lack_of_permissions));
        }).start();
}

    public void trimData(int onClickViewId){
        String eventType = getView().etEventType.getText().toString().trim();
        String eventTitle = getView().etEventTitle.getText().toString().trim();
        String eventRemark = getView().etEventRemark.getText().toString().trim();
        int normal = getView().cbNormal.isChecked() ? 1 : 0;
        if(null == mCurrEventEntity)
            mCurrEventEntity = new EventEntity();
        mCurrEventEntity.setUserId(mCurrUser.getUserId());
        mCurrEventEntity.setReceiveUserId(mReceiverUser.getUserId());
        mCurrEventEntity.setDeptId(mCurrDeptEntitiy.getId());
        mCurrEventEntity.setTime(System.currentTimeMillis());
        mCurrEventEntity.setEventType(eventType);
        mCurrEventEntity.setEventTitle(eventTitle);
        mCurrEventEntity.setEventRemark(eventRemark);
        mCurrEventEntity.setNormal(normal);
        mCurrEventEntity.setLatitude(mCurrLocationInfo.getLatitude());
        mCurrEventEntity.setLongitude(mCurrLocationInfo.getLongitude());
        mCurrEventEntity.setAddress(mCurrLocationInfo.getAddress());
        List<String> filePaths = getView().mMediaFileAdapter.getData();
        mCurrEventEntity.setMediaUrls(filePaths);

        if(onClickViewId == R.id.btn_save){
            if(mCurrEventEntity.save()){
                getView().showSuccessToast(R.string.save_success);
                getView().finish();
            } else
                getView().showErrorToast(R.string.save_fail);
        }else if(onClickViewId == R.id.btn_upload){
            upLoadPatrolEvent(mCurrEventEntity);
        }
    }

    public void upLoadPatrolEvent(EventEntity eventEntity){
        CompressPhotoUtil.compressPhotos(getView(),eventEntity.getMediaUrls())
                .doOnSubscribe(disposable -> {
                    putDisposable(disposable);
                    LogUtils.i("1");
                }).doOnNext(files -> {
                    LogUtils.i("2");
                })
                .compose(RxSchedulers.io_main())
                .flatMap(files -> {
                    LogUtils.i("3");
                    return PatrolModel.getInstance().upLoadPatrolEvent(eventEntity,files);
                })
                .subscribe(new Observer<MyResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getView().showProgressDialog(R.string.data_uploading,d);
                    }

                    @Override
                    public void onNext(MyResponse value) {
                        getView().showSuccessToast(value.getMsg());

                        mCurrEventEntity.delete();
                        getView().setResult(Activity.RESULT_OK);
                        getView().finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showErrorToast(e.getMessage());
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        getView().dismissProgressDialog();
                    }
                });
    }
}
