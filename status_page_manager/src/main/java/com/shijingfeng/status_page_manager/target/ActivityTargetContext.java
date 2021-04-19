package com.shijingfeng.status_page_manager.target;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shijingfeng.status_page_manager.view.StatusPageContainer;

/**
 * Function: Activity目标环境
 * Date: 2021/4/18 20:44
 * Description:
 *
 * @author ShiJingFeng
 */
public class ActivityTargetContext implements ITargetContext {

    /** Activity */
    private Activity mActivity;

    public ActivityTargetContext(@NonNull Object target) {
        this.mActivity = (Activity) target;
    }

    /**
     * 创建状态页容器
     *
     * @return 状态页容器
     */
    @Nullable
    @Override
    public StatusPageContainer createStatusPageContainer() {
        // 内容View下标
        final int contentViewIndex = 0;
        final ViewGroup activityContainer = mActivity.findViewById(android.R.id.content);
        final View contentView = activityContainer.getChildAt(contentViewIndex);
        final StatusPageContainer statusPageContainer = new StatusPageContainer(mActivity);
        final ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();

        activityContainer.removeViewAt(contentViewIndex);
        statusPageContainer.addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        activityContainer.addView(statusPageContainer, contentViewIndex, layoutParams);
        return statusPageContainer;
    }

    /**
     * 获取Context
     *
     * @return Context
     */
    @Override
    public Context getContext() {
        return mActivity;
    }

}
