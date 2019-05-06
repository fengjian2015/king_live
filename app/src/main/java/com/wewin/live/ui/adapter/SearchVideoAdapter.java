package com.wewin.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.live.R;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.utils.GlideUtil;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author jsaon
 * @date 2019/3/1
 *  视频适配器
 */

public class SearchVideoAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<Map> mData;
    private OnItemListener onItemListener;

    public SearchVideoAdapter(Context context, List<Map> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mData.size() != 0) {
            return mData.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        int position;
        @InjectView(R.id.iv_image)
        ImageView ivImage;
        @InjectView(R.id.tv_title)
        TextView tvTitle;
        @InjectView(R.id.tv_see_number)
        TextView tvSeeNumber;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemListener!=null)
                        onItemListener.onItemClick(position);
                }
            });
        }

        public void setData(Map dataBean, final int position) {
            this.position = position;
            GlideUtil.setImg(mContext,dataBean.get(BaseInfoConstants.COVER),ivImage,R.mipmap.icon_placeholder);
            tvTitle.setText(dataBean.get(BaseInfoConstants.TITLE)+"");
            tvSeeNumber.setText(mContext.getString(R.string.look_number)+dataBean.get(BaseInfoConstants.PAGEVIEW_VIRTUAL));
        }
    }

    public void addOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
