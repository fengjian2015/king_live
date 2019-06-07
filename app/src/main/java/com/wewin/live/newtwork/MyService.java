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
    String BASE="api/public/?service=";
    String BASE1="index.php?g=Wwapi&m=";

    /**
     * 登录
     * @param userLogin
     * @param userPass
     * @return
     */
    @FormUrlEncoded
    @POST(BASE+"Login.userLogin")
    Observable<ResponseBody> sendlogin(
            @Field("user_login") String userLogin,
            @Field("user_pass") String userPass
    );

    /**
     * 顶部导航栏
     * @return
     */
    @GET(BASE1+"Sysconf&a=getNavMenu")
    Observable<ResponseBody> getNavMenu(
    );

    /**
     * 获取验证码
     * @param mobile
     * @return
     */
    @GET(BASE+"Login.getForgetCode")
    Observable<ResponseBody> getForGetCode(
            @Query("mobile") String mobile
    );

    /**
     * 获取忘记密码验证码
     * @param mobile
     * @return
     */
    @GET(BASE+"Login.getCode")
    Observable<ResponseBody> getLoginCode(
            @Query("mobile") String mobile
    );

    /**
     * 忘记密码
     * @param userLogin 登录用户名
     * @param userPass 登录密码
     * @param code 某个登录号
     * @return
     */
    @FormUrlEncoded
    @POST(BASE+"Login.userFindPass")
    Observable<ResponseBody> userFindPass(
            @Field("user_login") String userLogin,
            @Field("user_pass") String userPass,
            @Field("code") String code
    );

    /**
     * 注册
     * @param userLogin
     * @param userPass
     * @param userNiceName
     * @param code
     * @return
     */
    @FormUrlEncoded
    @POST(BASE+"Login.userReg")
    Observable<ResponseBody> registeren(
            @Field("user_login") String userLogin,
            @Field("user_pass") String userPass,
            @Field("user_nicename") String userNiceName,
            @Field("code") String code
    );

    /**
     * 微信获取token
     * @param appId
     * @param secret
     * @param code
     * @param grantType
     * @return
     */
    @GET("oauth2/access_token")
    Observable<ResponseBody> getAccessToken(
            @Query("appid") String appId,
            @Query("secret") String secret,
            @Query("code") String code,
            @Query("grant_type") String grantType
    );

    /**
     * 微信获取用户信息
     * @param access
     * @param openId
     * @return
     */
    @GET("userinfo")
    Observable<ResponseBody> getWXUserInfo(
            @Query("access_token") String access,
            @Query("openId") String openId
    );

    /**
     * 上传文件
     * @param uid
     * @param token
     * @param file
     * @return
     */
    @Multipart
    @POST(BASE+"User.updateAvatar")
    Observable<ResponseBody> uploadImage(
            @Part("uid") RequestBody  uid,
            @Part("token") RequestBody  token,
            @Part MultipartBody.Part file
    );


    /**
     * 修改用户信息  fields  传入jason字符串
     * @param userLogin
     * @param token
     * @param fields
     * @return
     */
    @FormUrlEncoded
    @POST(BASE+"User.updateFields")
    Observable<ResponseBody> updateFields(
            @Field("uid") int userLogin,
            @Field("token") String token,
            @Field("fields") String fields
    );

    /**
     * 顶部导航栏
     * @param uid
     * @param token
     * @return
     */
    @GET(BASE1+"User&a=index")
    Observable<ResponseBody> getPersonl(
            @Query("uid") int uid,
            @Query("token") String token
    );

    /**
     * 获取视频分类
     * @return
     */
    @GET(BASE1+"Shortvideo&a=getCategories")
    Observable<ResponseBody> getCategories(
    );

    /**
     * 上传短视频
     * @param uid
     * @param token
     * @param file
     * @return
     */
    @Multipart
    @POST(BASE1+"Shortvideo&a=upload")
    Observable<ResponseBody> uploadVideo(
            @Part("uid") RequestBody  uid,
            @Part("token") RequestBody  token,
            @Part MultipartBody.Part file
    );

    /**
     * 视频投稿  fields  传入jason字符串
     * @param uid
     * @param token
     * @param catId
     * @param title
     * @param videoUrlLocal
     * @param file
     * @return
     */
    @Multipart
    @POST(BASE1+"Shortvideo&a=contribute")
    Observable<ResponseBody> contribute(
            @Part("uid") RequestBody uid,
            @Part("token") RequestBody token,
            @Part("catid") RequestBody catId,
            @Part("title") RequestBody title,
            @Part("videoUrlLocal") RequestBody videoUrlLocal,
            @Part MultipartBody.Part file
    );

    /**
     * 国家码列表
     * @return
     */
    @GET(BASE1+"Sysconf&a=getCountryCode")
    Observable<ResponseBody> getCountryCode(
    );

    /**
     * 任务中心
     * @param uid
     * @param token
     * @return
     */
    @GET(BASE1+"User&a=taskCenter")
    Observable<ResponseBody> taskCenter(
            @Query("uid") int uid,
            @Query("token") String token
    );


    /**
     * 领取奖励任务中心
     * @param uid
     * @param token
     * @param action
     * @return
     */
    @GET(BASE1+"User&a=claim")
    Observable<ResponseBody> taskCenterClaim(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("action") String action
    );

    /**
     * 意见反馈
     * @param uid
     * @param content
     * @param version
     * @return
     */
    @GET("index.php?g=Appapi&m=Feedback&a=feedbackSave")
    Observable<ResponseBody> feedback(
            @Query("uid") int uid,
            @Query("content") String content,
            @Query("version") String version
    );

    /**
     * 签到
     * @param uid
     * @param token
     * @param day
     * @return
     */
    @GET(BASE1+"User&a=signin")
    Observable<ResponseBody> signin(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("day") int day
    );

    /**
     * 获取主播配置信息
     * @return
     */
    @GET(BASE+"Home.getConfig")
    Observable<ResponseBody> getConfig(
    );

    /**
     * 绑定邮箱
     * @param uid
     * @param token
     * @param code
     * @param email
     * @return
     */
    @GET(BASE+"User.bindEmail")
    Observable<ResponseBody> bindEmail(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("code") String code,
            @Query("email") String email
    );

    /**
     * 创建直播间
     * @param userLogin
     * @param token
     * @param title
     * @param type
     * @param typeVal
     * @param zbType
     * @param zbTypeDetail
     * @return
     */
    @FormUrlEncoded
    @POST(BASE+"live.createRoom")
    Observable<ResponseBody> createLive(
            @Field("uid") int userLogin,
            @Field("token") String token,
            @Field("title") String title,
            @Field("type") int type,
            @Field("type_val") String typeVal,
            @Field("zbtype") int zbType,
            @Field("zbtype_detail") String zbTypeDetail
    );

    /**
     * 获取个人信息
     * @param uid
     * @param token
     * @param deviceToken
     * @return
     */
    @GET(BASE+"User.getBaseInfo")
    Observable<ResponseBody> getBaseInfo(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("device_token") String deviceToken

    );

    /**
     * 修改手机号
     * @param uid
     * @param token
     * @param newMobile
     * @param code
     * @param userPass
     * @return
     */
    @FormUrlEncoded
    @POST(BASE+"User.modifyMobile")
    Observable<ResponseBody> modifyMobile(
            @Field("uid") int uid,
            @Field("token") String token,
            @Field("newMobile") String newMobile,
            @Field("code") String code,
            @Field("user_pass") String userPass

    );

    /**
     * 获取修改手机验证码
     * @param mobile
     * @return
     */
    @GET(BASE+"User.getBindCode")
    Observable<ResponseBody> getBindCode(
            @Query("mobile") String mobile
    );


    /**
     * 搜索全部
     * @param uid
     * @param token
     * @param keyword
     * @return
     */
    @GET(BASE1+"Search&a=index")
    Observable<ResponseBody> searchAll(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("keyword") String keyword
    );

    /**
     * 搜索指定频道
     * @param uid
     * @param token
     * @param keyword
     * @param name
     * @param page
     * @param pageSize
     * @return
     */
    @GET(BASE1+"search&a=channel")
    Observable<ResponseBody> searchType(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("keyword") String keyword,
            @Query("name") String name,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );


    /**
     * 获取热门关键词列表
     * @param number
     * @return
     */
    @GET(BASE1+"search&a=getHotKeywords")
    Observable<ResponseBody> getHotKeywords(
            @Query("number") int number
    );


    /**
     * 是否关注
     * @param uid
     * @param token
     * @param touId
     * @return
     */
    @GET(BASE+"User.isAttent")
    Observable<ResponseBody> isAttent(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("touid") String touId
    );

    /**
     * 是否关注
     * @param uid
     * @param token
     * @param touId
     * @return
     */
    @GET(BASE+"User.setAttent")
    Observable<ResponseBody> setAttent(
            @Query("uid") int uid,
            @Query("token") String token,
            @Query("touid") String touId
    );

    /**
     * 绑定邮箱
     * @param uid
     * @param token
     * @return
     */
    @GET(BASE+"Login.userLogout")
    Observable<ResponseBody> userLogout(
            @Query("uid") int uid,
            @Query("token") String token
    );


    /**
     * 竞猜
     * @param uid
     * @param amount
     * @param quizid
     * @param tabid
     * @return
     */
    @GET(BASE+"Quiz.userQuiz")
    Observable<ResponseBody> userQuiz(
            @Query("uid") String uid,
            @Query("amount") String amount,
            @Query("quizid") String quizId,
            @Query("tabid") String tabId
    );

}
