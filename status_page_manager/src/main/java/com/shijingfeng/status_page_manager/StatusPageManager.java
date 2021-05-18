package com.shijingfeng.status_page_manager;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.AnimatorRes;
import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shijingfeng.status_page_manager.listener.DataConvertor;
import com.shijingfeng.status_page_manager.listener.OnStatusPageClickListener;
import com.shijingfeng.status_page_manager.listener.OnStatusPageStatusListener;
import com.shijingfeng.status_page_manager.status_page.StatusPage;
import com.shijingfeng.status_page_manager.target.ITargetContext;
import com.shijingfeng.status_page_manager.util.EventListenerUtil;
import com.shijingfeng.status_page_manager.util.StatusPageManagerUtil;
import com.shijingfeng.status_page_manager.util.ThreadUtil;
import com.shijingfeng.status_page_manager.view.StatusPageContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Function: 状态页管理器
 * Date: 2021/4/19 13:05
 * Description:
 *
 * @author ShiJingFeng
 */
public class StatusPageManager {

    /** 状态页所在状态页容器内的下标 */
    private static final int STATUS_PAGE_INDEX = 1;

    /** Builder构建器 */
    private final Builder mBuilder;

    /** 当前状态页Class对象 */
    private Class<? extends StatusPage> mCurStatusPageClass;
    /** 状态页Map */
    private Map<Class<? extends StatusPage>, StatusPage> mStatusPageMap = new HashMap<>();

    /** 是否正在隐藏状态页  true: 正在操作 */
    private boolean mIsExitOperating = false;
    /** 是否正在显示状态页  true: 正在操作 */
    private boolean mIsEnterOperating = false;

    public StatusPageManager(@NonNull Builder builder) {
        this.mBuilder = builder;
        if (mBuilder.defaultStatusPage != null) {
            showStatusPage(mBuilder.defaultStatusPage);
        }
    }

    /**
     * 显示状态页
     *
     * @param statusPageClass 要显示的状态页Class对象
     * @return StatusPageManager
     */
    @AnyThread
    public StatusPageManager showStatusPage(@NonNull Class<? extends StatusPage> statusPageClass) {
        if (ThreadUtil.isMainThread()) {
            showStatusPageInMainThread(statusPageClass, null);
        } else {
            ThreadUtil.runOnUiThread(() -> showStatusPageInMainThread(statusPageClass, null));
        }
        return this;
    }

    /**
     * 显示状态页
     *
     * @param statusPageClass 要显示的状态页Class对象
     * @param data 状态页View数据
     * @return StatusPageManager
     */
    @AnyThread
    public StatusPageManager showStatusPage(@NonNull Class<? extends StatusPage> statusPageClass, @Nullable Object data) {
        if (ThreadUtil.isMainThread()) {
            showStatusPageInMainThread(statusPageClass, data);
        } else {
            ThreadUtil.runOnUiThread(() -> showStatusPageInMainThread(statusPageClass, data));
        }
        return this;
    }

    /**
     * 显示成功状态页
     *
     * @return StatusPageManager
     */
    public StatusPageManager showSuccess() {
        final StatusPage curStatusPage = mCurStatusPageClass != null ? mStatusPageMap.get(mCurStatusPageClass) : null;

        if (curStatusPage != null) {
            hideStatusPageInternal(curStatusPage);
        }
        return this;
    }

    /**
     * 通过数据显示不同的状态页
     *
     * @param data 数据
     * @return StatusPageManager
     */
    @AnyThread
    public StatusPageManager showStatusPageWithData(@NonNull Bundle data) {
        final DataConvertor dataConvertor = mBuilder.dataConvertor;

        if (dataConvertor != null) {
            final Class<? extends StatusPage> statusPageClass = dataConvertor.convert(data);

            if (statusPageClass == null) {
                showSuccess();
            } else {
                showStatusPage(statusPageClass);
            }
        }
        return this;
    }

    /**
     * 获取当前正在显示的状态页
     *
     * @return 当前正在显示的状态页 (如果为null，则无状态页，即显示内容页)
     */
    @Nullable
    public StatusPage getCurrentStatusPage() {
        if (mCurStatusPageClass == null) {
            return null;
        }
        return mStatusPageMap.get(mCurStatusPageClass);
    }

