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
package com.supersit.easeim.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.widget.EaseImageView;
import com.supersit.common.utils.ImageLoader;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupAdapter extends ArrayAdapter<EMGroup> {

	private LayoutInflater inflater;
	private String newGroup;

	public GroupAdapter(Context context, int res, List<EMGroup> groups) {
		super(context, res, groups);
		this.inflater = LayoutInflater.from(context);
		newGroup = context.getResources().getString(R.string.The_new_group_chat);
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return 0;
		} else {
			return 1;
		}
	}

	static class ViewHolder {
		@BindView(R2.id.iv_avatar)
		EaseImageView ivAvatar;
		@BindView(R2.id.tv_name) TextView tvName;
		@BindView(R2.id.tv_remark) TextView tvRemark;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.item_group, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		if (getItemViewType(position) == 0) {
			holder.ivAvatar.setImageResource(R.mipmap.ic_add_public_group);
			holder.tvName.setText(newGroup);

		} else {

			holder.ivAvatar.setImageResource(R.mipmap.ic_group);
			holder.tvName.setText(getItem(position - 1).getGroupName());
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return super.getCount() + 1;
	}

}
