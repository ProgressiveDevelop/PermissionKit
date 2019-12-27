package com.permission.kit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.SparseArray;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 6.0权限处理工具类
 */
@TargetApi(Build.VERSION_CODES.M)
public class PermissionKit {
    private PermissionKit() {
    }

    private static class PermissionHolder {
        private static final PermissionKit PERMISSION_KIT = new PermissionKit();
    }

    /**
     * 获取权限工具类
     *
     * @return 权限工具对象
     */
    public static PermissionKit getInstance() {
        return PermissionHolder.PERMISSION_KIT;
    }

    //权限监听存储器
    private SparseArray<OnPermissionListener> listenerSparseArray = new SparseArray<>();

    /**
     * 是否是6.0及以上版本
     *
     * @return true|false
     */
    public boolean isM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查应用是否有该权限
     *
     * @param context     当前 context
     * @param permissions 权限数组{Manifest.permission.CAMERA}
     * @return true ==> 已经授权
     */
    public boolean hasPermission(Context context, String... permissions) {
        if (isM()) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 申请权限
     *
     * @param activity    当前Activity
     * @param requestCode 请求码
     * @param callback    回调接口
     * @param permissions 权限
     */
    public void requestPermission(Activity activity, int requestCode, OnPermissionListener callback, String... permissions) {
        if (!hasPermission(activity, permissions)) {
            String[] ps = registerCallback(activity, requestCode, callback, permissions);
            if (ps == null || ps.length == 0) {
                callback.onSuccess(requestCode, permissions);
            } else {
                activity.requestPermissions(ps, requestCode);
            }
        } else {
            callback.onSuccess(requestCode, permissions);
        }
    }

    /**
     * 申请权限结果
     *
     * @param requestCode 请求码
     * @param permissions 所有请求权限
     * @param unAgreeList 未同意的权限
     */
   public void requestPermissionsResult(int requestCode, String[] permissions, List<String> unAgreeList) {
        int hashCode = Arrays.hashCode(permissions);
        OnPermissionListener listener = listenerSparseArray.get(hashCode);
        if (listener != null) {
            if (unAgreeList.size() > 0) {
                listener.onFail(requestCode, list2Array(unAgreeList));
            } else {
                listener.onSuccess(requestCode, permissions);
            }
        }
        listenerSparseArray.remove(hashCode);
    }

    /**
     * 申请悬浮框权限
     *
     * @param activity 当前Activity
     * @param callback 回调接口
     */
    public void requestAlertWindow(Activity activity, OnPermissionListener callback) {
        if (isM()) {
            if (activity != null) {
                registerCallback(callback, Arrays.hashCode(new String[]{Settings.ACTION_MANAGE_OVERLAY_PERMISSION}));
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(getUri(activity));
                activity.startActivityForResult(intent, OnPermissionListener.PERMISSION_ALERT_WINDOW);
            }
        }
    }

    /**
     * 请求修改系统设置权限
     *
     * @param activity 当前Activity
     * @param callback 回调接口
     */
    public void requestWriteSetting(Activity activity, OnPermissionListener callback) {
        if (isM()) {
            registerCallback(callback, Arrays.hashCode(new String[]{Settings.ACTION_MANAGE_WRITE_SETTINGS}));
            if (activity != null) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(getUri(activity));
                activity.startActivityForResult(intent, OnPermissionListener.PERMISSION_WRITE_SETTING);
            }
        }
    }

