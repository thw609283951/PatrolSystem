package com.supersit.common.net;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/16.
 */

public class RxSchedulers {
    public static <T> ObservableTransformer<T, T> io_main() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> new_main() {
        return upstream ->
                upstream.subscribeOn(Schedulers.newThread())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> io_io() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io());
    }

    public static <T> ObservableTransformer<T, T> io_new() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread());
    }

}
