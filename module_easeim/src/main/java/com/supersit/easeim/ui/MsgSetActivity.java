package com.supersit.easeim.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;
import com.supersit.easeim.model.IMModel;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
@Route(path = ARouterConstant.MsgSetActivity)
public class MsgSetActivity extends ToolbarActivity {


    @BindView(R2.id.switch_notification)
    EaseSwitchButton switchNotification;
    @BindView(R2.id.textview1)
    TextView textview1;
    @BindView(R2.id.switch_sound)
    EaseSwitchButton switchSound;
    @BindView(R2.id.textview2)
    TextView textview2;
    @BindView(R2.id.switch_vibrate)
    EaseSwitchButton switchVibrate;
    @BindView(R2.id.switch_speaker)
    EaseSwitchButton switchSpeaker;
    @BindView(R2.id.rl_switch_notification)
    RelativeLayout rlSwitchNotification;
    @BindView(R2.id.rl_switch_sound)
    RelativeLayout rlSwitchSound;
    @BindView(R2.id.rl_switch_vibrate)
    RelativeLayout rlSwitchVibrate;
    @BindView(R2.id.rl_switch_speaker)
    RelativeLayout rlSwitchSpeaker;
    Unbinder unbinder;

    public static void start(Context context){
        Intent intent=new Intent(context,MsgSetActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_msg_set;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.messages_set);
        // the vibrate and sound notification are allowed or not?
        if (IMModel.getInstance().getSettingMsgNotification()) {
            switchNotification.openSwitch();
        } else {
            switchNotification.closeSwitch();
        }

        // sound notification is switched on or not?
        if (IMModel.getInstance().getSettingMsgSound()) {
            switchSound.openSwitch();
        } else {
            switchSound.closeSwitch();
        }

        // vibrate notification is switched on or not?
        if (IMModel.getInstance().getSettingMsgVibrate()) {
            switchVibrate.openSwitch();
        } else {
            switchVibrate.closeSwitch();
        }

        // the speaker is switched on or not?
        if (IMModel.getInstance().getSettingMsgSpeaker()) {
            switchSpeaker.openSwitch();
        } else {
            switchSpeaker.closeSwitch();
        }
    }


    @OnClick({R2.id.rl_switch_notification, R2.id.rl_switch_sound, R2.id.rl_switch_vibrate, R2.id.rl_switch_speaker})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.rl_switch_notification) {
            if (switchNotification.isSwitchOpen()) {
                switchNotification.closeSwitch();
                rlSwitchSound.setVisibility(View.GONE);
                rlSwitchVibrate.setVisibility(View.GONE);
                textview1.setVisibility(View.GONE);
                textview2.setVisibility(View.GONE);
                IMModel.getInstance().setSettingMsgNotification(false);
            } else {
                switchNotification.openSwitch();
                rlSwitchSound.setVisibility(View.VISIBLE);
                rlSwitchVibrate.setVisibility(View.VISIBLE);
                textview1.setVisibility(View.VISIBLE);
                textview2.setVisibility(View.VISIBLE);
                IMModel.getInstance().setSettingMsgNotification(true);
            }

        } else if (i == R.id.rl_switch_sound) {
            if (switchSound.isSwitchOpen()) {
                switchSound.closeSwitch();
                IMModel.getInstance().setSettingMsgSound(false);
            } else {
                switchSound.openSwitch();
                IMModel.getInstance().setSettingMsgSound(true);
            }

        } else if (i == R.id.rl_switch_vibrate) {
            if (switchVibrate.isSwitchOpen()) {
                switchVibrate.closeSwitch();
                IMModel.getInstance().setSettingMsgVibrate(false);
            } else {
                switchVibrate.openSwitch();
                IMModel.getInstance().setSettingMsgVibrate(true);
            }

        } else if (i == R.id.rl_switch_speaker) {
            if (switchSpeaker.isSwitchOpen()) {
                switchSpeaker.closeSwitch();
                IMModel.getInstance().setSettingMsgSpeaker(false);
            } else {
                switchSpeaker.openSwitch();
                IMModel.getInstance().setSettingMsgVibrate(true);
            }

        }
    }

}
