package com.wewin.live.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.example.jasonutil.util.ActivityUtil;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.wewin.live.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * @author jsaon
 * @date 2019/3/7
 * 王者号
 */
public class KingListDialog extends Dialog implements View.OnClickListener,View.OnTouchListener {
    private Context context;
    /**
     * 更多状态
     */
    private RecyclerView recyclerAttention;
    /**
     * 更多推荐
     */
    private RecyclerView recyclerRecommendations;
    private ImageView ivFinish;
    /**
     * 数据外层
     */
    private ScrollView scrollView;

    private List<Map> attentionList=new ArrayList<>();
    private List<Map> recommendationsList=new ArrayList<>();
    private View view;

    public KingListDialog(Context context) {
        super(context, R.style.BottomDialog2);
        view = View.inflate(context, R.layout.dialog_king_list, null);
        this.context = context;
        initView(view);
        //获得dialog的window窗口
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        if (!ActivityUtil.isActivityOnTop(context)) {
            return;
        }
        window.setWindowAnimations(R.style.BottomDialog1);
        setContentView(view);
    }


    private void initView(View view) {
        for(int i=0;i<10;i++){
            attentionList.add(new HashMap());
        }

        recyclerAttention = view.findViewById(R.id.recycler_attention);
        recyclerRecommendations = view.findViewById(R.id.recycler_recommendations);
        ivFinish=view.findViewById(R.id.iv_finish);
        scrollView=view.findViewById(R.id.scrollView);
        ivFinish.setOnClickListener(this);
        scrollView.setOnTouchListener(this);

        recyclerAttention.setLayoutManager(new GridLayoutManager(context,5));
        recyclerAttention.addItemDecoration(new GridSpacingItemDecoration(5,context.getResources().getDimensionPixelOffset(R.dimen.d5dp),false));
        AttentionAdapter attentionAdapter = new AttentionAdapter();
        recyclerAttention.setAdapter(attentionAdapter);


        recyclerRecommendations.setLayoutManager(new GridLayoutManager(context,5));
        recyclerRecommendations.addItemDecoration(new GridSpacingItemDecoration(5,context.getResources().getDimensionPixelOffset(R.dimen.d5dp),false));
        AttentionAdapter attentionAdapter1 = new AttentionAdapter();
        recyclerRecommendations.setAdapter(attentionAdapter1);
    }

    public void showAtLocation() {
        if (!ActivityUtil.isActivityOnTop(context)) {
            return;
        }
        show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_finish:
                dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * 触摸数据
     */
    private float y1, y2;
    private float oldY;
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if(view.getId()==R.id.scrollView){
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    y1 =y2= event.getRawY();
                    oldY=getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    y2 = event.getRawY();
                    if (y1 - y2 > 1) {
                        changeY(y2-y1);
                    } else if (y2 - y1 > 1) {
                        changeY(y2-y1);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(y2-y1<-200){
                        dismiss();
                    }else {
                        changeY(0);
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }


    /**
     * 更改控件的位置
     * @param v
     */
    public void changeY(float v){
        float last=oldY+v;
        if(last>=0){
            view.setY(0);
        }else {
            view.setY((oldY + v));
        }
    }

    /**
     * 获取控件的位置
     * @return
     */
    private float getY(){
        return view.getY();
    }


    class AttentionAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_king, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.setData(attentionList.get(position), position);
        }

        @Override
        public int getItemCount() {
            if (attentionList.size() != 0) {
                return attentionList.size();
            }
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            int position;

            ViewHolder(View view) {
                super(view);
                ButterKnife.inject(this, view);
            }

            public void setData(Map dataBean, final int position) {
                this.position = position;
            }
        }
    }
}
