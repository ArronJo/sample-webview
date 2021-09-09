package com.snc.zero.permission;

import android.content.Context;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

import androidx.annotation.NonNull;

public class RPermission {

    public static RPermission with(Context context) {
        return new RPermission(context);
    }

    private final Context context;
    private String[] permissions;
    private RPermissionListener listener;

    public RPermission(Context context) {
        this.context = context;
    }

    public RPermission setPermissionListener(RPermissionListener listener) {
        this.listener = listener;
        return this;
    }

    public RPermission setPermissions(List<String> permissions) {
        this.permissions = permissions.toArray(new String[] {});
        return this;
    }

    public RPermission setPermissions(String[] permissions) {
        this.permissions = permissions;
        return this;
    }

    public void check() {
        TedPermission.create()
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        //Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        if (null != listener) {
                            listener.onPermissionGranted();
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        //Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                        if (null != listener) {
                            listener.onPermissionDenied(deniedPermissions);
                        }
                    }
                })
                //.setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(permissions)
                .check();
    }

    public static boolean isGranted(@NonNull String... permissions) {
        return TedPermission.isGranted(permissions);
    }

}