    /**
     * 显示状态页 (主线程)
     *
     * @param statusPageClass 要显示的状态页Class对象
     * @param data 状态页View数据
     */
    private void showStatusPageInMainThread(@NonNull Class<? extends StatusPage> statusPageClass, @Nullable Object data) {
        if (mIsExitOperating || mIsEnterOperating) {
            // 隐藏状态页动画进行中 或 显示状态页动画进行中 禁止操作
            return;
        }
        if (mCurStatusPageClass == statusPageClass) {
            // 当前状态页已显示，只更新数据
            final OnStatusPageStatusListener statusPageStatusListener = mBuilder.onStatusPageStatusListener;
            final StatusPage statusPage = getStatusPage(statusPageClass);

            if (statusPage != null) {
                statusPage.onUpdateData(data);
                if (statusPageStatusListener != null) {
                    statusPageStatusListener.onUpdateData(statusPage, data);
                }
            }
            return;
        }

        final StatusPage preStatusPage = mCurStatusPageClass != null ? mStatusPageMap.get(mCurStatusPageClass) : null;

        // 隐藏前一个状态页
        if (preStatusPage != null) {
            hideStatusPageInternal(preStatusPage);
        }
        // 显示当前状态页
        showStatusPageInternal(statusPageClass, data);
    }

