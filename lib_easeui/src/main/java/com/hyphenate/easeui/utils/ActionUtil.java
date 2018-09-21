package com.hyphenate.easeui.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/11/4.
 */

public class ActionUtil {

    public static void openFile(Context context, String filePath){

        File file = new File(filePath);
        if(!file.exists()) return;

        Intent intent;

        /* 取得扩展名 */
        String end=file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase();

        /* 依扩展名的类型决定MimeType */
        if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
                end.equals("xmf")||end.equals("ogg")||end.equals("wav")){
            intent= getAudioFileIntent(context,file);
        }else if(end.equals("3gp")||end.equals("mp4")){
            intent= getVideoFileIntent(context,file);
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
            Uri uri = getUriForFile(context,file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri,mimeType);
        }
        context.startActivity(intent);
//        return intent;
    }

    //Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(Context context,File file) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri =getUriForFile(context,file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    //Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(Context context,File file){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri =getUriForFile(context,file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    public static Uri getUriForFile(Context context,File file){
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            String authorities = context.getPackageName()+".my.fileProvider";;
            return FileProvider.getUriForFile(context, authorities, file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
