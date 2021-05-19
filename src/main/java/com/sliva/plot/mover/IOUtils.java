/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sliva.plot.mover;

import java.io.File;
import java.util.stream.Stream;

/**
 *
 * @author Sliva Co
 */
public final class IOUtils {

    /**
     * Check if there are files with extension fileExtension are present in the
     * directory.
     *
     * @param directory Directory to lookup files in
     * @param fileExtension File extension to look for
     * @return true if at least one file with provided extension is present
     */
    public static boolean isPresentFileWithExt(File directory, String fileExtension) {
        return Stream.of(directory.listFiles(f -> f.isFile() && f.getName().toLowerCase().endsWith("." + fileExtension.toLowerCase()))).findAny().isPresent();
    }
}
