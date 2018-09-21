package com.supersit.main.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supersit.common.base.ToolbarActivity;
import com.supersit.main.R;
import com.supersit.main.R2;

import butterknife.BindView;


public class ShareApkActivity extends ToolbarActivity {



    public static void start(Context context){
        Intent intent=new Intent(context,ShareApkActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_share_apk;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.share_apk);
    }


    // singleTop , singleTask Activity 不重新创建时，回调此方法拦截 intent 信息
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