    /**
     * 显示状态页
     *
     * @param statusPageClass 要显示的状态页Class对象
     * @param data 数据
     */
    private void showStatusPageInternal(@NonNull Class<? extends StatusPage> statusPageClass, @Nullable Object data) {
        // 设置显示状态页中
        mIsEnterOperating = true;

        final StatusPage statusPage = getStatusPage(statusPageClass);
        final Context context = mBuilder.context;
        final StatusPageContainer statusPageContainer = mBuilder.statusPageContainer;
        final Animator enterAnimator = mBuilder.enterAnimator;
        final OnStatusPageClickListener onStatusPageClickListener = mBuilder.onStatusPageClickListener;
        final OnStatusPageStatusListener statusPageStatusListener = mBuilder.onStatusPageStatusListener;

        if (!statusPage.isInitialized()) {
            // 状态页初始化回调 (状态页懒加载，用到时才初始化)
            statusPage.init(context, data);
            if (statusPageStatusListener != null) {
                statusPageStatusListener.onInit(statusPage, data);
            }
            assert statusPage.getView() != null;
            // 设置状态页点击回调
            EventListenerUtil.setOnClickListener(statusPage.getView(), v -> {
                if (onStatusPageClickListener != null) {
                    onStatusPageClickListener.onClick(statusPage);
                }
            });
        }
        statusPageContainer.addView(statusPage.getView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (enterAnimator != null) {
            enterAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    // 必须移除当前监听器，否则会多次回调(不移除会有多个监听器)
                    enterAnimator.removeListener(this);

                    // 状态页被绑定回调 (显示状态页回调)
                    statusPage.onShow(data);
                    if (statusPageStatusListener != null) {
                        statusPageStatusListener.onShow(statusPage, data);
                    }
                    // 设置当前显示的状态页Class对象
                    mCurStatusPageClass = statusPageClass;
                    // 显示状态页已完成
                    mIsEnterOperating = false;
                }
            });
            // 设置动画目标View
            enterAnimator.setTarget(statusPage.getView());
            // 状态页退出动画
            enterAnimator.start();
        } else {
            // 状态页被绑定回调 (显示状态页回调)
            statusPage.onShow(data);
            if (statusPageStatusListener != null) {
                statusPageStatusListener.onShow(statusPage, data);
            }
            // 设置当前显示的状态页Class对象
            mCurStatusPageClass = statusPageClass;
            // 显示状态页已完成
            mIsEnterOperating = false;
        }
    }

    /**
     * 隐藏状态页
     *
     * @param statusPage 要隐藏的状态页
     */
    private void hideStatusPageInternal(@NonNull StatusPage statusPage) {
        final StatusPageContainer statusPageContainer = mBuilder.statusPageContainer;
        final Animator exitAnimator = mBuilder.exitAnimator;
        final OnStatusPageStatusListener statusPageStatusListener = mBuilder.onStatusPageStatusListener;

        // 设置隐藏状态页中
        mIsExitOperating = true;
        if (exitAnimator != null) {
            exitAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    // 必须移除当前监听器，否则会多次回调(不移除会有多个监听器)
                    exitAnimator.removeListener(this);

                    // 前一个状态页被解绑回调 (隐藏状态页回调)
                    statusPage.onHide();
                    if (statusPageStatusListener != null) {
                        statusPageStatusListener.onHide(statusPage);
                    }
                    if (statusPageContainer.getChildCount() > 1) {
                        statusPageContainer.removeViewAt(STATUS_PAGE_INDEX);
                    }
                    // 清空当前显示的状态页Class对象
                    mCurStatusPageClass = null;
                    // 隐藏状态页已完成
                    mIsExitOperating = false;
                }
            });
            // 设置动画目标View
            exitAnimator.setTarget(statusPage.getView());
            // 状态页退出动画
            exitAnimator.start();
        } else {
            // 前一个状态页被解绑回调 (隐藏状态页回调)
            statusPage.onHide();
            if (statusPageStatusListener != null) {
                statusPageStatusListener.onHide(statusPage);
            }
            if (statusPageContainer.getChildCount() > 1) {
                statusPageContainer.removeViewAt(STATUS_PAGE_INDEX);
            }
            // 清空当前显示的状态页Class对象
            mCurStatusPageClass = null;
            // 隐藏状态页已完成
            mIsExitOperating = false;
        }
    }

    /**
     * 获取 状态页实例
     *
     * @param statusPageClass 状态页Class对象
     * @return statusPage
     */
    private StatusPage getStatusPage(@NonNull Class<? extends StatusPage> statusPageClass) {
        final StatusPage statusPage;

        if (mStatusPageMap.containsKey(statusPageClass)) {
            statusPage = mStatusPageMap.get(statusPageClass);
        } else {
            try {
                statusPage = statusPageClass.newInstance();
                mStatusPageMap.put(statusPageClass, statusPage);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("状态页实例创建失败, 继承StatusPage的子类至少有一个无参构造方法");
            }
        }
        return statusPage;
    }

    /**
     * 构建器
     */
    public static class Builder {

        /** Context */
        private Context context;
        /** 状态页容器 */
        private StatusPageContainer statusPageContainer;
        /** 状态页进入动画 */
        private Animator enterAnimator;
        /** 状态页退出动画 */
        private Animator exitAnimator;
        /** 默认状态页 */
        private Class<? extends StatusPage> defaultStatusPage;
        /** 重新加载监听器 */
        private OnStatusPageClickListener onStatusPageClickListener;
        /** 状态页状态监听器 */
        private OnStatusPageStatusListener onStatusPageStatusListener;
        /** 数据转换器 */
        private DataConvertor dataConvertor;

        public Builder(@NonNull Object target) {
            final ITargetContext targetContext = StatusPageManagerUtil.getTargetContext(target);

            this.context = targetContext.getContext();
            this.statusPageContainer = targetContext.createStatusPageContainer();
        }

        /**
         * 设置 状态页进入动画
         *
         * @param enterAnimatorRes 状态页进入动画资源
         * @return Builder
         */
        public Builder setEnterAnimator(@AnimatorRes int enterAnimatorRes) {
            final Context context = this.context.getApplicationContext();

            this.enterAnimator = AnimatorInflater.loadAnimator(context.getApplicationContext(), enterAnimatorRes);
            return this;
        }

        /**
         * 设置 状态页进入动画
         *
         * @param enterAnimator 状态页进入动画
         * @return Builder
         */
        public Builder setEnterAnimator(@NonNull Animator enterAnimator) {
            this.enterAnimator = enterAnimator;
            return this;
        }

        /**
         * 设置 状态页退出动画
         *
         * @param exitAnimatorRes 状态页退出动画资源
         * @return Builder
         */
        public Builder setExitAnimator(@AnimatorRes int exitAnimatorRes) {
            final Context context = this.context.getApplicationContext();

            this.exitAnimator = AnimatorInflater.loadAnimator(context.getApplicationContext(), exitAnimatorRes);
            return this;
        }

        /**
         * 设置 状态页退出动画
         *
         * @param exitAnimator 状态页退出动画
         * @return Builder
         */
        public Builder setExitAnimator(@NonNull Animator exitAnimator) {
            this.exitAnimator = exitAnimator;
            return this;
        }

        /**
         * 设置默认状态页
         *
         * @param defaultStatusPage 默认状态页
         * @return Builder
         */
        public Builder setDefaultStatusPage(@NonNull Class<? extends StatusPage> defaultStatusPage) {
            this.defaultStatusPage = defaultStatusPage;
            return this;
        }

        /**
         * 设置状态页点击监听器
         *
         * @param listener 重新加载监听器
         * @return Builder
         */
        public Builder setOnStatusPageClickListener(@Nullable OnStatusPageClickListener listener) {
            this.onStatusPageClickListener = listener;
            return this;
        }

        /**
         * 设置状态页状态监听器
         *
         * @param listener 状态页状态监听器
         * @return Builder
         */
        public Builder setOnStatusPageStatusListener(@Nullable OnStatusPageStatusListener listener) {
            this.onStatusPageStatusListener = listener;
            return this;
        }

        /**
         * 设置数据转换器
         *
         * @param convertor 数据转换器
         * @return Builder
         */
        public Builder setDataConvertor(@Nullable DataConvertor convertor) {
            this.dataConvertor = convertor;
            return this;
        }

        /**
         * 构建
         *
         * @return StatusPageManager
         */
        public StatusPageManager build() {
            return new StatusPageManager(this);
        }

    }

}
