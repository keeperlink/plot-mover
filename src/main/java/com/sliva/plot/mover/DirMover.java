/*
 * GNU GENERAL PUBLIC LICENSE
 */
package com.sliva.plot.mover;

import static com.sliva.plot.mover.IOUtils.GB;
import static com.sliva.plot.mover.IOUtils.MB;
import static com.sliva.plot.mover.IOUtils.isPresentFileWithExt;
import static com.sliva.plot.mover.LoggerUtil.format;
import static com.sliva.plot.mover.LoggerUtil.log;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 *
 * @author Sliva Co
 */
@SuppressWarnings("SleepWhileInLoop")
public class DirMover {

    private static final int COPY_BUFFER_SIZE = 10 * MB;
    private final File sourceDir;
    private final File destinationDir;
    private final int copyThrottle;
    private final AtomicBoolean interrupted;
    private final byte[] copyBuffer = new byte[COPY_BUFFER_SIZE];
    private final AtomicBoolean paused = new AtomicBoolean();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });

    public DirMover(File sourceDir, File destinationDir, int copyThrottle, AtomicBoolean interrupted) {
        this.sourceDir = sourceDir;
        this.destinationDir = destinationDir;
        this.copyThrottle = copyThrottle;
        this.interrupted = interrupted;
        log("DirMover: sourceDir=" + sourceDir + ", destinationDir=" + destinationDir);
        scheduler.scheduleAtFixedRate(() -> checkPause(), 5, 5, TimeUnit.SECONDS);
    }

    public void run() throws InterruptedException {
        for (;;) {
            Stream.of(sourceDir.listFiles(f -> f.isFile() && f.getName().toLowerCase().endsWith(".plot"))).max(Comparator.comparing((File f) -> f.getName()))
                    .ifPresent(file -> processFile(file));
            if (interrupted.get()) {
                return;
            }
            Thread.sleep(1000);
            if (interrupted.get()) {
                log("Interrupted at waiting for plot");
                return;
            }
        }
    }

    private void processFile(File file) {
        try {
            while (isTmpFilePresent()) {
                Thread.sleep(100);
                if (interrupted.get()) {
                    log("Interrupted at tmp waiting");
                    return;
                }
            }
            log("Source file: " + file.getAbsolutePath());
            long destinationTotalSpace = destinationDir.getTotalSpace();
            long destinationAvailableSpace = destinationDir.getUsableSpace();
            log("Destination drive available space: " + format(destinationAvailableSpace / GB) + " out of " + format(destinationTotalSpace / GB) + " GB");
            new FileMover(file, destinationDir, copyThrottle, copyBuffer, paused, interrupted).run();
        } catch (InterruptedException | IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private void checkPause() {
        boolean tmpPresent = isTmpFilePresent();
        if (!paused.get() && tmpPresent) {
            paused.set(true);
            log("Pause copying - tmp file is present");
        } else if (paused.get() && !tmpPresent) {
            paused.set(false);
            log("Resume copying - tmp file is absent");
        }
    }

    private boolean isTmpFilePresent() {
        return isPresentFileWithExt(sourceDir, "tmp") || isPresentFileWithExt(sourceDir, "moving") || isPresentFileWithExt(destinationDir, "tmp");
    }
}
