package com.shijingfeng.status_page_manager.util;

/**
 * Function:
 * Date: 2021/4/19 16:10
 * Description:
 *
 * @author ShiJingFeng
 */

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.AnyThread;

/**
 * Function: 线程相关工具类
 * Date: 2021/2/14 19:54
 * Description:
 *
 * @author ShiJingFeng
 */
public class ThreadUtil {

    /** 主线程 Handler */
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 是不是主线程
     *
     * @return  true: 主线程
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 获取 主线程 Handler
     */
    public static Handler getMainHandler() {
        return MAIN_HANDLER;
    }

    /**
     * 运行在主线程
     *
     * @param runnable 要执行的回调
     */
    @AnyThread
    public static void runOnUiThread(Runnable runnable) {
        MAIN_HANDLER.post(runnable);
    }

    /**
     * 运行在主线程
     *
     * @param delay  延迟时间 (毫秒值)
     * @param runnable 要执行的回调
     */
    @AnyThread
    public static void runOnUiThread(long delay, Runnable runnable) {
        MAIN_HANDLER.postDelayed(runnable, delay);
    }

}
