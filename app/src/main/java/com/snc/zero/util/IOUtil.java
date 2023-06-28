package com.snc.zero.util;

import java.io.InputStream;
import java.io.OutputStream;

import timber.log.Timber;

/**
 * IO Utilities
 *
 * @author mcharima5@gmail.com
 * @since 2016
 */
public class IOUtil {

    public static void closeQuietly(InputStream input) {
        try {
            if (null != input) {
                input.close();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void closeQuietly(OutputStream output) {
        try {
            if (null != output) {
                output.flush();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        try {
            if (null != output) {
                output.close();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

}
