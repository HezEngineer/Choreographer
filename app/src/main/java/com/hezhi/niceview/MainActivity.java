package com.hezhi.niceview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.hezhi.choreographer.RippleFollowButton;

public class MainActivity extends AppCompatActivity {
    private RippleFollowButton rfp_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rfp_test = (RippleFollowButton) findViewById(R.id.rfp_test);
        rfp_test.setOnFollowListener(new RippleFollowButton.OnFollowListener() {
            @Override
            public void onFollow() {
                Toast.makeText(MainActivity.this, "点击关注按钮发关注请求", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnFollow() {
                Toast.makeText(MainActivity.this, "点击未关注按钮发取消请求", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
