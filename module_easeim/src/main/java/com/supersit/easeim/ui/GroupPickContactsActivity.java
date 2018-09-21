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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.widget.sidelist.Sidebar;
import com.supersit.easeim.R;
import com.supersit.easeim.adapter.PickContactAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupPickContactsActivity extends ToolbarActivity {
	/** if this is a new group */
	protected boolean isCreatingNewGroup;
	private PickContactAdapter mAdapter;
	/** members already in the group */
	private List<String> existMembers;
	private List<UserEntity> mUserEntities;

	public static void start(Activity mContext,int requestCode,List<String> existMembers,List<UserEntity> userEntities) {
		Bundle bundle = new Bundle();
		bundle.putStringArrayList("existMembers", (ArrayList<String>) existMembers);
		bundle.putParcelableArrayList("userEntities", (ArrayList<? extends Parcelable>) userEntities);
		Intent intent = new Intent(mContext, GroupPickContactsActivity.class);
		intent.putExtras(bundle);
		mContext.startActivityForResult(intent,requestCode);
	}


	@Override
	public void initData(Bundle bundle) {
		existMembers = bundle.getStringArrayList("existMembers");
		mUserEntities = bundle.getParcelableArrayList("userEntities");
		// sort the list
		if(null !=mUserEntities)
			Collections.sort(mUserEntities, (lhs, rhs) -> {
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

	@Override
	public int bindLayout() {
		return R.layout.activity_group_pick_contacts;
	}

	@Override
	public void initView(Bundle savedInstanceState, View view) {
		setTitle(R.string.select_contacts);
		ListView listView = (ListView) findViewById(R.id.list);
		mAdapter = new PickContactAdapter(this, existMembers, mUserEntities);
		listView.setAdapter(mAdapter);
		((Sidebar) findViewById(R.id.sidebar)).setListView(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
				checkBox.toggle();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_confirm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/**
		 * 菜单项被点击时调用，也就是菜单项的监听方法。
		 * 通过这几个方法，可以得知，对于Activity，同一时间只能显示和监听一个Menu 对象。
		 * method stub
		 */
		int i = item.getItemId();
		if (i == R.id.confirm) {
			save();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * save selected members
	 * 
	 */
	public void save() {
		List<String> var = getToBeAddMembers();
		setResult(RESULT_OK, new Intent().putExtra("newmembers", var.toArray(new String[var.size()])));
		finish();
	}

	/**
	 * get selected members
	 * 
	 * @return
	 */
	private List<String> getToBeAddMembers() {
		List<String> members = new ArrayList<String>();
		List<UserEntity> selectUsers= mAdapter.getSelectUsers();
		if(null != selectUsers && !selectUsers.isEmpty())
			for (UserEntity selectUser : selectUsers) {
				members.add(selectUser.getUserName());
			}
		return members;
	}



}
