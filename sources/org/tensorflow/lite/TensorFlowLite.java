package org.tensorflow.lite;

public final class TensorFlowLite {
    private static final Throwable LOAD_LIBRARY_EXCEPTION;
    private static volatile boolean isInit = false;

    public static native String nativeRuntimeVersion();

    static {
        try {
            System.loadLibrary("tensorflowlite_jni");
            e = null;
        } catch (UnsatisfiedLinkError e) {
            e = e;
        }
        LOAD_LIBRARY_EXCEPTION = e;
    }

    public static String runtimeVersion() {
        init();
        return nativeRuntimeVersion();
    }

    public static void init() {
        if (!isInit) {
            try {
                nativeRuntimeVersion();
                isInit = true;
            } catch (UnsatisfiedLinkError e) {
                e = e;
                Throwable th = LOAD_LIBRARY_EXCEPTION;
                if (th != null) {
                    e = th;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to load native TensorFlow Lite methods. Check that the correct native libraries are present, and, if using a custom native library, have been properly loaded via System.loadLibrary():\n  ");
                sb.append(e);
                throw new UnsatisfiedLinkError(sb.toString());
            }
        }
    }
}
