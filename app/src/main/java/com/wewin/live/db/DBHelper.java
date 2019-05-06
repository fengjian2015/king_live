package com.wewin.live.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author jsaon
 * @date 2019/3/15
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "king.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_USER);
    }

    /**
     * 不要加break，用於夸版本升級
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String userCodeDB;
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE "+UserInfoDao.TABLE_NAME +" ADD "+ UserInfoDao.JSON+" TEXT");
                break;
        }
    }

    private final String TABLE_USER = "create table "
            + UserInfoDao.TABLE_NAME + "(id integer primary key autoincrement,"
            + UserInfoDao.ACTUALNAME + " varchar, "
            + UserInfoDao.NICKNAME + " varchar, "
            + UserInfoDao.SEX + " varchar, "
            + UserInfoDao.AVATAR + " varchar, "
            + UserInfoDao.BIRTH + " varchar, "
            + UserInfoDao.SIGNATURE + " varchar, "
            + UserInfoDao.EMAIL + " varchar, "
            + UserInfoDao.USER + " varchar, "
            + UserInfoDao.AVATAR_THUMB + " varchar, "
            + UserInfoDao.COIN + " varchar, "
            + UserInfoDao.WEIXIN + " varchar, "
            + UserInfoDao.LEVEL + " varchar, "
            + UserInfoDao.CONSUMPTION + " varchar, "
            + UserInfoDao.LEVEL_UP + " varchar, "
            + UserInfoDao.LEVEL_ICON + " varchar, "
            + UserInfoDao.ISANCHOR + " varchar, "
            + UserInfoDao.JSON + " varchar, "
            + UserInfoDao.USER_ID + " varchar);";
}
