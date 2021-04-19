package com.shijingfeng.status_page_manager.listener;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.shijingfeng.status_page_manager.status_page.StatusPage;

/**
 * Function: 数据转换器 (根据数据显示不同的状态页)
 * Date: 2021/4/19 15:23
 * Description:
 *
 * @author ShiJingFeng
 */
@FunctionalInterface
public interface DataConvertor {

    /**
     * 转换回调
     *
     * @param data 要转换的数据
     * @return 转换后的状态页Class对象
     */
    @NonNull
    Class<? extends StatusPage> convert(@NonNull Bundle data);

}
