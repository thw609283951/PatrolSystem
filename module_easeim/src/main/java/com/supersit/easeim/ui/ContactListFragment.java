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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseContactList;
import com.supersit.common.base.BaseFragment;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.widget.sidelist.SideListView;
import com.supersit.easeim.IMHelper;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;
import com.supersit.easeim.adapter.ContactAdapter;
import com.supersit.easeim.model.IMModel;
import com.supersit.easeim.presenter.ContactListPresenter;
import com.supersit.mvp.presenter.RequirePresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * contact list
 */
@RequirePresenter(ContactListPresenter.class)
public class ContactListFragment extends BaseFragment<ContactListPresenter> {
    public static final String TAG = ContactListFragment.class.getSimpleName();


    @BindView(R2.id.contact_list)
    SideListView contactList;
    @BindView(R2.id.et_search)
    EditText etSearch;
    @BindView(R2.id.btn_search_clear)
    ImageButton btnSearchClear;
    @BindView(R2.id.ll_search)
    LinearLayout llSearch;

    private ListView listView;

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_contact_list;
    }

    @Override
    protected boolean isUseStatusPages() {
        return true;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        listView = contactList.getListView();
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            UserEntity user = (UserEntity) listView.getItemAtPosition(position);
            if (user != null) {
                String username = user.getUserName();
                UserProfileActivity.start(mContext,username);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactList.filter(s);
                if (s.length() > 0) {
                    btnSearchClear.setVisibility(View.VISIBLE);
                } else {
                    btnSearchClear.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }


    @OnClick(R2.id.btn_search_clear)
    public void onViewClicked() {
        etSearch.getText().clear();
        KeyboardUtils.hideSoftInput(mContext);
    }

    public void setListData(List<UserEntity> contacts) {
        if(null == contacts || contacts.isEmpty()){
            showEmpty();
        }else{
            showContent();
            if(null == listView.getAdapter()){
                contactList.setAdapter(new ContactAdapter(getContext(),contacts));
            }else{
                contactList.refresh(contacts);
            }
        }
    }


    @Override
    public void onFragmentVisibleChange(boolean isVisible) {
    }

    @Override
    public void onFragmentFirstVisible() {
        if(IMModel.getInstance().isContactSynced()){
            getPresenter().refresh();
        }else{
            showError();
        }
    }

    @Override
    public void onClickErrorLoadData(View v) {
        super.onClickErrorLoadData(v);
        IMHelper.getInstance().asyncFetchContactsFromServer();
    }
}