package com.wewin.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewin.live.R;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author jsaon
 * @date 2019/3/1
 * 发布记录视频适配器
 */

public class ReleaseVideoAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<Map> mData;
    private OnItemListener onItemListener;

    public ReleaseVideoAdapter(Context context, List<Map> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_release_video, parent, false);
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
        @InjectView(R.id.tv_time)
        TextView tvTime;
        @InjectView(R.id.tv_classification)
        TextView tvClassification;
        @InjectView(R.id.tv_type)
        TextView tvType;
        @InjectView(R.id.tv_popularity)
        TextView tvPopularity;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

        public void setData(Map dataBean, final int position) {
            this.position = position;
        }
    }

    public void addOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}