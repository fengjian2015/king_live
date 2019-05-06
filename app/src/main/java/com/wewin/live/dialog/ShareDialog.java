package com.wewin.live.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jasonutil.util.ActivityUtil;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.wewin.live.R;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.thirdparty.QqShare;
import com.wewin.live.thirdparty.ShareUtil;
import com.wewin.live.thirdparty.WXUtil;
import com.wewin.live.thirdparty.WeiBoShare;
import com.example.jasonutil.util.AnimatorTool;
import com.wewin.live.utils.GlideUtil;
import com.example.jasonutil.util.UtilTool;

import java.util.HashMap;
import java.util.List;

/**
 * Created by xingyun on 2016/7/19.
 * 分享弹窗，点击操作都在本内完成，通过eventBus通知
 */
public class ShareDialog extends Dialog {

    public interface ListOnClick{
        /**
         * 0是最底下的取消按钮选项，1-n是从上往下的各个选项
         * @param position
         */
        public void onclickitem(int position);
    }

    private GridView lv_menu;
    /**
     * 取消按钮
     */
    private Button button_common3;
    private ListOnClick listOnClick;
    private LinearLayout quit_popupwindows_bg;
    private List<HashMap> menunames;
    private Activity context;
    private  MyMenuAdapter myMenuAdapter;
    private View view;
    public ShareDialog(Activity context, List<HashMap> menunames) {
        super(context, R.style.BottomDialog2);
        view = View.inflate(context, R.layout.dialog_share, null);
        this.menunames = menunames;
        this.context = context;
        initView();
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
        if(!ActivityUtil.isActivityOnTop(context))return;
        window.setWindowAnimations(R.style.BottomDialog);
        setContentView(view);

    }

    private void initView(){
        button_common3 = (Button) view.findViewById(R.id.button_common3);
        quit_popupwindows_bg = (LinearLayout) view.findViewById(R.id.quit_popupwindows_bg);
        lv_menu = (GridView) view.findViewById(R.id.lv_menu);
        myMenuAdapter = new MyMenuAdapter();
        lv_menu.setAdapter(myMenuAdapter);

        button_common3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        quit_popupwindows_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 显示
     */
    public ShareDialog showAtLocation(){
        if(!ActivityUtil.isActivityOnTop(context))return this;
        show();

        return this;
    }

    public ShareDialog initData(){
        menunames.clear();
        menunames.addAll(ShareUtil.getShareList());
        myMenuAdapter.notifyDataSetChanged();
        return this;
    }

    private class MyMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return menunames.size();
        }

        @Override
        public Object getItem(int position) {
            return menunames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = View.inflate(context, R.layout.item_share, null);
            ImageView iv_head=view.findViewById(R.id.iv_head);
            TextView tv_name=view.findViewById(R.id.tv_name);
            GlideUtil.setCircleImg(context,(int)menunames.get(position).get(BaseInfoConstants.AVATAR),iv_head);
            tv_name.setText(context.getResources().getString((int) menunames.get(position).get(BaseInfoConstants.NAME)) );
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (listOnClick != null) {
                        listOnClick.onclickitem(position);
                    }
                }
            });
            AnimatorTool.getInstance().viewAnimator(view);
            return view;
        }
    }

    public void setListOnClick(ListOnClick listOnClick){
        this.listOnClick = listOnClick;
    }

    /**
     * 分享
     * @param context
     * @param url
     * @param title
     * @param dese
     * @param imageUrl
     * @param position
     * @return 是否显示弹窗
     */
    public void goShare(Activity context,String url,String title,String dese,String imageUrl,int position){
        int name=(int)menunames.get(position).get(BaseInfoConstants.NAME);
        if(name==ShareUtil.content[0]){
            //微信
            WXUtil.getInstance().shareUrlToWx(context,url,title,dese,SendMessageToWX.Req.WXSceneSession);
        }else if(name==ShareUtil.content[1]){
            //微信朋友圈
            WXUtil.getInstance().shareUrlToWx(context,url,title,dese,SendMessageToWX.Req.WXSceneTimeline);
        }else if(name==ShareUtil.content[2]){
            //qq 传入图片
            QqShare.getInstance().shareFriendImageAndText(context,title,dese,url,imageUrl);
        }else if(name==ShareUtil.content[3]){
            //qq空间 传入图片
            QqShare.getInstance().shareToQzone(context,title,dese,url,imageUrl);
        }else if(name==ShareUtil.content[4]){
            //新浪
            WeiBoShare.getInstance().share(context,title,dese,url);
        }else if(name==ShareUtil.content[5]){
            //复制
            UtilTool.copyToClipboard(context,url);
        }
    }
}
