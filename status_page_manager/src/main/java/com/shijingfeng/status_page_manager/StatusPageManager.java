package com.shijingfeng.status_page_manager;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shijingfeng.status_page_manager.listener.DataConvertor;
import com.shijingfeng.status_page_manager.listener.OnReloadListener;
import com.shijingfeng.status_page_manager.listener.OnStatusPageStatusListener;
import com.shijingfeng.status_page_manager.status_page.StatusPage;
import com.shijingfeng.status_page_manager.target.ITargetContext;
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

    public StatusPageManager(@NonNull Builder builder) {
        this.mBuilder = builder;
        showStatusPage(mBuilder.defaultStatusPage);
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
    public StatusPageManager showStatusPage(@NonNull Class<? extends StatusPage> statusPageClass, @Nullable Bundle data) {
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
        final OnStatusPageStatusListener statusPageStatusListener = mBuilder.onStatusPageStatusListener;
        final StatusPageContainer statusPageContainer = mBuilder.statusPageContainer;
        final StatusPage curStatusPage = mCurStatusPageClass != null ? mStatusPageMap.get(mCurStatusPageClass) : null;

        mCurStatusPageClass = null;
        if (curStatusPage != null) {
            curStatusPage.onDetach();
            if (statusPageStatusListener != null) {
                statusPageStatusListener.onDetach(curStatusPage);
            }
        }
        if (statusPageContainer.getChildCount() > 1) {
            statusPageContainer.removeViewAt(STATUS_PAGE_INDEX);
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

            showStatusPage(statusPageClass);
        }
        return this;
    }

    /**
     * 显示状态页 (主线程)
     *
     * @param statusPageClass 要显示的状态页Class对象
     * @param data 状态页View数据
     */
    @MainThread
    private void showStatusPageInMainThread(@NonNull Class<? extends StatusPage> statusPageClass, @Nullable Bundle data) {
        if (mCurStatusPageClass != statusPageClass) {
            final Context context = mBuilder.context;
            final StatusPageContainer statusPageContainer = mBuilder.statusPageContainer;
            final OnReloadListener onReloadListener = mBuilder.onReloadListener;
            final OnStatusPageStatusListener statusPageStatusListener = mBuilder.onStatusPageStatusListener;
            final StatusPage preStatusPage = mCurStatusPageClass != null ? mStatusPageMap.get(mCurStatusPageClass) : null;
            final StatusPage curStatusPage;

            mCurStatusPageClass = statusPageClass;
            if (mStatusPageMap.containsKey(statusPageClass)) {
                curStatusPage = mStatusPageMap.get(statusPageClass);
            } else {
                curStatusPage = createStatusPage(statusPageClass);
                mStatusPageMap.put(statusPageClass, curStatusPage);
            }
            if (preStatusPage != null) {
                // 前一个状态页被解绑回调 (隐藏状态页回调)
                preStatusPage.onDetach();
                if (statusPageStatusListener != null) {
                    statusPageStatusListener.onDetach(preStatusPage);
                }
            }
            if (statusPageContainer.getChildCount() > 1) {
                statusPageContainer.removeViewAt(STATUS_PAGE_INDEX);
            }
            if (curStatusPage != null) {
                if (!curStatusPage.isInitialized()) {
                    // 状态页初始化回调 (状态页懒加载，用到时才初始化)
                    curStatusPage.init(context, data);
                    if (statusPageStatusListener != null) {
                        statusPageStatusListener.onInit(curStatusPage, data);
                    }
                    assert curStatusPage.getView() != null;
                    // 设置状态页点击回调
                    curStatusPage.getView().setOnClickListener(v -> {
                        if (onReloadListener != null) {
                            onReloadListener.onReload(curStatusPage);
                        }
                    });
                }
                statusPageContainer.addView(curStatusPage.getView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                // 状态页被绑定回调 (显示状态页回调)
                curStatusPage.onAttach(data);
                if (statusPageStatusListener != null) {
                    statusPageStatusListener.onAttach(curStatusPage, data);
                }
            }
        }
    }

    /**
     * 创建状态页对象
     *
     * @param statusPageClass 要创建的状态页对象的Class对象
     */
    private StatusPage createStatusPage(@NonNull Class<? extends StatusPage> statusPageClass) {
        try {
            return statusPageClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 构建器
     */
    public static class Builder {

        /** Context */
        private Context context;
        /** 状态页容器 */
        private StatusPageContainer statusPageContainer;
        /** 默认状态页 */
        private Class<? extends StatusPage> defaultStatusPage;
        /** 重新加载监听器 */
        private OnReloadListener onReloadListener;
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
         * 设置重新加载监听器
         *
         * @param listener 重新加载监听器
         * @return Builder
         */
        public Builder setOnReloadListener(@NonNull OnReloadListener listener) {
            this.onReloadListener = listener;
            return this;
        }

        /**
         * 设置状态页状态监听器
         *
         * @param listener 状态页状态监听器
         * @return Builder
         */
        public Builder setOnStatusPageStatusListener(@NonNull OnStatusPageStatusListener listener) {
            this.onStatusPageStatusListener = listener;
            return this;
        }

        /**
         * 设置数据转换器
         *
         * @param convertor 数据转换器
         * @return Builder
         */
        public Builder setDataConvertor(@NonNull DataConvertor convertor) {
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
