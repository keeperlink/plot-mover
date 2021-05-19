/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sliva.plot.mover;

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
}
