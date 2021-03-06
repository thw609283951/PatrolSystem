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

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.supersit.common.base.BaseActivity;
import com.supersit.easeim.IMConstant;
import com.supersit.easeim.R;

public class ContextMenuActivity extends BaseActivity {
    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_RECALL = 4;

	@Override
	public void initData(Bundle bundle) {

	}

	@Override
	public int bindLayout() {
		EMMessage message = getIntent().getParcelableExtra("message");

		int layoutRes=0;
		int type = message.getType().ordinal();
		if (type == EMMessage.Type.TXT.ordinal()) {
			if(message.getBooleanAttribute(IMConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)
					|| message.getBooleanAttribute(IMConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
				layoutRes = R.layout.context_menu_for_common;
			}else if(message.getBooleanAttribute(IMConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
				layoutRes = R.layout.context_menu_for_common;
			}else{
				layoutRes = R.layout.context_menu_for_text;
			}
		} else {
			layoutRes = R.layout.context_menu_for_common;
		}
		return layoutRes;
	}

	@Override
	public void initView(Bundle savedInstanceState, View view) {

	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void copy(View view){
		setResult(RESULT_CODE_COPY);
		finish();
	}
	public void delete(View view){
		setResult(RESULT_CODE_DELETE);
		finish();
	}
	public void forward(View view){
		setResult(RESULT_CODE_FORWARD);
		finish();
	}
	public void recall(View view){
		setResult(RESULT_CODE_RECALL);
		finish();
	}

}
