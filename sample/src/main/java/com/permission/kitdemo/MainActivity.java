package com.permission.kitdemo;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.permission.kit.OnPermissionListener;
import com.permission.kit.PermissionActivity;
import com.permission.kit.PermissionKit;

import java.util.Arrays;

/**
 * 请求权限实例
 */
public class MainActivity extends PermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnPermission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求权限
                PermissionKit.getInstance().requestPermission(MainActivity.this, 200, new OnPermissionListener() {
                    @Override
                    public void onSuccess(int requestCode, String... permissions) {
                        Log.e(getClass().getSimpleName(), "onSuccess :" + Arrays.toString(permissions));
                    }

                    @Override
                    public void onFail(int requestCode, String... permissions) {
                        Log.e(getClass().getSimpleName(), "onFail " + Arrays.toString(permissions));
                        //授权失败后再次操作
                        PermissionKit.getInstance().guideSetting(MainActivity.this, true, requestCode, null, permissions);
                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE);
            }
        });
    }
}
