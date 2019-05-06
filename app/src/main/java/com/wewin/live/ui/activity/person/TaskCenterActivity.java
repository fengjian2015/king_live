package com.wewin.live.ui.activity.person;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.jasonutil.util.ToastShow;
import com.example.jasonutil.util.UtilTool;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wewin.live.R;
import com.wewin.live.base.BaseActivity;
import com.wewin.live.modle.BaseInfoConstants;
import com.wewin.live.modle.BaseMapInfo2;
import com.wewin.live.newtwork.OnSuccess;
import com.wewin.live.presenter.PersenterPersonal;
import com.wewin.live.ui.adapter.TaskCenterAdapter;
import com.wewin.live.ui.widget.ErrorView;
import com.wewin.live.ui.widget.recyclerive.VegaLayoutManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import butterknife.InjectView;

/**
 * 任务中心
 */
public class TaskCenterActivity extends BaseActivity implements ErrorView.OnContinueListener {

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    List<Map> mHashMapList = new ArrayList<>();
    private TaskCenterAdapter mTaskCenterAdapter;

    //错误界面
    private ErrorView mErrorView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_task_center;
    }

    @Override
    protected void init() {
        setTitle(getString(R.string.task_center));
        initRecyclerView();
        initHttp(true);
        initError();
    }

    private void initError() {
        mErrorView = new ErrorView(this);
        mErrorView.setTvError(getString(R.string.error));
        mErrorView.setOnContinueListener(this);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new VegaLayoutManager());
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initHttp(false);
            }
        });
        mTaskCenterAdapter = new TaskCenterAdapter(this, mHashMapList);
        mTaskCenterAdapter.addOnItemListener(new TaskCenterAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                if(BaseInfoConstants.SIGNIN.equals(mHashMapList.get(position).get(BaseInfoConstants.INPART)+"")){
                    //签到任务
                    sign(UtilTool.parseInt(mHashMapList.get(position).get(BaseInfoConstants.DAY)+""),position);
                }else {
                    //普通任务
                    taskClaim((String) mHashMapList.get(position).get(BaseInfoConstants.ACTION), position);
                }
            }
        });
        mRecyclerView.setAdapter(mTaskCenterAdapter);
    }


    /**
     * 获取网络任务状态
     */
    private void initHttp(boolean isShow) {
        PersenterPersonal.getInstance().taskCenter(isShow, new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                mRefreshLayout.setVisibility(View.VISIBLE);
                mErrorView.hint();
                mRefreshLayout.finishRefresh();
                mHashMapList.clear();
                BaseMapInfo2 baseMapInfo = (BaseMapInfo2) content;
                Map jsonMap = JSONObject.parseObject(((Map) baseMapInfo.getData()).get(BaseInfoConstants.JSON) + "", Map.class);

                List<Map> newPersonMap = JSONArray.parseArray(jsonMap.get(BaseInfoConstants.NEWPERSON) + "", Map.class);
                List<Map> dailyMap = JSONArray.parseArray(jsonMap.get(BaseInfoConstants.DAILY) + "", Map.class);
                List<Map> signinMap = JSONArray.parseArray(jsonMap.get(BaseInfoConstants.SIGNIN) + "", Map.class);
                mHashMapList.addAll(newPersonMap);
                mHashMapList.addAll(dailyMap);
                mHashMapList.addAll(signinMap);
                sort();
                mTaskCenterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFault(String error) {
                mErrorView.errorShow(true);
                mRefreshLayout.setVisibility(View.GONE);
                mRefreshLayout.finishRefresh();
            }
        }));
    }

    /**
     * 领取任务奖励
     */
    private void taskClaim(String action, final int position) {
        PersenterPersonal.getInstance().taskCenterClaim(action, new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                ToastShow.showToast2(TaskCenterActivity.this, getString(R.string.receive));
                mHashMapList.get(position).put(BaseInfoConstants.STATUS, "2");
                mTaskCenterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFault(String error) {

            }
        }));
    }

    /**
     * 签到任务
     */
    private void sign(int day,final int position){
        PersenterPersonal.getInstance().signin(day,new OnSuccess(this, new OnSuccess.OnSuccessListener() {
            @Override
            public void onSuccess(Object content) {
                ToastShow.showToast2(TaskCenterActivity.this,getString(R.string.check_in_success));
                mHashMapList.get(position).put(BaseInfoConstants.STATUS, "2");
                mTaskCenterAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFault(String error) {

            }
        }));
    }

    private void sort() {
        //将mHashMapList按状态排序，待领取最前面，其次未完成
        Collections.sort(mHashMapList, new Comparator<Map>() {
            @Override
            public int compare(Map map1, Map map2) {

                int a_status = UtilTool.parseInt(map1.get(BaseInfoConstants.STATUS) + "");
                int b_status = UtilTool.parseInt(map2.get(BaseInfoConstants.STATUS) + "");

                try {
                    if (a_status != 1 && b_status == 1) {
                        return 1;
                    } else if (a_status == 1 && b_status != 1) {
                        return -1;
                    }else if (a_status != 0 && b_status == 0) {
                        return 1;
                    } else if (a_status == 0 && b_status != 0) {
                        return -1;
                    }  else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    @Override
    public void again() {
        initHttp(true);
    }
}
