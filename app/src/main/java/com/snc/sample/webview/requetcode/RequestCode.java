package com.snc.sample.webview.requetcode;

public class RequestCode {
    // mask
    private static final int REQUEST_CODE_MASK = 0x0000FFFF;

    // file chooser
    public static final int REQUEST_CODE_FILE_CHOOSER_NORMAL   = 0x0101 & REQUEST_CODE_MASK;
    public static final int REQUEST_CODE_FILE_CHOOSER_LOLLIPOP = 0x0102 & REQUEST_CODE_MASK;

    // take a picture
    public static final int REQUEST_CODE_TAKE_A_PICTURE = 0x0201 & REQUEST_CODE_MASK;

}
