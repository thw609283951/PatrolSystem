package com.supersit.patrol.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.widget.recyclerview.SpaceItemDecoration;
import com.supersit.mvp.presenter.RequirePresenter;
import com.supersit.patrol.R;
import com.supersit.patrol.R2;
import com.supersit.patrol.adapter.PatrolRecordAdapter;
import com.supersit.common.entity.EventEntity;
import com.supersit.patrol.presenter.PatrolUploadItemsPresenter;

import java.util.List;

import butterknife.BindView;

/**
 * Created by 田皓午 on 2018/4/16.
 */
@RequirePresenter(PatrolUploadItemsPresenter.class)
public class PatrolUploadItemsActivity extends ToolbarActivity<PatrolUploadItemsPresenter> {

    public static final int mRequestCode = 1;

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    private PatrolRecordAdapter mAdapter;

    public static void start(Activity mContext) {
        Intent intent = new Intent(mContext, PatrolUploadItemsActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        mContext.startActivityForResult(intent,mRequestCode);
    }

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public boolean isUseStatusPages() {
        return true;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_recycler;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.wait_up_record);
        swipeLayout.setEnabled(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_m);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        initAdapter();
    }

    private void initAdapter() {
        List<EventEntity> eventEntitys = getPresenter().getEventEntity();
        if(null != eventEntitys && !eventEntitys.isEmpty()){
            showContent();
            mAdapter = new PatrolRecordAdapter(getPresenter().getEventEntity());
            mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
            mAdapter.bindToRecyclerView(recyclerView);
            mAdapter.setEmptyView(R.layout.base_empty_page);
            mAdapter.setOnItemClickListener((adapter, view1, position) -> {
                EventEntity eventEntity = (EventEntity) adapter.getItem(position);
                PatrolUploadActivity.start((Activity) mContext,eventEntity);
            });
        }else{
            showEmpty();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK != resultCode)
            return;
        if(null == getPresenter().getEventEntity() || getPresenter().getEventEntity().isEmpty())
            showEmpty();
        else{
            showContent();
            mAdapter.setNewData(getPresenter().getEventEntity());
        }
    }


}
