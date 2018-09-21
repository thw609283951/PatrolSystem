/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.supersit.easeim.ui;

import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMCallManager.EMCameraDataProcessor;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMVideoCallHelper;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMCallSurfaceView;
import com.hyphenate.util.EMLog;
import com.superrtc.sdk.VideoView;
import com.supersit.common.Constant;
import com.supersit.common.ui.RecorderVideoActivity;
import com.supersit.common.widget.MyChronometer;
import com.supersit.easeim.IMHelper;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


public class VideoCallActivity extends CallActivity implements OnClickListener {

    @BindView(R2.id.opposite_surface)
    EMCallSurfaceView oppositeSurface;
    @BindView(R2.id.local_surface)
    EMCallSurfaceView localSurface;
    @BindView(R2.id.layout_surface_container)
    RelativeLayout layoutSurfaceContainer;
    @BindView(R2.id.tv_call_state)
    TextView tvCallState;
    @BindView(R2.id.tv_nick)
    TextView tvNick;
    @BindView(R2.id.chronometer)
    MyChronometer chronometer;
    @BindView(R2.id.btn_switch_camera)
    ImageButton btnSwitchCamera;
    @BindView(R2.id.btn_capture_image)
    ImageButton btnCaptureImage;
    @BindView(R2.id.ll_utils)
    LinearLayout llUtils;
    @BindView(R2.id.rl_top_container)
    RelativeLayout rlTopContainer;
    @BindView(R2.id.seekbar_y_detal)
    SeekBar seekbarYDetal;
    @BindView(R2.id.tv_call_monitor)
    TextView tvCallMonitor;
    @BindView(R2.id.iv_mute)
    ImageView ivMute;
    @BindView(R2.id.iv_handsfree)
    ImageView ivHandsfree;
    @BindView(R2.id.ll_voice_control)
    LinearLayout llVoiceControl;
    @BindView(R2.id.btn_hangup_call)
    Button btnHangupCall;
    @BindView(R2.id.btn_refuse_call)
    Button btnRefuseCall;
    @BindView(R2.id.btn_answer_call)
    Button btnAnswerCall;
    @BindView(R2.id.ll_coming_call)
    LinearLayout llComingCall;
    @BindView(R2.id.ll_bottom_container)
    LinearLayout llBottomContainer;
    @BindView(R2.id.ll_btns)
    RelativeLayout llBtns;
    @BindView(R2.id.tv_network_status)
    TextView tvNetworkStatus;
    @BindView(R2.id.root_layout)
    RelativeLayout rootLayout;
    private boolean isMuteState;
    private boolean isHandsfreeState;
    private boolean isAnswered;
    private boolean endCallTriggerByMe = false;
    private boolean monitor = true;
    private int surfaceState = -1;

    private Handler uiHandler;

    private boolean isInCalling;
    boolean isRecording = false;
    private EMVideoCallHelper callHelper;

    private BrightnessDataProcess dataProcessor = new BrightnessDataProcess();

    // dynamic adjust brightness
    class BrightnessDataProcess implements EMCameraDataProcessor {
        byte yDelta = 0;

        synchronized void setYDelta(byte yDelta) {
            Log.d("VideoCallActivity", "brigntness uDelta:" + yDelta);
            this.yDelta = yDelta;
        }

        // data size is width*height*2
        // the first width*height is Y, second part is UV
        // the storage layout detailed please refer 2.x demo CameraHelper.onPreviewFrame
        @Override
        public synchronized void onProcessData(byte[] data, Camera camera, final int width, final int height, final int rotateAngel) {
            int wh = width * height;
            for (int i = 0; i < wh; i++) {
                int d = (data[i] & 0xFF) + yDelta;
                d = d < 16 ? 16 : d;
                d = d > 235 ? 235 : d;
                data[i] = (byte) d;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_video_call);
        ButterKnife.bind(this);
        IMHelper.getInstance().isVideoCalling = true;
        callType = 1;

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        uiHandler = new Handler();


        btnRefuseCall.setOnClickListener(this);
        btnAnswerCall.setOnClickListener(this);
        btnHangupCall.setOnClickListener(this);
        ivMute.setOnClickListener(this);
        ivHandsfree.setOnClickListener(this);
        rootLayout.setOnClickListener(this);
        findViewById(R.id.btn_switch_camera).setOnClickListener(this);
        findViewById(R.id.btn_capture_image).setOnClickListener(this);

        seekbarYDetal.setOnSeekBarChangeListener(new YDeltaSeekBarListener());

        msgid = UUID.randomUUID().toString();
        isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
        username = getIntent().getStringExtra("username");

        tvNick.setText(username);

        // local surfaceview
        localSurface.setOnClickListener(this);
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);

