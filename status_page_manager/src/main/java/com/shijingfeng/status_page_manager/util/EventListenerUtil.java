package com.shijingfeng.status_page_manager.util;

import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Function: 事件监听相关 工具类
 * Date: 2021/2/27 14:55
 * Description:
 *
 * @author ShiJingFeng
 */
public class EventListenerUtil {

    /** 默认点击防连击间隔时间(毫秒值) */
    private static final long DEFAULT_CLICK_THROTTLE_MS = 500L;

    /**
     * 设置点击回调监听器
     *
     * @param view View
     * @param listener 回调监听
     */
    public static void setOnClickListener(@NonNull View view, @Nullable View.OnClickListener listener) {
        view.setOnClickListener(new OnDebounceClickListener(DEFAULT_CLICK_THROTTLE_MS) {
            @Override
            public void onDebounceClick(@NonNull View view) {
                if (listener != null) {
                    listener.onClick(view);
                }
            }
        });
    }

    /**
     * 设置点击回调监听器
     *
     * @param view View
     * @param clickThrottleMs 点击防连击间隔时间(毫秒值)
     * @param listener 回调监听
     */
    public static void setOnClickListener(@NonNull View view, @IntRange(from = 0) long clickThrottleMs, @Nullable View.OnClickListener listener) {
        if (clickThrottleMs <= 0) {
            view.setOnClickListener(listener);
        } else {
            view.setOnClickListener(new OnDebounceClickListener(clickThrottleMs) {
                @Override
                public void onDebounceClick(@NonNull View view) {
                    if (listener != null) {
                        listener.onClick(view);
                    }
                }
            });
        }
    }

    /**
     * 防抖动点击监听器
     */
    private abstract static class OnDebounceClickListener implements View.OnClickListener {

        /** 是否被拦截  true: 被拦截 */
        private static boolean sIntercepted = false;

        /** 点击防连击间隔时间(毫秒值) */
        private final long mClickThrottleMs;

        /** 取消拦截Runnable */
        private final Runnable mCancelInterceptRunnable = () -> sIntercepted = false;

        public OnDebounceClickListener(long clickThrottleMs) {
            this.mClickThrottleMs = clickThrottleMs;
        }

        @Override
        public void onClick(View v) {
            if (sIntercepted) {
                return;
            }
            sIntercepted = true;
            v.postDelayed(mCancelInterceptRunnable, mClickThrottleMs);
            onDebounceClick(v);
        }

        /**
         * 防抖点击回调
         *
         * @param view View
         */
        public abstract void onDebounceClick(@NonNull View view);

    }

}
