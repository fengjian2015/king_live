package com.wewin.live.ui.activity.Live;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.example.jasonutil.permission.Permission;
import com.example.jasonutil.permission.PermissionCallback;
import com.example.jasonutil.permission.Rigger;
import com.example.jasonutil.util.AnimatorTool;
import com.example.jasonutil.util.StringUtils;
import com.example.jasonutil.util.ToastShow;
import com.example.jasonutil.util.UtilTool;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.BaseMapInfo;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterMedia;
import com.wewin.live.ui.adapter.LiveConfigAdapter;
import com.wewin.live.ui.widget.ErrorView;
import com.wewin.live.utils.IntentStart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LiveConfigActivity extends BaseActivity implements ErrorView.OnContinueListener {

    @InjectView(R.id.et_title)
    EditText etTitle;
    @InjectView(R.id.recycler_room)
    RecyclerView recyclerRoom;
    @InjectView(R.id.recycler_live)
    RecyclerView recyclerLive;
    @InjectView(R.id.recycler_coin)
    RecyclerView recyclerCoin;
    @InjectView(R.id.et_password)
    EditText etPassword;
    @InjectView(R.id.recycler_time_coin)
    RecyclerView recyclerTimeCoin;
    @InjectView(R.id.scrollView)
    ScrollView scrollView;
    @InjectView(R.id.btn_commit)
    Button btnCommit;

    //房间类型
    List<Map> mRoomMapList = new ArrayList<>();

    private LiveConfigAdapter mRoomAdapter;

    //直播类型
    List<Map> mLiveMapList = new ArrayList<>();
    private LiveConfigAdapter mLiveAdapter;

    //分类类型
    List<ArrayList<String>> mCoinList = new ArrayList<>();
    List<Map> mCoinMapList = new ArrayList<>();
    private LiveConfigAdapter mCoinAdapter;

    //计费列表
    List<Map> mTimeCoinMapList = new ArrayList<>();
    private LiveConfigAdapter mTimeCoinAdapter;

    /**
     * 记录上传数据
     */
    private Map dataContent = new HashMap();
    //错误界面
    private ErrorView mErrorView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_config;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.live_config));
        initError();
        initRecyclerView();
        initHttp();

    }

    private void initError() {
        mErrorView = new ErrorView(this);
        mErrorView.setTvError(getString(R.string.error));
        mErrorView.setOnContinueListener(this);
    }


    /**
     * 初始化适配器
     */
    private void initRecyclerView() {
        recyclerRoom.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerRoom.setNestedScrollingEnabled(false);

        recyclerLive.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerLive.setNestedScrollingEnabled(false);

        recyclerCoin.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerCoin.setNestedScrollingEnabled(false);

        recyclerTimeCoin.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerTimeCoin.setNestedScrollingEnabled(false);
        //房间类型
        mRoomAdapter = new LiveConfigAdapter(this, mRoomMapList);
        mRoomAdapter.addOnItemListener(new LiveConfigAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                int type = UtilTool.parseInt(mRoomMapList.get(position).get(BaseInfoConstants.ID) + "");
                if (type == 0) {
                    //普通房间
                    etPassword.setVisibility(View.GONE);
                    recyclerTimeCoin.setVisibility(View.GONE);
                } else if (type == 1) {
                    //密码房间
                    etPassword.setVisibility(View.VISIBLE);
                    recyclerTimeCoin.setVisibility(View.GONE);
                    etPassword.setText("");
                    etPassword.setHint(getString(R.string.setting_password));
                    etPassword.setInputType(InputType.TYPE_NULL);
                } else if (type == 2) {
                    //门票房间
                    etPassword.setVisibility(View.VISIBLE);
                    recyclerTimeCoin.setVisibility(View.GONE);
                    etPassword.setText("");
                    etPassword.setHint(getString(R.string.setting_money));
                    etPassword.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                } else if (type == 3) {
                    //计时房间
                    etPassword.setVisibility(View.GONE);
                    recyclerTimeCoin.setVisibility(View.VISIBLE);
                }
            }
        });
        recyclerRoom.setAdapter(mRoomAdapter);

        //直播类型
        mLiveAdapter = new LiveConfigAdapter(this, mLiveMapList);
        mLiveAdapter.addOnItemListener(new LiveConfigAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                setCoinData(position);
            }
        });
        recyclerLive.setAdapter(mLiveAdapter);

        //分类
        mCoinAdapter = new LiveConfigAdapter(this, mCoinMapList);
        mCoinAdapter.addOnItemListener(new LiveConfigAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        recyclerCoin.setAdapter(mCoinAdapter);

        //计费列表
        mTimeCoinAdapter = new LiveConfigAdapter(this, mTimeCoinMapList);
        mTimeCoinAdapter.addOnItemListener(new LiveConfigAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        recyclerTimeCoin.setAdapter(mTimeCoinAdapter);
    }


    /**
     * 获取主播开播配置信息
     */
    private void initHttp() {
        PersenterMedia.getInstance().getConfig(mRoomMapList, mLiveMapList, mCoinMapList, mCoinList, mTimeCoinMapList, new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                mErrorView.hint();
                scrollView.setVisibility(View.VISIBLE);
                btnCommit.setVisibility(View.VISIBLE);
                mRoomAdapter.notifyDataSetChanged();
                mLiveAdapter.notifyDataSetChanged();
                mTimeCoinAdapter.notifyDataSetChanged();
                setCoinData(0);
                setDataContent();
            }

            @Override
            public void onFault(String error) {
                mErrorView.errorShow(true);
                scrollView.setVisibility(View.GONE);
                btnCommit.setVisibility(View.GONE);
            }
        }));
    }


    /**
     * 设置分类子类
     *
     * @param position
     */
    private void setCoinData(int position) {
        mCoinMapList.clear();
        for (int i = 0; i < mCoinList.get(position).size(); i++) {
            HashMap coinMap = new HashMap();
            coinMap.put(BaseInfoConstants.CONTENT, mCoinList.get(position).get(i));
            coinMap.put(BaseInfoConstants.ID, i);
            mCoinMapList.add(coinMap);
        }
        mCoinAdapter.setSeletct(0);
        mCoinAdapter.notifyDataSetChanged();
    }

    /**
     * 配置上传数据
     */
    private void setDataContent() {
        try {
            if (mRoomMapList.size() <= 0) return;
            int selectRoom = mRoomAdapter.getSelect();
            int roomType = UtilTool.parseInt(mRoomMapList.get(selectRoom).get(BaseInfoConstants.ID) + "");
            if (roomType == 0) {
                //普通房间
            } else if (roomType == 1) {
                //密码房间
                dataContent.put(BaseInfoConstants.TYPE_VAL, etPassword.getText().toString() + "");
            } else if (roomType == 2) {
                //门票房间
                dataContent.put(BaseInfoConstants.TYPE_VAL, etPassword.getText().toString() + "");
            } else if (roomType == 3) {
                //计时房间
                dataContent.put(BaseInfoConstants.TYPE_VAL, mTimeCoinMapList.get(mTimeCoinAdapter.getSelect()).get(BaseInfoConstants.ID));
            }
            dataContent.put(BaseInfoConstants.TITLE, etTitle.getText().toString());
            dataContent.put(BaseInfoConstants.TYPE, roomType);
            dataContent.put(BaseInfoConstants.ZBTYPE, mLiveMapList.get(mLiveAdapter.getSelect()).get(BaseInfoConstants.ID));
            dataContent.put(BaseInfoConstants.ZBTYPE_DETAIL, mCoinList.get(mLiveAdapter.getSelect()).get(mCoinAdapter.getSelect()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查数据是否正确
     *
     * @return
     */
    private boolean check() {
        int roomType = UtilTool.parseInt(dataContent.get(BaseInfoConstants.TYPE) + "");
        String roomData = dataContent.get(BaseInfoConstants.TYPE_VAL) + "";
        if (roomType == 0) {
            //普通房间
        } else if (roomType == 1) {
            //密码房间
            if (StringUtils.isEmpty(roomData)) {
                AnimatorTool.getInstance().editTextAnimator(etPassword);
                ToastShow.showToast2(this, getString(R.string.setting_password));
                return false;
            }
        } else if (roomType == 2) {
            //门票房间
            if (StringUtils.isEmpty(roomData)) {
                AnimatorTool.getInstance().editTextAnimator(etPassword);
                ToastShow.showToast2(this, getString(R.string.setting_money));
                return false;
            }
        } else if (roomType == 3) {
            //计时房间
            if (StringUtils.isEmpty(roomData)) {
                AnimatorTool.getInstance().editTextAnimator(etPassword);
                ToastShow.showToast2(this, getString(R.string.setting_select_money));
                return false;
            }
        }
        if (StringUtils.isEmpty(dataContent.get(BaseInfoConstants.TITLE) + "")) {
            AnimatorTool.getInstance().editTextAnimator(etTitle);
            ToastShow.showToast2(this, getString(R.string.input_live_title));
            return false;
        }
        return true;
    }


    @OnClick({R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                //开播
                Rigger.on(LiveConfigActivity.this)
                        .isShowDialog(true)
                        .permissions(Permission.RECORD_AUDIO, Permission.CAMERA, Permission.WRITE_EXTERNAL_STORAGE)
                        .start(new PermissionCallback() {
                            @Override
                            public void onGranted() {
                                setDataContent();
                                if (check()) {
                                    PersenterMedia.getInstance().createLive(dataContent, new OnSuccess(LiveConfigActivity.this, new OnSuccess.OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object content) {
                                            Map map = BaseMapInfo.getInfo((BaseMapInfo) content).get(0);
                                            Bundle bundle = new Bundle();
                                            bundle.putString(BaseInfoConstants.URL, map.get(BaseInfoConstants.PUSH)+"");
                                            bundle.putBoolean(BaseInfoConstants.IS_LIVE, true);
                                            IntentStart.starLogin(LiveConfigActivity.this, LiveStartActivity.class, bundle);
                                            LiveConfigActivity.this.finish();
                                        }

                                        @Override
                                        public void onFault(String error) {

                                        }
                                    }));
                                }
                            }

                            @Override
                            public void onDenied(HashMap<String, Boolean> permissions) {
                            }
                        });

                break;

        }
    }

    @Override
    public void again() {
        initHttp();
    }

}
