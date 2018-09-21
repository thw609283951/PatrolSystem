package com.supersit.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.supersit.common.R;

/**
 * Created by Administrator on 2017/3/15.
 */

public class ImageLoader {

    public static void displayVideo(Context mContext, String path, ImageView imageView){
        Glide.with(mContext).load(path).into(imageView);
    }

    public static void displayImage(Context mContext, String path, ImageView imageView){
        Glide.with(mContext).load(path).crossFade().error(R.mipmap.ic_load_err).into(imageView);
    }

    public static void display2PlaceholderImage(Context mContext, String path, ImageView imageView){
        Glide.with(mContext).load(path).crossFade().placeholder(R.mipmap.ic_default_image)
                .error(R.mipmap.ic_load_err).into(imageView);
    }


    public static void displayImage(Context mContext, int resId, ImageView imageView){
        Glide.with(mContext).load(resId).into(imageView);
    }

    public static void displayImage(Context mContext, Uri uri, ImageView imageView){
        Glide.with(mContext).load(uri).placeholder(R.mipmap.ic_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.mipmap.ic_load_err).into(imageView);
    }

    public static void displayImage(Context mContext, String path, int errIcnResId, ImageView imageView){
        Glide.with(mContext).load(path).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(errIcnResId)
                .error(errIcnResId).into(imageView);
    }

    public static void diskCacheImage(String url){
//        Glide.with(Utils.getApp())
//                .load(url)
//                .downloadOnly(500, 500);
    }

    public class GlideCircleTransform extends BitmapTransformation {
        public GlideCircleTransform(Context context) {
            super(context);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }

}
