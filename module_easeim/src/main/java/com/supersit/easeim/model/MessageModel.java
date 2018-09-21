package com.supersit.easeim.model;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.supersit.mvp.model.SuperModel;

import java.util.List;

/**
 * Created by 田皓午 on 2018/4/2.
 */

public class MessageModel extends SuperModel{

    public MessageModel() {
    }

    @Override
    public void reset() {

    }

    public static MessageModel getInstance() {
        return getInstance(MessageModel.class);
    }

}
