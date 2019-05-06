package com.wewin.live.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.wewin.live.modle.UserInfo;
import com.wewin.live.utils.SignOutUtil;

/**
 * @author jsaon
 * @date 2019/3/15
 */
public class UserInfoDao {

    public static final String TABLE_NAME = "userinfo";

    public static final String USER_ID = "user_id";
    public static final String USER = "user";
    public static final String ACTUALNAME = "actualName";
    public static final String NICKNAME = "nickName";
    public static final String SEX = "sex";
    public static final String AVATAR = "avatar";
    public static final String BIRTH = "birth";
    public static final String SIGNATURE = "signature";
    public static final String EMAIL = "email";
    public static final String AVATAR_THUMB="avatar_thumb";
    public static final String COIN="coin";
    public static final String WEIXIN="weixin";
    public static final String LEVEL="level";
    public static final String CONSUMPTION="consumption";
    public static final String LEVEL_UP="level_up";
    public static final String LEVEL_ICON="level_icon";
    public static final String ISANCHOR="isanchor";
    public static final String JSON="json";
    /**
     * 添加
     * @param userInfo
     */
    public static void addUser(UserInfo userInfo) {
        if (findUser(userInfo.getUser_id())) {
            updateUser(userInfo);
            return;
        }
        SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
        ContentValues values = getContentValues(userInfo);
        db.insert(TABLE_NAME, null, values);
        DatabaseManager.getInstance().closeWritableDatabase();
    }


