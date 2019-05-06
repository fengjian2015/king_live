package com.wewin.live.ui.activity.video;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.dialog.MenuListPopWindow;
import com.wewin.live.modle.BaseMapInfo2;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterMedia;
import com.example.jasonutil.util.AnimatorTool;
import com.example.jasonutil.util.FileUtil;
import com.wewin.live.utils.IntentStart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class VideoUploadActivity extends BaseActivity {
    @InjectView(R.id.iv_video_bg)
    ImageView ivVideoBg;
    @InjectView(R.id.rl_select)
    RelativeLayout rlSelect;
    @InjectView(R.id.tv_class)
    TextView tvClass;
    @InjectView(R.id.et_title)
    EditText etTitle;

    private String file_url;//视频地址
    //分类数据
    private List<String> mStringList = new ArrayList<>();

    private String image_url;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_upload;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.add_details));
        setIvMore(R.mipmap.icon_video_send);
        initIntent();
        initData();
        initHttp();
    }

    /**
     * 获取上个界面数据
     */
    private void initIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        file_url = bundle.getString(IntentStart.FILE_URL);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (!StringUtils.isEmpty(file_url)) {
            ivVideoBg.setImageBitmap(getImagePathBitmap());
        }
    }

    /**
     * 获取分类
     */
    private void initHttp() {
        new PersenterMedia().getCategories(new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                mStringList.clear();
                BaseMapInfo2 baseMapInfo2 = (BaseMapInfo2) content;
                mStringList.addAll((List<String>) baseMapInfo2.getData());
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * 上传视频地址
     *
     * @param file
     */
    private void uploadVideo(File file) {
        new PersenterMedia().uploadVideo(file, new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                BaseMapInfo2 baseMapInfo2= (BaseMapInfo2) content;
                contribute(baseMapInfo2.getData()+"");
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * 投稿
     */
    private void contribute(String videoUrlLocal) {
        //为空则重新获取
        if(StringUtils.isEmpty(image_url)){

        }
        new PersenterMedia().contribute(mStringList.indexOf(tvClass.getText().toString())
                , etTitle.getText().toString(), videoUrlLocal,new File(image_url), new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                ToastShow.showToast2(VideoUploadActivity.this,"上传成功"+content.toString());
            }

                    @Override
                    public void onFault(String error) {

                    }
                }));
    }

    /**
     * 检查内容是否正常
     */
    private boolean check() {
        if (StringUtils.isEmpty(etTitle.getText().toString())) {
            AnimatorTool.getInstance().editTextAnimator(etTitle);
            ToastShow.showToast2(this, getString(R.string.title_cannot_empty));
            return false;
        }else if (mStringList.size()<=0||!mStringList.contains(tvClass.getText().toString())) {
            AnimatorTool.getInstance().editTextAnimator(tvClass);
            ToastShow.showToast2(this, getString(R.string.select_class));
            return false;
        }
        return true;
    }

    /**
     * 保存封面图并返回bitmap
     * @return
     */
    private Bitmap getImagePathBitmap(){
        Bitmap bitmap=FileUtil.getVideoFirst(file_url);
        image_url=FileUtil.getImageDirString(VideoUploadActivity.this)+FileUtil.createFileName(FileUtil.FILE_IMAGE_PNG);
        FileUtil.saveBitmapFile(bitmap,image_url);
        return bitmap;
    }


    @OnClick({R.id.iv_more, R.id.rl_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_more:
                if(check()){
                    uploadVideo(new File(file_url));
                }
                break;
            case R.id.rl_select:
                showSelect();
                break;
        }
    }

    /**
     * 显示选择弹窗
     */
    private void showSelect() {
        if (mStringList.size() <= 0) {
            initHttp();
            return;
        }
        new MenuListPopWindow(this, mStringList, rlSelect).setBackground(R.color.white_bg).setListOnClick(new MenuListPopWindow.ListOnClick() {
            @Override
            public void onclickitem(int position) {
                tvClass.setText(mStringList.get(position));
            }
        });
    }
}
