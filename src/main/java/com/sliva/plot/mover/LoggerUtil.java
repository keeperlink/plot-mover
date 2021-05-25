/*
 * GNU GENERAL PUBLIC LICENSE
 */
package com.sliva.plot.mover;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Sliva Co
 */
public final class LoggerUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static void log(String message) {
        System.out.println(new SimpleDateFormat(DATE_FORMAT).format(new Date()) + " " + message);
    }

    public static String format(long value) {
        return NumberFormat.getIntegerInstance().format(value);
    }
}
