package com.wewin.live.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.jasonutil.util.ActivityUtil;
import com.wewin.live.R;

import java.util.List;

/**
 * @author jason
 * Created by xingyun on 2016/7/19.
 * 列表，从下往上，底部显示
 */
public class MenuListDialog extends Dialog {

    public interface ListOnClick{
        /**
         * 0是最底下的取消按钮选项，1-n是从上往下的各个选项
         * @param position
         */
         void onClickItem(int position);
    }

    private ListView lvMenu;
    /**
     * 取消按钮
     */
    private Button buttonCommon3;
    private ListOnClick listOnClick;
    private LinearLayout quitPopupwindowsBg;
    private List<String> menunames;
    private Context context;
    private int color = 0;

    public MenuListDialog(Context context, List<String> menunames) {
        super(context, R.style.BottomDialog2);
        View view = View.inflate(context, R.layout.menulist_popwindow, null);
        this.menunames = menunames;
        this.context = context;
        lvMenu = (ListView) view.findViewById(R.id.lv_menu);
        lvMenu.setAdapter(new MyMenuAdapter());
        buttonCommon3 = (Button) view.findViewById(R.id.button_common3);
        quitPopupwindowsBg = (LinearLayout) view.findViewById(R.id.quit_popupwindows_bg);

        buttonCommon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOnClick != null) {
                    listOnClick.onClickItem(0);
                }
            }
        });

        quitPopupwindowsBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOnClick != null) {
                    listOnClick.onClickItem(0);
                }
            }
        });

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
        if(!ActivityUtil.isActivityOnTop(context)) {
            return;
        }
        window.setWindowAnimations(R.style.BottomDialog);
        setContentView(view);

    }
    public void showAtLocation(){
        if(!ActivityUtil.isActivityOnTop(context)) {
            return;
        }
        show();
    }

    public void setColor(int color) {
        this.color = color;
    }

    private class MyMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return menunames.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = View.inflate(context, R.layout.menulist_item, null);
            Button button = (Button) view.findViewById(R.id.button_common2);
            button.setText(menunames.get(position));
            if (color!=0){
                button.setTextColor(color);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listOnClick != null) {
                        listOnClick.onClickItem(position + 1);
                    }
                }
            });

            return view;
        }
    }

    public void setListOnClick(ListOnClick listOnClick){
        this.listOnClick = listOnClick;
    }


}
