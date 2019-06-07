package com.wewin.live.utils;

import android.app.Activity;

import com.example.jasonutil.permission.Permission;
import com.example.jasonutil.permission.PermissionCallback;
import com.example.jasonutil.permission.Rigger;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.HashMap;
import java.util.List;

/**
 * @author jsaon
 * @date 2019/3/6
 * 打开相册类
 */
public class PictureSelectorUtil {
    public static void selectImage(final Activity activity, final List<LocalMedia> selectList, final boolean isCrop, final int selectNumber, final int requestCode) {
        Rigger.on(activity)
                .permissions(Permission.CAMERA)
                .isShowDialog(true)
                .start(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        PictureSelector.create(activity)
                                //全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                                .openGallery(PictureMimeType.ofImage())
//                        .theme(R.style.picture_white_style)
                                // 最大图片选择数量 int
                                .maxSelectNum(selectNumber)
                                // 每行显示个数 int
                                .imageSpanCount(3)
                                // 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                .selectionMode(PictureConfig.SINGLE)
                                // 是否可预览图片 true or false
                                .previewImage(true)
                                // 是否可预览视频 true or false
                                .previewVideo(true)
                                // 是否可播放音频 true or false
                                .enablePreviewAudio(true)
                                // luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                                .compressGrade(Luban.THIRD_GEAR)
                                // 是否显示拍照按钮 true or false
                                .isCamera(true)
                                // 图片列表点击 缩放效果 默认true
                                .isZoomAnim(true)
                                // glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                                .sizeMultiplier(0.5f)
                                // 自定义拍照保存路径,可不填
                                .setOutputCameraPath("/CustomPath")
                                // 是否裁剪 true or false
                                .enableCrop(isCrop)
                                // 是否压缩 true or false
                                .compress(true)
                                //系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)
                                // int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                .glideOverride(160, 160)
                                // int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                                .withAspectRatio(1, 1)
                                // 是否显示uCrop工具栏，默认不显示 true or false
                                .hideBottomControls(true)
                                // 是否显示gif图片 true or false
                                .isGif(false)
                                // 裁剪框是否可拖拽 true or false
                                .freeStyleCropEnabled(true)
                                // 是否圆形裁剪 true or false
                                .circleDimmedLayer(true)
                                // 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                                .showCropFrame(false)
                                // 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                                .showCropGrid(true)
                                // 是否开启点击声音 true or false
                                .openClickSound(true)
                                // 是否传入已选图片 List<LocalMedia> list
                                .selectionMedia(selectList)
                                // 裁剪是否可旋转图片 true or false
                                .rotateEnabled(true)
                                // 裁剪是否可放大缩小图片 true or false
                                .scaleEnabled(true)
                                //结果回调onActivityResult code
                                .forResult(requestCode);
                    }

                    @Override
                    public void onDenied(HashMap<String, Boolean> permissions) {

                    }
                });

    }


    public static void selectVideo(final Activity activity, final List<LocalMedia> selectList, final int selectNumber, final int requestCode) {
        Rigger.on(activity)
                .permissions(Permission.CAMERA)
                .isShowDialog(true)
                .start(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        PictureSelector.create(activity)
                                //全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                                .openGallery(PictureMimeType.ofVideo())
//                        .theme(R.style.picture_white_style)
                                // 最大图片选择数量 int
                                .maxSelectNum(selectNumber)
                                // 每行显示个数 int
                                .imageSpanCount(3)
                                // 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                .selectionMode(PictureConfig.SINGLE)
                                // 是否可预览图片 true or false
                                .previewImage(true)
                                // 是否可预览视频 true or false
                                .previewVideo(true)
                                // 是否可播放音频 true or false
                                .enablePreviewAudio(true)
                                // luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                                .compressGrade(Luban.THIRD_GEAR)
                                // 是否显示拍照按钮 true or false
                                .isCamera(false)
                                // 图片列表点击 缩放效果 默认true
                                .isZoomAnim(true)
                                // glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                                .sizeMultiplier(0.5f)
                                // 自定义拍照保存路径,可不填
                                .setOutputCameraPath("/CustomPath")
                                // 是否裁剪 true or false
                                .enableCrop(false)
                                // 是否压缩 true or false
                                .compress(true)
                                //系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)
                                // int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                .glideOverride(160, 160)
                                // int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                                .withAspectRatio(1, 1)
                                // 是否显示uCrop工具栏，默认不显示 true or false
                                .hideBottomControls(true)
                                // 是否显示gif图片 true or false
                                .isGif(false)
                                // 裁剪框是否可拖拽 true or false
                                .freeStyleCropEnabled(true)
                                // 是否圆形裁剪 true or false
                                .circleDimmedLayer(true)
                                // 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                                .showCropFrame(false)
                                // 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                                .showCropGrid(true)
                                // 是否开启点击声音 true or false
                                .openClickSound(true)
                                // 是否传入已选图片 List<LocalMedia> list
                                .selectionMedia(selectList)
                                // 裁剪是否可旋转图片 true or false
                                .rotateEnabled(true)
                                // 裁剪是否可放大缩小图片 true or false
                                .scaleEnabled(true)
                                //结果回调onActivityResult code
                                .forResult(requestCode);
                    }

                    @Override
                    public void onDenied(HashMap<String, Boolean> permissions) {

                    }
                });

    }
}
