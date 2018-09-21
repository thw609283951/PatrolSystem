/************************************************************
 *  * EaseMob CONFIDENTIAL 
 * __________________ 
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved. 
 *
 * NOTICE: All information contained herein is, and remains 
 * the property of EaseMob Technologies.
 * Dissemination of this information or reproduction of this material 
 * is strictly forbidden unless prior written permission is obtained
 * from EaseMob Technologies.
 */
package com.supersit.common.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.supersit.common.Constant;
import com.supersit.common.R;
import com.supersit.common.R2;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.utils.MyUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class RecorderVideoActivity extends ToolbarActivity implements SurfaceHolder.Callback, OnErrorListener,
        OnInfoListener {
    private static final String TAG = RecorderVideoActivity.class.getSimpleName();
    private final static String CLASS_LABEL = "RecorderVideoActivity";

    @BindView(R2.id.videoView)
    VideoView videoView;
    @BindView(R2.id.chronometer)
    Chronometer chronometer;
    @BindView(R2.id.iv_recorder_start)
    ImageView ivRecorderStart;
    @BindView(R2.id.iv_recorder_stop)
    ImageView ivRecorderStop;
    @BindView(R2.id.btn_switch_camera)
    ImageButton btnSwitchCamera;

    private PowerManager.WakeLock mWakeLock;
    private MediaRecorder mediaRecorder;
    String localPath = "";// path to save recorded video
    private Camera mCamera;
    private int previewWidth = 480;
    private int previewHeight = 480;

    private int frontCamera = 0; // 0 is back camera，1 is front camera
    Parameters cameraParameters = null;
    private SurfaceHolder mSurfaceHolder;
    int defaultVideoFrameRate = -1;

    @Override
    public void initData(Bundle bundle) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
        mWakeLock.acquire();
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_recorder;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.video_recorder);

        btnSwitchCamera.setVisibility(View.VISIBLE);

        mSurfaceHolder = videoView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @OnClick({R2.id.btn_switch_camera, R2.id.iv_recorder_start, R2.id.iv_recorder_stop})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.btn_switch_camera) {
            switchCamera();

        } else if (i == R.id.iv_recorder_start) {// start recording
            if (!startRecording())
                return;
            Toast.makeText(this, R.string.The_video_to_start, Toast.LENGTH_SHORT).show();
            btnSwitchCamera.setVisibility(View.INVISIBLE);
            ivRecorderStart.setVisibility(View.INVISIBLE);
            ivRecorderStart.setEnabled(false);
            ivRecorderStop.setVisibility(View.VISIBLE);
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();

        } else if (i == R.id.iv_recorder_stop) {
            ivRecorderStop.setEnabled(false);
            stopRecording();
            btnSwitchCamera.setVisibility(View.VISIBLE);
            chronometer.stop();
            ivRecorderStart.setVisibility(View.VISIBLE);
            ivRecorderStop.setVisibility(View.INVISIBLE);
            new AlertDialog.Builder(this)
                    .setMessage(R.string.whether_to_make)
                    .setPositiveButton(R.string.confirm,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    sendVideo(null);

                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (localPath != null) {
                                        File file = new File(localPath);
                                        if (file.exists())
                                            file.delete();
                                    }
                                    finish();

                                }
                            }).setCancelable(false).show();

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mWakeLock == null) {
            // keep screen on
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                    CLASS_LABEL);
            mWakeLock.acquire();
        }
    }

    @SuppressLint("NewApi")
    private boolean initCamera() {
        try {
            if (frontCamera == 0) {
                mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
            } else {
                mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
            }
            Parameters camParams = mCamera.getParameters();
            mCamera.lock();
            mSurfaceHolder = videoView.getHolder();
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mCamera.setDisplayOrientation(90);

        } catch (RuntimeException ex) {
            LogUtils.e("video", "init Camera fail " + ex.getMessage());
            return false;
        }
        return true;
    }

    private void handleSurfaceChanged() {
        if (mCamera == null) {
            finish();
            return;
        }
        boolean hasSupportRate = false;
        List<Integer> supportedPreviewFrameRates = mCamera.getParameters()
                .getSupportedPreviewFrameRates();
        if (supportedPreviewFrameRates != null
                && supportedPreviewFrameRates.size() > 0) {
            Collections.sort(supportedPreviewFrameRates);
            for (int i = 0; i < supportedPreviewFrameRates.size(); i++) {
                int supportRate = supportedPreviewFrameRates.get(i);

                if (supportRate == 15) {
                    hasSupportRate = true;
                }

            }
            if (hasSupportRate) {
                defaultVideoFrameRate = 15;
            } else {
                defaultVideoFrameRate = supportedPreviewFrameRates.get(0);
            }

        }

        // get all resolutions which camera provide
        List<Size> resolutionList =  com.supersit.common.utils.video.Utils.getResolutionList(mCamera);
        if (resolutionList != null && resolutionList.size() > 0) {
            Collections.sort(resolutionList, new com.supersit.common.utils.video.Utils.ResolutionComparator());
            Size previewSize = null;
            boolean hasSize = false;

            // use 60*480 if camera support
            for (int i = 0; i < resolutionList.size(); i++) {
                Size size = resolutionList.get(i);
                if (size != null && size.width == 640 && size.height == 480) {
                    previewSize = size;
                    previewWidth = previewSize.width;
                    previewHeight = previewSize.height;
                    hasSize = true;
                    break;
                }
            }
            // use medium resolution if camera don't support the above resolution
            if (!hasSize) {
                int mediumResolution = resolutionList.size() / 2;
                if (mediumResolution >= resolutionList.size())
                    mediumResolution = resolutionList.size() - 1;
                previewSize = resolutionList.get(mediumResolution);
                previewWidth = previewSize.width;
                previewHeight = previewSize.height;

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }

        releaseRecorder();
        releaseCamera();

        finish();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera == null) {
            if (!initCamera()) {
                showFailDialog();
                return;
            }

        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            handleSurfaceChanged();
        } catch (Exception e1) {
            LogUtils.e("video", "start preview fail " + e1.getMessage());
            showFailDialog();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        LogUtils.v("video", "surfaceDestroyed");
    }

    public boolean startRecording() {
        if (mediaRecorder == null) {
            if (!initRecorder())
                return false;
        }
        mediaRecorder.setOnInfoListener(this);
        mediaRecorder.setOnErrorListener(this);
        mediaRecorder.start();
        return true;
    }

    @SuppressLint("NewApi")
    private boolean initRecorder() {
        if (!SDCardUtils.isSDCardEnable()) {
            showNoSDCardDialog();
            return false;
        }

        if (mCamera == null) {
            if (!initCamera()) {
                showFailDialog();
                return false;
            }
        }
        videoView.setVisibility(View.VISIBLE);
        mCamera.stopPreview();
        mediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if (frontCamera == 1) {
            mediaRecorder.setOrientationHint(270);
        } else {
            mediaRecorder.setOrientationHint(90);
        }

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // set resolution, should be set after the format and encoder was set
        mediaRecorder.setVideoSize(previewWidth, previewHeight);
        mediaRecorder.setVideoEncodingBitRate(384 * 1024);
        // set frame rate, should be set after the format and encoder was set
        if (defaultVideoFrameRate != -1) {
            mediaRecorder.setVideoFrameRate(defaultVideoFrameRate);
        }
        // set the path for video file
        localPath = MyUtil.getNewFilePath(this, Constant.VEDIO_CODE);
        mediaRecorder.setOutputFile(localPath);
        mediaRecorder.setMaxDuration(30000);
        mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setOnInfoListener(null);
            try {
                mediaRecorder.stop();
            } catch (Exception e) {
                LogUtils.e("video", "stopRecording error:" + e.getMessage());
            }
        }
        releaseRecorder();

        if (mCamera != null) {
            mCamera.stopPreview();
            releaseCamera();
        }
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    protected void releaseCamera() {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
        }
    }

    @SuppressLint("NewApi")
    public void switchCamera() {

        if (mCamera == null) {
            return;
        }
        if (Camera.getNumberOfCameras() >= 2) {
            btnSwitchCamera.setEnabled(false);
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }

            switch (frontCamera) {
                case 0:
                    mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
                    frontCamera = 1;
                    break;
                case 1:
                    mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
                    frontCamera = 0;
                    break;
            }
            try {
                mCamera.lock();
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(videoView.getHolder());
                mCamera.startPreview();
            } catch (IOException e) {
                mCamera.release();
                mCamera = null;
            }
            btnSwitchCamera.setEnabled(true);

        }

    }

    MediaScannerConnection msc = null;
    ProgressDialog progressDialog = null;

    public void sendVideo(View view) {
        if (TextUtils.isEmpty(localPath)) {
            LogUtils.e("Recorder", "recorder fail please try again!");
            return;
        }
        if (msc == null)
            msc = new MediaScannerConnection(this,
                    new MediaScannerConnectionClient() {

                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            LogUtils.d(TAG, "scanner completed");
                            msc.disconnect();
                            progressDialog.dismiss();

//							setResult(RESULT_OK, getIntent().putExtra("uri", uri));

                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("uri", uri);
                            bundle.putString("path", path);
                            intent.putExtras(bundle);

                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onMediaScannerConnected() {
                            msc.scanFile(localPath, "video/*");
                        }
                    });


        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("processing...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
        msc.connect();

    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        LogUtils.v("video", "onInfo");
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            LogUtils.v("video", "max duration reached");
            stopRecording();
            btnSwitchCamera.setVisibility(View.VISIBLE);
            chronometer.stop();
            ivRecorderStart.setVisibility(View.VISIBLE);
            ivRecorderStop.setVisibility(View.INVISIBLE);
            chronometer.stop();
            if (localPath == null) {
                return;
            }
            String st3 = getResources().getString(R.string.whether_to_make);
            new AlertDialog.Builder(this)
                    .setMessage(st3)
                    .setPositiveButton(R.string.confirm, (arg0, arg1) -> {

                                arg0.dismiss();
                                sendVideo(null);

                            }).setNegativeButton(R.string.cancel, null)
                    .setCancelable(false).show();
        }

    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        LogUtils.e("video", "recording onError:");
        stopRecording();
        Toast.makeText(this,
                "Recording error has occurred. Stopping the recording",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();

        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }

    }

    public void back() {
        releaseRecorder();
        releaseCamera();
        finish();
    }


    @Override
    public void onBackPressed() {
        back();
    }

    private void showFailDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage(R.string.Open_the_equipment_failure)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();

                            }
                        }).setCancelable(false).show();

    }

    private void showNoSDCardDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage("No sd card!")
                .setPositiveButton(R.string.confirm, (dialog, which) -> finish())
                .setCancelable(false).show();
    }



}