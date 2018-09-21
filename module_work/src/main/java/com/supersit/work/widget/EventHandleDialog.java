package com.supersit.work.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.KeyboardUtils;
import com.supersit.common.ARouterConstant;
import com.supersit.common.Constant;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.widget.dialog.BaseDialog;
import com.supersit.work.R;
import com.supersit.work.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by 田皓午 on 2018/5/7.
 */

public class EventHandleDialog extends DialogFragment implements TextWatcher {


    @BindView(R2.id.et_msg)
    EditText etMsg;
    @BindView(R2.id.et_select_user)
    EditText etSelectUser;
    @BindView(R2.id.btn_send)
    TextView btnSend;

    private BaseDialog dialog;

    private String mInputText;
    private UserEntity mReceiverUser;

    //信息监听回调,发送与保存
    public CommentListener commentListener;
    public interface CommentListener {
        void sendComment(String inputText,UserEntity receiverUser);
        void saveComment(String inputText,UserEntity receiverUser);
    }

    public EventHandleDialog() {
    }


    @SuppressLint("ValidFragment")
    public EventHandleDialog(CommentListener commentListener) {//提示文字
        this.commentListener = commentListener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("anAnt","Android");
        super.onSaveInstanceState(outState);
    }




    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if( savedInstanceState != null ){
            mInputText= savedInstanceState.getString("anAnt");
        }

        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        dialog = new BaseDialog(getActivity(), R.style.CommentDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        View contentView = View.inflate(getActivity(), R.layout.dialog_event_handle, null);
//        ButterKnife.bind(contentView);
        ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);

        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.alpha = 1;
        lp.dimAmount = 0.0f;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        etMsg.addTextChangedListener(this);
        etMsg.setFocusable(true);
        etMsg.setFocusableInTouchMode(true);
        etMsg.requestFocus();


        if(null != mInputText){
            etMsg.setText(mInputText);
            etMsg.setSelection(mInputText.length());
        }

        if(null != mReceiverUser)
            etSelectUser.setText(mReceiverUser.getNickName());

        final Handler handler = new Handler();
        dialog.setOnDismissListener(dialog -> handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtils.hideSoftInput(getActivity());
            }
        }, 200));
        dialog.setOnTouchOutsideListener(new BaseDialog.OnTouchOutsideListener() {
            @Override
            public void onTouchOutside() {
                commentListener.saveComment(mInputText,mReceiverUser);
                dismiss();
            }
        });
        return dialog;
    }

    public void setText(String text){
        mInputText = text;
    }

    public void setSelectUser(UserEntity user){
        mReceiverUser = user;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mInputText = s.toString();
        if (s.length() > 0) {
            btnSend.setEnabled(true);
        } else {
            btnSend.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void checkContent() {

        String content = etMsg.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getContext(), "评论内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (null == mReceiverUser) {
            Toast.makeText(getContext(), "请选择下一个受理人~", Toast.LENGTH_SHORT).show();
            return;
        }
        commentListener.sendComment(content,mReceiverUser);
//        dismiss();
    }

    @OnClick({R2.id.et_select_user, R2.id.btn_send})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.et_select_user) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isSelect",true);
            ARouter.getInstance().build(ARouterConstant.ContactListActivity).with(bundle)
                    .navigation((Activity) getContext(), Constant.CODE_SELECT_USER_REQUEST);
        } else if (i == R.id.btn_send) {
            checkContent();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;

        Bundle bundle = null;
        if (data != null)
            bundle = data.getExtras();
        switch (requestCode) {
            case Constant.CODE_SELECT_USER_REQUEST:
                mReceiverUser = bundle.getParcelable("user");
                etSelectUser.setText(mReceiverUser.getNickName());
                break;
            default:
                break;
        }
    }
}
