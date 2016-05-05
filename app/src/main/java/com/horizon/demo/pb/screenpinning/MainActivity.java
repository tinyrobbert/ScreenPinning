package com.horizon.demo.pb.screenpinning;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Object statusBarManagerService;
    private Method methodDisable;
    private TextView tvInfo;
//    SCREENPINNING_MODE_FLAG = StatusBarManager.DISABLE_NONE
//    | StatusBarManager.DISABLE_EXPAND
//    | StatusBarManager.DISABLE_NOTIFICATION_ICONS
//    | StatusBarManager.DISABLE_NOTIFICATION_ALERTS
//    // | StatusBarManager.DISABLE_CLOCK
//    // | StatusBarManager.DISABLE_SYSTEM_INFO
//    | StatusBarManager.DISABLE_HOME
//    | StatusBarManager.DISABLE_SEARCH
//    | StatusBarManager.DISABLE_BACK
//    | StatusBarManager.DISABLE_RECENT;
    private final static int SCREENPINNING_MODE_FLAG = 57081856;
    private final static int NORMAL_MODE_FLAG = 0;
    private final static int SCREENPINNING_MODE_WITH_BACK_FLAG = 52887552;
    private final static int DATE_SETTINGS_REQUEST = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tvInfo = (TextView) findViewById(R.id.tv_info);
        // Get android @hide class android.app.StatusBarManager by reflection.
        try
        {
            Class clsStatusBarManager = Class.forName("android.app.StatusBarManager");
            Context appContext = getApplicationContext();
            if (appContext != null) {
                statusBarManagerService = appContext.getSystemService("statusbar");
                methodDisable = clsStatusBarManager.getMethod("disable", int.class);
            }
        } catch (ClassNotFoundException e) {
            reflectionError(e);
        } catch (NoSuchMethodException e) {
            reflectionError(e);
        }
    }

    private void reflectionError(Exception ex) {
        tvInfo.setBackgroundColor(Color.RED);
        tvInfo.setText("reflection error: " + ex.toString());
    }

    private void disableStatusBar(int flag) {
        try {
            methodDisable.invoke(statusBarManagerService, flag);
        } catch (IllegalAccessException e) {
            reflectionError(e);
        } catch (InvocationTargetException e) {
            reflectionError(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_pin: {
                disableStatusBar(SCREENPINNING_MODE_FLAG);
                break;
            }
            case R.id.bt_unpin: {
                disableStatusBar(NORMAL_MODE_FLAG);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Enable BACK button to return from Settings
            disableStatusBar(SCREENPINNING_MODE_WITH_BACK_FLAG);
            startActivityForResult(new Intent(Settings.ACTION_DATE_SETTINGS), DATE_SETTINGS_REQUEST);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == DATE_SETTINGS_REQUEST) {
            // Disable BACK button when returning from Settings
            disableStatusBar(SCREENPINNING_MODE_FLAG);
        }
    }

}
