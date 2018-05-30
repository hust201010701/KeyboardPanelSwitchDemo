package com.orzangleli.myapplication;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * 解决了一半的表情面板和输入法的切换闪烁的问题，不太明显，但是还是能看到一些微弱的闪烁
 *
 */

public class HalfSolvedFragment extends Fragment {

    private EditText mInputEt;
    private ImageView mFaceBtn;
    private View mPanel;
    private View mRoot;
    private View mDecorView;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;
    private int mOldDecorViewHeight;
    private int dp60;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_half_solved, container, false);
        dp60 = dp2px(60);
        initTitle("半解决键盘与面板冲突");
        initView(mRoot);
        return mRoot;
    }

    private void initTitle(String title) {
        this.getActivity().setTitle(title);
    }

    private void initView(View root) {
        mInputEt = root.findViewById(R.id.et_input);
        mFaceBtn = root.findViewById(R.id.btn_face);
        mPanel = root.findViewById(R.id.panel);

        mFaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPanel.getVisibility() == View.VISIBLE) {
                    mFaceBtn.setImageResource(R.drawable.emoji_download_icon);
                    openKeyBoard(mInputEt);
                } else {
                    mFaceBtn.setImageResource(R.drawable.zz_chat_reply_keyboard);
                    closeKeyBoard(mInputEt);
                }
            }
        });


        mDecorView = this.getActivity().getWindow().getDecorView();
        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                mDecorView.getWindowVisibleDisplayFrame(rect);
                // 不能使用decorView.getHeight()获取decorview的高度，获取的高度不会发生变化
                int displayHeight = rect.bottom;
                if (Math.abs(displayHeight - mOldDecorViewHeight) > dp60) {
                    mOldDecorViewHeight = displayHeight;
                    int rootHeight = mRoot.getHeight();
                    int statusBarHeight = getStatusBarHeight();
                    int screenHeight = getScreenHeight();
                    int titleBarHeight = getTitleBarHeight();
                    //在非全屏模式下， 键盘高度 = 屏幕高度 - 状态栏高度 - 主视图高度 - 标题栏高度
                    int keyboardHeight = screenHeight - statusBarHeight - rootHeight - titleBarHeight;
                    Log.i("lxc", "keyboardHeight ---> " + keyboardHeight + "  键盘:  " + (keyboardHeight > 0 ? "弹出" : "收起"));
                    if (keyboardHeight == 0) {
                        mPanel.setVisibility(View.VISIBLE);
                    } else {
                        mPanel.setVisibility(View.GONE);
                    }
                }
            }
        };
        mDecorView.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);


    }

    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        try {
            /**
             * 通过反射机制获取StatusBar高度
             */
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int height = Integer.parseInt(field.get(object).toString());
            /**
             * 设置StatusBar高度
             */
            statusBarHeight = this.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    public int getScreenHeight() {
        DisplayMetrics displayMetrics = this.getContext().getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public int dp2px(int dp) {
        DisplayMetrics displayMetrics = this.getContext().getResources().getDisplayMetrics();
        return (int) (displayMetrics.density * dp + 0.5);
    }

    public int getTitleBarHeight() {
        Rect outRect1 = new Rect();
        this.getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);

        Rect outRect2 = new Rect();
        this.getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect2);
        return outRect1.height() - outRect2.height();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDecorView.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    public void openKeyBoard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void closeKeyBoard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
