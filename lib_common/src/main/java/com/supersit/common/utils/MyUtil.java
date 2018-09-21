package com.supersit.common.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;


import com.blankj.utilcode.util.AppUtils;
import com.supersit.common.Constant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/3/2.
 */

public class MyUtil {

    /**
     * 设置user昵称(没有昵称取username)的首字母属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param str
     */
    public static String getStrInitialLetter(String str) {
        final String DefaultLetter = "#";
        String letter = DefaultLetter;

        final class GetInitialLetter {
            String getLetter(String name) {
                if (TextUtils.isEmpty(name)) {
                    return DefaultLetter;
                }
                char char0 = name.toLowerCase().charAt(0);
                if (Character.isDigit(char0)) {
                    return DefaultLetter;
                }
                ArrayList<HanziToPinyin.Token> l = HanziToPinyin.getInstance().get(name.substring(0, 1));
                if (l != null && l.size() > 0 && l.get(0).target.length() > 0) {
                    HanziToPinyin.Token token = l.get(0);
                    String letter = token.target.substring(0, 1).toUpperCase();
                    char c = letter.charAt(0);
                    if (c < 'A' || c > 'Z') {
                        return DefaultLetter;
                    }
                    return letter;
                }
                return DefaultLetter;
            }
        }

        if (!TextUtils.isEmpty(str)) {
            letter = new GetInitialLetter().getLetter(str);
            return letter;
        }
        if (letter == DefaultLetter && !TextUtils.isEmpty(str)) {
            letter = new GetInitialLetter().getLetter(str);
        }
        return letter;
    }

    public static int getFileType(String filePath){
        File file = new File(filePath);
//	        if(!file.exists()) return -1;
	        /* 取得扩展名 */
        String end=file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase();
	        /* 依扩展名的类型决定MimeType */

        switch (end) {
            case "3gp":
            case "mp4":
                return Constant.VEDIO_CODE;

            case "jpg":
            case "gif":
            case "jpeg":
            case "bmp":
            case "png":
                return Constant.IMAGE_CODE;

            case "doc":
            case "docx":
                return Constant.WORD_CODE;


            case "pdf":
                return Constant.PDF_CODE;

            case "ppt":
            case "pptx":
            case "xls":
            case "xlsx":
            default:
                return Constant.OTHER_CODE;
        }
    }

    /**
     * 获取 一个新文件的的缓存路径
     * @return
     */
    public synchronized static String getNewFilePath(Context mContext, int fileType) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date curDate = new Date( System.currentTimeMillis());//获取当前时间

        String sFileType;
        String suffixName;
        switch (fileType){
            case Constant.IMAGE_CODE:

                sFileType = "IMG";
                suffixName = ".jpg";
                break;
            case Constant.VEDIO_CODE:
                sFileType = "VID";
                suffixName = ".mp4";
                break;
            default:
                sFileType = "TXT";
                suffixName = ".txt";
                break;
        }
        StringBuffer sFile= new StringBuffer();
        sFile.append(sFileType);
        sFile.append("_");
        sFile.append(formatter.format(curDate));
        sFile.append(suffixName);

        String dirPath =  getSigninCachePath(mContext) +"/"+ sFileType;
        File dirFile= new File(dirPath);
        if(!dirFile.exists())
            dirFile.mkdirs();
        return dirPath +"/"+ sFile.toString();
    }


    /**
     * 获取签到文件的缓存路径
     * @return
     */
    public static String getSigninCachePath(Context mContext) {
        //判断是否有SD卡
        String sdDir = null;
        boolean isSDcardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDcardExist) {
            sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            sdDir = Environment.getRootDirectory().getAbsolutePath();
        }
        //确定相片保存路径
        String targetDir = sdDir + "/" + AppUtils.getAppPackageName();
        File file = new File(targetDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return targetDir;
    }

    /*
      *  list转String
     * @param sChar
     * @param stringList
     * @return
             */
    public static String listToString(String sChar,List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(sChar);
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    public static ArrayList<String> stringToList(String sChar, String listString) {

        if (listString == null)
            return null;

        String strs[] = listString.split(sChar);
        ArrayList<String> stringList = new ArrayList<String>();
        for (String str : strs) {
            stringList.add(str);
        }
        return stringList;
    }
}
