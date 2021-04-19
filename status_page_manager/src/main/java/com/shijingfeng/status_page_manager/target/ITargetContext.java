package com.shijingfeng.status_page_manager.target;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shijingfeng.status_page_manager.view.StatusPageContainer;

/**
 * Function: 目标环境基类
 * Date: 2021/4/18 20:45
 * Description:
 *
 * @author ShiJingFeng
 */
public interface ITargetContext {

    /**
     * 创建状态页容器
     *
     * @return 状态页容器 (因为特殊情况可能为null)
     */
    @Nullable
    StatusPageContainer createStatusPageContainer();

    /**
     * 获取Context
     *
     * @return Context
     */
    Context getContext();

}
