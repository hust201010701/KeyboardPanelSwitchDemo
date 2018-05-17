package com.orzangleli.myapplication;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;

public class MainActivity extends AppCompatActivity {

    Button change, send;
    EditText editText;
    View quick;
    KPSwitchPanelLinearLayout panel;
    View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        change = this.findViewById(R.id.change);
        send = this.findViewById(R.id.send);
        editText = this.findViewById(R.id.edittext);
        quick = this.findViewById(R.id.quick);
        panel = this.findViewById(R.id.panel_root);
        root = this.findViewById(R.id.rootView);


//        change.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (panel.getVisibility() == View.GONE) {
//                    panel.setVisibility(View.VISIBLE);
//                    editText.clearFocus();
//                    closeKeybord(editText);
//                    change.setText("X");
//                } else {
//                    panel.setVisibility(View.GONE);
//                    editText.requestFocus();
//                    openKeybord(editText);
//                    change.setText("+");
//                }
//            }
//        });


        KeyboardUtil.attach(this, panel,
                // Add keyboard showing state callback, do like this when you want to listen in the
                // keyboard's show/hide change.
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        Log.d("lxc", String.format("Keyboard is %s", isShowing ? "showing" : "hiding"));
                    }
                });

        KPSwitchConflictUtil.attach(panel, change, editText,
                new KPSwitchConflictUtil.SwitchClickListener() {
                    @Override
                    public void onClickSwitch(boolean switchToPanel) {
                        if (switchToPanel) {
                            editText.clearFocus();
                            change.setText("X");
                        } else {
                            editText.requestFocus();
                            change.setText("+");
                        }
                    }
                });


    }


    /**
     * 打开软键盘
     *
     * @param mEditText
     */
    public void openKeybord(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     */
    public void closeKeybord(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }


}
