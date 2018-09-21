package com.supersit.patrol.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.supersit.common.ARouterConstant;
import com.supersit.common.Constant;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.model.UserModel;
import com.supersit.common.widget.recyclerview.SpaceItemDecoration;
import com.supersit.mvp.presenter.RequirePresenter;
import com.supersit.patrol.R;
import com.supersit.patrol.R2;
import com.supersit.patrol.adapter.PatrolRecordAdapter;
import com.supersit.common.entity.EventEntity;
import com.supersit.patrol.presenter.PatrolRecorPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@RequirePresenter(PatrolRecorPresenter.class)
public class PatrolRecordActivity extends ToolbarActivity<PatrolRecorPresenter> {

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private PatrolRecordAdapter mAdapter;
    private long mLastTimeMillis=0;
    private String sQuery=null;
    private int PAGE_SIZE = 10;

    public int mForm = USER_CODE;
    public static final int USER_CODE=0;
    public static final int DEPT_CODE=1;
    public long mId;

    public static void start(Activity mContext) {
        start(mContext,USER_CODE, UserModel.getInstance().getCurrUserId());
    }

    public static void start(Activity mContext,int form,long id){
        Intent intent = new Intent(mContext, PatrolRecordActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("form",form);
        bundle.putLong("id",id);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {
        mForm = bundle.getInt("form");
        mId = bundle.getLong("id");
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
        setTitle(R.string.history_record);

        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_m);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        initAdapter();
        initRefreshLayout();
//        refresh();
        initData();
    }

    private void initData(){
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            refresh();
        });
        hideSoftKeyboard();
    }

    @Override
    public void onClickErrorLoadData(View v) {
        super.onClickErrorLoadData(v);
        initData();
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
        if(USER_CODE == mForm){
            getMenuInflater().inflate(R.menu.search_view, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_search_and_users, menu);
        }

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);//显示提交按钮
        searchView.setOnQueryTextListener(mOnQueryTextListener);
        MenuItemCompat.setOnActionExpandListener(menuItem, mOnActionExpandListener);
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
        if (i == R.id.users) {
            DeptEntity deptEntity = UserModel.getInstance().getDeptById(mId);
            if(null != deptEntity && null!=deptEntity.getUserEntitys() && !deptEntity.getUserEntitys().isEmpty()){
                Bundle bundle = new Bundle();
                bundle.putBoolean("isSelect",false);
                bundle.putParcelableArrayList("userEntitys", (ArrayList<? extends Parcelable>) deptEntity.getUserEntitys());
                Intent childIntent = new Intent(mContext,PatrolRecordActivity.class);
                bundle.putParcelable("childIntent",childIntent);
                ARouter.getInstance().build(ARouterConstant.ContactListActivity).with(bundle)
                        .navigation(mContext);
            }


            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initAdapter() {
        mAdapter = new PatrolRecordAdapter(null);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);

        mAdapter.bindToRecyclerView(recyclerView);
        mAdapter.setEmptyView(R.layout.base_empty_page);
        mAdapter.setOnItemClickListener((adapter, view1, position) -> {
            EventEntity eventEntity = (EventEntity) adapter.getItem(position);
            EventInfoActivity.start((Activity) mContext,eventEntity);
        });
        mAdapter.setOnLoadMoreListener(() -> loadMore());
    }

    private void initRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> refresh());
    }

    private void refresh() {
        mLastTimeMillis = System.currentTimeMillis();
        mAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        getPresenter().getPatrolRecord(true,mLastTimeMillis,sQuery);
    }

    private void loadMore() {
        if(null == mAdapter.getData() || mAdapter.getData().isEmpty()){
            mLastTimeMillis = System.currentTimeMillis();
        }else{
            mLastTimeMillis = mAdapter.getItem (mAdapter.getData().size()-1).getTime();
        }
        getPresenter().getPatrolRecord(false,mLastTimeMillis,sQuery);
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
            if(0 == size){
                showEmpty();
            }

            mAdapter.loadMoreEnd(isRefresh);
        } else {

            mAdapter.loadMoreComplete();
        }
    }
}
