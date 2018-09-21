package com.supersit.main.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.utils.ActionUtil;
import com.supersit.main.R;
import com.supersit.main.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AboutActivity extends ToolbarActivity {


    @BindView(R2.id.logo)
    ImageView logo;
    @BindView(R2.id.tell_author)
    LinearLayout tellAuthor;
    @BindView(R2.id.feedback)
    TextView feedback;
    @BindView(R2.id.tv_phone_number)
    TextView tvPhoneNumber;
    @BindView(R2.id.main_activity_about)
    LinearLayout mainActivityAbout;

    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_about;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.about);
    }


    // singleTop , singleTask Activity 不重新创建时，回调此方法拦截 intent 信息
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @OnClick(R2.id.tv_phone_number)
    public void onViewClicked() {
        ActionUtil.call(mContext,tvPhoneNumber.getText().toString());
    }
}
