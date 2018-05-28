package com.orzangleli.myapplication;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class KeyboardActivity extends AppCompatActivity {

    public static final int MODE_CLASSICAL = 0;
    public static final int MODE_SLOVED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        Intent intent = this.getIntent();
        int mode = intent.getIntExtra("mode", MODE_CLASSICAL);
        if (mode == 0) {
            replaceFragment(new ClassicalFragment());
        } else {
            // todo 解决冲突的fragment
        }



    }

    public void replaceFragment(Fragment fragment) {
        this.getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}
