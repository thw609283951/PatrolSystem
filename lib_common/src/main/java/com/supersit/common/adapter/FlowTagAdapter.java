package com.supersit.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.supersit.common.R;
import com.supersit.common.interfaces.IFlowTagName;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by 田皓午 on 2018/4/12.
 */

public class FlowTagAdapter extends TagAdapter{
    private Context mContext;
    public FlowTagAdapter(Context context,List datas) {
        super(datas);
        mContext = context;
    }

    @Override
    public View getView(FlowLayout parent, int position, Object o) {
//        TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_flow_tag,
//                this, false);
//        IFlowTagName flowTagName = (IFlowTagName) o;
//        tv.setText(flowTagName.getFlowTagName());
        return null;
    }
}
