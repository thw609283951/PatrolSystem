package com.supersit.common.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.PhoneUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrListener;
import com.supersit.common.R;
import com.supersit.common.R2;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.AlbumDirInfo;
import com.supersit.common.utils.ImageLoader;
import com.supersit.common.widget.recyclerview.GridSpacingItemDecoration;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class AlbumGridActivity extends ToolbarActivity {


    @BindView(R2.id.rv_album)
    RecyclerView rvAlbum;
    @BindView(R2.id.rl_operate)
    RelativeLayout rlOperate;
    @BindView(R2.id.btn_finish)
    Button btnFinish;

    private AlbumDirInfo mAlbumDirInfo;
    public ArrayList<String> selectedImages = new ArrayList<>();

    private int spanCount;

    public static void start(Activity context, AlbumDirInfo albumDirInfo, List<String> selectedImages) {
        Intent intent = new Intent(context, AlbumGridActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("albumDirInfo", albumDirInfo);
        bundle.putStringArrayList("selectedImages", (ArrayList<String>) selectedImages);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, 1);
    }


    @Override
    public void initData(Bundle bundle) {
        mAlbumDirInfo = bundle.getParcelable("albumDirInfo");

        List<String> tempSelectedImages = bundle.getStringArrayList("selectedImages");
        if (null != tempSelectedImages)
            selectedImages.addAll(tempSelectedImages);
        spanCount = PhoneUtils.isPhone() ? 4 : 6;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_album_grid;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(mAlbumDirInfo.getDirName());

        SlidrConfig config = new SlidrConfig.Builder().listener(new SlidrListener() {
            @Override
            public void onSlideStateChanged(int state) {

            }

            @Override
            public void onSlideChange(float percent) {

            }

            @Override
            public void onSlideOpened() {

            }

            @Override
            public void onSlideClosed() {
                back();
            }
        }).build();
        Slidr.attach(this, config);

        rvAlbum.setLayoutManager(new GridLayoutManager(mContext, spanCount));
        int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.spacing_l);
        rvAlbum.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacingInPixels, true));

        showSelectedCount();

        doBusiness();
    }

    public void doBusiness() {
        BaseQuickAdapter adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_album_grid, mAlbumDirInfo.getImageList()) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                ImageLoader.displayImage(mContext, item, (ImageView) helper.getView(R.id.imageView));
                if (selectedImages != null && selectedImages.contains(item)) {
                    helper.setChecked(R.id.checkBox, true);
                } else {
                    helper.setChecked(R.id.checkBox, false);
                }
            }
        };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String imgPath = (String) adapter.getItem(position);
                if (selectedImages != null && selectedImages.contains(imgPath)) {
                    selectedImages.remove(imgPath);
                } else {
                    selectedImages.add(imgPath);
                }
                adapter.notifyItemChanged(position);
                showSelectedCount();
            }
        });
        adapter.bindToRecyclerView(rvAlbum);
    }

    @OnClick(R2.id.btn_finish)
    public void onViewClicked() {
        complete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cancel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * 菜单项被点击时调用，也就是菜单项的监听方法。
         * 通过这几个方法，可以得知，对于Activity，同一时间只能显示和监听一个Menu 对象。 TODO Auto-generated
         * method stub
         */
        int i = item.getItemId();
        if (i == R.id.cancel) {
            cancel();

        } else if (i == android.R.id.home) {
            back();

        }

        return true;
    }

    public void showSelectedCount() {
        if (selectedImages.isEmpty()) {
            rlOperate.setVisibility(View.GONE);
        } else {
            rlOperate.setVisibility(View.VISIBLE);
            btnFinish.setText(String.format(mContext.getResources().getString(R.string.select_quantity), selectedImages.size()));
        }
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        back();
    }


    private void back() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedImages", selectedImages);
        intent.putExtras(bundle);
        setResult(RESULT_FIRST_USER, intent);
        finish();
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }


    private void complete() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedImages", selectedImages);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }



}
