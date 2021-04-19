package com.shijingfeng.status_page_manager.util;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.shijingfeng.status_page_manager.target.ActivityTargetContext;
import com.shijingfeng.status_page_manager.target.FragmentTargetContext;
import com.shijingfeng.status_page_manager.target.ITargetContext;
import com.shijingfeng.status_page_manager.target.ViewTargetContext;

/**
 * Function: 状态页管理工具类
 * Date: 2021/4/18 21:53
 * Description:
 *
 * @author ShiJingFeng
 */
public class StatusPageManagerUtil {

    /**
     * 获取目标环境
     *
     * @param target 目标
     * @return 目标环境
     */
    public static ITargetContext getTargetContext(@NonNull Object target) {
        if (target instanceof Activity) {
            return new ActivityTargetContext(target);
        } else if (target instanceof Fragment) {
            return new FragmentTargetContext(target);
        } else if (target instanceof View) {
            return new ViewTargetContext(target);
        } else {
            throw new RuntimeException("目标不支持");
        }
    }

}
