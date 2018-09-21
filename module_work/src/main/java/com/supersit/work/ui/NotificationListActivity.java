package com.supersit.work.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.EventEntity;
import com.supersit.common.widget.recyclerview.SpaceItemDecoration;
import com.supersit.mvp.presenter.RequirePresenter;
import com.supersit.work.R;
import com.supersit.work.R2;
import com.supersit.work.adapter.NotifyAdapter;
import com.supersit.work.adapter.WorkEventRecordAdapter;
import com.supersit.work.entity.NotificationEntity;
import com.supersit.work.entity.WeatherEntity;
import com.supersit.work.presenter.NotificationListPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@RequirePresenter(NotificationListPresenter.class)
public class NotificationListActivity extends ToolbarActivity<NotificationListPresenter> {


    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private NotifyAdapter mAdapter;
    private long mLastTimeMillis=0;
    private int PAGE_SIZE = 10;

    public static void start(Activity mContext) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(mContext, NotificationListActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }


    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_recycler;
    }

    @Override
    public boolean isUseStatusPages() {
        return true;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.notification);

        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_m);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        initAdapter();
        initRefreshLayout();
        initData();
    }

    private void initData(){
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                refresh();
            }
        });
        hideSoftKeyboard();
    }


    private void initAdapter() {
        mAdapter = new NotifyAdapter(null);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);

        mAdapter.bindToRecyclerView(recyclerView);
        mAdapter.setOnItemClickListener((adapter, view1, position) -> {
            NotificationEntity entity = (NotificationEntity) adapter.getItem(position);
            if(null == entity.getDetailsUrl()){
                showInfoToast("详情页暂未开放,如需请联系管理员~~");
                NotificationActivity.start((Activity) mContext,entity.getId(),entity.getDetailsUrl());
            }

        });
        mAdapter.setOnLoadMoreListener(() -> loadMore());
    }

    @Override
    public void onClickErrorLoadData(View v) {
        super.onClickErrorLoadData(v);
        initData();
    }

    private void initRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> refresh());
    }

    private void refresh() {
        mLastTimeMillis = System.currentTimeMillis();
        mAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        getPresenter().getNotifications(true,mLastTimeMillis);
    }

    private void loadMore() {
        if(null == mAdapter.getData() || mAdapter.getData().isEmpty()){
            mLastTimeMillis = System.currentTimeMillis();
        }else{
            mLastTimeMillis = mAdapter.getItem (mAdapter.getData().size()-1).getTime();
        }
        getPresenter().getNotifications(false,mLastTimeMillis);
    }

    public void loadSuccess(boolean isRefresh, List<NotificationEntity> entities){
        setData(isRefresh, entities);
        if(isRefresh){
            mAdapter.setEnableLoadMore(true);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void loadError(boolean isRefresh){
        if(isRefresh){
            showError();
            mAdapter.setEnableLoadMore(true);
            mSwipeRefreshLayout.setRefreshing(false);
        }else{
            showErrorToast(R.string.load_fail);
            mAdapter.loadMoreFail();
        }
    }

    private void setData(boolean isRefresh, List data) {
        showContent();
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            if(0 == size)
                showEmpty();
            mAdapter.setNewData(data);
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            }
        }
        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            mAdapter.loadMoreEnd(isRefresh);
        } else {
            mAdapter.loadMoreComplete();
        }
    }
}
