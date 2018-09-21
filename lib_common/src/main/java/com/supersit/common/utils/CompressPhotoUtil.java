package com.supersit.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.supersit.common.Constant;
import com.supersit.common.R;
import com.supersit.common.utils.video.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2017/11/20.
 */

public class CompressPhotoUtil {

    public static Observable<File> compressPhoto(Context context, String filePath){
        return Observable.create(new ObservableOnSubscribe<File>() {
            @SuppressLint("StringFormatInvalid")
            @Override
            public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                File sourceFile  = new File(filePath);
                if(!sourceFile.exists()){
                    emitter.onError(new Throwable(context.getString(R.string.error_file_no_exist,filePath)));
                    return;
                }
                String newFileName = MyUtil.getNewFilePath(context, Constant.IMAGE_CODE);
                 File compressedFile=new Compressor(context).setDestinationDirectoryPath(MyUtil.getSigninCachePath(context))
                        .compressToFile(new File(filePath),newFileName);
                emitter.onNext(compressedFile);
            }
        });
    }

    public static Observable<List<File>> compressPhotos(Context context, List<String> filePaths){
        return Observable.create(new ObservableOnSubscribe<List<File>>() {
            @Override
            public void subscribe(ObservableEmitter<List<File>> emitter) throws Exception {
                List<File> compressedFiles = new ArrayList<>();
                Compressor compressor =new Compressor(context).setDestinationDirectoryPath(MyUtil.getSigninCachePath(context));
                for (String filePath : filePaths) {
                    File file = new File(filePath);
                    if(!file.exists()){
                        emitter.onError(new Throwable(context.getString(R.string.error_file_no_exist)));
                        return;
                    }

                    if(MyUtil.getFileType(filePath) == Constant.IMAGE_CODE){
                        String newFileName =MyUtil.getNewFilePath(context, Constant.IMAGE_CODE);
                        compressedFiles.add(compressor.compressToFile(file,newFileName));
                    }else
                        compressedFiles.add(file);

                }
                emitter.onNext(compressedFiles);
            }
        });
    }
}
