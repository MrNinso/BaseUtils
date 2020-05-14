package com.developer.base.utils.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.developer.base.utils.lib.object.BaseList;
import com.developer.base.utils.lib.object.BaseMap;
import com.developer.base.utils.lib.object.BaseOptional;
import com.developer.base.utils.lib.tool.BaseDevice;

import java.util.Objects;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {

    private ListView maListView;
    private ArrayAdapter<String> maAdapter;
    private BaseMap<String, BaseOptional<demo>> maDemos;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maListView = findViewById(R.id.maList);

        if (getIntent().getStringExtra(demosScreens.Screen) == null) {
            maDemos = buildHome();
        } else {
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(demosScreens.Screen));

            switch (getIntent().getStringExtra(demosScreens.Screen)) {
                case demosScreens.BaseDevice:
                    maDemos = buildBaseDevice();
                    break;
                case demosScreens.BaseScreen:
                    maDemos = buildBaseScreen();
                    break;
                default:
                    maDemos = new BaseMap<>();
                    break;
            }
        }

        maAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                maDemos.getKeyList()
        );

        maListView.setAdapter(maAdapter);

        maListView.setOnItemClickListener((parent, view, position, id) -> {
            BaseOptional<demo> d = maDemos.get(maDemos.getKeyList().get(position));

            assert d != null;
            if (d.get() != null)
                d.get().demo();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private BaseMap<String, BaseOptional<demo>> buildBaseDevice() {
        BaseMap<String, BaseOptional<demo>> home = new BaseMap<>();

        home.put("Async", BaseOptional.of(() ->
            start(demosScreens.Async)
        ));

        home.put("BaseScreen", BaseOptional.of(() ->
            start(demosScreens.BaseScreen)
        ));

        home.put("BaseVibrator", BaseOptional.of(() ->
                start(demosScreens.BaseVibrator)
        ));

        return home;
    }

    private BaseMap<String, BaseOptional<demo>> buildBaseScreen() {
        BaseMap<String, BaseOptional<demo>> home = new BaseMap<>();

        home.put("PrintScreen", BaseOptional.of(() -> {
            Bitmap b = BaseDevice.getScreen().screenShotFullScreen(MainActivity.this);
        }));

        return home;
    }

    private BaseMap<String, BaseOptional<demo>> buildHome() {
        BaseMap<String, BaseOptional<demo>> home = new BaseMap<>();

        home.put("BaseDevice", BaseOptional.of(() ->
                start(demosScreens.BaseDevice)
        ));

       return home;
    }

    private void start(String screen) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(demosScreens.Screen, screen);

        startActivity(i);
    }

    interface demosScreens {
        String Screen = "MainActivity.key.Screen";

        String BaseDevice = "BaseDevice";
        String Async = "Async";
        String BaseScreen = "BaseScreen";
        String BaseVibrator = "BaseVibrator";
    }

    interface demo {
        void demo();
    }
}