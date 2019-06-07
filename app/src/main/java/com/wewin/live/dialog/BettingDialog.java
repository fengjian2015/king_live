package com.wewin.live.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.view.menu.MenuAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.jasonutil.util.ActivityUtil;
import com.wewin.live.R;
import com.wewin.live.modle.BaseInfoConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jason
 * 投注弹窗
 */

public class BettingDialog extends Dialog {
    private GridView gvType;
    private EditText etAmount;
    private GridView gvAmount;
    private Button btnConfirm;

    private List<Map> typeList=new ArrayList<>();
    private List<Map> amountList=new ArrayList<>();

    private MenuAdapter typeMenuAdapter;
    private MenuAdapter amountMenuAdapter;
    private String amount="[{\"name\":\"100\"},{\"name\":\"200\"},{\"name\":\"400\"},{\"name\":\"800\"},{\"name\":\"1000\"},{\"name\":\"2000\"}]";
    /**
     * 记录选择项
     */
    private int typeSelect=0;
    private int amountSelect=0;

    private OnClickListener onClickListener;
    private Context context;

    public BettingDialog(Context context) {
        super(context, R.style.dialog);
        this.context=context;
        createDialog(context);
    }

    public BettingDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context=context;
        createDialog(context);
    }

    public BettingDialog setTypeList(List<Map> typeList){
        if (typeList==null||typeList.size()<=0){
            return this;
        }
        this.typeList.clear();
        this.typeList.addAll(typeList);
        typeMenuAdapter.notifyDataSetChanged();
        gvType.setVisibility(View.VISIBLE);
        return this;
    }

    private void createDialog(Context context) {
        View view = View.inflate(context, R.layout.dialog_betting, null);
        gvType=view.findViewById(R.id.gv_type);
        etAmount=view.findViewById(R.id.et_amount);
        gvAmount=view.findViewById(R.id.gv_amount);
        btnConfirm=view.findViewById(R.id.btn_confirm);
        setContentView(view);
        init();
    }

    public BettingDialog showDialog(){
        if (ActivityUtil.isActivityOnTop(context)) {
            setCanceledOnTouchOutside(true);
            show();
        }
        return this;
    }

    private void init() {
        amountList=  JSON.parseArray(amount,Map.class);
        gvAmount.setVisibility(View.VISIBLE);
        typeMenuAdapter=new MenuAdapter(typeList);
        etAmount.setText(amountList.get(0).get(BaseInfoConstants.NAME)+"");
        etAmount.setSelection(etAmount.getText().length());
        gvType.setSelection(typeSelect);
        gvType.setAdapter(typeMenuAdapter);
        gvType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                typeSelect=position;
                typeMenuAdapter.setSelect(typeSelect);
                typeMenuAdapter.notifyDataSetChanged();
            }
        });

        amountMenuAdapter=new MenuAdapter(amountList);
        amountMenuAdapter.setSelect(amountSelect);
        gvAmount.setAdapter(amountMenuAdapter);
        gvAmount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                amountSelect=position;
                amountMenuAdapter.setSelect(amountSelect);
                etAmount.setText(amountList.get(amountSelect).get(BaseInfoConstants.NAME)+"");
                etAmount.setSelection(etAmount.getText().length());
                amountMenuAdapter.notifyDataSetChanged();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (onClickListener != null) {
                    String typeId;
                    if (typeList==null||typeList.size()<=0){
                        typeId="";
                    }else {
                        typeId=typeList.get(typeSelect).get(BaseInfoConstants.ID)+"";
                    }
                    onClickListener.onClick(typeId, amountList.get(amountSelect).get(BaseInfoConstants.NAME) + "", etAmount.getText().toString());
                }
            }
        });
    }

    private class MenuAdapter extends BaseAdapter {
        private List<Map> list;
        private int select;
        public MenuAdapter(List<Map> list) {
            this.list=list;
        }

        public void setSelect(int select){
            this.select=select;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if(convertView==null) {
                convertView= View.inflate(context, R.layout.dialog_betting_type_item, null);
                holder=new ViewHolder();
                convertView.setTag(holder);
                holder.textView=convertView.findViewById(com.wewin.live.R.id.tv_content);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(list.get(position).get(BaseInfoConstants.NAME)+"");
            if(select==position){
                holder.textView.setSelected(true);
            }else {
                holder.textView.setSelected(false);
            }
            return convertView;
        }
        class ViewHolder{
            TextView textView;
        }
    }

    public BettingDialog setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public interface OnClickListener {
        /**
         * 点击事件返回选择项和金额
         * @param typeId
         * @param amountSelect
         * @param amount
         */
        void onClick(String typeId,String amountSelect,String amount);
    }
}
