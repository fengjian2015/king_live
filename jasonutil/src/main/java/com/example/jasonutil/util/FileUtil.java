package com.example.jasonutil.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.UUID;

/**
 * @author jsaon
 * @date 2019/3/1
 * 文件相关类
 */
public class FileUtil {
    public static final String CACH = "/king/";
    public static final String LOG_DIR = "log/";
    public static final String APK_LOC =  "apk/";
    public static final String VIDEO_DIR =  "video/";
    public static final String IMAGE_DIR ="image/";
    public static final String ASSETS_DIR ="assets/";
    public static final String ANIMATION_DIR ="animation/";

    //声明各种类型文件的dataType
    public static final String DATA_TYPE_APK = "application/vnd.android.package-archive";
    public static final String DATA_TYPE_VIDEO = "video/*";
    public static final String DATA_TYPE_AUDIO = "audio/*";
    public static final String DATA_TYPE_HTML = "text/html";
    public static final String DATA_TYPE_IMAGE = "image/*";
    public static final String DATA_TYPE_IMAGE_PNG = "image/png";
    public static final String DATA_TYPE_PPT = "application/vnd.ms-powerpoint";
    public static final String DATA_TYPE_EXCEL = "application/vnd.ms-excel";
    public static final String DATA_TYPE_WORD = "application/msword";
    public static final String DATA_TYPE_CHM = "application/x-chm";
    public static final String DATA_TYPE_TXT = "text/plain";
    public static final String DATA_TYPE_PDF = "application/pdf";
    //未指定明确的文件类型，不能使用精确类型的工具打开，需要用户选择
    public static final String DATA_TYPE_ALL = "*/*";
    /*-------文件后缀---------*/
    public static final String FILE_VIDEO_MP4 = ".mp4";
    public static final String FILE_IMAGE_PNG = ".png";

    //apk名字
    public static final String FILE_APK_NAME="wangzhezhibo";


    /**
     * 创建apk下载文件名，传入版本号区分
     * @param versionName
     * @return
     */
    public static String getAPKFileName(String versionName) {
        return FILE_APK_NAME+versionName+".apk" ;
    }

    /**
     * 获取assets存储地址
     * @return
     */
    public static String getAssetsDirString(Context context) {
        createNomedia(getStorageFile(context).getAbsolutePath() + CACH + ASSETS_DIR);
        return getStorageFile(context).getAbsolutePath() + CACH + ASSETS_DIR;
    }
    /**
     * 获取图片存储地址
     * @return
     */
    public static String getImageDirString(Context context) {
        createNomedia(getStorageFile(context).getAbsolutePath() + CACH + IMAGE_DIR);
        return getStorageFile(context).getAbsolutePath() + CACH + IMAGE_DIR;
    }

    /**
     * 获取视频存储地址
     * @return
     */
    public static String getVideoDirString(Context context) {
        createNomedia(getStorageFile(context).getAbsolutePath() + CACH + VIDEO_DIR);
        return getStorageFile(context).getAbsolutePath() + CACH + VIDEO_DIR;
    }

    /**
     * 获取日志存储地址
     * @return
     */
    public static String getLogDirString(Context context) {
        createNomedia(getStorageFile(context).getAbsolutePath() + CACH + LOG_DIR);
        return getStorageFile(context).getAbsolutePath() + CACH + LOG_DIR;
    }

    /**
     * 获取apk下载位置
     *
     * @return
     */
    public static String getApkLoc(Context context) {
        createFile(getStorageFile(context).getAbsolutePath() + CACH + APK_LOC);
        return getStorageFile(context).getAbsolutePath() + CACH + APK_LOC;
    }


    /**
     * 获取d动画下载地址
     *
     * @return
     */
    public static String getAnimationLoc(Context context) {
        createFile(getStorageFile(context).getAbsolutePath() + CACH + ANIMATION_DIR);
        return getStorageFile(context).getAbsolutePath() + CACH + ANIMATION_DIR;
    }

