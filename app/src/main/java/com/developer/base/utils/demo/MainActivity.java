package com.developer.base.utils.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.developer.base.utils.lib.tool.BaseDevice;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BaseDevice.getApp(this.getApplication());
    }
}