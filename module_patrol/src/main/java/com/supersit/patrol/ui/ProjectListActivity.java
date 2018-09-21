package com.supersit.patrol.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.widget.recyclerview.SpaceItemDecoration;
import com.supersit.mvp.presenter.RequirePresenter;
import com.supersit.patrol.R;
import com.supersit.patrol.R2;
import com.supersit.patrol.adapter.ProjectListAdapter;
import com.supersit.patrol.presenter.ProjectListPresenter;


import java.util.List;

import butterknife.BindView;

@Route(path = ARouterConstant.ProjectListActivity)
@RequirePresenter(ProjectListPresenter.class)
public class ProjectListActivity extends ToolbarActivity<ProjectListPresenter> {

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ProjectListAdapter mAdapter;

    public static void start(Activity mContext) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(mContext, ProjectListActivity.class);
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
        setTitle(R.string.project_list);

        mSwipeRefreshLayout.setEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_m);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));

        getPresenter().getProjectList();
    }

    @Override
    public void onClickErrorLoadData(View v) {
        super.onClickErrorLoadData(v);
        getPresenter().getProjectList();
    }

    SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(null != mAdapter)
                mAdapter.getFilter().filter(newText);
            return true;
        }

    };
    MenuItemCompat.OnActionExpandListener mOnActionExpandListener = new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            if(null != mAdapter)
                mAdapter.getFilter().filter("");
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

    public void loadSuccess(List<DeptEntity> deptEntitys){
        if(null == deptEntitys || deptEntitys.isEmpty()){
            showEmpty();
        }else{
            showContent();
            mAdapter = new ProjectListAdapter(deptEntitys);
            mAdapter.setOnItemClickListener((adapter1, view1, position) -> {
                DeptEntity deptEntitiy = (DeptEntity) adapter1.getItem(position);
                ProjectInfoActivity.start((Activity) mContext,deptEntitiy);
            });
            mAdapter.bindToRecyclerView(recyclerView);
        }
    }

    public void loadError(){
        showError();
    }
}
