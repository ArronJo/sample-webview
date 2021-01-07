package com.snc.sample.webview.requetcode;

/**
 * Request Code
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class RequestCode {
    // mask
    private static final int REQUEST_CODE_MASK = 0x0000FFFF;

    //++ [[START] File Chooser]
    public static final int REQUEST_FILE_CHOOSER_NORMAL   = 0x0101 & REQUEST_CODE_MASK;
    public static final int REQUEST_FILE_CHOOSER_LOLLIPOP = 0x0102 & REQUEST_CODE_MASK;
    //-- [[E N D] File Chooser]

    //++ [[START] Take a picture]
    public static final int REQUEST_TAKE_A_PICTURE = 0x0201 & REQUEST_CODE_MASK;
    //-- [[E N D] Take a picture]

}
