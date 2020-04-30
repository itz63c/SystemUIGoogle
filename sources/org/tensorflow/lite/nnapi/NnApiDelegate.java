package org.tensorflow.lite.nnapi;

import org.tensorflow.lite.Delegate;
import org.tensorflow.lite.TensorFlowLite;

public class NnApiDelegate implements Delegate, AutoCloseable {
    private long delegateHandle;

    public static final class Options {
        String accelerator_name = null;
        String cache_dir = null;
        int executionPreference = -1;
        String model_token = null;
    }

    private static native long createDelegate(int i, String str, String str2, String str3);

    private static native void deleteDelegate(long j);

    public NnApiDelegate(Options options) {
        TensorFlowLite.init();
        this.delegateHandle = createDelegate(options.executionPreference, options.accelerator_name, options.cache_dir, options.model_token);
    }

    public NnApiDelegate() {
        this(new Options());
    }

    public long getNativeHandle() {
        return this.delegateHandle;
    }

    public void close() {
        long j = this.delegateHandle;
        if (j != 0) {
            deleteDelegate(j);
            this.delegateHandle = 0;
        }
    }
}
