package com.supersit.easeim.presenter;


import android.support.v4.app.Fragment;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.supersit.common.base.BasePresenter;
import com.supersit.easeim.db.IMDatabase;
import com.supersit.easeim.ui.BlankFragment;
import com.supersit.easeim.ui.ContactListFragment;
import com.supersit.easeim.ui.ConversationListFragment;
import com.supersit.easeim.ui.GroupListFragment;
import com.supersit.easeim.ui.IMFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/21.
 */

public class IMPresenter extends BasePresenter<IMFragment> {
    private List<Fragment> mIMFragments;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public List<Fragment> getFragments(){
        if(null == mIMFragments){
            mIMFragments = new ArrayList<>();
            mIMFragments.add(new ConversationListFragment());
            mIMFragments.add(new ContactListFragment());
            mIMFragments.add(new GroupListFragment());

        }
        return mIMFragments;
    }
}
