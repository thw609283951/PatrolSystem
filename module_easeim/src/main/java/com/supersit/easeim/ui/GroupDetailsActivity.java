package com.supersit.easeim.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.supersit.common.base.ToolbarActivity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.model.UserModel;
import com.supersit.common.widget.recyclerview.GridSpacingItemDecoration;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;
import com.supersit.easeim.adapter.GroupUserAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class GroupDetailsActivity extends ToolbarActivity {

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.tv_group_id)
    TextView tvGroupId;
    @BindView(R2.id.btn_un_group)
    Button btnUnGroup;
    private String mGroupId;
    private EMGroup mGroup;
    private List<String> mMembers;
    private GroupUserAdapter mAdapter;
    private boolean mIsMyGroup=false;


    private static final int REQUEST_ADD_USER_CODE= 1;
    private static final int REQUEST_DEL_USER_CODE= 2;

    public static void start(Activity mContext, String groupId) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(mContext, GroupDetailsActivity.class);
        bundle.putString("groupId", groupId);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void initData(Bundle bundle) {
        mGroupId = bundle.getString("groupId");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_group_details;
    }

    @Override
    public boolean isUseStatusPages() {
        return true;
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        mGroup = EMClient.getInstance().groupManager().getGroup(mGroupId);
        if (mGroup == null) {
            finish();
            return;
        }
        StringBuffer groupName = new StringBuffer(mGroup.getGroupName())
                .append("(").append(mGroup.getMemberCount()).append(")");
        setTitle(groupName.toString());
        tvGroupId.setText(mGroupId);

        // 如果自己是群主，显示解散按钮
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {
            mIsMyGroup=true;
            btnUnGroup.setVisibility(View.VISIBLE);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,5);
        recyclerView.setLayoutManager(gridLayoutManager);
        int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.spacing_m);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(5,spacingInPixels,false));


        // 保证每次进详情看到的都是最新的group
        updateGroup();
    }

    @OnClick({R2.id.rl_change_group_name, R2.id.rl_clear_chat_record, R2.id.btn_un_group})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.rl_change_group_name) {

        } else if (i == R.id.rl_clear_chat_record) {
            clearGroupHistory();
        } else if (i == R.id.btn_un_group) {
            deleteGrop();
        }
    }

    @Override
    public void onClickErrorLoadData(View v) {
        super.onClickErrorLoadData(v);
        updateGroup();
    }

    private void refreshMembers(){
        setTitle(mGroup.getGroupName() + "(" + mGroup.getMemberCount() + ")");
       mMembers = mGroup.getMembers();
        mMembers.add(mGroup.getOwner());
        if(mIsMyGroup){
            mMembers.add("1");
            mMembers.add("2");
        }
        if(null == mAdapter){
            mAdapter = new GroupUserAdapter(mMembers,mIsMyGroup);
            mAdapter.bindToRecyclerView(recyclerView);
            mAdapter.setOnItemClickListener((adapter, view, position) ->{
                if(mIsMyGroup){
                    if(position == adapter.getData().size()-2){
                        GroupPickContactsActivity.start((Activity) mContext,REQUEST_ADD_USER_CODE,mMembers, UserModel.getInstance().getUsers());
                        return;
                    }else if(position == adapter.getData().size()-1 ){
                        if(mMembers.size()>2){
                            List<UserEntity> userEntities = new ArrayList<>();
                            for (int i = 0; i < mMembers.size()-2; i++) {
                                userEntities.add(UserModel.getInstance().getUserByUserName(mMembers.get(i)));
                            }
                            GroupPickContactsActivity.start((Activity) mContext,REQUEST_DEL_USER_CODE,null, userEntities);
                        }
                        return;
                    }
                }
            });
        }else{
            mAdapter.setNewData(mMembers);
        }
        mAdapter.setNewData(mMembers);
    }

    /**
     * 清空群聊天记录
     */
    private void clearGroupHistory() {

        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mGroupId, EMConversation.EMConversationType.GroupChat);
        if (conversation != null) {
            conversation.clearAllMessages();
        }
        showInfoToast(R.string.messages_are_empty);
    }

    protected void updateGroup() {
        new Thread(() -> {
            try {
                mGroup=EMClient.getInstance().groupManager().getGroupFromServer(mGroupId,true);
                runOnUiThread(() -> {
                    showContent();
                    refreshMembers();
                });
            } catch (Exception e) {
                runOnUiThread(() -> showError());
            }
        }).start();
    }

    /**
     * 增加群成员
     *
     * @param newmembers
     */
    private void addMembersToGroup(final String[] newmembers) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.being_added));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Thread(() -> {
            try {
                // 创建者调用add方法
                EMClient.getInstance().groupManager().addUsersToGroup(mGroupId, newmembers);
                updateGroup();
            } catch (final Exception e) {
                runOnUiThread(() -> showErrorToast(e.getMessage()));
            }finally {
                runOnUiThread(() -> {
                    if(null !=progressDialog)
                        progressDialog.dismiss();
                });
            }
        }).start();
    }

    /**
     * 删除群成员
     *
     * @param newmembers
     */
    protected void deleteMembersFromGroup(final String[] newmembers) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.Are_removed));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Thread(() -> {
            try {
                if(null==newmembers) return;
                // 删除被选中的成员
                for (String newmember : newmembers) {
                    EMClient.getInstance().groupManager().removeUserFromGroup(mGroupId, newmember);
                }
                updateGroup();
            } catch (final Exception e) {
                runOnUiThread(() -> showErrorToast(e.getMessage()));
            }finally {
                runOnUiThread(() -> {
                    if(null !=progressDialog)
                        progressDialog.dismiss();
                });
            }

        }).start();
    }

    /**
     * 解散群组
     *
     */
    private void deleteGrop() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.chatting_is_dissolution));
        progressDialog.setCanceledOnTouchOutside(false);
        final String st5 = getResources().getString(R.string.Dissolve_group_chat_tofail);
        new Thread(() -> {
            try {
                EMClient.getInstance().groupManager().destroyGroup(mGroupId);
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                    if(ChatActivity.activityInstance != null)
                        ChatActivity.activityInstance.finish();
                });
            } catch (final Exception e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    runOnUiThread(() -> showErrorToast(e.getMessage()));
                });
            }
        }).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK != resultCode)
            return;
        switch (requestCode){
            case REQUEST_ADD_USER_CODE:
                final String[] newmembers = data.getStringArrayExtra("newmembers");
                addMembersToGroup(newmembers);
                break;

            case REQUEST_DEL_USER_CODE:
                final String[] newmembers1 = data.getStringArrayExtra("newmembers");
                deleteMembersFromGroup(newmembers1);
                break;
            default:
                break;
        }
    }
}
