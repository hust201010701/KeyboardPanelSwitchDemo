package com.orzangleli.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

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

public class KBPanelConflictLayout extends FrameLayout {
    private boolean mHide;

    public KBPanelConflictLayout(@NonNull Context context) {
        super(context);
    }

    public KBPanelConflictLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KBPanelConflictLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHide) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
            setVisibility(View.GONE);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setHide() {
        this.mHide = true;
    }

    public void setShow() {
        this.mHide = false;
        setVisibility(View.VISIBLE);
    }

}
