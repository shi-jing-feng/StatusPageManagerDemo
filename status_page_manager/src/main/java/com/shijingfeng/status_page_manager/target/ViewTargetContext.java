package com.shijingfeng.status_page_manager.target;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shijingfeng.status_page_manager.view.StatusPageContainer;

/**
 * Function: View目标环境
 * Date: 2021/4/18 20:58
 * Description:
 *
 * @author ShiJingFeng
 */
public class ViewTargetContext implements ITargetContext {

    /** View */
    private View mView;

    public ViewTargetContext(@NonNull Object target) {
        this.mView = (View) target;
    }

    /**
     * 创建状态页容器
     *
     * @return 状态页容器
     */
    @Nullable
    @Override
    public StatusPageContainer createStatusPageContainer() {
        final ViewParent parent = mView.getParent();

        if (parent == null) {
            return null;
        }

        final ViewGroup parentViewGroup = (ViewGroup) parent;
        final StatusPageContainer statusPageContainer = new StatusPageContainer(mView.getContext());
        final ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
        final int parentChildCount = parentViewGroup.getChildCount();
        int contentViewIndex = 0;

        for (int i = 0; i < parentChildCount; ++i) {
            final View parentChild = parentViewGroup.getChildAt(i);

            if (parentChild == mView) {
                contentViewIndex = i;
                parentViewGroup.removeViewAt(i);
                break;
            }
        }
        statusPageContainer.addView(mView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        parentViewGroup.addView(statusPageContainer, contentViewIndex, layoutParams);
        return statusPageContainer;
    }

    /**
     * 获取Context
     *
     * @return Context
     */
    @Override
    public Context getContext() {
        return mView.getContext();
    }

}
