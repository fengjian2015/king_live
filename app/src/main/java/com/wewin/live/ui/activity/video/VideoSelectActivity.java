package com.wewin.live.ui.activity.video;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.example.jasonutil.util.FileUtil;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.PictureSelectorUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class VideoSelectActivity extends BaseActivity {
    private List<LocalMedia> selectList = new ArrayList<>();
    private final int REQUEST_CODE_CAMERA = 123;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_select;
    }

    @Override
    protected void init() {
        setTitle("");
    }

    @OnClick({R.id.ll_video, R.id.ll_take})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_video:
                PictureSelectorUtil.selectVideo(VideoSelectActivity.this, selectList, 1, PictureConfig.CHOOSE_REQUEST);
                break;
            case R.id.ll_take:
                IntentStart.takeVideo(VideoSelectActivity.this, REQUEST_CODE_CAMERA);
                break;
            default:
                break;
        }
    }

    private void goDetails(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(IntentStart.FILE_URL, url);
        IntentStart.star(this, VideoUploadActivity.class,bundle);
        finish();
    }

    //拿到选择的视频
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    goDetails(selectList.get(0).getPath());
                    break;
                case REQUEST_CODE_CAMERA:
                    Uri uri = data.getData();
                    Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
                        // 视频路径
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                        String newPath = FileUtil.getVideoDirString(VideoSelectActivity.this) + FileUtil.createFileName(FileUtil.FILE_VIDEO_MP4);
                        FileUtil.copyFile(filePath, newPath);
                        cursor.close();
                        goDetails(newPath);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
