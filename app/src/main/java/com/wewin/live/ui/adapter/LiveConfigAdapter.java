package com.wewin.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * 直播配置适配器
 */

public class LiveConfigAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final List<Map> mData;

    private OnItemListener onItemListener;
    private int select=0;

    public LiveConfigAdapter(Context context, List<Map> data) {
        mContext = context;
        mData = data;
    }

    //返回当前选中框
    public int getSelect(){
        return select;
    }


    //设置当前选中框
    public void setSeletct(int i){
        select=i;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_live_config, parent, false);
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

        @InjectView(R.id.tv_contnet)
        TextView tvContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

        public void setData(Map dataBean, final int position) {
            this.position = position;
            if(position==select){
                tvContent.setSelected(true);
            }else {
                tvContent.setSelected(false);
            }
            tvContent.setText(dataBean.get(BaseInfoConstants.CONTENT)+"");
            tvContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select=position;
                    if (onItemListener!=null) {
                        onItemListener.onItemClick(position);
                    }
                    notifyDataSetChanged();
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
