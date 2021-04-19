package com.shijingfeng.status_page_manager.status_page;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Function: 状态页
 * Date: 2021/4/18 19:00
 * Description:
 *
 * @author ShiJingFeng
 */
public abstract class StatusPage {

    /** Context */
    private Context mContext;
    /** View */
    private View mView;
    /** 是否已初始化  true: 已初始化 */
    private boolean mIsInitialized = false;

    /**
     * 初始化
     *
     * @param context Context
     * @param data 数据
     */
    public final void init(@NonNull Context context, @Nullable Bundle data) {
        if (isInitialized()) {
            return;
        }
        this.mIsInitialized = true;
        this.mContext = context;
        this.mView = onCreateView(context);
        onInit(data);
    }

    /**
     * 创建View
     *
     * @param context Context
     * @return View
     */
    @NonNull
    public abstract View onCreateView(@NonNull Context context);

    /**
     * 状态页初始化时回调
     *
     * @param data 数据
     */
    public void onInit(@Nullable Bundle data) {}

    /**
     * 状态页被绑定时回调 (当前状态页会被显示)
     *
     * @param data 数据
     */
    public void onAttach(@Nullable Bundle data) {}

    /**
     * 状态页被解绑时回调 (当前状态页会被隐藏)
     */
    public void onDetach() {}

    /**
     * 是否初始化了
     *
     * @return  true: 初始化了
     */
    public boolean isInitialized() {
        return mIsInitialized;
    }

    /**
     * 获取 Context
     *
     * @return Context
     */
    @Nullable
    public Context getContext() {
        return mContext;
    }

    /**
     * 获取View
     *
     * @return View
     */
    @Nullable
    public View getView() {
        return mView;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

}
