package com.supersit.common.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.blankj.utilcode.util.LogUtils;
import com.supersit.common.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;

import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;
import static com.supersit.common.ARouterConstant.MainActivity;

/**
 * Created by 田皓午 on 2018/4/10.
 */

public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getSimpleName();

    public static void requestPermissions(final Context context){
        AndPermission.with(context)
                .runtime()
                .permission(
                        Permission.Group.STORAGE,
                        Permission.Group.CAMERA,
                        Permission.Group.LOCATION,
                        Permission.Group.MICROPHONE,
                        Permission.Group.PHONE,
                        Permission.Group.SENSORS,
                        Permission.Group.STORAGE,
                        Permission.Group.SENSORS
                ).rationale((context1, data, executor) -> {
                    // 此对话框可以自定义，调用rationale.resume()就可以继续申请。
                    new AlertDialog.Builder(context1)
                            .setTitle("提示")
                            .setMessage("你已经拒绝过我们申请授权，请您同意授权，否则功能无法正常使用")
                            .setPositiveButton(R.string.confirm, (dialog, which) -> {
                                executor.execute();
                                dialog.dismiss();

                            }).setNegativeButton(R.string.cancel, (dialog, which) -> {
                                executor.cancel();
                                dialog.dismiss();
                            }).create().show();
                }).onGranted(data -> {

                }).onDenied(data -> {
//                    showMissingPermissionDialog(context);
                }).start();
    }

    // 显示缺失权限提示
    private static void showMissingPermissionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.quit, (dialog, which) -> {});

        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                startAppSettings(context);
            }
        });

        builder.setCancelable(false);

        builder.show();
    }

    // 启动应用的设置
    private static void startAppSettings(Context context) {
        AndPermission.with(context)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        // 用户从设置回来了。
//                        Toasty.error(context,"拒绝授权~").show();
//                        requestPermissions(context);
                    }
                })
                .start();
    }

}
