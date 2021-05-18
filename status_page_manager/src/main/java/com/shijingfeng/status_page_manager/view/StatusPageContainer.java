package com.shijingfeng.status_page_manager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Function: 状态页容器 (内容View和状态页同一级，而状态页容器是他们的父布局)
 * Date: 2021/4/18 20:48
 * Description:
 *
 * @author ShiJingFeng
 */
public class StatusPageContainer extends FrameLayout {

    public StatusPageContainer(@NonNull Context context) {
        super(context);
    }

    public StatusPageContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusPageContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatusPageContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
