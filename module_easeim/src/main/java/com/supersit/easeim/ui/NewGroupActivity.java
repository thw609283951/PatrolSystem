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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.model.UserModel;
import com.supersit.common.utils.ViewUtils;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewGroupActivity extends ToolbarActivity {

    @BindView(R2.id.et_group_name)
    EditText etGroupName;
    @BindView(R2.id.et_group_remark)
    EditText etGroupRemark;

    private ProgressDialog progressDialog;

    public static void start(Activity mContext) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(mContext, NewGroupActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_new_group;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        setTitle(R.string.new_group);
        etGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ViewUtils.hasEmptyOfEditText(etGroupName);
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
         * 通过这几个方法，可以得知，对于Activity，同一时间只能显示和监听一个Menu 对象。 TODO Auto-generated
         * method stub
         */
        int i = item.getItemId();
        if (i == R.id.confirm) {
            save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void save() {
        if (ViewUtils.hasEmptyOfEditText(etGroupName)) {
            return;
        }

        List<String> existMembers = new ArrayList<>();
        existMembers.add(UserModel.getInstance().getCurrUserName());
        GroupPickContactsActivity.start((Activity) mContext, 1, existMembers, UserModel.getInstance().getUsers());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        findViewById(R.id.ll_root).setFitsSystemWindows(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        final String st2 = getResources().getString(R.string.Failed_to_create_groups);
        if (resultCode == RESULT_OK) {
            //new group
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(st1);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String groupName = etGroupName.getText().toString().trim();
                    String desc = etGroupRemark.getText().toString();
                    String[] members = data.getStringArrayExtra("newmembers");
                    try {
                        EMGroupOptions option = new EMGroupOptions();
                        option.maxUsers = 200;
                        option.inviteNeedConfirm = false;

                        String reason = NewGroupActivity.this.getString(R.string.invite_join_group);
                        reason = EMClient.getInstance().getCurrentUser() + reason + groupName;
                        option.style = EMGroupStyle.EMGroupStylePublicOpenJoin;
                        EMClient.getInstance().groupManager().createGroup(groupName, desc, members, reason, option);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    } catch (final HyphenateException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            }).start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
