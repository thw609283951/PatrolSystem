package com.supersit.easeim.presenter;

import com.supersit.common.Constant;
import com.supersit.common.base.BasePresenter;
import com.supersit.common.entity.RxBusDataSyncEntity;
import com.supersit.common.entity.RxBusEntity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.model.UserModel;
import com.supersit.easeim.ui.ContactListFragment;
import com.threshold.rxbus2.annotation.RxSubscribe;
import com.threshold.rxbus2.util.EventThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by linlongxin on 2016/9/17.
 */

public class ContactListPresenter extends BasePresenter<ContactListFragment> {
    private static final String TAG = ContactListPresenter.class.getSimpleName();

    private List<UserEntity> contactList;

    @RxSubscribe(observeOnThread = EventThread.MAIN)
    public void RecRxBusDataSync(RxBusDataSyncEntity syncEntity) {
        if(Constant.RXBUS_UPIM_CONTACTS == syncEntity.getId()){
            if(syncEntity.isSuccess())
                refresh();
            else
                getView().showError();
        }
    }


    public void refresh(){
        getContactList();
        getView().setListData(contactList);
    }

    /**
     * get contact list and sort
     */
    protected void getContactList() {

        List<UserEntity> tempUsers = UserModel.getInstance().getUsers();
        if(null ==contactList)
            contactList = new ArrayList<>();
        else
            contactList.clear();
        if(tempUsers == null || tempUsers.isEmpty()){
            return;
        }
        contactList.addAll(tempUsers);
        // sorting
        Collections.sort(contactList, (lhs, rhs) -> {
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
