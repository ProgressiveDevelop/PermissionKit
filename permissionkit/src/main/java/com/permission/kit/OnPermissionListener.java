package com.permission.kit;

/**
 * 权限监听接口
 */
public interface OnPermissionListener {
    //悬浮窗
    int PERMISSION_ALERT_WINDOW = 0xad1;
    //系统设置
    int PERMISSION_WRITE_SETTING = 0xad2;
    //去设置
    int PERMISSION_SETTING = 0xad3;

    /**
     * 授权成功
     *
     * @param requestCode 请求码
     * @param permissions 所有同意权限
     */
    void onSuccess(int requestCode, String... permissions);

    /**
     * 授权失败
     *
     * @param requestCode 请求码
     * @param permissions 未同意权限
     */
    void onFail(int requestCode, String... permissions);
}