        // remote surfaceview

        // set call state listener
        addCallStateListener();
        if (!isInComingCall) {// outgoing call
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.em_outgoing, 1);

            llUtils.setVisibility(View. VISIBLE);
            llComingCall.setVisibility(View.INVISIBLE);
            btnHangupCall.setVisibility(View.VISIBLE);
            String st = getResources().getString(R.string.Are_connected_to_each_other);
            tvCallState.setText(st);
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
            handler.sendEmptyMessage(MSG_CALL_MAKE_VIDEO);
            handler.postDelayed(new Runnable() {
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);
        } else { // incoming call

            tvCallState.setText("Ringing");
            if (EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.IDLE
                    || EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.DISCONNECTED) {
                // the call has ended
                finish();
                return;
            }
            llVoiceControl.setVisibility(View.INVISIBLE);
            localSurface.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }

        final int MAKE_CALL_TIMEOUT = 50 * 1000;
        handler.removeCallbacks(timeoutHangup);
        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);

        // get instance of call helper, should be called after setSurfaceView was called
        callHelper = EMClient.getInstance().callManager().getVideoCallHelper();

        EMClient.getInstance().callManager().setCameraDataProcessor(dataProcessor);
    }

    class YDeltaSeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            dataProcessor.setYDelta((byte) (20.0f * (progress - 50) / 50.0f));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    /**
     * 切换通话界面，这里就是交换本地和远端画面控件设置，以达到通话大小画面的切换
     */
    private void changeCallView() {
        if (surfaceState == 0) {
            surfaceState = 1;
            EMClient.getInstance().callManager().setSurfaceView(oppositeSurface, localSurface);
        } else {
            surfaceState = 0;
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }
    }

    /**
     * set call state listener
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(final CallState callState, final CallError error) {
                switch (callState) {

                    case CONNECTING: // is connecting
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                tvCallState.setText(R.string.Are_connected_to_each_other);
                            }

                        });
                        break;
                    case CONNECTED: // connected
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
//                            tvCallState.setText(R.string.have_connected_with);
                            }

                        });
                        break;

                    case ACCEPTED: // call is accepted
                        surfaceState = 0;
                        handler.removeCallbacks(timeoutHangup);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    if (soundPool != null)
                                        soundPool.stop(streamID);
                                    EMLog.d("EMCallManager", "soundPool stop ACCEPTED");
                                } catch (Exception e) {
                                }
                                openSpeakerOn();
//                            ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
//                                    ? R.string.direct_call : R.string.relay_call);
                                ivHandsfree.setImageResource(R.mipmap.em_icon_speaker_on);
                                isHandsfreeState = true;
                                isInCalling = true;
                                chronometer.setVisibility(View.VISIBLE);
                                chronometer.setBase(SystemClock.elapsedRealtime());
                                // call durations start
                                chronometer.start();
                                tvNick.setVisibility(View.VISIBLE);
                                tvCallState.setText(R.string.In_the_call);
                                callingState = CallingState.NORMAL;
                                startMonitor();
                            }

                        });
                        break;
                    case NETWORK_DISCONNECTED:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                tvNetworkStatus.setVisibility(View.VISIBLE);
                                tvNetworkStatus.setText(R.string.network_unavailable);
                            }
                        });
                        break;
                    case NETWORK_UNSTABLE:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                tvNetworkStatus.setVisibility(View.VISIBLE);
                                if (error == CallError.ERROR_NO_DATA) {
                                    tvNetworkStatus.setText(R.string.no_call_data);
                                } else {
                                    tvNetworkStatus.setText(R.string.network_unstable);
                                }
                            }
                        });
                        break;
                    case NETWORK_NORMAL:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                tvNetworkStatus.setVisibility(View.INVISIBLE);
                            }
                        });
                        break;
                    case VIDEO_PAUSE:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VIDEO_PAUSE", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case VIDEO_RESUME:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VIDEO_RESUME", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case VOICE_PAUSE:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VOICE_PAUSE", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case VOICE_RESUME:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "VOICE_RESUME", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case DISCONNECTED: // call is disconnected
                        handler.removeCallbacks(timeoutHangup);
                        @SuppressWarnings("UnnecessaryLocalVariable") final CallError fError = error;
                        runOnUiThread(new Runnable() {
                            private void postDelayedCloseMsg() {
                                uiHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        removeCallStateListener();
                                        saveCallRecord();
                                        Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                        animation.setDuration(1200);
                                        rootLayout.startAnimation(animation);
                                        finish();
                                    }

                                }, 200);
                            }

                            @Override
                            public void run() {
                                chronometer.stop();
                                callDruationText = chronometer.getText().toString();
                                String s1 = getResources().getString(R.string.The_other_party_refused_to_accept);
                                String s2 = getResources().getString(R.string.Connection_failure);
                                String s3 = getResources().getString(R.string.The_other_party_is_not_online);
                                String s4 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                                String s5 = getResources().getString(R.string.The_other_party_did_not_answer);

                                String s6 = getResources().getString(R.string.hang_up);
                                String s7 = getResources().getString(R.string.The_other_is_hang_up);
                                String s8 = getResources().getString(R.string.did_not_answer);
                                String s9 = getResources().getString(R.string.Has_been_cancelled);
                                String s10 = getResources().getString(R.string.Refused);

                                if (fError == CallError.REJECTED) {
                                    callingState = CallingState.BEREFUSED;
                                    tvCallState.setText(s1);
                                } else if (fError == CallError.ERROR_TRANSPORT) {
                                    tvCallState.setText(s2);
                                } else if (fError == CallError.ERROR_UNAVAILABLE) {
                                    callingState = CallingState.OFFLINE;
                                    tvCallState.setText(s3);
                                } else if (fError == CallError.ERROR_BUSY) {
                                    callingState = CallingState.BUSY;
                                    tvCallState.setText(s4);
                                } else if (fError == CallError.ERROR_NORESPONSE) {
                                    callingState = CallingState.NO_RESPONSE;
                                    tvCallState.setText(s5);
                                } else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
                                    callingState = CallingState.VERSION_NOT_SAME;
                                    tvCallState.setText(R.string.call_version_inconsistent);
                                } else {
                                    if (isRefused) {
                                        callingState = CallingState.REFUSED;
                                        tvCallState.setText(s10);
                                    } else if (isAnswered) {
                                        callingState = CallingState.NORMAL;
                                        if (endCallTriggerByMe) {
//                                        tvCallState.setText(s6);
                                        } else {
                                            tvCallState.setText(s7);
                                        }
                                    } else {
                                        if (isInComingCall) {
                                            callingState = CallingState.UNANSWERED;
                                            tvCallState.setText(s8);
                                        } else {
                                            if (callingState != CallingState.NORMAL) {
                                                callingState = CallingState.CANCELLED;
                                                tvCallState.setText(s9);
                                            } else {
                                                tvCallState.setText(s6);
                                            }
                                        }
                                    }
                                }
                                Toast.makeText(VideoCallActivity.this, tvCallState.getText(), Toast.LENGTH_SHORT).show();
                                postDelayedCloseMsg();
                            }

                        });

                        break;

                    default:
                        break;
                }

            }
        };
        EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }

    void removeCallStateListener() {
        EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.local_surface) {
            changeCallView();

        } else if (i == R.id.btn_refuse_call) {
            isRefused = true;
            btnRefuseCall.setEnabled(false);
            handler.sendEmptyMessage(MSG_CALL_REJECT);

        } else if (i == R.id.btn_answer_call) {
            AndPermission.with(this)
                    .runtime()
                    .permission(
                            Permission.Group.CAMERA,
                            Permission.Group.MICROPHONE,
                            Permission.Group.STORAGE)
                    .onGranted(data -> {
                        EMLog.d(TAG, "btn_answer_call clicked");
                        btnAnswerCall.setEnabled(false);
                        openSpeakerOn();
                        if (ringtone != null)
                            ringtone.stop();

                        tvCallState.setText("answering...");
                        handler.sendEmptyMessage(MSG_CALL_ANSWER);
                        ivHandsfree.setImageResource(R.mipmap.em_icon_speaker_on);
                        isAnswered = true;
                        isHandsfreeState = true;
                        llUtils.setVisibility(View.VISIBLE);
                        llComingCall.setVisibility(View.INVISIBLE);
                        btnHangupCall.setVisibility(View.VISIBLE);
                        llVoiceControl.setVisibility(View.VISIBLE);
                        localSurface.setVisibility(View.VISIBLE);

                    }).onDenied(data -> {
                Toasty.error(this,this.getString(com.supersit.common.R.string.lack_of_permissions)).show();
            }).start();


        } else if (i == R.id.btn_hangup_call) {
            btnHangupCall.setEnabled(false);
            chronometer.stop();
            endCallTriggerByMe = true;
            tvCallState.setText(getResources().getString(R.string.hanging_up));
            if (isRecording) {
                callHelper.stopVideoRecord();
            }
            EMLog.d(TAG, "btn_hangup_call");
            handler.sendEmptyMessage(MSG_CALL_END);

        } else if (i == R.id.iv_mute) {
            if (isMuteState) {
                // resume voice transfer
                ivMute.setImageResource(R.mipmap.em_icon_mute_normal);
                try {
                    EMClient.getInstance().callManager().resumeVoiceTransfer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                isMuteState = false;
            } else {
                // pause voice transfer
                ivMute.setImageResource(R.mipmap.em_icon_mute_on);
                try {
                    EMClient.getInstance().callManager().pauseVoiceTransfer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                isMuteState = true;
            }

        } else if (i == R.id.iv_handsfree) {
            if (isHandsfreeState) {
                // turn off speaker
                ivHandsfree.setImageResource(R.mipmap.em_icon_speaker_normal);
                closeSpeakerOn();
                isHandsfreeState = false;
            } else {
                ivHandsfree.setImageResource(R.mipmap.em_icon_speaker_on);
                openSpeakerOn();
                isHandsfreeState = true;
            }

        } else if (i == R.id.root_layout) {
            if (callingState == CallingState.NORMAL) {
                if (llBottomContainer.getVisibility() == View.VISIBLE) {
                    llBottomContainer.setVisibility(View.GONE);
                    rlTopContainer.setVisibility(View.GONE);
                    oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);

                } else {
                    llBottomContainer.setVisibility(View.VISIBLE);
                    rlTopContainer.setVisibility(View.VISIBLE);
                    oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
                }
            }

        } else if (i == R.id.btn_switch_camera) {
            handler.sendEmptyMessage(MSG_CALL_SWITCH_CAMERA);

        } else if (i == R.id.btn_capture_image) {
            DateFormat df = new DateFormat();
            Date d = new Date();
            File storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            final String filename = storage.getAbsolutePath() + "/" + df.format("MM-dd-yy--h-mm-ss", d) + ".jpg";
            EMClient.getInstance().callManager().getVideoCallHelper().takePicture(filename);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VideoCallActivity.this, "saved image to:" + filename, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
        }
    }

    @Override
    protected void onDestroy() {
        IMHelper.getInstance().isVideoCalling = false;
        stopMonitor();
        if (isRecording) {
            callHelper.stopVideoRecord();
            isRecording = false;
        }
        localSurface.getRenderer().dispose();
        localSurface = null;
        oppositeSurface.getRenderer().dispose();
        oppositeSurface = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        callDruationText = chronometer.getText().toString();
        super.onBackPressed();
    }

    /**
     * for debug & testing, you can remove this when release
     */
    void startMonitor() {
        monitor = true;
       /* new Thread(new Runnable() {
            public void run() {
                while(monitor){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
                                    ? R.string.direct_call : R.string.relay_call);
                        }
                    });
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }, "CallMonitor").start();*/
    }

    void stopMonitor() {
        monitor = false;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (isInCalling) {
            try {
                EMClient.getInstance().callManager().pauseVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isInCalling) {
            try {
                EMClient.getInstance().callManager().resumeVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

}
