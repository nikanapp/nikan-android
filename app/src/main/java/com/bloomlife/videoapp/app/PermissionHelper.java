package com.bloomlife.videoapp.app;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bloomlife.android.common.util.UiHelper;
import com.bloomlife.videoapp.manager.LocationManager;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class PermissionHelper {

    public static final String TAG = PermissionHelper.class.getSimpleName();

    public static void startLocation(Activity activity) {
        XXPermissions.with(activity)
                // 申请单个权限
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            Log.i(TAG, "获取部分定位权限成功，但部分权限未正常授予");
                            return;
                        }
                        Log.i(TAG, "获取定位权限成功");
                        LocationManager.getInstance(activity.getApplicationContext()).startLocation();
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) {
                            UiHelper.showToast(activity.getApplicationContext(), "被永久拒绝授权，请手动定位权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(activity, permissions);
                        } else {
                            UiHelper.showToast(activity.getApplicationContext(), "获取定位权限失败");
                        }
                    }
                });
    }

    public static void requestExternalStorage(Activity activity) {
        XXPermissions.with(activity)
                // 申请单个权限
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            Log.i(TAG, "获取部分储存空间权限成功，但部分权限未正常授予");
                            return;
                        }
                        Log.i(TAG, "获取储存空间权限成功");
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        if (doNotAskAgain) {
                            UiHelper.showToast(activity.getApplicationContext(), "被永久拒绝授权，请手动授予储存空间权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(activity, permissions);
                        } else {
                            UiHelper.showToast(activity.getApplicationContext(), "获取储存空间权限失败");
                            activity.finish();
                        }
                    }
                });
    }

}
