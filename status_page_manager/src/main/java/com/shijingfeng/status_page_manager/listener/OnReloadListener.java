package com.shijingfeng.status_page_manager.listener;

import androidx.annotation.NonNull;

import com.shijingfeng.status_page_manager.status_page.StatusPage;

/**
 * Function: 重新加载监听器
 * Date: 2021/4/18 18:59
 * Description:
 *
 * @author ShiJingFeng
 */
@FunctionalInterface
public interface OnReloadListener {

    /**
     * 重新加载
     *
     * @param statusPage 当前状态页
     */
    void onReload(@NonNull StatusPage statusPage);

}
