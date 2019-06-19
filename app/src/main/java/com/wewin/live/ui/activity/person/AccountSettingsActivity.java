package com.wewin.live.ui.activity.person;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.example.jasonutil.dialog.ConfirmCancelDialog;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.db.UserInfoDao;
import com.wewin.live.dialog.LoadingProgressDialog;
import com.wewin.live.dialog.MenuListDialog;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.BaseMapInfo;
import com.wewin.live.modle.UserInfo;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.newtwork.OnSuccessAndFaultListener;
import com.wewin.live.presenter.PersenterPersonal;
import com.wewin.live.thirdparty.WXUtil;
import com.wewin.live.ui.widget.HintTextView;
import com.wewin.live.utils.Constants;
import com.wewin.live.utils.GlideUtil;
import com.example.jasonutil.util.LogUtil;
import com.wewin.live.utils.IntentStart;
import com.wewin.live.utils.MessageEvent;
import com.wewin.live.utils.PictureSelectorUtil;
import com.wewin.live.utils.SignOutUtil;
import com.wewin.live.utils.TimeSelectUtil;
import com.example.jasonutil.util.UtilTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 账号设置页面
 */
public class AccountSettingsActivity extends BaseActivity {
    @InjectView(R.id.iv_avatar)
    ImageView ivAvatar;
    @InjectView(R.id.et_nickName)
    EditText etNickName;
    @InjectView(R.id.et_actual_name)
    EditText etActualName;
    @InjectView(R.id.et_gender)
    HintTextView etGender;
    @InjectView(R.id.et_signature)
    EditText etSignature;
    @InjectView(R.id.et_date_birth)
    HintTextView etDateBirth;
    @InjectView(R.id.et_wechat_number)
    HintTextView etWechatNumber;
    @InjectView(R.id.rl_wechat)
    RelativeLayout rlWechat;
    @InjectView(R.id.et_email_address)
    HintTextView etEmailAddress;

    //返回数据
    private final int REQUEST_CODE_EMAIL = 123;
    //判断当前是否改变过
    private boolean isChange = false;

