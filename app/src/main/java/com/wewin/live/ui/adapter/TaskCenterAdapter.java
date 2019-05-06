package com.wewin.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.UtilTool;
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
 * 任务中心适配器
 */

public class TaskCenterAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<Map> mData;
    private OnItemListener onItemListener;

    public TaskCenterAdapter(Context context, List<Map> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_task_center, parent, false);
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
        @InjectView(R.id.tv_money)
        TextView tvMoney;
        @InjectView(R.id.tv_content)
        TextView tvContent;
        @InjectView(R.id.tv_state)
        TextView tvState;
        @InjectView(R.id.tv_receive_award)
        TextView tvReceiveAward;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

        public void setData(Map dataBean, final int position) {
            this.position = position;
            GlideUtil.setImg(mContext, dataBean.get(BaseInfoConstants.ICONOFAPP) + "", ivImage,R.mipmap.icon_avatar);
            tvTitle.setText(dataBean.get(BaseInfoConstants.TITLE) + "");
            tvContent.setText(dataBean.get(BaseInfoConstants.DESCRIPTION) + "");
            if (StringUtils.isEmpty(dataBean.get(BaseInfoConstants.TOTALCOIN) + "")) {
                tvMoney.setText("+" + dataBean.get(BaseInfoConstants.COIN) + mContext.getString(R.string.coin));
            } else {
                tvMoney.setText("+" + dataBean.get(BaseInfoConstants.TOTALCOIN) + mContext.getString(R.string.coin));
            }
            int state = UtilTool.parseInt(dataBean.get(BaseInfoConstants.STATUS) + "");
            if (state == 0) {
                //未完成
                tvReceiveAward.setVisibility(View.GONE);
                tvState.setVisibility(View.VISIBLE);
                tvState.setText(mContext.getString(R.string.on_the_stocks));
            } else if (state == 1) {
                if (BaseInfoConstants.SIGNIN.equals(dataBean.get(BaseInfoConstants.INPART) + "")) {
                    //可签到
                    tvReceiveAward.setVisibility(View.VISIBLE);
                    tvState.setVisibility(View.GONE);
                    tvReceiveAward.setText(mContext.getString(R.string.click_check_in));
                } else {
                    //待领取
                    tvReceiveAward.setVisibility(View.VISIBLE);
                    tvReceiveAward.setText(mContext.getString(R.string.receive_award));
                    tvState.setVisibility(View.GONE);
                }
            } else if (state == 2) {
                //已完成
                tvReceiveAward.setVisibility(View.GONE);
                tvState.setVisibility(View.VISIBLE);
                tvState.setText(mContext.getString(R.string.off_the_stocks));
            }
            tvReceiveAward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemListener != null) {
                        onItemListener.onItemClick(position);
                    }
                }
            });
        }
    }

    public void addOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
