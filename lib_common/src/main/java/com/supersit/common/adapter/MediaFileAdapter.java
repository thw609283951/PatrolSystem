package com.supersit.common.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.supersit.common.Constant;
import com.supersit.common.R;
import com.supersit.common.utils.ImageLoader;
import com.supersit.common.utils.MyUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */

public class MediaFileAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private boolean canSelect = false;

    public MediaFileAdapter(@Nullable List<String> data, boolean canSelect) {
        super(R.layout.item_mediafile, data);
        this.canSelect = canSelect;
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        int type = MyUtil.getFileType(item);
        ImageView ivMedia = helper.getView(R.id.iv_media);
        if(type == Constant.VEDIO_CODE){
            helper.setVisible(R.id.iv_video,true);
            ImageLoader.displayVideo(mContext,item,ivMedia);
        }else{
            helper.setVisible(R.id.iv_video,false);
            ImageLoader.display2PlaceholderImage(mContext, item, ivMedia);
        }
        if(canSelect)
            helper.addOnClickListener(R.id.iv_badge_delete);
        helper.setVisible(R.id.iv_badge_delete,canSelect);

    }
}
