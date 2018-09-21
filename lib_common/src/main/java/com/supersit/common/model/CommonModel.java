package com.supersit.common.model;


import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.DeptEntity_Table;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.entity.UserEntity_Table;
import com.supersit.mvp.model.SuperModel;

import java.util.List;


/**
 * Created by linlongxin on 2016/8/7.
 */

public class CommonModel extends SuperModel {
    protected String TAG = CommonModel.class.getSimpleName();


    public CommonModel() {
    }

    @Override
    public void reset() {

    }

    public static CommonModel getInstance() {
        return getInstance(CommonModel.class);
    }


    public UserEntity getUserByUserId(long userId){
        return SQLite.select()
                .from(UserEntity.class)
                .where(UserEntity_Table.userId.eq(userId))
                .querySingle();
    }

    public UserEntity getUserByUserName(String userName){
        return SQLite.select()
                .from(UserEntity.class)
                .where(UserEntity_Table.userName.eq(userName))
                .querySingle();
    }

    public DeptEntity getDeptEntityById(long deptId){
        return SQLite.select()
                .from(DeptEntity.class)
                .where(DeptEntity_Table.id.eq(deptId))
                .querySingle();
    }

    public List<DeptEntity> getDeptEntitys(){
        return SQLite.select()
                .from(DeptEntity.class)
                .queryList();
    }

}
