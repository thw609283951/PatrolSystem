package com.supersit.common.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.supersit.common.R;
import com.supersit.common.R2;
import com.supersit.common.adapter.ImagePagerAdapter;
import com.supersit.common.base.ToolbarActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ShowMediasActivity extends ToolbarActivity {


    @BindView(R2.id.viewpager)
    ViewPager viewpager;
    @BindView(R2.id.tv_title)
    TextView tvTitle;

    private String mTitle;
    private List<String> mMediasUrls;
    private int mCurrentItem;

    public static void start(Context mContext, int currentItem, List<String> imageUrls) {
       start(mContext,null,currentItem,imageUrls);
    }

    public static void start(Context mContext, String title,int currentItem, List<String> imageUrls) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("currentItem", currentItem);
        bundle.putStringArrayList("medias", (ArrayList<String>) imageUrls);
        Intent intent = new Intent(mContext, ShowMediasActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    public static void start(Context mContext, String imageUrl) {
        Bundle bundle = new Bundle();
        List<String> imageUrls = new ArrayList<>();
        bundle.putStringArrayList("medias", (ArrayList<String>) imageUrls);
        Intent intent = new Intent(mContext, ShowMediasActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }


    @Override
    public void initData(Bundle bundle) {
        mTitle = bundle.getString("title", "");
        mCurrentItem = bundle.getInt("currentItem", 0);
        mMediasUrls = bundle.getStringArrayList("medias");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_images;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(mTitle);
        ImagePagerAdapter adapter = new ImagePagerAdapter(mContext, mMediasUrls);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(mCurrentItem);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}