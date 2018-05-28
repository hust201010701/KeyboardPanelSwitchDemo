package com.orzangleli.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 传统思路实现表情面板和输入法的切换
 *
 */

public class ClassicalFragment extends Fragment {

    private EditText mInputEt;
    private ImageView mFaceBtn;
    private View mPanel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_classical, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        mInputEt = root.findViewById(R.id.et_input);
        mFaceBtn = root.findViewById(R.id.btn_face);
        mPanel = root.findViewById(R.id.panel);

        mFaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPanel.getVisibility() == View.VISIBLE) {
                    mPanel.setVisibility(View.GONE);
                    mFaceBtn.setImageResource(R.drawable.emoji_download_icon);
                    openKeyBoard(mInputEt);
                } else {
                    mPanel.setVisibility(View.VISIBLE);
                    mFaceBtn.setImageResource(R.drawable.zz_chat_reply_keyboard);
                    closeKeyBoard(mInputEt);
                }
            }
        });
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
