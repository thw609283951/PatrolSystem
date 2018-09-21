package com.supersit.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;
import com.supersit.common.Constant;
import com.supersit.common.R;
import com.supersit.common.ui.AlbumDirActivity;
import com.supersit.common.ui.RecorderVideoActivity;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.List;

import es.dmoral.toasty.Toasty;


/**
 * Created by Administrator on 2017/11/4.
 */

public class ActionUtil {


    /**
     * 打开相机
     * @param mContext
     * @param imagePath
     * @return
     */
    public static void openCamera(Activity mContext, String imagePath){
        AndPermission.with(mContext)
                .runtime()
                .permission(
                        Permission.Group.CAMERA,
                        Permission.Group.STORAGE
                ).onGranted(data -> {
                    Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 隐式调用
                    if ( null == photoIntent.resolveActivity(mContext.getPackageManager()) || null == imagePath )
                        return;
                        Uri uri = getUriForFile(new File(imagePath));
                    photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                    mContext.startActivityForResult(photoIntent, Constant.CODE_CAMERA_REQUEST);
                }).onDenied(data -> Toasty.error(mContext,"打开摄像头失败~~").show()).start();
    }


    /**
     * 打开位置纠正
     * @param mContext
     * @return
     */
    public static void openLocationCorrection(Activity mContext){
//        LocationCorrectionActivity.start(mContext);
    }

    /**
     * 打开相册 多选
     * @param mContext
     * @return
     */
    public static void openAblum(Activity mContext, List<String> selectedimages){
        AndPermission.with(mContext)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(data -> {
                    AlbumDirActivity.start(mContext,selectedimages);
                }).onDenied(data -> {
            Toasty.error(mContext,mContext.getString(R.string.lack_of_permissions)).show();
        }).start();

    }

    /**
     * 相册 单选
     * @param mContext
     */
    public static void openLocalAblum(Activity mContext){
        AndPermission.with(mContext)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(data -> {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setType("image/*");//从所有图片中进行选择
                    mContext.startActivityForResult(intent, Constant.CODE_RADIO_ALBUM_REQUEST);
                }).onDenied(data -> {
            Toasty.error(mContext,mContext.getString(R.string.lack_of_permissions)).show();
        }).start();
    }


    @SuppressLint("MissingPermission")
    public static void call(Context mContext, String modePhone){
        AndPermission.with(mContext)
                .runtime()
                .permission(Permission.Group.PHONE)
                .onGranted(data -> {
                    mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + modePhone)));
                }).onDenied(data -> {
            Toasty.error(mContext,mContext.getString(R.string.lack_of_permissions)).show();
        }).start();
    }

    /**
     * 裁剪图片
     * @param mContext
     */
    public static void clipPhoto(Activity mContext, Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 340);
        intent.putExtra("outputY", 340);
        intent.putExtra("return-data", true);
        mContext.startActivityForResult(intent,Constant.CODE_CLIP_REQUEST);
    }

    /**
     * 打开录制
     */
    public static void recorderVideo(Activity mContext){
        AndPermission.with(mContext)
                .runtime()
                .permission(
                        Permission.Group.CAMERA,
                        Permission.Group.MICROPHONE,
                        Permission.Group.STORAGE)
                .onGranted(data -> {
                    mContext.startActivityForResult(new Intent(mContext, RecorderVideoActivity.class),Constant.CODE_VIDEO_REQUEST);
                }).onDenied(data -> {
                    Toasty.error(mContext,mContext.getString(R.string.lack_of_permissions)).show();
        }).start();
    }

    public static void openFile(Context context, String filePath){

        File file = new File(filePath);
        if(!file.exists()) return;

        Intent intent;

        /* 取得扩展名 */
        String end=file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase();

        /* 依扩展名的类型决定MimeType */
        if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
                end.equals("xmf")||end.equals("ogg")||end.equals("wav")){
            intent= getAudioFileIntent(file);
        }else if(end.equals("3gp")||end.equals("mp4")){
            intent= getVideoFileIntent(file);
        }else{
            String mimeType = null;
            switch (end){
                case "jpg":
                case "gif":
                case "png":
                case "jpeg":
                case "bmp":
                    mimeType = "image/*";
                    break;

                case "apk":
                    mimeType = "application/vnd.android.package-archive";
                    break;

                case "pdf":
                    mimeType = "application/pdf";
                    break;

                case "doc":
                    mimeType = "application/msword";
                    break;

                case "ppt":
                    mimeType = "application/vnd.ms-powerpoint";

                    break;

                case "xls":
                    mimeType = "application/vnd.ms-excel";
                    break;

                case "chm":
                    mimeType = "application/x-chm";
                    break;

                case "txt":
                    mimeType = "text/plain";
                    break;

                default:
                    mimeType = "*/*";
                    break;

            }
            intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = getUriForFile(file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri,mimeType);
        }
        context.startActivity(intent);
//        return intent;
    }

    //Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(File file) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri =getUriForFile(file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "video/mp4");
        return intent;
    }

    public static Intent getVideoUrlIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url), "video/mp4");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }


    //Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(File file){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri =getUriForFile(file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    public static Uri getUriForFile(File file){
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            String authorities = AppUtils.getAppPackageName()+".my.fileProvider";;
            return FileProvider.getUriForFile(Utils.getApp(), authorities, file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
