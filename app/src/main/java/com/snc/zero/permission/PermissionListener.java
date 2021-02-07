package com.snc.zero.permission;

import java.util.List;

/**
 * (TedPermission Style) Interface
 */
public interface PermissionListener {

    void onPermissionGranted();

    void onPermissionDenied(List<String> deniedPermissions);

}