    private List<LocalMedia> selectList = new ArrayList<>();
    private TimeSelectUtil timeSelectUtil;
    private UserInfo mUserInfo;
    private LoadingProgressDialog mLoadingProgressDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_settings;
    }

    @Override
    protected void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setTitleNoBack(getString(R.string.account_settings));
        setTvRight(getString(R.string.save));
        initDate();

    }

    private void initDate() {
        mUserInfo = UserInfoDao.queryUserInfo(SignOutUtil.getUserId());

        timeSelectUtil = new TimeSelectUtil(this);
        timeSelectUtil.setOnTimeReturnListener(new TimeSelectUtil.OnTimeReturnListener() {
            @Override
            public void getTime(String time) {
                LogUtil.log(time);
                setEt(etDateBirth, time);
            }
        });
        GlideUtil.setCircleImg(this, UserInfoDao.findAvatar(), ivAvatar);
        timeSelectUtil.setDefault(mUserInfo.getBirth());
        if (StringUtils.isEmpty(mUserInfo.getWeixin())) {
            setEtHine(etWechatNumber, getString(R.string.bind));
        } else {
            setEtHine(etWechatNumber, getString(R.string.replace));
        }
        if (StringUtils.isEmpty(mUserInfo.getEmail())) {
            setEtHine(etEmailAddress, getString(R.string.bind));
        } else {
            setEt(etEmailAddress, mUserInfo.getEmail());
        }
        setEt(etActualName, mUserInfo.getActualName());
        setEt(etGender, mUserInfo.getSex());
        setEt(etSignature, mUserInfo.getSignature());
        setEt(etDateBirth, mUserInfo.getBirth());
        setEt(etNickName, mUserInfo.getNickName());
        etNickName.setSelection(mUserInfo.getNickName().length());
        etNickName.requestFocus();
        setOnClick();
    }

    public void setOnClick() {
        etActualName.addTextChangedListener(textWatcher);
        etSignature.addTextChangedListener(textWatcher);
        etNickName.addTextChangedListener(textWatcher);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            isChange = true;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        int msgId = event.getMsgId();
        if (msgId == MessageEvent.AUTHORIZATION_SUCCESS) {
            mLoadingProgressDialog.hideDialog();
            getWXUserInfo(event.getCode());
        } else if (msgId == MessageEvent.SHARE_FAIL) {
            mLoadingProgressDialog.hideDialog();
            ToastShow.showToast2(AccountSettingsActivity.this, "" + event.getError());
        } else if (msgId == MessageEvent.SHARE_CANCEL) {
            mLoadingProgressDialog.hideDialog();
            ToastShow.showToast2(AccountSettingsActivity.this, getString(R.string.share_cancel));
        }
    }

    @OnClick({R.id.rl_avatar, R.id.rl_gender, R.id.et_gender, R.id.rl_date_birth, R.id.et_date_birth, R.id.rl_wechat, R.id.tv_right, R.id.bark, R.id.rl_emil})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_avatar:
                isChange = true;
                //选择头像
                UtilTool.closeKeybord(AccountSettingsActivity.this);
                PictureSelectorUtil.selectImage(AccountSettingsActivity.this, selectList, true, 1, PictureConfig.CHOOSE_REQUEST);
                break;
            case R.id.rl_gender:
            case R.id.et_gender:
                isChange = true;
                //选择性别
                selectGenderDialog();
                UtilTool.closeKeybord(AccountSettingsActivity.this);
                break;
            case R.id.rl_date_birth:
            case R.id.et_date_birth:
                isChange = true;
                //出生日期
                selectBirth();
                UtilTool.closeKeybord(AccountSettingsActivity.this);
                break;
            case R.id.rl_wechat:
                isChange = true;
                //绑定微信
                bindWX();
                break;
            case R.id.tv_right:
                //保存
                save();
                break;
            case R.id.bark:
                //返回
                finishDialog();
                break;
            case R.id.rl_emil:
                Bundle bundle = new Bundle();
                bundle.putString(BaseInfoConstants.USER_EMAIL, mUserInfo.getEmail());
                IntentStart.starForResult(this, ChangeEmailActivity.class, bundle, REQUEST_CODE_EMAIL);
                break;
            default:
                break;
        }
    }

    /**
     * 关闭前确定是否有内容改变和保存
     */
    private void finishDialog() {
        if (!isChange) {
            finish();
            return;
        }
        new ConfirmCancelDialog(this)
                .showDialog()
                .setTvTitle(getString(R.string.save_help_content))
                .setOnClickListener(new ConfirmCancelDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        finish();
                    }

                    @Override
                    public void onCancel() {
                    }
                });
    }


    /**
     * 设置内容
     *
     * @param content
     * @param editText
     */
    private void setEt(EditText editText, String content) {
        if (StringUtils.isEmpty(content)) {
            editText.setText("");
        } else {
            editText.setText(content);
        }
    }

    /**
     * 设置内容
     *
     * @param textView
     * @param content
     */
    private void setEtHine(HintTextView textView, String content) {
        textView.setHint(content);
    }

    /**
     * 设置内容
     *
     * @param textView
     * @param content
     */
    private void setEt(HintTextView textView, String content) {
        textView.setMyText(content);
    }


    /**
     * 绑定微信
     */
    private void bindWX() {
        mLoadingProgressDialog = LoadingProgressDialog.createDialog(this);
        WXUtil.getInstance().sendAuthorization();
    }

    /**
     * 弹出选择日期窗口
     */
    private void selectBirth() {
        timeSelectUtil.initOptionPicker();
    }


    /**
     * 选择性别
     */
    private void selectGenderDialog() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.male));
        list.add(getString(R.string.female));
        final MenuListDialog menu = new MenuListDialog(this, list);
        menu.setListOnClick(new MenuListDialog.ListOnClick() {
            @Override
            public void onClickItem(int position) {
                switch (position) {
                    case 0:
                        menu.dismiss();
                        break;
                    case 1:
                        menu.dismiss();
                        setEt(etGender, getString(R.string.male));
                        break;
                    case 2:
                        menu.dismiss();
                        setEt(etGender, getString(R.string.female));
                        break;
                    default:
                        break;
                }
            }
        });
        menu.setColor(Color.BLACK);
        menu.showAtLocation();
    }

    /**
     * 获取微信用户信息
     *
     * @param code
     */
    public void getWXUserInfo(String code) {
        WXUtil.getInstance().getAccessToken(AccountSettingsActivity.this, code, new OnSuccessAndFaultListener() {
            @Override
            public void onFault(String content) {

            }

            @Override
            public void onSuccess(String content) {
                if (StringUtils.isEmpty(content)) {
                    return;
                }
                mUserInfo.setWeixin(content);
                setEtHine(etWechatNumber, getString(R.string.replace));
                ToastShow.showToast(AccountSettingsActivity.this, getString(R.string.replace_success));
            }
        });
    }


    //记录文件是否需要上传，点击保存继续上传，否则直接保存
    private boolean isUpload = false;
    //判断是否点击了保存按钮
    private boolean isSave = false;
    /**
     * 上传图片返回地址
     */
    private String uploadImageUrl;

    /**
     * 上传图片
     */
    private void upload() {
        PersenterPersonal.getInstance().upLoadImage(new File(selectList.get(0).getCutPath()), new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                ToastShow.showToast2(AccountSettingsActivity.this, getString(R.string.upload_success));
                isUpload = false;
                uploadImageUrl=BaseMapInfo.getInfo((BaseMapInfo) content).get(0).get(BaseInfoConstants.AVATAR)+"";
                mUserInfo.setAvatar(Constants.BASE_URL+uploadImageUrl);
                mUserInfo.setAvatar_thumb(Constants.BASE_URL+BaseMapInfo.getInfo((BaseMapInfo) content).get(0).get(BaseInfoConstants.AVATAR_THUMB));
                if (isSave) {
                    save();
                }
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * 保存
     */
    private void save() {
        if (isUpload) {
            isSave = true;
            upload();
            return;
        }
        isSave = false;
        PersenterPersonal.getInstance().updateFields(getUserJson(), new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                ToastShow.showToast2(AccountSettingsActivity.this, getString(R.string.change_success));
                UserInfoDao.updateUser(mUserInfo);
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UPTADA_INFO));
                finish();
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * 用户信息数据
     *
     * @return
     */
    private String getUserJson() {
        mUserInfo.setNickName(etNickName.getText().toString());
        mUserInfo.setActualName(etActualName.getText().toString());
        mUserInfo.setSex(etGender.getTextContent());
        mUserInfo.setSignature(etSignature.getText().toString());
        mUserInfo.setBirth(etDateBirth.getTextContent());
        mUserInfo.setEmail(etEmailAddress.getText().toString());

        HashMap hashMap = new HashMap();
        if(StringUtils.isEmpty(uploadImageUrl)){
            String url=mUserInfo.getAvatar();
            url=url.replace(Constants.BASE_URL,"");
            hashMap.put(BaseInfoConstants.AVATAR, url);
        }else {
            hashMap.put(BaseInfoConstants.AVATAR,uploadImageUrl);
        }

        hashMap.put(BaseInfoConstants.USER_NICENAME, mUserInfo.getNickName());
        hashMap.put(BaseInfoConstants.TRUENAME, mUserInfo.getActualName());
        if (getString(R.string.male).equals(mUserInfo.getSex())) {
            hashMap.put(BaseInfoConstants.SEX, "1");
        } else {
            hashMap.put(BaseInfoConstants.SEX, "2");
        }
        hashMap.put(BaseInfoConstants.SIGNATURE, mUserInfo.getSignature());
        hashMap.put(BaseInfoConstants.BIRTHDAY, mUserInfo.getBirth());
        hashMap.put(BaseInfoConstants.WEIXIN, mUserInfo.getWeixin());
        hashMap.put(BaseInfoConstants.USER_EMAIL, mUserInfo.getEmail());
        String user = JSONObject.toJSONString(hashMap);
        return user;
    }

    /**
     * 返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishDialog();
        }
        return false;
    }


    //拿到选择的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    isUpload = true;
                    isSave = false;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    GlideUtil.setCircleImg(AccountSettingsActivity.this, selectList.get(0).getCutPath(), ivAvatar);
                    upload();
                    break;
                case REQUEST_CODE_EMAIL:
                    Bundle extras = data.getExtras();
                    String email = extras.getString(BaseInfoConstants.USER_EMAIL);
                    mUserInfo.setEmail(email);
                    if (StringUtils.isEmpty(email)) {
                        setEtHine(etEmailAddress, getString(R.string.bind));
                    } else {
                        setEt(etEmailAddress, email);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
