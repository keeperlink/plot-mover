/*
 * GNU GENERAL PUBLIC LICENSE
 */
package com.sliva.plot.mover;

import static com.sliva.plot.mover.LoggerUtil.log;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Sliva Co
 */
public class Main {

    private static final int copyThrottle = 0;
    private static final AtomicBoolean interrupted = new AtomicBoolean();
    private static final AtomicBoolean finished = new AtomicBoolean();

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: plot-mover.jar <source-directory> <destination-directory>");
        } else {
            String sourceDirectory = args[0];
            String destinationDirectory = args[1];
            log("START: sourceDirectory=" + sourceDirectory + ", destinationDirectory=" + destinationDirectory);
            File fSource = new File(sourceDirectory);
            if (!fSource.isDirectory()) {
                throw new IOException("Source path \"" + fSource.getAbsolutePath() + "\" is not a directory");
            }
            File fDestination = new File(destinationDirectory);
            if (!fDestination.isDirectory()) {
                throw new IOException("Destination path \"" + fDestination.getAbsolutePath() + "\" is not a directory");
            }
            try {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        onShutdown();
                    }
                });
                new DirMover(fSource, fDestination, copyThrottle, interrupted).run();
            } finally {
                finished.set(true);
                log("Finished.");
            }
        }
    }

    @SuppressWarnings({"CallToPrintStackTrace", "SleepWhileInLoop", "UseSpecificCatch"})
    private static void onShutdown() {
        if (finished.get()) {
            return;
        }
        try {
            log("Ctrl-C - Interrupting...");
            interrupted.set(true);
            while (!finished.get()) {
                Thread.sleep(50);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            log("onShutdown hook finished");
        }
    }
}
