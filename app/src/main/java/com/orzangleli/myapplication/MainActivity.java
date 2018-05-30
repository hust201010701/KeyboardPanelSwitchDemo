package com.orzangleli.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button mButton1, mButton2, mButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton1 = this.findViewById(R.id.button1);
        mButton2 = this.findViewById(R.id.button2);
        mButton3 = this.findViewById(R.id.button3);


        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, KeyboardActivity.class);
        switch (v.getId()) {
            case R.id.button1:
                intent.putExtra("mode", KeyboardActivity.MODE_CLASSICAL);
                this.startActivity(intent);
                break;
            case R.id.button2:
                intent.putExtra("mode", KeyboardActivity.MODE_HALF_SOLVED);
                this.startActivity(intent);
                break;
            case R.id.button3:
                intent.putExtra("mode", KeyboardActivity.MODE_SOLVED);
                this.startActivity(intent);
                break;
        }
    }
}
