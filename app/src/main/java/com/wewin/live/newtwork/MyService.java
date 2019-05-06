package com.wewin.live.newtwork;



import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import io.reactivex.Observable;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by GA on 2017/10/27.
 * 这里由于后台接口混乱，所以有些接口需要自己配置很长一串
 */

public interface MyService {
    String base="api/public/?service=";
    String base1="index.php?g=Wwapi&m=";
    //登录
    @FormUrlEncoded
    @POST(base+"Login.userLogin")
    Observable<ResponseBody> sendlogin(
            @Field("user_login") String user_login,
            @Field("user_pass") String user_pass
    );

    //顶部导航栏
    @GET(base1+"Sysconf&a=getNavMenu")
    Observable<ResponseBody> getNavMenu(
    );

    //获取验证码
    @GET(base+"Login.getForgetCode")
    Observable<ResponseBody> getForGetCode(
            @Query("mobile") String mobile
    );

    //获取忘记密码验证码
    @GET(base+"Login.getCode")
    Observable<ResponseBody> getLoginCode(
            @Query("mobile") String mobile
    );

    //忘记密码
    @FormUrlEncoded
    @POST(base+"Login.userFindPass")
    Observable<ResponseBody> userFindPass(
            @Field("user_login") String user_login,
            @Field("user_pass") String user_pass,
            @Field("code") String code
    );

    //注册
    @FormUrlEncoded
    @POST(base+"Login.userReg")
    Observable<ResponseBody> registeren(
            @Field("user_login") String user_login,
            @Field("user_pass") String user_pass,
            @Field("user_nicename") String user_nicename,
            @Field("code") String code
    );

    //微信获取token
    @GET("oauth2/access_token")
    Observable<ResponseBody> getAccessToken(
            @Query("appid") String appid,
            @Query("secret") String secret,
            @Query("code") String code,
            @Query("grant_type") String grant_type
    );

    //微信获取用户信息
    @GET("userinfo")
    Observable<ResponseBody> getWXUserInfo(
            @Query("access_token") String access,
            @Query("openId") String openId
    );
    /*上传文件*/
    @Multipart
    @POST(base+"User.updateAvatar")
    Observable<ResponseBody> uploadImage(
            @Part("uid") RequestBody  uid,
            @Part("token") RequestBody  token,
            @Part MultipartBody.Part file
    );


    //修改用户信息
    //fields  传入jason字符串
    @FormUrlEncoded
    @POST(base+"User.updateFields")
    Observable<ResponseBody> updateFields(
            @Field("uid") int user_login,
            @Field("token") String token,
            @Field("fields") String fields
    );

    //顶部导航栏
    @GET(base1+"User&a=index")
    Observable<ResponseBody> getPersonl(
            @Query("uid") int uid,
            @Query("token") String token
    );

    //获取视频分类
    @GET(base1+"Shortvideo&a=getCategories")
    Observable<ResponseBody> getCategories(
    );

    //上传短视频
    @Multipart
    @POST(base1+"Shortvideo&a=upload")
    Observable<ResponseBody> uploadVideo(
            @Part("uid") RequestBody  uid,
            @Part("token") RequestBody  token,
            @Part MultipartBody.Part file
    );

    //视频投稿
    //fields  传入jason字符串
    @Multipart
    @POST(base1+"Shortvideo&a=contribute")
    Observable<ResponseBody> contribute(
            @Part("uid") RequestBody uid,
            @Part("token") RequestBody token,
            @Part("catid") RequestBody catid,
            @Part("title") RequestBody title,
            @Part("videoUrlLocal") RequestBody videoUrlLocal,
            @Part MultipartBody.Part file
    );

    //国家码列表
    @GET(base1+"Sysconf&a=getCountryCode")
    Observable<ResponseBody> getCountryCode(
    );

    //任务中心
    @GET(base1+"User&a=taskCenter")
    Observable<ResponseBody> taskCenter(
            @Query("uid") int uid,
            @Query("token") String token
    );


    //领取奖励任务中心
    @GET(base1+"User&a=claim")
    Observable<ResponseBody> taskCenterClaim(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("action") String action
    );

    //意见反馈
    @GET("index.php?g=Appapi&m=Feedback&a=feedbackSave")
    Observable<ResponseBody> feedback(
            @Query("uid") int uid,
            @Query("content") String content,
            @Query("version") String version
    );

    //签到
    @GET(base1+"User&a=signin")
    Observable<ResponseBody> signin(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("day") int day
    );

    //获取主播配置信息
    @GET(base+"Home.getConfig")
    Observable<ResponseBody> getConfig(
    );

    //绑定邮箱
    @GET(base+"User.bindEmail")
    Observable<ResponseBody> bindEmail(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("code") String code,
            @Query("email") String email
    );

    //创建直播间
    @FormUrlEncoded
    @POST(base+"Live.createRoom")
    Observable<ResponseBody> createLive(
            @Field("uid") int user_login,
            @Field("token") String token,
            @Field("title") String title,
            @Field("type") int type,
            @Field("type_val") String type_val,
            @Field("zbtype") int zbtype,
            @Field("zbtype_detail") String zbtype_detail
    );

    //获取个人信息
    @GET(base+"User.getBaseInfo")
    Observable<ResponseBody> getBaseInfo(
            @Query("uid") int uid,
            @Query("token") String token
    );

    //修改手机号
    @FormUrlEncoded
    @POST(base+"User.modifyMobile")
    Observable<ResponseBody> modifyMobile(
            @Field("uid") int uid,
            @Field("token") String token,
            @Field("newMobile") String newMobile,
            @Field("code") String code,
            @Field("user_pass") String user_pass

    );

    //获取修改手机验证码
    @GET(base+"User.getBindCode")
    Observable<ResponseBody> getBindCode(
            @Query("mobile") String mobile
    );


    //搜索全部
    @GET(base1+"Search&a=index")
    Observable<ResponseBody> searchAll(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("keyword") String keyword
    );

    //搜索指定频道
    @GET(base1+"search&a=channel")
    Observable<ResponseBody> searchType(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("keyword") String keyword,
            @Query("name") String name,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );


    //获取热门关键词列表
    @GET(base1+"search&a=getHotKeywords")
    Observable<ResponseBody> getHotKeywords(
            @Query("number") int number
    );
}
