package com.supersit.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.supersit.common.R;
import com.supersit.common.base.BaseEntity;
import com.supersit.common.utils.ImageLoader;
import com.supersit.common.widget.recyclerview.GridSpacingItemDecoration;

import java.util.List;


/**
 *
 */

public class GridRadioDialog extends AlertDialog {

    private static final String TAG = GridRadioDialog.class.getSimpleName();

    private Context mContext;
    private RecyclerView mList;

    //数据
    private List datas;

    private int spanCount =3;

    private BaseQuickAdapter mAdapter;

    public GridRadioDialog(Context context, int spanCount) {
        this(context, 0, spanCount);
    }

    public GridRadioDialog(Context context, int theme, int spanCount) {
        super(context, theme);
        this.spanCount = spanCount;
        mContext = context;
        initView();
    }


    public void initView() {
        mList = new RecyclerView(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mList.setLayoutParams(layoutParams);
        mList.setLayoutManager(new GridLayoutManager(mContext, spanCount));
        int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.spacing_h);
        mList.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacingInPixels, true));
        setView(mList, 20, 20, 20, 20);


    }


    public void setDatas(List datas) {
        this.datas = datas;
    }

    @Override
    public void show() {
        mAdapter = new BaseQuickAdapter(R.layout.item_grid_radio, datas) {
            @Override
            protected void convert(BaseViewHolder helper, Object item) {
                BaseEntity baseInfo = (BaseEntity) item;
                ImageView imageView = helper.getView(R.id.imageView);
                ImageLoader.displayImage(mContext, baseInfo.getId(), imageView);
                helper.setText(R.id.texeView, baseInfo.getName());
            }
        };
        mAdapter.setOnItemClickListener(onItemClickListener);
        mList.setAdapter(mAdapter);
        super.show();
    }

    BaseQuickAdapter.OnItemClickListener onItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            if (null != mOnItemClickListener) {
                mOnItemClickListener.onClick(GridRadioDialog.this, mAdapter.getItem(position));
            }
        }
    };

    public OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onClick(Dialog dialog, Object data);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

}