    /**
     * 查询是否存在
     * @param user_id
     * @return
     */
    public static boolean findUser(String user_id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + USER_ID + "=?",
                new String[]{user_id});
        boolean result = cursor.moveToNext();
        cursor.close();
        DatabaseManager.getInstance().closeWritableDatabase();
        return result;
    }

    /**
     * 获取昵称
     * @return
     */
    public static String findNickName() {
        try {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String name = null;
            Cursor cursor = db.rawQuery("select "+NICKNAME+" from "+TABLE_NAME+" where "+USER_ID+"=?",
                    new String[]{SignOutUtil.getUserId()});
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex(NICKNAME));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取等级
     * @return
     */
    public static String findLevel() {
        try {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String level = null;
            Cursor cursor = db.rawQuery("select "+LEVEL+" from "+TABLE_NAME+" where "+USER_ID+"=?",
                    new String[]{SignOutUtil.getUserId()});
            while (cursor.moveToNext()) {
                level = cursor.getString(cursor.getColumnIndex(LEVEL));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return level;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取当前账号是否为主播
     * @return
     */
    public static String findisanchor() {
        try {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String isanchor = null;
            Cursor cursor = db.rawQuery("select "+ISANCHOR+" from "+TABLE_NAME+" where "+USER_ID+"=?",
                    new String[]{SignOutUtil.getUserId()});
            while (cursor.moveToNext()) {
                isanchor = cursor.getString(cursor.getColumnIndex(ISANCHOR));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return isanchor;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取余额
     * @return
     */
    public static String findCoin() {
        try {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String coin = null;
            Cursor cursor = db.rawQuery("select "+COIN+" from "+TABLE_NAME+" where "+USER_ID+"=?",
                    new String[]{SignOutUtil.getUserId()});
            while (cursor.moveToNext()) {
                coin = cursor.getString(cursor.getColumnIndex(COIN));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return coin;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取头像
     * @return
     */
    public static String findAvatar() {
        try {
            SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
            String avatar = null;
            Cursor cursor = db.rawQuery("select "+AVATAR+" from "+TABLE_NAME+" where "+USER_ID+"=?",
                    new String[]{SignOutUtil.getUserId()});
            while (cursor.moveToNext()) {
                avatar = cursor.getString(cursor.getColumnIndex(AVATAR));
            }
            cursor.close();
            DatabaseManager.getInstance().closeWritableDatabase();
            return avatar;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 修改某个用户的信息
     * @param userInfo
     */
    public static void updateUser(UserInfo userInfo) {
        SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
        ContentValues values = getContentValues(userInfo);
        db.update(TABLE_NAME, values, USER_ID + "=?", new String[]{userInfo.getUser_id()});
        DatabaseManager.getInstance().closeWritableDatabase();
    }

    /**
     * 修改用户的邮箱
     * @param email
     */
    public static void updateEmail(String email,String user_id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(true);
        ContentValues values = new ContentValues();
        values.put(EMAIL, email);
        db.update(TABLE_NAME, values, USER_ID + "=?", new String[]{user_id});
        DatabaseManager.getInstance().closeWritableDatabase();
    }


    /**
     * 查询某个用户的信息
     * @param user_id
     * @return
     */
    public static UserInfo queryUserInfo(String user_id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openWritableDatabase(false);
        UserInfo userInfo = new UserInfo();
        String sql = "select * from " + TABLE_NAME + " where " + USER_ID + "=?";
        Cursor c = db.rawQuery(sql, new String[]{user_id});
        if (c != null) {
            if (c.moveToNext()) {
                userInfo=getUserInfo(c);
            }
            c.close();
        }
        DatabaseManager.getInstance().closeWritableDatabase();
        return userInfo;
    }


    private static ContentValues getContentValues(UserInfo userInfo){
        ContentValues values = new ContentValues();
        values.put(USER_ID, userInfo.getUser_id());
        values.put(USER, userInfo.getUser());
        values.put(ACTUALNAME, userInfo.getActualName());
        values.put(NICKNAME, userInfo.getNickName());
        values.put(SEX, userInfo.getSex());
        values.put(AVATAR, userInfo.getAvatar());
        values.put(BIRTH, userInfo.getBirth());
        values.put(SIGNATURE, userInfo.getSignature());
        values.put(EMAIL, userInfo.getEmail());
        values.put(AVATAR_THUMB,userInfo.getAvatar_thumb());
        values.put(COIN,userInfo.getCoin());
        values.put(WEIXIN,userInfo.getWeixin());
        values.put(LEVEL,userInfo.getLevel());
        values.put(CONSUMPTION,userInfo.getConsumption());
        values.put(LEVEL_UP,userInfo.getLevel_up());
        values.put(LEVEL_ICON,userInfo.getLevel_icon());
        values.put(ISANCHOR,userInfo.getIsanchor());
        values.put(JSON,userInfo.getJson());
        return values;
    }

    private static UserInfo getUserInfo(Cursor c){
        UserInfo userInfo = new UserInfo();
        userInfo.setUser_id(c.getString(c.getColumnIndex(USER_ID)));
        userInfo.setUser(c.getString(c.getColumnIndex(USER)));
        userInfo.setActualName(c.getString(c.getColumnIndex(ACTUALNAME)));
        userInfo.setNickName(c.getString(c.getColumnIndex(NICKNAME)));
        userInfo.setSex(c.getString(c.getColumnIndex(SEX)));
        userInfo.setAvatar(c.getString(c.getColumnIndex(AVATAR)));
        userInfo.setBirth(c.getString(c.getColumnIndex(BIRTH)));
        userInfo.setSignature(c.getString(c.getColumnIndex(SIGNATURE)));
        userInfo.setEmail(c.getString(c.getColumnIndex(EMAIL)));
        userInfo.setAvatar_thumb(c.getString(c.getColumnIndex(AVATAR_THUMB)));
        userInfo.setCoin(c.getString(c.getColumnIndex(COIN)));
        userInfo.setWeixin(c.getString(c.getColumnIndex(WEIXIN)));
        userInfo.setLevel(c.getString(c.getColumnIndex(LEVEL)));
        userInfo.setConsumption(c.getString(c.getColumnIndex(CONSUMPTION)));
        userInfo.setLevel_up(c.getString(c.getColumnIndex(LEVEL_UP)));
        userInfo.setLevel_icon(c.getString(c.getColumnIndex(LEVEL_ICON)));
        userInfo.setIsanchor(c.getString(c.getColumnIndex(ISANCHOR)));
        userInfo.setJson(c.getString(c.getColumnIndex(JSON)));
        return userInfo;
    }
}
