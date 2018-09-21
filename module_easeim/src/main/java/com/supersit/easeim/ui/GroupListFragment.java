/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.supersit.easeim.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.hyphenate.chat.EMGroup;
import com.supersit.common.base.BaseFragment;
import com.supersit.easeim.IMConstant;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;
import com.supersit.easeim.adapter.GroupAdapter;
import com.supersit.easeim.model.IMModel;
import com.supersit.easeim.presenter.GroupListPresenter;
import com.supersit.mvp.presenter.RequirePresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * contact list
 */
@RequirePresenter(GroupListPresenter.class)
public class GroupListFragment extends BaseFragment<GroupListPresenter> {
    public static final String TAG = GroupListFragment.class.getSimpleName();


    @BindView(R2.id.et_search)
    EditText etSearch;
    @BindView(R2.id.btn_search_clear)
    ImageButton btnSearchClear;
    @BindView(R2.id.lv_group)
    ListView lvGroup;

    private GroupAdapter mAdapter;

    @Override
    public void initData(Bundle bundle) {}

    @Override
    public int bindLayout() {
        return R.layout.fragment_group_list;
    }

    @Override
    protected boolean isUseStatusPages() {
        return true;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        lvGroup.setOnItemClickListener((parent, view1, position, id) -> {
            if (position == 0) {
                // create a new group
                NewGroupActivity.start(mContext);
            } else {
                // enter group chat
                ChatActivity.start(mContext,mAdapter.getItem(position - 1).getGroupId(), IMConstant.CHATTYPE_GROUP);
            }

        });

        etSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(null != mAdapter)
                    mAdapter.getFilter().filter(s);
                if (s.length() > 0) {
                    btnSearchClear.setVisibility(View.VISIBLE);
                } else {
                    btnSearchClear.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void afterTextChanged(Editable s) {}
        });

        if (IMModel.getInstance().isGroupsSynced()) {
            showContent();
        }
    }

    @OnClick(R2.id.btn_search_clear)
    public void onViewClicked() {
        etSearch.getText().clear();
        KeyboardUtils.hideSoftInput(mContext);
    }

    @Override
    public void onClickErrorLoadData(View v) {
        super.onClickErrorLoadData(v);
        getPresenter().refresh();

    }

    public void onSyncComplete(final boolean success) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    showContent();
                    getPresenter().refresh();
                } else {
                    showError();
                }
            }
        });
    }


    public void showGroupList(List<EMGroup> groups){
        mAdapter = new GroupAdapter(getContext(), 1, groups);
        lvGroup.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
//        if(isVisible)
//            getPresenter().refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().refresh();
    }

    @Override
    public void onFragmentFirstVisible() {
//        getPresenter().refresh();
    }


}