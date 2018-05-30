package com.orzangleli.myapplication;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class KeyboardActivity extends AppCompatActivity {

    public static final int MODE_CLASSICAL = 0;
    public static final int MODE_HALF_SOLVED = 1;
    public static final int MODE_SOLVED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //无title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //全屏
//        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_keyboard);
//        AndroidBug5497Workaround.assistActivity(this);


        Intent intent = this.getIntent();
        int mode = intent.getIntExtra("mode", MODE_CLASSICAL);
        if (mode == MODE_CLASSICAL) {
            replaceFragment(new ClassicalFragment());
        } else if (mode == MODE_HALF_SOLVED) {
            replaceFragment(new HalfSolvedFragment());
        } else {
            replaceFragment(new SolvedFragment());
        }



    }

    public void replaceFragment(Fragment fragment) {
        this.getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}
