package com.supersit.common.widget.sidelist;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;


import com.supersit.common.R;
import com.supersit.common.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class SideListView<T> extends RelativeLayout {
    protected static final String TAG = SideListView.class.getSimpleName();

    protected Context context;
    protected ListView listView;
    protected BaseSideListAdapter adapter;
    protected Sidebar sidebar;

    protected int primaryColor;
    protected int primarySize;
    protected boolean showSiderBar;
    protected Drawable initialLetterBg;

    protected int initialLetterColor;


    public SideListView(Context context) {
        super(context);
        init(context, null);
    }

    public SideListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SideListView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SideListView);
        primaryColor = ta.getColor(R.styleable.SideListView_ctsListPrimaryTextColor, 0);
        primarySize = ta.getDimensionPixelSize(R.styleable.SideListView_ctsListPrimaryTextSize, 0);
        showSiderBar = ta.getBoolean(R.styleable.SideListView_ctsListShowSiderBar, true);
        initialLetterBg = ta.getDrawable(R.styleable.SideListView_ctsListInitialLetterBg);
        initialLetterColor = ta.getColor(R.styleable.SideListView_ctsListInitialLetterColor, 0);
        ta.recycle();
        
        
        LayoutInflater.from(context).inflate(R.layout.view_side_listview, this);
        listView = (ListView)findViewById(R.id.list);
        sidebar = (Sidebar) findViewById(R.id.sidebar);
        if(!showSiderBar)
            sidebar.setVisibility(View.GONE);

        if(showSiderBar){
            sidebar.setListView(listView);
        }
    }

    public void setAdapter(BaseSideListAdapter adapter){
        this.adapter = adapter;
        listView.setAdapter(adapter);
    }
    
    
    public void refresh(List<UserEntity> userEntities){
        if(adapter != null ){
            adapter.updateItems(userEntities);
        }
    }

    public void filter(CharSequence str) {
        if(adapter != null)
            adapter.getFilter().filter(str);
    }
    
    public ListView getListView(){
        return listView;
    }
    
    public void setShowSiderBar(boolean showSiderBar){
        if(showSiderBar){
            sidebar.setVisibility(View.VISIBLE);
        }else{
            sidebar.setVisibility(View.GONE);
        }
    }

}
