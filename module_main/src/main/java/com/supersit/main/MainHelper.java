package com.supersit.main;

import android.content.Context;

import com.supersit.common.base.BaseHelper;
import com.supersit.common.interfaces.LoginStatusObserver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 田皓午 on 2018/4/2.
 */

public class MainHelper extends BaseHelper{

    private static MainHelper instance = null;

    public MainHelper() {
    }
    public static MainHelper getInstance() {
        return getInstance(MainHelper.class);
    }


    @Override
    protected void init() {

    }

    @Override
    protected void onSyncDataAfter() {

    }

    @Override
    protected void checkSyncData() {

    }

    @Override
    protected void resetSyncData() {

    }
}

