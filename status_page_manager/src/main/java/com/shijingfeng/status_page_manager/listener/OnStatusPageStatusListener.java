package com.shijingfeng.status_page_manager.listener;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shijingfeng.status_page_manager.status_page.StatusPage;

/**
 * Function: 状态页监听器
 * Date: 2021/4/19 13:19
 * Description:
 *
 * @author ShiJingFeng
 */
public interface OnStatusPageStatusListener {

    /**
     * 状态页初始化时回调
     *
     * @param statusPage 状态页
     * @param data 数据
     */
    default void onInit(@NonNull StatusPage statusPage, @Nullable Bundle data) {}

    /**
     * 状态页显示回调 (当前状态页会被显示)
     *
     * @param statusPage 状态页
     * @param data 数据
     */
    default void onShow(@NonNull StatusPage statusPage, @Nullable Bundle data) {}

    /**
     * 状态页被解绑时回调 (当前状态页会被隐藏)
     *
     * @param statusPage 状态页
     */
    default void onHide(@NonNull StatusPage statusPage) {}

}
