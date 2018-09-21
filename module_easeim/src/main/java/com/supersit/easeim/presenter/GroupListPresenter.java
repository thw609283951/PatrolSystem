package com.supersit.easeim.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.util.EMLog;
import com.supersit.common.base.BasePresenter;
import com.supersit.common.interfaces.DataSyncListener;
import com.supersit.easeim.IMConstant;
import com.supersit.easeim.IMHelper;
import com.supersit.easeim.ui.GroupListFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by linlongxin on 2016/9/17.
 */

public class GroupListPresenter extends BasePresenter<GroupListFragment> {
    private static final String TAG = GroupListPresenter.class.getSimpleName();

    private GroupSyncListener groupSyncListener;
    List<EMGroup> mGroups;

    @Override
    public void onCreate() {
        super.onCreate();
        groupSyncListener = new GroupSyncListener();
        IMHelper.getInstance().addSyncGroupListener(groupSyncListener);


        registerGroupChangeReceiver();
    }
    public void refresh(){
        initGroups();
        getView().showGroupList(mGroups);
    }

    public void initGroups() {
        if(null == mGroups)
            mGroups = new ArrayList<>();
        else
            mGroups.clear();
        List<EMGroup> tempGroup = EMClient.getInstance().groupManager().getAllGroups();
        if(null == tempGroup)
            return;
        mGroups.addAll(tempGroup);
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (groupSyncListener != null) {
            IMHelper.getInstance().removeSyncGroupListener(groupSyncListener);
            groupSyncListener = null;
        }

    }

    class GroupSyncListener implements DataSyncListener {
        @Override
        public void onSyncComplete(final boolean success) {
            EMLog.d(TAG, "on contact list sync success:" + success);
            getView().onSyncComplete(success);
        }
    }

    void registerGroupChangeReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMConstant.ACTION_GROUP_CHANAGED);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(IMConstant.ACTION_GROUP_CHANAGED)){
                    if (getView().isVisible()) {
                        refresh();
                    }
                }
            }
        };
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getView().getContext());
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }
}