    /**
     * 获取存储地址
     *
     * @param context
     * @return
     */
    public static String getStorageString(Context context) {
        return getStorageFile(context).getAbsolutePath() + CACH;
    }

    /**
     * 获取存储地址，判断是否有外部存储
     *
     * @param context
     * @return
     */
    public static File getStorageFile(Context context) {
        if (isExternalStorage()) {
            return getEx();
        } else {
            return getDir(context);
        }
    }

    /**
     * 判断是否有外部存储
     *
     * @return
     */
    public static boolean isExternalStorage() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取外部存储根目录
     *
     * @return
     */
    public static File getEx() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 获取内部存储根目录
     *
     * @param context
     * @return
     */
    public static File getDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * 获取文件大小
     *
     * @param file
     * @return
     */
    public static long getFolderSize(File file) {
        long size = 0;
        if (file.exists()) {
            size = 0;
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].isDirectory()) {
                        size = size + getFolderSize(fileList[i]);
                    } else {
                        size = size + fileList[i].length();
                    }
                }
            } else {
                size = file.length();
            }
        }
        return size;
    }

    /**
     * 转换数据格式
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("0.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = UtilTool.removeZero(df.format((double) fileS)) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = UtilTool.removeZero(df.format((double) fileS / 1024)) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = UtilTool.removeZero(df.format((double) fileS / 1048576)) + "M";
        } else {
            fileSizeString = UtilTool.removeZero(df.format((double) fileS / 1073741824)) + "G";
        }
        return fileSizeString;
    }

    /**
     * 创建.nomedia文件，不显示图片在系统相册
     *
     * @param file
     */
    public static void createNomedia(String file) {
        File cacheDir = new File(file);
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        File nomedia = new File(file + "/.nomedia");
        if (!nomedia.exists())
            try {
                nomedia.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * 创建文件夹
     *
     * @param filePath
     */
    public static void createFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param filePath
     * @param deleteThisPath
     * @return
     */
    public static boolean deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }


    /**
     * 获取本地视频第一帧
     * @param videoPath
     * @return
     */
    public static Bitmap getVideoFirst(String videoPath) {
        if(StringUtils.isEmpty(videoPath))return null;
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);// videoPath 本地视频的路径
        Bitmap bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        return bitmap;
    }


    /**
     * 创建文件名
     * @param suffix
     * @return
     */
    public static String createFileName(String suffix) {
        return (UUID.randomUUID().toString()+suffix) ;

    }

    /**
     * 把batmap 转file
     *
     * @param bitmap
     * @param filepath
     */
    public static File saveBitmapFile(Bitmap bitmap, String filepath) {
        File file = new File(filepath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 复制单个文件
     *
     * @param fileName String 要复制的文件名 如：abc.txt
     * @return <code>true</code> if and only if the file was copied; <code>false</code> otherwise
     */
    public static boolean copyAssetsSingleFile(Context context, String fileName) {
        File file = new File(getAssetsDirString(context));
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return false;
            }
        }
        try {
            File outFile = new File(file, fileName);
            if(outFile.exists())return true;
            InputStream inputStream =context.getAssets().open(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(outFile);
            // Transfer bytes from inputStream to fileOutputStream
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = inputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            inputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 创建apk下载文件名，传入版本号区分
     * @param context
     * @param versionName
     * @return
     */
    public static File createAPKFile(Context context,String versionName) {
        String root =FileUtil.getApkLoc(context);
        FileUtil.createFile(root);
        File file = new File(root,getAPKFileName(versionName));
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null ;
    }

    /**
     * 创建动画json下载文件名
     * @param context
     * @param fileName
     * @return
     */
    public static File createAnimationFile(Context context,String fileName) {
        String root =FileUtil.getAnimationLoc(context);
        File file = new File(root,fileName+".json" );
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null ;
    }
}
