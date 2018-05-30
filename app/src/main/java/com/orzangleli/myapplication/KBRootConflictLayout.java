package com.orzangleli.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2018/5/30 下午4:00
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class KBRootConflictLayout extends LinearLayout {

    private KBPanelConflictLayout mKBPanelConflictLayout;
    private int mOldHeight = -1;

    public KBRootConflictLayout(@NonNull Context context) {
        super(context);
    }

    public KBRootConflictLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KBRootConflictLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        preNotifyChild(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void preNotifyChild(int width, int height) {
        if (mOldHeight < 0) {
            mOldHeight = height;
            return ;
        }
        int deltaY = height - mOldHeight;
        mOldHeight = height;
        int minKeyboardHeight = 180;
        if (Math.abs(deltaY) >= minKeyboardHeight) {
            if (deltaY < 0) {
                // 键盘弹起
                if (mKBPanelConflictLayout != null) {
                    // 隐藏面板
                    mKBPanelConflictLayout.setHide();
                }
            } else {
                // 键盘收起
                if (mKBPanelConflictLayout != null) {
                    // 显示面板
                    mKBPanelConflictLayout.setShow();
                }
            }
        }
    }

    public void setKBPanelConflictLayout(KBPanelConflictLayout kBPanelConflictLayout) {
        mKBPanelConflictLayout = kBPanelConflictLayout;
    }

}
