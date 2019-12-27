package com.permission.kit;

import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PermissionActivity
 * 接收权限返回结果
 */
public class PermissionActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    /**
     * 请求权限结果
     *
     * @param requestCode  请求码
     * @param permissions  权限数组
     * @param grantResults 结果数组
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (BuildConfig.DEBUG) {
            Log.e(getClass().getSimpleName(), "请求结果：" + Arrays.toString(grantResults));
        }
        List<String> unAgreeList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                unAgreeList.add(permissions[i]);
            }
        }
        PermissionKit.getInstance().requestPermissionsResult(requestCode, permissions, unAgreeList);
    }
}
