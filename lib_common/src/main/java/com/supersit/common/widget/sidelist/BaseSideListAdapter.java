package com.supersit.common.widget.sidelist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;

import com.supersit.common.R;
import com.supersit.common.base.BaseViewAdapter;
import com.supersit.common.interfaces.IBaseInitialLetter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by 田皓午 on 2018/4/3.
 */

public abstract class BaseSideListAdapter<T> extends BaseViewAdapter<T> implements SectionIndexer, Filterable {
    List<String> list;
    protected List<T> mCopyDatas;
    protected LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;

    public BaseSideListAdapter(@NonNull Context context,List<T> objects) {
        super(context, objects);
        mCopyDatas = new ArrayList<>();
        if(null != objects)
            mCopyDatas.addAll(objects);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        list = new ArrayList<String>();
        list.add(context.getString(R.string.search_new));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {
            IBaseInitialLetter initialLetter = (IBaseInitialLetter) getItem(i);
            String letter = initialLetter.getInitialLetter();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                //初始化过滤结果对象
                FilterResults results = new FilterResults();
                //假如搜索为空的时候，将复制的数据添加到原始数据，用于继续过滤操作
                if (results.values == null) {
                    if(null != mDatas)
                        mDatas.clear();
                    if(null == mDatas)
                        mDatas = new ArrayList<>();

                    if(null != mCopyDatas)
                        mDatas.addAll(mCopyDatas);
                }
                //关键字为空的时候，搜索结果为复制的结果
                if (constraint == null || constraint.length() == 0) {
                    results.values = mCopyDatas;
                    results.count = mCopyDatas.size();
                } else {
                    String prefixString = constraint.toString().trim();
                    final int count = mDatas == null ? 0  : mDatas.size();
                    //用于存放暂时的过滤结果
                    final ArrayList<T> newValues = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        final T  value = mDatas.get(i);
                        if(isContains(prefixString,value)){
                            newValues.add(value);
                        }
                        /*String name = value.getName();
                        // First match against the whole ,non-splitted value，假如含有关键字的时候，添加
                        if (name.contains(prefixString)) {
                            newValues.add(value);
                        } else {
                            //过来空字符开头
                            final String[] words = name.split(" ");
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
                if(null != mDatas)
                    mDatas.clear();
                else
                    mDatas = new ArrayList<>();

                mDatas.addAll((Collection<? extends T>) filterResults.values);//将过滤结果添加到这个对象
                if (filterResults.count > 0) {
                    notifyDataSetChanged();//有关键字的时候刷新数据
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    updateItems(mCopyDatas);
                }
            }
        };
    }


    //检查是否匹配
    protected abstract boolean isContains(String prefix, T item);

    public void addItem(T info){
        mCopyDatas.add(info);
        super.addItem(info);
    }

    public void updateItems(List<T> data) {
        mCopyDatas.clear();
        //复制数据
        if(null != data){
            mCopyDatas.addAll(data);
        }
        super.updateItems(data);
    }

    public void removeAllItems(){
        mCopyDatas.clear();
        super.removeAllItems();
    }


    protected int primaryColor;
    protected int primarySize;
    protected Drawable initialLetterBg;
    protected int initialLetterColor;

    public BaseSideListAdapter<T> setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }


    public BaseSideListAdapter setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
        return this;
    }

    public BaseSideListAdapter setInitialLetterBg(Drawable initialLetterBg) {
        this.initialLetterBg = initialLetterBg;
        return this;
    }

    public BaseSideListAdapter setInitialLetterColor(int initialLetterColor) {
        this.initialLetterColor = initialLetterColor;
        return this;
    }

}
