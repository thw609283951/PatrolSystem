package com.supersit.easeim;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMMessage.Status;
import com.hyphenate.chat.EMMessage.Type;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;

import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.EaseUI.EaseEmojiconInfoProvider;
import com.hyphenate.easeui.EaseUI.EaseSettingsProvider;
import com.hyphenate.easeui.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.model.EaseNotifier.EaseNotificationInfoProvider;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
//import com.raizlabs.android.dbflow.config.IMGeneratedDatabaseHolder;
import com.supersit.common.ARouterConstant;
import com.supersit.common.Constant;
import com.supersit.common.base.BaseApplication;
import com.supersit.common.base.BaseHelper;
import com.supersit.common.entity.RxBusDataSyncEntity;
import com.supersit.common.entity.RxBusEntity;
import com.supersit.common.interfaces.DataSyncListener;
import com.supersit.common.interfaces.LoginStatusObserver;
import com.supersit.common.model.UserModel;
import com.supersit.common.net.RxSchedulers;
import com.supersit.common.net.model.MyResponse;
import com.supersit.easeim.entity.EmojiconExampleGroupData;
import com.supersit.easeim.model.IMModel;
import com.supersit.easeim.receiver.CallReceiver;
import com.supersit.easeim.ui.ChatActivity;
import com.supersit.easeim.ui.VideoCallActivity;
import com.supersit.easeim.ui.VoiceCallActivity;
import com.threshold.rxbus2.RxBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class IMHelper extends BaseHelper{

	protected static final String TAG = IMHelper.class.getSimpleName();
	

	private EaseUI easeUI;

	/**
	 * EMEventListener
	 */
	protected EMMessageListener messageListener = null;

	/**
	 * sync groups status listener
	 */
	private List<DataSyncListener> syncGroupsListeners;

	private boolean isSyncingGroupsWithServer = false;
	private boolean isSyncingContactsWithServer = false;

	public boolean isVoiceCalling;
	public boolean isVideoCalling;



	private CallReceiver callReceiver;

	private LocalBroadcastManager broadcastManager;

	private boolean isGroupAndContactListenerRegisted;

	public IMHelper() {
	}
	public static IMHelper getInstance() {
		return getInstance(IMHelper.class);
	}

	@Override
	public void init() {
		EMOptions options = initChatOptions();
		//use default options if options is null
		if (EaseUI.getInstance().init(mContext, options)) {

			//debug mode, you'd better set it to false, if you want release your App officially.
			EMClient.getInstance().setDebugMode(AppUtils.isAppDebug());
			//get easeui instance
			easeUI = EaseUI.getInstance();
			//to set user's profile and avatar
			setEaseUIProviders();
			//set Call options
			setGlobalListeners();
			broadcastManager = LocalBroadcastManager.getInstance(mContext);
		}
	}

	@Override
	protected void onSyncDataAfter() {
		isSyncingGroupsWithServer = false;
		isSyncingContactsWithServer = false;
	}

	@Override
	protected void checkSyncData() {
		if(IMModel.getInstance().isGroupsSynced() && IMModel.getInstance().isContactSynced()){
			LogUtils.d(TAG, "already synced with servre");
		}else{
			if(!IMModel.getInstance().isGroupsSynced()){
				asyncFetchGroupsFromServer(null);
			}
			if(!IMModel.getInstance().isContactSynced()){
				asyncFetchContactsFromServer();
			}
		}
	}

	@Override
	protected void resetSyncData() {
		isGroupAndContactListenerRegisted = false;
		IMModel.getInstance().reset();
	}

	private EMOptions initChatOptions(){
		Log.d(TAG, "init HuanXin Options");

		EMOptions options = new EMOptions();
		// set if accept the invitation automatically
		options.setAcceptInvitationAlways(false);
		// set if you need read ack
		options.setRequireAck(true);
		// set if you need delivery ack
		options.setRequireDeliveryAck(false);

		/**
		 * NOTE:你需要设置自己申请的Sender ID来使用Google推送功能，详见集成文档
		 */
//		options.setFCMNumber("921300338324");
		//you need apply & set your own id if you want to use Mi push notification
//		options.setMipushConfig("2882303761517426801", "5381742660801");

		options.allowChatroomOwnerLeave(true);
		options.setDeleteMessagesAsExitGroup(true);
		options.setAutoAcceptGroupInvitation(true);
		// Whether the message attachment is automatically uploaded to the Hyphenate server,
		options.setAutoTransferMessageAttachments(true);
		// Set Whether auto download thumbnail, default value is true.
		options.setAutoDownloadThumbnail(true);
		return options;
	}


	protected void setEaseUIProviders() {
		//set user avatar to circle shape
		EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
		avatarOptions.setAvatarShape(1);
		easeUI.setAvatarOptions(avatarOptions);

		// set profile provider if you want easeUI to handle avatar and nickname
		easeUI.setUserProfileProvider(new EaseUserProfileProvider() {

			@Override
			public EaseUser getUser(String username) {
				return IMModel.getInstance().getEaseUserByUserName(username);
			}
		});

		//set options
		easeUI.setSettingsProvider(new EaseSettingsProvider() {

			@Override
			public boolean isSpeakerOpened() {
				return IMModel.getInstance().getSettingMsgSpeaker();
			}

			@Override
			public boolean isMsgVibrateAllowed(EMMessage message) {
				return IMModel.getInstance().getSettingMsgVibrate();
			}

			@Override
			public boolean isMsgSoundAllowed(EMMessage message) {
				return IMModel.getInstance().getSettingMsgSound();
			}

			@Override
			public boolean isMsgNotifyAllowed(EMMessage message) {
				if(message == null){
					return IMModel.getInstance().getSettingMsgNotification();
				}
				if(!IMModel.getInstance().getSettingMsgNotification()){
					return false;
				}else{
					return true;
				}
			}
		});
		//set emoji icon provider
		easeUI.setEmojiconInfoProvider(new EaseEmojiconInfoProvider() {

			@Override
			public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
				EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
				for(EaseEmojicon emojicon : data.getEmojiconList()){
					if(emojicon.getIdentityCode().equals(emojiconIdentityCode)){
						return emojicon;
					}
				}
				return null;
			}

			@Override
			public Map<String, Object> getTextEmojiconMapping() {
				return null;
			}
		});

		//set notification options, will use default if you don't set it
		easeUI.getNotifier().setNotificationInfoProvider(new EaseNotificationInfoProvider() {

			@Override
			public String getTitle(EMMessage message) {
				//you can update title here
				return null;
			}

			@Override
			public int getSmallIcon(EMMessage message) {
				//you can update icon here
				return 0;
			}

			@Override
			public String getDisplayedText(EMMessage message) {
				// be used on notification bar, different text according the message type.
				String ticker = EaseCommonUtils.getMessageDigest(message, mContext);
				if(message.getType() == Type.TXT){
					ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
				}
				EaseUser user = IMModel.getInstance().getEaseUserByUserName(message.getFrom());
				if(user != null){
					if(EaseAtMessageHelper.get().isAtMeMsg(message)){
						return String.format(mContext.getString(R.string.at_your_in_group), user.getNickname());
					}
					return user.getNickname() + ": " + ticker;
				}else{
					if(EaseAtMessageHelper.get().isAtMeMsg(message)){
						return String.format(mContext.getString(R.string.at_your_in_group), message.getFrom());
					}
					return message.getFrom() + ": " + ticker;
				}
			}

			@Override
			public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
				// here you can customize the text.
				// return fromUsersNum + "contacts send " + messageNum + "messages to you";
				return null;
			}

			@Override
			public Intent getLaunchIntent(EMMessage message) {
				// you can set what activity you want display when user click the notification
				Intent intent = new Intent(mContext, ChatActivity.class);
				// open calling activity if there is call
				if(isVideoCalling){
					intent = new Intent(mContext, VideoCallActivity.class);
				}else if(isVoiceCalling){
					intent = new Intent(mContext, VoiceCallActivity.class);
				}else{
					Bundle bundle = new Bundle();
					ChatType chatType = message.getChatType();
					if (chatType == ChatType.Chat) { // single chat message
						bundle.putString("userId", message.getFrom());
						bundle.putInt("chatType", IMConstant.CHATTYPE_SINGLE);
					} else { // group chat message
						// message.getTo() is the group id
						bundle.putString("userId", message.getTo());
						bundle.putInt("chatType", IMConstant.CHATTYPE_GROUP);
					}
					intent.putExtras(bundle);
				}
				return intent;
			}
		});
	}

	EMConnectionListener connectionListener;
	/**
	 * set global listener
	 */
	public void setGlobalListeners(){
		syncGroupsListeners = new ArrayList<>();


		// create the global connection listener
		connectionListener = new EMConnectionListener() {
			@Override
			public void onDisconnected(int error) {
				EMLog.d("global listener", "onDisconnect" + error);
				if (error == EMError.USER_REMOVED) {
					onUserException(IMConstant.ACCOUNT_REMOVED);
				} else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
					onUserException(IMConstant.ACCOUNT_CONFLICT);
				} else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
					onUserException(IMConstant.ACCOUNT_FORBIDDEN);
				} else if (error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
					onUserException(IMConstant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD);
				} else if (error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
					onUserException(IMConstant.ACCOUNT_KICKED_BY_OTHER_DEVICE);
				}
			}

			@Override
			public void onConnected() {
				LogUtils.d("IHelperListener onConnected");
				BaseApplication.getInstance().noitifyLoginObserver();
			}
		};

		IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
		if(callReceiver == null){
			callReceiver = new CallReceiver();
		}
		//register incoming call receiver
		mContext.registerReceiver(callReceiver, callFilter);
		//register connection listener
		EMClient.getInstance().addConnectionListener(connectionListener);
		//register group and contact event listener
		registerGroupAndContactListener();
		//register message event listener
		registerMessageListener();

	}

	/**
	 * register group and contact listener, you need register when login
	 */
	public void registerGroupAndContactListener(){
		if(!isGroupAndContactListenerRegisted){
			EMClient.getInstance().groupManager().addGroupChangeListener(new MyGroupChangeListener());
			EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
			isGroupAndContactListenerRegisted = true;
		}
	}

	/**
	 * group change listener
	 */
	class MyGroupChangeListener implements EMGroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
			broadcastManager.sendBroadcast(new Intent(IMConstant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onInvitationAccepted(String groupId, String invitee, String reason) {

			//user accept your invitation
			boolean hasGroup = false;
			EMGroup _group = null;
			for (EMGroup group : EMClient.getInstance().groupManager().getAllGroups()) {
				if (group.getGroupId().equals(groupId)) {
					hasGroup = true;
					_group = group;
					break;
				}
			}
			if (!hasGroup)
				return;
			broadcastManager.sendBroadcast(new Intent(IMConstant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {
			//user declined your invitation
			EMGroup group = null;
			for (EMGroup _group : EMClient.getInstance().groupManager().getAllGroups()) {
				if (_group.getGroupId().equals(groupId)) {
					group = _group;
					break;
				}
			}
			if (group == null)
			broadcastManager.sendBroadcast(new Intent(IMConstant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			//user is removed from group
			broadcastManager.sendBroadcast(new Intent(IMConstant.ACTION_GROUP_CHANAGED));
			showToast("current user removed, groupId:" + groupId);
		}

		@Override
		public void onGroupDestroyed(String groupId, String groupName) {
			// group is dismissed,
			broadcastManager.sendBroadcast(new Intent(IMConstant.ACTION_GROUP_CHANAGED));
			showToast("group destroyed, groupId:" + groupId);
		}

		@Override
		public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) {

			broadcastManager.sendBroadcast(new Intent(IMConstant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

			String st4 = mContext.getString(R.string.Agreed_to_your_group_chat_application);
			// your application was accepted
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new EMTextMessageBody(accepter + " " +st4));
			msg.setStatus(Status.SUCCESS);
			// save accept message
			EMClient.getInstance().chatManager().saveMessage(msg);
			// notify the accept message
			getNotifier().vibrateAndPlayTone(msg);

			showToast("request to join accepted, groupId:" + groupId);
			broadcastManager.sendBroadcast(new Intent(IMConstant.ACTION_GROUP_CHANAGED));
		}

		@Override
		public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
			// your application was declined, we do nothing here in demo
			showToast("request to join declined, groupId:" + groupId);
		}

		@Override
		public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
			// got an invitation
			String st3 = mContext.getString(R.string.Invite_you_to_join_a_group_chat);
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(inviter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new EMTextMessageBody(inviter + " " +st3));
			msg.setStatus(EMMessage.Status.SUCCESS);
			// save invitation as messages
			EMClient.getInstance().chatManager().saveMessage(msg);
			// notify invitation message
			getNotifier().vibrateAndPlayTone(msg);
			showToast("auto accept invitation from groupId:" + groupId);
			broadcastManager.sendBroadcast(new Intent(IMConstant.ACTION_GROUP_CHANAGED));
		}

		// ============================= group_reform new add api begin
		@Override
		public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
			StringBuilder sb = new StringBuilder();
			for (String member : mutes) {
				sb.append(member).append(",");
			}
			showToast("onMuterListAdded: " + sb.toString());
		}


		@Override
		public void onMuteListRemoved(String groupId, final List<String> mutes) {
			StringBuilder sb = new StringBuilder();
			for (String member : mutes) {
				sb.append(member).append(",");
			}
			showToast("onMuterListRemoved: " + sb.toString());
		}


		@Override
		public void onAdminAdded(String groupId, String administrator) {
			showToast("onAdminAdded: " + administrator);
		}

		@Override
		public void onAdminRemoved(String groupId, String administrator) {
			showToast("onAdminRemoved: " + administrator);
		}

		@Override
		public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
			showToast("onOwnerChanged new:" + newOwner + " old:" + oldOwner);
		}

		@Override
		public void onMemberJoined(String groupId, String member) {
			showToast("onMemberJoined: " + member);
		}

		@Override
		public void onMemberExited(String groupId, String member) {
			showToast("onMemberExited: " + member);
		}

		@Override
		public void onAnnouncementChanged(String groupId, String announcement) {
			showToast("onAnnouncementChanged, groupId" + groupId);
		}

		@Override
		public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {
			showToast("onSharedFileAdded, groupId" + groupId);
		}

		@Override
		public void onSharedFileDeleted(String groupId, String fileId) {
			showToast("onSharedFileDeleted, groupId" + groupId);

		}
		// ============================= group_reform new add api end
	}

	void showToast(final String message) {
		Log.d(TAG, "receive invitation to join the group：" + message);

//		ToastUtils.showShort(message);
	}


	/***
	 * 好友变化listener
	 *
	 */
	public class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(String username) {

			//broadcastManager.sendBroadcast(new Intent(IMConstant.ACTION_CONTACT_CHANAGED));
			showToast("onContactAdded:" + username);
		}

		@Override
		public void onContactDeleted(String username) {


			EMClient.getInstance().chatManager().deleteConversation(username, false);
			//broadcastManager.sendBroadcast(new Intent(IMConstant.ACTION_CONTACT_CHANAGED));
			showToast("onContactDeleted:" + username);
		}

		@Override
		public void onContactInvited(String username, String reason) {
			//broadcastManager.sendBroadcast(new Intent(IMConstant.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onFriendRequestAccepted(String username) {
			//broadcastManager.sendBroadcast(new Intent(IMConstant.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onFriendRequestDeclined(String username) {
			// your request was refused
			showToast(username + " refused to be your friend");
		}
	}

	/**
	 * user met some exception: conflict, removed or forbidden
	 */
	protected void onUserException(String exception){
		EMLog.e(TAG, "onUserException: " + exception);
		ARouter.getInstance()
				.build(ARouterConstant.MainActivity)
				.withBoolean( exception , true)  //参数：键：key 值：123
				.withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
				.navigation();
//		showToast(exception);
	}

	/**
	 * Global listener
	 * If this event already handled by an activity, you don't need handle it again
	 * activityList.size() <= 0 means all activities already in background or not in Activity Stack
	 */
	protected void registerMessageListener() {
		messageListener = new EMMessageListener() {
			private BroadcastReceiver broadCastReceiver = null;

			@Override
			public void onMessageReceived(List<EMMessage> messages) {
				for (EMMessage message : messages) {
					EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
					// in background, do not refresh UI, notify it in notification bar
					if(easeUI.hasForegroundActivies() && easeUI.getTopActivity() instanceof  ChatActivity){
						ChatActivity chatActivity = (ChatActivity) easeUI.getTopActivity();
						String toChatUsername =  chatActivity.toChatUsername;
						if(toChatUsername.equals(message.getFrom()))
							return;
					}
					getNotifier().onNewMsg(message);
				}
			}

			@Override
			public void onCmdMessageReceived(List<EMMessage> messages) {
				for (EMMessage message : messages) {
					EMLog.d(TAG, "receive command message");
					//get message body
					EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
					final String action = cmdMsgBody.action();//获取自定义action
					//获取扩展属性 此处省略
					//maybe you need get extension of your message
					//message.getStringAttribute("");
					CmdMessageManage.sendCmdMsg(action);
//					getNotifier().vibrateAndPlayTone(null);
					EMLog.d(TAG, String.format("Command：action:%s,message:%s", action,message.toString()));
				}
			}

			@Override
			public void onMessageRead(List<EMMessage> messages) {
			}

			@Override
			public void onMessageDelivered(List<EMMessage> message) {
			}

			@Override
			public void onMessageRecalled(List<EMMessage> messages) {
				for (EMMessage msg : messages) {
					if(msg.getChatType() == ChatType.GroupChat && EaseAtMessageHelper.get().isAtMeMsg(msg)){
						EaseAtMessageHelper.get().removeAtMeGroup(msg.getTo());
					}
					EMMessage msgNotification = EMMessage.createReceiveMessage(Type.TXT);
					EMTextMessageBody txtBody = new EMTextMessageBody(String.format(mContext.getString(R.string.msg_recall_by_user), msg.getFrom()));
					msgNotification.addBody(txtBody);
					msgNotification.setFrom(msg.getFrom());
					msgNotification.setTo(msg.getTo());
					msgNotification.setUnread(false);
					msgNotification.setMsgTime(msg.getMsgTime());
					msgNotification.setLocalTime(msg.getMsgTime());
					msgNotification.setChatType(msg.getChatType());
					msgNotification.setAttribute(IMConstant.MESSAGE_TYPE_RECALL, true);
					msgNotification.setStatus(Status.SUCCESS);
					EMClient.getInstance().chatManager().saveMessage(msgNotification);
				}
			}

			@Override
			public void onMessageChanged(EMMessage message, Object change) {
				EMLog.d(TAG, "change:");
				EMLog.d(TAG, "change:" + change);
			}
		};

		EMClient.getInstance().chatManager().addMessageListener(messageListener);
	}

	/**
	 * if ever logged in
	 *
	 * @return
	 */
	public boolean isLoggedIn() {
		return EMClient.getInstance().isLoggedInBefore();
	}


	public void logout(){
		endCall();
		EMClient.getInstance().logout(true);
		reset();
	}
	/**
	 * logout
	 *
	 * @param unbindDeviceToken
	 *            whether you need unbind your device token
	 * @param callback
	 *            callback
	 */
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		endCall();
		Log.d(TAG, "logout: " + unbindDeviceToken);
		EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

			@Override
			public void onSuccess() {
				Log.d(TAG, "logout: onSuccess");
				reset();
				if (callback != null) {
					callback.onSuccess();
				}

			}

			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

			@Override
			public void onError(int code, String error) {
				Log.d(TAG, "logout: onSuccess");
				reset();
				if (callback != null) {
					callback.onError(code, error);
				}
			}
		});
	}

	/**
	 * get instance of EaseNotifier
	 * @return
	 */
	public EaseNotifier getNotifier(){
		return easeUI.getNotifier();
	}


	void endCall() {
		try {
			EMClient.getInstance().callManager().endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addSyncGroupListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (!syncGroupsListeners.contains(listener)) {
			syncGroupsListeners.add(listener);
		}
	}

	public void removeSyncGroupListener(DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (syncGroupsListeners.contains(listener)) {
			syncGroupsListeners.remove(listener);
		}
	}
	/**
	 * Get group list from server
	 * This method will save the sync state
	 * @throws HyphenateException
	 */
	public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback){
		if(isSyncingGroupsWithServer){
			return;
		}

		isSyncingGroupsWithServer = true;

		new Thread(){
			@Override
			public void run(){
				try {
					List<EMGroup> groups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

					// in case that logout already before server returns, we should return immediately
					if(!isLoggedIn()){
						return;
					}

					IMModel.getInstance().setGroupsSynced(true);
					isSyncingGroupsWithServer = false;

					//notify sync group list success
					noitifyGroupSyncListeners(true);

					if(callback != null){
						callback.onSuccess();
					}
				} catch (HyphenateException e) {
					IMModel.getInstance().setGroupsSynced(false);
					isSyncingGroupsWithServer = false;
					noitifyGroupSyncListeners(false);
					if(callback != null){
						callback.onError(e.getErrorCode(), e.toString());
					}
				}

			}
		}.start();
	}

	public void noitifyGroupSyncListeners(boolean success){
		for (DataSyncListener listener : syncGroupsListeners) {
			listener.onSyncComplete(success);
		}
	}



	public void asyncFetchContactsFromServer(){
		if(isSyncingContactsWithServer){
			return;
		}
		isSyncingContactsWithServer = true;

		long userId = UserModel.getInstance().getCurrUserId();
		long sycnDeptTimeMillis = IMModel.getInstance().getLastSycnDeptTimeMillis();
		boolean isNewDataOfDept = -1 == sycnDeptTimeMillis;

		long sycnContactTimeMillis = IMModel.getInstance().getLastSycnContactTimeMillis();
		boolean isNewDataOfContact = -1 == sycnContactTimeMillis;

		UserModel.getInstance().getDeptsByServer(userId,sycnDeptTimeMillis)
				.compose(RxSchedulers.io_new())
				.concatMap(myResponse ->{
					LogUtils.e("同步部门"+isNewDataOfDept);
					UserModel.getInstance().saveDepts(isNewDataOfDept,myResponse.getResultData());


					return UserModel.getInstance().getUsersByServer(userId,sycnContactTimeMillis);
				})
				.concatMap(myResponse ->{
					LogUtils.e("同步用户"+isNewDataOfContact);
					UserModel.getInstance().saveUsers(isNewDataOfContact,myResponse.getResultData());

					return UserModel.getInstance().saveUser_DeptsRelations(isNewDataOfContact,myResponse.getResultData());
				})
				.compose(RxSchedulers.io_main())
				.subscribe(new Observer<MyResponse>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(MyResponse value) {
						if(!mIsLogin)return;//不是登录状态 直接返回

						IMModel.getInstance().setLastSycnContactTimeMillis(System.currentTimeMillis());
						IMModel.getInstance().setLastSycnDeptTimeMillis(System.currentTimeMillis());
						IMModel.getInstance().setContactSynced(true);
						postRxBusDataSyncOfContacts(true);
					}

					@Override
					public void onError(Throwable e) {
						isSyncingContactsWithServer = false;
						postRxBusDataSyncOfContacts(false);
					}

					@Override
					public void onComplete() {
						isSyncingContactsWithServer = false;
					}
				});
	}

	public void postRxBusDataSyncOfContacts(boolean isSuccess){
		RxBus.getDefault().post(new RxBusDataSyncEntity(Constant.RXBUS_UPIM_CONTACTS,isSuccess));
	}


	synchronized void reset(){
		BaseApplication.getInstance().noitifyLogoutObserver();
	}

	public void pushActivity(Activity activity) {
		easeUI.pushActivity(activity);
	}

	public void popActivity(Activity activity) {
		easeUI.popActivity(activity);
	}

}
