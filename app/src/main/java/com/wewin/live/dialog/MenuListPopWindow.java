package com.wewin.live.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wewin.live.R;

import java.util.List;

/**
 * Created by xingyun on 2016/7/19.
 */
public class MenuListPopWindow extends PopupWindow implements View.OnClickListener {
    private ListView lvMenu;

    private ListOnClick listOnClick;
    private List<String> menunames;
    private Context context;
    public MyMenuAdapter myMenuAdapter;
    private View view;

    public MenuListPopWindow(Context context, List<String> menunames,View mView) {
        this.menunames = menunames;
        this.context = context;
        init(mView);
    }

    private void init(View mView){
        view = View.inflate(context, R.layout.pop_menu_list, null);
        lvMenu =  view.findViewById(R.id.lv_menu);
        myMenuAdapter=new MyMenuAdapter();
        lvMenu.setAdapter(myMenuAdapter);
        setContentView(view);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        showAsDropDown(mView);
    }

    public MenuListPopWindow setBackground(int color){
        lvMenu.setBackgroundColor(context.getResources().getColor(color));
        return this;
    }

    /**
     * 设置数据
     * @param menuname
     */
    public MenuListPopWindow setList(List<String> menuname){
        menunames.clear();
        menunames.addAll(menuname);
        notifyDataSetChanged();
        return this;
    }


    public MenuListPopWindow notifyDataSetChanged(){
        myMenuAdapter.notifyDataSetChanged();
        return this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_data:
                dismiss();
                break;
            default:
                break;
        }
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
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if(convertView==null) {
                convertView= View.inflate(context, R.layout.pop_menu_list_item, null);
                holder=new ViewHolder();
                convertView.setTag(holder);
                holder.textView=convertView.findViewById(R.id.tv_content);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(menunames.get(position));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (listOnClick != null) {
                        listOnClick.onclickitem(position);
                    }
                }
            });
            return convertView;
        }
        class ViewHolder{
            TextView textView;
        }
    }



    public MenuListPopWindow setListOnClick(ListOnClick listOnClick){
        this.listOnClick = listOnClick;
        return this;
    }


    public interface ListOnClick{
        /**
         * 0是最底下的取消按钮选项，1-n是从上往下的各个选项
         * @param position
         */
        void onclickitem(int position);
    }

}
