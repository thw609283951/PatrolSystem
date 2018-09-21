package com.supersit.easeim.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.EasyUtils;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.easeim.R;

public class ChatActivity extends ToolbarActivity {

    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    public String toChatUsername;
    int chatType;

    public static void start(Activity context, String toChatUsername) {
        Intent intent = new Intent(context, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userId", toChatUsername);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void start(Activity context, String toChatUsername,int chatType) {
        Intent intent = new Intent(context, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userId", toChatUsername);
        bundle.putInt("chatType", chatType);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {
        toChatUsername = bundle.getString("userId");
        chatType = bundle.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        EaseUI.getInstance().pushActivity(this);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_chat;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(toChatUsername);
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            // set title
            if(EaseUserUtils.getUserInfo(toChatUsername) != null){
                EaseUser user = EaseUserUtils.getUserInfo(toChatUsername);
                if (user != null) {
                    setTitle(user.getNick());
                }
            }
        } else {
            //group chat
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group != null)
               setTitle(group.getGroupName());
        }
        chatFragment = new ChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container, chatFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // cancel the notification
        EaseUI.getInstance().getNotifier().reset();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(chatType == EaseConstant.CHATTYPE_SINGLE)
            getMenuInflater().inflate(R.menu.menu_remove, menu);
        else
            getMenuInflater().inflate(R.menu.menu_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * 菜单项被点击时调用，也就是菜单项的监听方法。
         * 通过这几个方法，可以得知，对于Activity，同一时间只能显示和监听一个Menu 对象。 TODO Auto-generated
         * method stub
         */
        if(null !=chatFragment){
            int i = item.getItemId();
            if (i == R.id.remove && null != chatFragment.chatFragmentHelper) {
                chatFragment.emptyHistory();
            } else if(i == R.id.users && null != chatFragment.chatFragmentHelper){
                chatFragment.toGroupDetails();
            } else if (i == android.R.id.home) {
                chatFragment.onBackPressed();
//                if (EasyUtils.isSingleActivity(this)) {
                    //发起路由跳转
                    ARouter.getInstance().build(ARouterConstant.MainActivity).navigation();
//                }
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
        EaseUI.getInstance().popActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getExtras().getString("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }


    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
//        if (EasyUtils.isSingleActivity(this)) {
            //发起路由跳转
            ARouter.getInstance().build(ARouterConstant.MainActivity).navigation();
//        }
    }
}
