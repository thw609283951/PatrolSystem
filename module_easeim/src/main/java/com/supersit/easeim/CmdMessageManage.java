package com.supersit.easeim;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.supersit.common.Constant;
import com.supersit.common.adapter.NullStringToEmptyAdapterFactory;
import com.supersit.common.entity.CmdMessage;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.UserEntity;
import com.supersit.common.model.UserModel;
import com.threshold.rxbus2.RxBus;

/**
 * Created by 田皓午 on 2018/5/7.
 */

public class CmdMessageManage {

    public static void sendCmdMsg(String action){
        try {
            JsonObject jsonObject=new JsonParser().parse(action).getAsJsonObject();
            JsonObject joAction=jsonObject.get("action").getAsJsonObject();

            String message = joAction.get("message").getAsString();
            CmdMessage cmdMessage = new CmdMessage(message);

            JsonElement jsonElement = joAction.get("data");
            if(!jsonElement.isJsonNull()){
                cmdMessage.setData(joAction.get("data").getAsJsonObject());
            }

//            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
//            CmdMessage cmdMessage = gson.fromJson(joAction,CmdMessage.class);

            checkMessage(cmdMessage);
//            RxBus.getDefault().post(cmdMessage);
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.e(e.getMessage());
        }
    }

    private static void checkMessage(CmdMessage cmdMessage)throws Exception{
        switch (cmdMessage.getMessage()){
            case "userInfoChange":
                saveUser( cmdMessage.getData());
                break;
            case "deleteUser":
                deleteUser(cmdMessage.getData());
                break;

            case "saveDept":
                saveDept(cmdMessage.getData());
                break;

            case "deleteDept":
                deleteDept(cmdMessage.getData());
                break;

            default:
                RxBus.getDefault().post(cmdMessage);
                break;
        }

    }

    private synchronized static void saveUser(JsonObject jsonObject)throws Exception{
        UserEntity userEntity =  new Gson().fromJson(jsonObject,UserEntity.class);
        UserModel.getInstance().saveUser(userEntity);
        RxBus.getDefault().post(Constant.RXBUS_UPIM_CONTACTS);
    }

    private synchronized static void deleteUser(JsonObject jsonObject)throws Exception{
        long userId = jsonObject.get("userId").getAsInt();
        UserModel.getInstance().deleteUser(userId);
        RxBus.getDefault().post(Constant.RXBUS_UPIM_CONTACTS);
    }

    private synchronized static void saveDept(JsonObject jsonObject)throws Exception{
        DeptEntity deptEntity =  new Gson().fromJson(jsonObject,DeptEntity.class);
        deptEntity.save();
        RxBus.getDefault().post(Constant.RXBUS_UPIM_CONTACTS);
    }

    private synchronized static void deleteDept(JsonObject jsonObject)throws Exception{
        long deptId = jsonObject.get("deptId").getAsInt();
        UserModel.getInstance().deleteDept(deptId);
        RxBus.getDefault().post(Constant.RXBUS_UPIM_CONTACTS);
    }


}
