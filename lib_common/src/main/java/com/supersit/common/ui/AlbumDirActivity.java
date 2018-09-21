package com.supersit.common.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.supersit.common.Constant;
import com.supersit.common.R;
import com.supersit.common.R2;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.AlbumDirInfo;
import com.supersit.common.utils.ImageLoader;
import com.supersit.common.utils.album.AlbumHelper;
import com.supersit.common.widget.recyclerview.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class AlbumDirActivity extends ToolbarActivity {


    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;

    public ArrayList<String> selectedImages;

    private List<AlbumDirInfo> dataList;
    private AlbumHelper helper;

    public static void start(Activity context, List<String> selectedImages){
        Intent intent=new Intent(context,AlbumDirActivity.class);
        Bundle bundle=new Bundle();
        bundle.putStringArrayList("selectedImages", (ArrayList<String>) selectedImages);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, Constant.CODE_MULTI_ALBUM_REQUEST);
    }

    @Override
    public void initData(Bundle bundle) {
        selectedImages = bundle.getStringArrayList("selectedImages");

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_album_bucket;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.album);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerDecoration(mContext, LinearLayoutManager.VERTICAL));
        doBusiness();
    }

    public void doBusiness() {
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        dataList = helper.getImagesBucketList(true);

        BaseQuickAdapter adapter = new BaseQuickAdapter<AlbumDirInfo,BaseViewHolder>(R.layout.item_album_bucket,dataList ) {
            @Override
            protected void convert(BaseViewHolder helper, final AlbumDirInfo item) {
                final AlbumDirInfo albumDirInfo = (AlbumDirInfo) item;
                helper.setText(R.id.tv_dir_name, albumDirInfo.getDirName());
                helper.setText(R.id.tv_count, String.valueOf(albumDirInfo.getCount()));
                ImageLoader.displayImage(mContext, albumDirInfo.getImageList().get(0), (ImageView) helper.getView(R.id.imageView));

            }
        };
        adapter.setOnItemClickListener((adapter1, view, position) -> AlbumGridActivity.start((Activity) mContext, (AlbumDirInfo) adapter1.getItem(position), selectedImages));

        adapter.bindToRecyclerView(recyclerView);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                setResult(Activity.RESULT_OK, data);
                finish();
                break;

            case Activity.RESULT_CANCELED:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;

            default:
                Bundle bundle = data.getExtras();
                ArrayList<String> dates = bundle.getStringArrayList("selectedImages");
                selectedImages = dates;
                break;
        }
    }
}
