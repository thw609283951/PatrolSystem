package com.supersit.common.adapter;

import android.support.annotation.Nullable;
import android.widget.Filter;
import android.widget.Filterable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 */

public abstract class BaseFilterAdapter<T> extends BaseQuickAdapter<T,BaseViewHolder> implements Filterable {

    protected List<T> mCopyDatas;
    protected String mConstraint;

    public BaseFilterAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
        mCopyDatas = new ArrayList<>();
        if(null != data)
            mCopyDatas.addAll(data);
    }

    public void addData(T data){
        mCopyDatas.add(data);
        super.addData(data);
    }

    public void setNewData(List<T> data) {
        //复制数据
        mCopyDatas = new ArrayList<>();
        if(null != data)
            mCopyDatas.addAll(data);
        super.setNewData(data);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                mConstraint = constraint.toString().trim();

                //初始化过滤结果对象
                FilterResults results = new FilterResults();
                //假如搜索为空的时候，将复制的数据添加到原始数据，用于继续过滤操作
                if (results.values == null) {
                    if(null != mData)
                        mData.clear();
                    if(null == mData)
                        mData = new ArrayList<>();

                    if(null != mCopyDatas)
                        mData.addAll(mCopyDatas);
                }
                //关键字为空的时候，搜索结果为复制的结果
                if (constraint == null || constraint.length() == 0) {
                    results.values = mCopyDatas;
                    results.count = mCopyDatas.size();
                } else {
                    String prefixString = constraint.toString().trim();
                    final int count = mData == null ? 0  : mData.size();
                    //用于存放暂时的过滤结果
                    final ArrayList<T> newValues = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        final T value = mData.get(i);
                        // First match against the whole ,non-splitted value，假如含有关键字的时候，添加
                        if (isContainsData(prefixString,value)) {
                            newValues.add(value);
                        }
                      /*  else {
                            //过来空字符开头
                            final String[] words = title.split(" ");
                            final int wordCount = words.length;

                            // Start at index 0, in case valueText starts with space(s)
                            for (int k = 0; k < wordCount; k++) {
                                if (words[k].contains(prefixString)) {
                                    newValues.add(value);
                                    break;
                                }
                            }
                        }*/
                    }
                    results.values = newValues;
                    results.count = newValues.size();
                }
                return results;//过滤结果

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                if(null != mData)
                    mData.clear();

                mData.addAll((List<T>) filterResults.values);//将过滤结果添加到这个对象
                if (filterResults.count > 0) {
                    notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据

                    setNewData(mCopyDatas);
                }
            }
        };
    }

    //检查是否匹配
    protected abstract boolean isContainsData(String prefix, T item);
}
