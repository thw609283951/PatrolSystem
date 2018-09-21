package com.supersit.easeim.presenter;

import android.os.Handler;
import android.util.Pair;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.supersit.common.base.BasePresenter;
import com.supersit.easeim.ui.ConversationListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * Created by linlongxin on 2016/9/17.
 */

public class ConversationListPresenter extends BasePresenter<ConversationListFragment> {
    private static final String TAG = ConversationListPresenter.class.getSimpleName();
    private final static int MSG_REFRESH = 2;
    protected List<EMConversation> conversationList = new ArrayList<EMConversation>();

    @Override
    public void onCreate() {
        super.onCreate();
        conversationList.addAll(loadConversationList());
        getView().refresh(conversationList);

        EMClient.getInstance().addConnectionListener(connectionListener);
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    protected EMConnectionListener connectionListener = new EMConnectionListener() {

        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE || error == EMError.SERVER_SERVICE_RESTRICTED
                    || error == EMError.USER_KICKED_BY_CHANGE_PASSWORD || error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                getView().isConflict = true;
            } else {
                handler.sendEmptyMessage(0);
            }
        }

        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };

    public void refresh() {
        if(!handler.hasMessages(MSG_REFRESH)){
            handler.sendEmptyMessage(MSG_REFRESH);
        }
    }

    protected Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    getView().onConnectionDisconnected();
                    break;
                case 1:
                    getView().onConnectionConnected();
                    break;

                case MSG_REFRESH:
                {
                    conversationList.clear();
                    conversationList.addAll(loadConversationList());
                    getView().refresh(conversationList);
                    break;
                }
                default:
                    break;
            }
        }
    };


    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
//            refresh();
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {}

        @Override
        public void onMessageDelivered(List<EMMessage> message) {}

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            refresh();
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {}
    };


    /**
     * load conversation list
     *
     * @return
    +    */
    protected List<EMConversation> loadConversationList(){
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        EMClient.getInstance().removeConnectionListener(connectionListener);
    }
}
