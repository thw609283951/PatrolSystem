package com.supersit.work.entity;


import com.supersit.work.R;

/**
 * Created by Administrator on 2016/12/2.
 */

public class WeaterIcon {
    private static int[] ids = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 99};
    private static String[] texts = {"晴", "晴", "晴", "晴", "多云", "晴间多云", "晴间多云", "大部多云", "大部多云", "阴", "阵雨", "雷阵雨", "雷阵雨伴有冰雹", "小雨", "中雨", "大雨", "暴雨", "大暴雨", "特大暴雨", "冻雨", "雨夹雪", "阵雪", "小雪", "中雪", "大雪", "暴雪", "浮尘", "扬沙", "沙尘暴", "强沙尘暴", "雾", "霾", "风", "大风", "飓风", "热带风暴", "龙卷风", "冷", "热", "未知"};
    private static int[] icons = {R.mipmap.w0, R.mipmap.w1, R.mipmap.w2, R.mipmap.w3, R.mipmap.w4, R.mipmap.w5, R.mipmap.w6, R.mipmap.w7, R.mipmap.w8, R.mipmap.w9, R.mipmap.w10, R.mipmap.w11, R.mipmap.w12, R.mipmap.w13, R.mipmap.w14, R.mipmap.w15, R.mipmap.w16, R.mipmap.w17, R.mipmap.w18, R.mipmap.w19, R.mipmap.w20, R.mipmap.w21, R.mipmap.w22, R.mipmap.w23, R.mipmap.w24, R.mipmap.w25, R.mipmap.w26, R.mipmap.w27
            , R.mipmap.w28, R.mipmap.w29, R.mipmap.w30, R.mipmap.w31, R.mipmap.w32, R.mipmap.w33, R.mipmap.w34, R.mipmap.w35, R.mipmap.w36, R.mipmap.w37, R.mipmap.w38, R.mipmap.w99};

    public static Integer getWeaterIcon(String text, int hour) {
        int icon = icons[icons.length - 1];
        for (int i = 0; i < texts.length; i++) {
            if (texts[i].equals(text)) {
                icon = icons[i];
                break;
            }
        }
        for (int i = 0; i < texts.length; i++) {
            if (text.contains(texts[i])) {
                icon = icons[i];
                break;
            }
        }
        return icon;
    }
}
