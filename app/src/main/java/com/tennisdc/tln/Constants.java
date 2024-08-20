package com.tennisdc.tln;

import android.Manifest;

/**
 * Created  on 2015-05-08.
 */
public class Constants {
// Local Server Address
//    public static final String BASE_URL = "http://192.168.0.102:3000";

    // Server Address
    public static final String BASE_URL = "https://www.tennisdc.com";
//    public static final String BASE_URL = "https://6e64bae3.ngrok.io";
//    public static final String BASE_URL = "http://ec2-18-232-251-207.compute-1.amazonaws.com/";
    //    public static final String BASE_URL = "https://62dd9cdf.ngrok.io";
    public static final int REQUEST_CODE_ASK_CAMERA_MULTIPLE_PERMISSIONS = 1005;//request codes used in app
    public static final int REQUEST_CAMERA = 1001;
    public static final int REQUEST_GALLERY = 1002;
    public static final String CAMERA_PERMISSION = android.Manifest.permission.CAMERA;
    public static final String READ_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int DIALOG_DENY = 0;
    public static final int DIALOG_NEVER_ASK = 1;
    public static final String UPDATE_NOTIFICATION_COUNT = "update_notification_count";
    public static final String UPDATE_PROFILE = "update_profile";

    public static final String EXTRA_RESULT_CONFIRMATION = "payment_result";
}
