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

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author jsaon
 * @date 2019/3/1
 * 资讯适配器
 */

public class SearchNewsAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<Map> mData;

    private OnItemListener onItemListener;

    public SearchNewsAdapter(Context context, List<Map> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_news, parent, false);
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

        @InjectView(R.id.tv_title)
        TextView tvTitle;
        @InjectView(R.id.tv_time)
        TextView tvTime;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemListener!=null) {
                        onItemListener.onItemClick(position);
                    }
                }
            });
        }

        public void setData(Map dataBean, final int position) {
            this.position = position;
            tvTitle.setText(dataBean.get(BaseInfoConstants.TITLE)+"");
            tvTime.setText(mContext.getString(R.string.time)+dataBean.get(BaseInfoConstants.PUBLISHTIME));
        }
    }

    public void addOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
