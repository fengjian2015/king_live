package com.wewin.live.ui.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 懒加载
 * Created by Administrator on 2016/7/14.
 */
public abstract class LazyFragment extends Fragment {
    protected boolean isVisible=false;
    protected boolean isPrepared=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()&&isVisible==false) {
            isVisible = true;
            onVisible();
        } else {
            onInvisible();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        isPrepared = true;
        onVisible();
        super.onActivityCreated(savedInstanceState);
    }



    protected void onVisible(){
        if (!isPrepared || !isVisible) {
            return;
        }
        lazyLoad();
    }

    public boolean getIsVisible(){
        if (!isPrepared || !isVisible) {
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isPrepared=false;
    }

    protected abstract void lazyLoad();

    protected void onInvisible(){}
}