    /**
     * 拒绝后引导去设置
     *
     * @param activity     当前Activity
     * @param defaultGuide 是否使用默认引导
     * @param requestCode  请求码
     * @param permissions  未授权权限
     * @param customGuide  用户自定义引导设置权限接口：defaultGuide 为true时，不应 null
     */
    public void guideSetting(Activity activity, boolean defaultGuide, int requestCode, onCustomGuide customGuide, String... permissions) {
        for (String str : permissions) {
                /*
                  获取是否应显示具有请求权限的UI界面。
                  仅当您没有权限且请求权限的上下文未明确告知用户时，才应执行此操作。
                 */
            if (activity.shouldShowRequestPermissionRationale(str)) {
                //再次询问
                if (defaultGuide) {
                    showTipDialog(activity, requestCode, 1, permissions);
                } else {
                    if (customGuide != null) {
                        customGuide.againRequest();
                    } else {
                        throw new NullPointerException("自定义权限处理,onCustomGuide不能为空");
                    }
                }
                break;
            } else {
                //拒绝并不再询问,引导用户去设置
                if (defaultGuide) {
                    showTipDialog(activity, requestCode, 2, permissions);
                } else {
                    if (customGuide != null) {
                        customGuide.goSetting();
                    } else {
                        throw new NullPointerException("自定义权限处理,onCustomGuide不能为空");
                    }
                }
            }
        }
    }

    /**
     * 提示用户授权
     *
     * @param activity    当前Activity
     * @param requestCode 请求码
     * @param type        1表示再次询问；2表示去设置
     * @param permissions 权限
     */
    private void showTipDialog(final Activity activity, final int requestCode, final int type, final String[] permissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.tip_permission_title));
        builder.setMessage(activity.getResources().getString(type == 1 ? R.string.tip_permission_msg_ask : R.string.tip_permission_msg_setting));
        builder.setPositiveButton(activity.getResources().getString(type == 1 ? R.string.tip_permission_btn_ok : R.string.tip_permission_btn_ok_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (type == 1) {
                    //再次询问申请
                    activity.requestPermissions(permissions, requestCode);
                } else {
                    //去设置
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(getUri(activity));
                    activity.startActivityForResult(intent, OnPermissionListener.PERMISSION_SETTING);
                }
            }
        });
        builder.setNegativeButton(activity.getResources().getString(R.string.tip_permission_btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 获取Uri
     *
     * @param activity 当前Activity
     * @return Uri
     */
    private Uri getUri(Activity activity) {
        return Uri.parse("package:" + activity.getPackageName());
    }

    /**
     * 注册回调接口
     *
     * @param callback 回调接口
     * @param hashCode 权限码
     */
    private void registerCallback(OnPermissionListener callback, int hashCode) {
        OnPermissionListener mBack = listenerSparseArray.get(hashCode);
        if (mBack == null) {
            listenerSparseArray.append(hashCode, callback);
        }
    }

    /**
     * 注册回调接口
     *
     * @param activity    Activity
     * @param requestCode 请求码
     * @param callback    回调接口
     * @param permission  权限数组
     * @return 请求码数组
     */
    private String[] registerCallback(Activity activity, int requestCode, OnPermissionListener callback,
                                      String... permission) {
        if (permission == null || permission.length == 0) {
            callback.onSuccess(requestCode, permission);
            return null;
        }
        List<String> list = checkPermission(activity, permission);
        if (list.size() == 0) {
            callback.onSuccess(requestCode, permission);
            return null;
        }
        String[] denyPermission = list2Array(list);
        int hashCode = Arrays.hashCode(denyPermission);
        OnPermissionListener listener = listenerSparseArray.get(hashCode);
        if (listener == null) {
            listenerSparseArray.append(hashCode, callback);
        }
        return denyPermission;
    }

    /**
     * List 转 数组
     *
     * @param denyPermission 权限列表
     * @return 权限数组
     */
    private String[] list2Array(List<String> denyPermission) {
        String[] array = new String[denyPermission.size()];
        for (int i = 0, count = denyPermission.size(); i < count; i++) {
            array[i] = denyPermission.get(i);
        }
        return array;
    }

    /**
     * 检查没有被授权的权限
     */
    private List<String> checkPermission(Activity activity, String... permission) {
        List<String> denyPermissions = new ArrayList<>();
        for (String p : permission) {
            if (activity.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(p);
            }
        }
        return denyPermissions;
    }
}
