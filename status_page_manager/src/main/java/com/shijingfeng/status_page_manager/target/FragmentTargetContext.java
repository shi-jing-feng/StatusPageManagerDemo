package com.shijingfeng.status_page_manager.target;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shijingfeng.status_page_manager.view.StatusPageContainer;

/**
 * Function: Fragment目标环境
 * Date: 2021/4/18 21:10
 * Description:
 *
 * @author ShiJingFeng
 */
public class FragmentTargetContext implements ITargetContext {

    /** Fragment */
    private Fragment mFragment;

    public FragmentTargetContext(@NonNull Object target) {
        this.mFragment = (Fragment) target;
    }

    /**
     * 创建状态页容器
     *
     * @return 状态页容器
     */
    @Nullable
    @Override
    public StatusPageContainer createStatusPageContainer() {
        final View contentView = mFragment.getView();

        if (contentView == null) {
            return null;
        }

        final ViewParent parent = contentView.getParent();

        if (parent == null) {
            return null;
        }

        final ViewGroup parentViewGroup = (ViewGroup) parent;
        final StatusPageContainer statusPageContainer = new StatusPageContainer(mFragment.requireActivity());
        final ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        final int parentChildCount = parentViewGroup.getChildCount();
        int contentViewIndex = 0;

        for (int i = 0; i < parentChildCount; ++i) {
            final View parentChild = parentViewGroup.getChildAt(i);

            if (parentChild == contentView) {
                contentViewIndex = i;
                parentViewGroup.removeViewAt(i);
                break;
            }
        }
        statusPageContainer.addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
        return mFragment.requireActivity();
    }
}
