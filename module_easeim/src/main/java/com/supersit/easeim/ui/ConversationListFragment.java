package com.supersit.easeim.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.KeyboardUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.hyphenate.util.NetUtils;
import com.supersit.common.Constant;
import com.supersit.common.base.BaseFragment;
import com.supersit.easeim.IMConstant;
import com.supersit.easeim.R;
import com.supersit.easeim.R2;
import com.supersit.common.entity.RxBusEntity;
import com.supersit.easeim.presenter.ConversationListPresenter;
import com.supersit.mvp.presenter.RequirePresenter;
import com.threshold.rxbus2.RxBus;

import java.util.List;

import butterknife.BindView;

@RequirePresenter(ConversationListPresenter.class)
public class ConversationListFragment extends BaseFragment<ConversationListPresenter> {
    public static final String TAG = ConversationListFragment.class.getSimpleName();

    @BindView(R2.id.fl_error_item)
    FrameLayout flErrorItem;
    @BindView(R2.id.list)
    EaseConversationList conversationListView;
    @BindView(R2.id.et_search)
    EditText query;
    @BindView(R2.id.btn_search_clear)
    ImageButton searchClear;

    private TextView errorText;

    public boolean isConflict;

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_conversation_list;
    }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            if (savedInstanceState != null && savedInstanceState.getBoolean(IMConstant.IS_CONFLICT, false))
                return;
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if (isConflict) {
                outState.putBoolean(IMConstant.IS_CONFLICT, true);
            }
        }

    @Override
    public void initView(Bundle savedInstanceState, View view) {
        View errorView = View.inflate(mContext, R.layout.view_chat_neterror, null);
        flErrorItem.addView(errorView);
        errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);

        conversationListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardUtils.hideSoftInput(mContext);
                return false;
            }
        });
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                conversationListView.filter(s);
                if (s.length() > 0) {
                    searchClear.setVisibility(View.VISIBLE);
                } else {
                    searchClear.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                KeyboardUtils.hideSoftInput(mContext);
            }
        });
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                String username = conversation.conversationId();
                if (username.equals(EMClient.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                else {
                    if (conversation.isGroup()) {
                        ChatActivity.start(mContext,username, IMConstant.CHATTYPE_GROUP);
                    }else{
                        ChatActivity.start(mContext,username);
                    }

                }
            }
        });
    }


    public void refresh(List<EMConversation> emConversations) {
        conversationListView.init(emConversations);
        IMFragment imFragment = (IMFragment) ConversationListFragment.this.getParentFragment();
        if(null != imFragment)
            imFragment.updateUnreadLabel();
        RxBus.getDefault().post(new RxBusEntity(Constant.RXBUS_UPIM_UNREAD_COUNT,null));
    }

    /**
     * connected to server
     */
    public void onConnectionConnected() {
        flErrorItem.setVisibility(View.GONE);
    }

    /**
     * disconnected with server
     */
    public void onConnectionDisconnected() {
        flErrorItem.setVisibility(View.VISIBLE);
        if (NetUtils.hasNetwork(mContext)) {
            errorText.setText(R.string.can_not_connect_chat_server_connection);
        } else {
            errorText.setText(R.string.the_current_network);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        mContext.getMenuInflater().inflate(R.menu.menu_delete_message, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean deleteMessage = false;
        if (item.getItemId() == R.id.delete_message) {
            deleteMessage = true;
        } else if (item.getItemId() == R.id.delete_conversation) {
            deleteMessage = false;
        }
        EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
        if (tobeDeleteCons == null) {
            return true;
        }
        if (tobeDeleteCons.getType() == EMConversation.EMConversationType.GroupChat) {
            EaseAtMessageHelper.get().removeAtMeGroup(tobeDeleteCons.conversationId());
        }
        try {
            // delete conversation
            EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.conversationId(), deleteMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getPresenter().refresh();

        return true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {   // 不在最前端显示 相当于调用了onPause();

        }else{  // 在最前端显示 相当于调用了onResume();
            getPresenter().refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().refresh();
    }

    @Override
    public void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void onFragmentFirstVisible() {

    }
}
