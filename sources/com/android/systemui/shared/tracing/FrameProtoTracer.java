package com.android.systemui.shared.tracing;

import android.os.Trace;
import android.util.Log;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import com.android.internal.util.TraceBuffer;
import com.android.internal.util.TraceBuffer.ProtoProvider;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class FrameProtoTracer<P, S extends P, T extends P, R> implements FrameCallback {
    private final TraceBuffer<P, S, T> mBuffer;
    private volatile boolean mEnabled;
    private final Object mLock = new Object();
    /* access modifiers changed from: private */
    public final ProtoTraceParams<P, S, T, R> mParams;
    private final Queue<T> mPool = new LinkedList();
    private final ProtoProvider<P, S, T> mProvider = new ProtoProvider<P, S, T>() {
        public int getItemSize(P p) {
            return FrameProtoTracer.this.mParams.getProtoSize(p);
        }

        public byte[] getBytes(P p) {
            return FrameProtoTracer.this.mParams.getProtoBytes(p);
        }

        public void write(S s, Queue<T> queue, OutputStream outputStream) throws IOException {
            outputStream.write(FrameProtoTracer.this.mParams.serializeEncapsulatingProto(s, queue));
        }
    };
    private final ArrayList<ProtoTraceable<R>> mTmpTraceables = new ArrayList<>();
    private final File mTraceFile;
    private final ArrayList<ProtoTraceable<R>> mTraceables = new ArrayList<>();

    public interface ProtoTraceParams<P, S, T, R> {
        S getEncapsulatingTraceProto();

        byte[] getProtoBytes(P p);

        int getProtoSize(P p);

        File getTraceFile();

        byte[] serializeEncapsulatingProto(S s, Queue<T> queue);

        T updateBufferProto(T t, ArrayList<ProtoTraceable<R>> arrayList);
    }

    public FrameProtoTracer(ProtoTraceParams<P, S, T, R> protoTraceParams) {
        this.mParams = protoTraceParams;
        this.mBuffer = new TraceBuffer<>(1048576, this.mProvider, new Consumer<T>() {
            public void accept(T t) {
                FrameProtoTracer.this.onProtoDequeued(t);
            }
        });
        this.mTraceFile = protoTraceParams.getTraceFile();
        Choreographer.getMainThreadInstance();
    }

    public void start() {
        synchronized (this.mLock) {
            if (!this.mEnabled) {
                this.mBuffer.resetBuffer();
                this.mEnabled = true;
                logState();
            }
        }
    }

    public void stop() {
        synchronized (this.mLock) {
            if (this.mEnabled) {
                this.mEnabled = false;
                writeToFile();
            }
        }
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public void add(ProtoTraceable<R> protoTraceable) {
        synchronized (this.mLock) {
            this.mTraceables.add(protoTraceable);
        }
    }

    public void update() {
        if (this.mEnabled) {
            logState();
        }
    }

    public float getBufferUsagePct() {
        return ((float) this.mBuffer.getBufferSize()) / 1048576.0f;
    }

    public void doFrame(long j) {
        logState();
    }

    /* access modifiers changed from: private */
    public void onProtoDequeued(T t) {
        this.mPool.add(t);
    }

    private void logState() {
        synchronized (this.mLock) {
            this.mTmpTraceables.addAll(this.mTraceables);
        }
        this.mBuffer.add(this.mParams.updateBufferProto(this.mPool.poll(), this.mTmpTraceables));
        this.mTmpTraceables.clear();
    }

    private void writeToFile() {
        try {
            Trace.beginSection("ProtoTracer.writeToFile");
            this.mBuffer.writeTraceToFile(this.mTraceFile, this.mParams.getEncapsulatingTraceProto());
        } catch (IOException e) {
            Log.e("FrameProtoTracer", "Unable to write buffer to file", e);
        } catch (Throwable th) {
            Trace.endSection();
            throw th;
        }
        Trace.endSection();
    }
}
