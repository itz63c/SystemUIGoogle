package com.google.common.util.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

final class UncaughtExceptionHandlers$Exiter implements UncaughtExceptionHandler {
    private static final Logger logger = Logger.getLogger(UncaughtExceptionHandlers$Exiter.class.getName());
    private final Runtime runtime;

    public void uncaughtException(Thread thread, Throwable th) {
        try {
            logger.log(Level.SEVERE, String.format(Locale.ROOT, "Caught an exception in %s.  Shutting down.", new Object[]{thread}), th);
        } catch (Throwable th2) {
            this.runtime.exit(1);
            throw th2;
        }
        this.runtime.exit(1);
    }
}