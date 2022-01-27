package com.snc.zero.permission;

import java.util.List;

/**
 * (TedPermission Style) Interface
 */
public interface RPermissionListener {

    void onPermissionGranted();

    void onPermissionDenied(List<String> deniedPermissions);

    void onPermissionRationaleShouldBeShown(List<String> deniedPermissions);

}