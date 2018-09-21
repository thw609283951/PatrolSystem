package com.supersit.patrol.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseViewHolder;
import com.supersit.common.adapter.BaseFilterAdapter;
import com.supersit.common.entity.DeptEntity;
import com.supersit.common.entity.DeptLocationEntity;
import com.supersit.common.entity.EventEntity;
import com.supersit.common.utils.ImageLoader;
import com.supersit.common.utils.SpannableStringUtil;
import com.supersit.patrol.R;

import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 */

public class ProjectListAdapter extends BaseFilterAdapter<DeptEntity> {


    public ProjectListAdapter(@Nullable List<DeptEntity> data){
        this(R.layout.item_project,data);
    }

    public ProjectListAdapter(int layoutResId, @Nullable List<DeptEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected boolean isContainsData(String prefixString, DeptEntity value) {
        String name = value.getName();
        return name.contains(prefixString);
    }


    @Override
    protected void convert(BaseViewHolder helper, DeptEntity deptEntity) {

        int userCount = deptEntity.getUserEntitys() ==null ? 0 :deptEntity.getUserEntitys().size();
        if(null != mConstraint && !mConstraint.isEmpty()){
            String sName =deptEntity.getName() + "（"+userCount+"人）";
            if(null != sName && sName.contains(mConstraint)){
                String[] sNames = sName.split(mConstraint);
                SpannableStringUtil suName=new SpannableStringUtil();
                if(0 < sNames.length)
                    suName.append(sNames[0]);
                if(1 == sNames.length)
                    suName.append(mConstraint).setForegroundColor(Color.RED);
                for (int i = 1; i < sNames.length; i++) {
                    suName.append(mConstraint).setForegroundColor(Color.RED)
                            .append(sNames[i]);
                }
                helper.setText(R.id.tv_name,suName.create());
            }else{
                helper.setText(R.id.tv_name,sName);
            }

        }else{
            helper.setText(R.id.tv_name,deptEntity.getName() + "（"+userCount+"人）");

        }
        helper.setText(R.id.tv_remark,deptEntity.getRemark() == null ? "详情" : deptEntity.getRemark());
        DeptLocationEntity deptLocationEntity = deptEntity.getDeptLocationEntity();
        helper.setText(R.id.tv_address,deptLocationEntity == null ? "未设": deptLocationEntity.getAddress());


    }

}
