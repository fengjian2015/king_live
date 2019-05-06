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
 * 热门搜索适配器
 */

public class SearchPopularAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<Map> mData;

    private OnItemListener onItemListener;

    public SearchPopularAdapter(Context context, List<Map> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_popular, parent, false);
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
        @InjectView(R.id.tv_state)
        TextView tvState;
        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemListener!=null)
                        onItemListener.onItemClick(position);
                }
            });
        }

        public void setData(Map dataBean, final int position) {
            this.position = position;
            String keyWord= (String) dataBean.get(BaseInfoConstants.KEYWORD);
            int status= (int) dataBean.get(BaseInfoConstants.STATUS);

            tvTitle.setText(keyWord);
            if(status==1){
                //新
                tvState.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.GONE);
                tvState.setBackgroundResource(R.drawable.ft_orange_bt);
                tvState.setText(mContext.getString(R.string.new1));
            }else if(status==2){
                //热
                tvState.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.GONE);
                tvState.setBackgroundResource(R.drawable.ft_red_bt5);
                tvState.setText(mContext.getString(R.string.hot));
            }else {
                //普通
                tvState.setVisibility(View.GONE);
                ivImage.setVisibility(View.VISIBLE);
            }
        }
    }

    public void addOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
