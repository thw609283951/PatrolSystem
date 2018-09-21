/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.supersit.easeim.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.KeyboardUtils;
import com.supersit.common.ARouterConstant;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.model.UserModel;
import com.supersit.common.widget.sidelist.SideListView;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;
import com.supersit.easeim.adapter.ContactAdapter;
import com.supersit.easeim.presenter.ContactListPresenter;
import com.supersit.mvp.presenter.RequirePresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


@Route(path = ARouterConstant.ContactListActivity)
public class ContactListActivity extends ToolbarActivity {
	@BindView(R2.id.contact_list)
	SideListView contactList;
	@BindView(R2.id.et_search)
	EditText etSearch;
	@BindView(R2.id.btn_search_clear)
	ImageButton btnSearchClear;
	@BindView(R2.id.ll_search)
	LinearLayout llSearch;

	private ListView listView;

	private boolean mIsSelect;
	private List<UserEntity> mUserEntity;

	private Intent mChildIntent=null;

	public static void start(Activity mContext) {
		start(mContext,false,null);
	}

	public static void start(Activity mContext,boolean isSelect) {
		start(mContext,isSelect,null);
	}

	public static void start(Activity mContext,boolean isSelect,List<UserEntity> userEntities) {
		start(mContext,isSelect,userEntities,null);
	}

	public static void start(Activity mContext,boolean isSelect,List<UserEntity> userEntities,Intent childIntent) {

		Intent intent = new Intent(mContext, ContactListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean("isSelect", isSelect);
		bundle.putParcelable("childIntent", childIntent);
		bundle.putParcelableArrayList("userEntitys", (ArrayList<? extends Parcelable>) userEntities);
		intent.putExtras(bundle);
		mContext.startActivity(intent);
	}

	@Override
	public void initData(Bundle bundle) {
		if(null != bundle){
			mIsSelect = bundle.getBoolean("isSelect");
			mUserEntity = bundle.getParcelableArrayList("userEntitys");
			mChildIntent = bundle.getParcelable("childIntent");
		}
	}

	@Override
	public int bindLayout() {
		return R.layout.activity_select_contact;
	}


	@Override
	public void initView(Bundle savedInstanceState, View view) {
		setTitle(R.string.users);

		listView = contactList.getListView();
		listView.setOnItemClickListener((parent, view1, position, id) -> {
			UserEntity user = (UserEntity) listView.getItemAtPosition(position);
			if(mIsSelect){
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putParcelable("user",user);
				intent.putExtras(bundle);
				setResult(RESULT_OK,intent);
				finish();
			}else{
				if(null != mChildIntent){
					Bundle bundle = new Bundle();
					bundle.putLong("id",user.getUserId());
					mChildIntent.putExtras(bundle);
					startActivity(mChildIntent);
				}else{
					UserProfileActivity.start((Activity) mContext,user.getUserName());
				}
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
		sortList();
		contactList.setAdapter(new ContactAdapter(mContext,mUserEntity));
	}


	@OnClick(R2.id.btn_search_clear)
	public void onViewClicked() {
		etSearch.getText().clear();
		hideSoftKeyboard();
	}


	protected void sortList() {
		if(null == mUserEntity)
			mUserEntity = UserModel.getInstance().getUsers();
		if(null ==mUserEntity || mUserEntity.isEmpty())
			return;
		// sorting
		Collections.sort(mUserEntity, (lhs, rhs) -> {
            if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                return lhs.getNickName().compareTo(rhs.getNickName());
            }else{
                if("#".equals(lhs.getInitialLetter())){
                    return 1;
                }else if("#".equals(rhs.getInitialLetter())){
                    return -1;
                }
                return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
            }
        });
	}

}
