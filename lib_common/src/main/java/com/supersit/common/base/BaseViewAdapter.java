package com.supersit.common.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by lijuan on 2016/9/12.
 * @param <T>
 */
public class BaseViewAdapter<T> extends BaseAdapter {
    protected List<T> mDatas;
    /**
     * 每一页显示的个数
     */
    protected Context context;

    public BaseViewAdapter(Context context, List<T> mDatas) {
        this.context=context;
        this.mDatas = mDatas;
    }

    /**
     * 先判断数据集的大小是否足够显示满本页？mDatas.size() > (curIndex+1)*pageSize,
     * 如果够，则直接返回每一页显示的最大条目个数pageSize,
     * 如果不够，则有几项返回几,(mDatas.size() - curIndex * pageSize);(也就是最后一页的时候就显示剩余item)
     */
    @Override
    public int getCount() {
        if(mDatas==null)
            return 0;

        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
    	 return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return convertView;
    }

    /** 更新数据，替换原有数据 */
    public void updateItems(List<T> items) {
        mDatas = items;
        notifyDataSetChanged();
    }

    /** 插入一条数据 */
    public void addItem(T item) {
        if(null == mDatas)
            mDatas = new ArrayList<>();
        mDatas.add(item);
        notifyDataSetChanged();
    }

    /** 插入一条数据 */
    public void addItem(T item, int position) {
        position = Math.min(position, mDatas.size());
        if(null == mDatas)
            mDatas = new ArrayList<>();
        mDatas.add(position, item);
        notifyDataSetChanged();
    }

    /** 在列表尾添加一串数据 */
    public void addItems(List<T> items) {
        if(null == mDatas)
            mDatas = new ArrayList<>();
        mDatas.addAll(items);
        notifyDataSetChanged();
    }

    /** 移除一条数据 */
    public void removeItem(int position) {
        if (position > mDatas.size() - 1) {
            return;
        }
        mDatas.remove(position);
        notifyDataSetChanged();
    }

    /** 移除一条数据 */
    public void removeItem(T item) {
        int position = 0;
        ListIterator<T> iterator = mDatas.listIterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (next == item) {
                iterator.remove();
                notifyDataSetChanged();
            }
            position++;
        }
    }

    /** 清除所有数据 */
    public void removeAllItems() {
        if(null != mDatas)
            mDatas.clear();
        notifyDataSetChanged();
    }
}