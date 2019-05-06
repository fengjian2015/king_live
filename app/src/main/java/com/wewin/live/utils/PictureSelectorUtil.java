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
    public static void selectImage(final Activity activity, final List<LocalMedia> selectList, final boolean isCrop, final int SelectNumber, final int requestCode) {
        Rigger.on(activity)
                .permissions(Permission.CAMERA)
                .isShowDialog(true)
                .start(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        PictureSelector.create(activity)
                                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
//                        .theme(R.style.picture_white_style)
                                .maxSelectNum(SelectNumber)// 最大图片选择数量 int
                                .imageSpanCount(3)// 每行显示个数 int
                                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                .previewImage(true)// 是否可预览图片 true or false
                                .previewVideo(true)// 是否可预览视频 true or false
                                .enablePreviewAudio(true) // 是否可播放音频 true or false
                                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                                .isCamera(true)// 是否显示拍照按钮 true or false
                                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                                .enableCrop(isCrop)// 是否裁剪 true or false
                                .compress(true)// 是否压缩 true or false
                                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                                .glideOverride(160, 160)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                                .isGif(false)// 是否显示gif图片 true or false
                                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                                .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                                .openClickSound(true)// 是否开启点击声音 true or false
                                .selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                                .forResult(requestCode);//结果回调onActivityResult code
                    }

                    @Override
                    public void onDenied(HashMap<String, Boolean> permissions) {

                    }
                });

    }


    public static void selectVideo(final Activity activity, final List<LocalMedia> selectList, final int SelectNumber, final int requestCode) {
        Rigger.on(activity)
                .permissions(Permission.CAMERA)
                .isShowDialog(true)
                .start(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        PictureSelector.create(activity)
                                .openGallery(PictureMimeType.ofVideo())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
//                        .theme(R.style.picture_white_style)
                                .maxSelectNum(SelectNumber)// 最大图片选择数量 int
                                .imageSpanCount(3)// 每行显示个数 int
                                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                .previewImage(true)// 是否可预览图片 true or false
                                .previewVideo(true)// 是否可预览视频 true or false
                                .enablePreviewAudio(true) // 是否可播放音频 true or false
                                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.THIRD_GEAR、Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                                .isCamera(false)// 是否显示拍照按钮 true or false
                                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                                .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                                .enableCrop(false)// 是否裁剪 true or false
                                .compress(true)// 是否压缩 true or false
                                .compressMode(PictureConfig.SYSTEM_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                                .glideOverride(160, 160)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                                .isGif(false)// 是否显示gif图片 true or false
                                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                                .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                                .openClickSound(true)// 是否开启点击声音 true or false
                                .selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                                .forResult(requestCode);//结果回调onActivityResult code
                    }

                    @Override
                    public void onDenied(HashMap<String, Boolean> permissions) {

                    }
                });

    }
}
