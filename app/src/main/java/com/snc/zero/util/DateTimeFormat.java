package com.snc.zero.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Data & Time Utilities
 *
 * @author mcharima5@gmail.com
 * @since 2018
 */
public class DateTimeFormat {

    public static String format(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        return formatter.format(date);
    }

}
