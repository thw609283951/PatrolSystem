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
import com.supersit.work.WorkHelper;
import com.supersit.work.adapter.WorkEventRecordAdapter;
import com.supersit.work.presenter.WorkEventListPresenter;

import java.util.List;

import butterknife.BindView;

/**
 * Created by 田皓午 on 2018/4/21.
 */

@RequirePresenter(WorkEventListPresenter.class)
public class WorkEventListActivity extends ToolbarActivity<WorkEventListPresenter> {

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;


    public int mWorkEventStatus;

    private WorkEventRecordAdapter mAdapter;
    private long mLastTimeMillis=0;
    private String sQuery=null;
    private int PAGE_SIZE = 10;

    public static void start(Activity mContext, int workEventStauts) {
        Intent intent = new Intent(mContext, WorkEventListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("status", workEventStauts);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {
        mWorkEventStatus = bundle.getInt("status");
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

        int stringResId;
        switch (mWorkEventStatus){
            case 1:
                stringResId = R.string.wait_accept;
                break;
            case 2:
                stringResId = R.string.processing;
                break;
            case 3:
                stringResId = R.string.processed;
                break;
            default:
                stringResId = R.string.submited;
                break;
        }
        setTitle(stringResId);

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

    @Override
    protected void onResume() {
        super.onResume();
        if(1 == mWorkEventStatus){
            WorkHelper.getInstance().getWorkNotify().resetWorkEventNotify();
        }
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

    SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            sQuery = query;
            initData();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    MenuItemCompat.OnActionExpandListener mOnActionExpandListener = new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            sQuery=null;
            initData();
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);//显示提交按钮
        searchView.setOnQueryTextListener(mOnQueryTextListener);
        MenuItemCompat.setOnActionExpandListener(menuItem, mOnActionExpandListener);
        return true;
    }

    private void initAdapter() {
        mAdapter = new WorkEventRecordAdapter(null);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);

        mAdapter.bindToRecyclerView(recyclerView);
        mAdapter.setOnItemClickListener((adapter, view1, position) -> {
            EventEntity eventEntity = (EventEntity) adapter.getItem(position);
            WorkEventInfoActivity.start((Activity) mContext,eventEntity);
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

        getPresenter().getWorkEvents(true,mWorkEventStatus,mLastTimeMillis,sQuery);
    }

    private void loadMore() {
        if(null == mAdapter.getData() || mAdapter.getData().isEmpty()){
            mLastTimeMillis = System.currentTimeMillis();
        }else{
            mLastTimeMillis = mAdapter.getItem (mAdapter.getData().size()-1).getTime();
        }
        getPresenter().getWorkEvents(false,mWorkEventStatus,mLastTimeMillis,sQuery);
    }

    public void loadSuccess(boolean isRefresh, List<EventEntity> eventEntities){
        setData(isRefresh, eventEntities);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK != resultCode)
            return;
        switch (requestCode){
            case WorkEventInfoActivity.mRequestCode:
                initData();
                break;

            default:
                break;
        }
    }
}
