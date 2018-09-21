package com.supersit.work.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.ui.WebViewActivity;
import com.supersit.work.R;
import com.supersit.work.WorkHelper;

public class NotificationActivity extends WebViewActivity {


    private int mId;

    public static void start(Activity mContext,int notifyId,String url) {

        Intent intent = new Intent(mContext, NotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", notifyId);
        bundle.putString("title", mContext.getString(R.string.notification));
        bundle.putString("url", url);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {
        super.initData(bundle);
        mId = bundle.getInt("id");
    }

    @Override
    protected void onResume() {
        super.onResume();
        WorkHelper.getInstance().getWorkNotify().resetNotifyById(mId);
    }
}
