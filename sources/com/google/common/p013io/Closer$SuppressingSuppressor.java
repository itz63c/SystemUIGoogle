package com.google.common.p013io;

import java.lang.reflect.Method;

/* renamed from: com.google.common.io.Closer$SuppressingSuppressor */
final class Closer$SuppressingSuppressor implements Closer$Suppressor {
    Closer$SuppressingSuppressor() {
    }

    static {
        new Closer$SuppressingSuppressor();
        getAddSuppressed();
    }

    private static Method getAddSuppressed() {
        try {
            return Throwable.class.getMethod("addSuppressed", new Class[]{Throwable.class});
        } catch (Throwable unused) {
            return null;
        }
    }
}
