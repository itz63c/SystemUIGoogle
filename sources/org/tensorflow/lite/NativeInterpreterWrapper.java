package org.tensorflow.lite;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.tensorflow.lite.Interpreter.Options;
import org.tensorflow.lite.nnapi.NnApiDelegate;

final class NativeInterpreterWrapper implements AutoCloseable {
    private final List<Delegate> delegates = new ArrayList();
    private long errorHandle;
    private Tensor[] inputTensors;
    private long interpreterHandle;
    private boolean isMemoryAllocated = false;
    private ByteBuffer modelByteBuffer;
    private long modelHandle;
    private Tensor[] outputTensors;
    private final List<AutoCloseable> ownedDelegates = new ArrayList();

    private static native long allocateTensors(long j, long j2);

    private static native void allowBufferHandleOutput(long j, boolean z);

    private static native void allowFp16PrecisionForFp32(long j, boolean z);

    private static native void applyDelegate(long j, long j2, long j3);

    private static native long createErrorReporter(int i);

    private static native long createInterpreter(long j, long j2, int i);

    private static native long createModelWithBuffer(ByteBuffer byteBuffer, long j);

    private static native void delete(long j, long j2, long j3);

    private static native int getInputCount(long j);

    private static native int getInputTensorIndex(long j, int i);

    private static native int getOutputCount(long j);

    private static native int getOutputTensorIndex(long j, int i);

    private static native boolean hasUnresolvedFlexOp(long j);

    private static native boolean resizeInput(long j, long j2, int i, int[] iArr);

    private static native void run(long j, long j2);

    NativeInterpreterWrapper(ByteBuffer byteBuffer, Options options) {
        TensorFlowLite.init();
        if (byteBuffer == null || (!(byteBuffer instanceof MappedByteBuffer) && (!byteBuffer.isDirect() || byteBuffer.order() != ByteOrder.nativeOrder()))) {
            throw new IllegalArgumentException("Model ByteBuffer should be either a MappedByteBuffer of the model file, or a direct ByteBuffer using ByteOrder.nativeOrder() which contains bytes of model content.");
        }
        this.modelByteBuffer = byteBuffer;
        long createErrorReporter = createErrorReporter(512);
        init(createErrorReporter, createModelWithBuffer(this.modelByteBuffer, createErrorReporter), options);
    }

    private void init(long j, long j2, Options options) {
        if (options == null) {
            options = new Options();
        }
        this.errorHandle = j;
        this.modelHandle = j2;
        long createInterpreter = createInterpreter(j2, j, options.numThreads);
        this.interpreterHandle = createInterpreter;
        this.inputTensors = new Tensor[getInputCount(createInterpreter)];
        this.outputTensors = new Tensor[getOutputCount(this.interpreterHandle)];
        Boolean bool = options.allowFp16PrecisionForFp32;
        if (bool != null) {
            allowFp16PrecisionForFp32(this.interpreterHandle, bool.booleanValue());
        }
        Boolean bool2 = options.allowBufferHandleOutput;
        if (bool2 != null) {
            allowBufferHandleOutput(this.interpreterHandle, bool2.booleanValue());
        }
        applyDelegates(options);
        allocateTensors(this.interpreterHandle, j);
        this.isMemoryAllocated = true;
    }

    public void close() {
        int i = 0;
        while (true) {
            Tensor[] tensorArr = this.inputTensors;
            if (i >= tensorArr.length) {
                break;
            }
            if (tensorArr[i] != null) {
                tensorArr[i].close();
                this.inputTensors[i] = null;
            }
            i++;
        }
        int i2 = 0;
        while (true) {
            Tensor[] tensorArr2 = this.outputTensors;
            if (i2 >= tensorArr2.length) {
                break;
            }
            if (tensorArr2[i2] != null) {
                tensorArr2[i2].close();
                this.outputTensors[i2] = null;
            }
            i2++;
        }
        delete(this.errorHandle, this.modelHandle, this.interpreterHandle);
        this.errorHandle = 0;
        this.modelHandle = 0;
        this.interpreterHandle = 0;
        this.modelByteBuffer = null;
        this.isMemoryAllocated = false;
        this.delegates.clear();
        for (AutoCloseable close : this.ownedDelegates) {
            try {
                close.close();
            } catch (Exception e) {
                PrintStream printStream = System.err;
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to close flex delegate: ");
                sb.append(e);
                printStream.println(sb.toString());
            }
        }
        this.ownedDelegates.clear();
    }

    /* access modifiers changed from: 0000 */
    public void run(Object[] objArr, Map<Integer, Object> map) {
        if (objArr == null || objArr.length == 0) {
            throw new IllegalArgumentException("Input error: Inputs should not be null or empty.");
        } else if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException("Input error: Outputs should not be null or empty.");
        } else {
            int i = 0;
            for (int i2 = 0; i2 < objArr.length; i2++) {
                int[] inputShapeIfDifferent = getInputTensor(i2).getInputShapeIfDifferent(objArr[i2]);
                if (inputShapeIfDifferent != null) {
                    resizeInput(i2, inputShapeIfDifferent);
                }
            }
            boolean z = !this.isMemoryAllocated;
            if (z) {
                allocateTensors(this.interpreterHandle, this.errorHandle);
                this.isMemoryAllocated = true;
            }
            for (int i3 = 0; i3 < objArr.length; i3++) {
                getInputTensor(i3).setTo(objArr[i3]);
            }
            System.nanoTime();
            run(this.interpreterHandle, this.errorHandle);
            System.nanoTime();
            if (z) {
                while (true) {
                    Tensor[] tensorArr = this.outputTensors;
                    if (i >= tensorArr.length) {
                        break;
                    }
                    if (tensorArr[i] != null) {
                        tensorArr[i].refreshShape();
                    }
                    i++;
                }
            }
            for (Entry entry : map.entrySet()) {
                getOutputTensor(((Integer) entry.getKey()).intValue()).copyTo(entry.getValue());
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void resizeInput(int i, int[] iArr) {
        if (resizeInput(this.interpreterHandle, this.errorHandle, i, iArr)) {
            this.isMemoryAllocated = false;
            Tensor[] tensorArr = this.inputTensors;
            if (tensorArr[i] != null) {
                tensorArr[i].refreshShape();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public Tensor getInputTensor(int i) {
        if (i >= 0) {
            Tensor[] tensorArr = this.inputTensors;
            if (i < tensorArr.length) {
                Tensor tensor = tensorArr[i];
                if (tensor != null) {
                    return tensor;
                }
                long j = this.interpreterHandle;
                Tensor fromIndex = Tensor.fromIndex(j, getInputTensorIndex(j, i));
                tensorArr[i] = fromIndex;
                return fromIndex;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid input Tensor index: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    /* access modifiers changed from: 0000 */
    public Tensor getOutputTensor(int i) {
        if (i >= 0) {
            Tensor[] tensorArr = this.outputTensors;
            if (i < tensorArr.length) {
                Tensor tensor = tensorArr[i];
                if (tensor != null) {
                    return tensor;
                }
                long j = this.interpreterHandle;
                Tensor fromIndex = Tensor.fromIndex(j, getOutputTensorIndex(j, i));
                tensorArr[i] = fromIndex;
                return fromIndex;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid output Tensor index: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    private void applyDelegates(Options options) {
        boolean hasUnresolvedFlexOp = hasUnresolvedFlexOp(this.interpreterHandle);
        if (hasUnresolvedFlexOp) {
            Delegate maybeCreateFlexDelegate = maybeCreateFlexDelegate(options.delegates);
            if (maybeCreateFlexDelegate != null) {
                this.ownedDelegates.add((AutoCloseable) maybeCreateFlexDelegate);
                applyDelegate(this.interpreterHandle, this.errorHandle, maybeCreateFlexDelegate.getNativeHandle());
            }
        }
        try {
            for (Delegate delegate : options.delegates) {
                applyDelegate(this.interpreterHandle, this.errorHandle, delegate.getNativeHandle());
                this.delegates.add(delegate);
            }
            if (options.useNNAPI != null && options.useNNAPI.booleanValue()) {
                NnApiDelegate nnApiDelegate = new NnApiDelegate();
                this.ownedDelegates.add(nnApiDelegate);
                applyDelegate(this.interpreterHandle, this.errorHandle, nnApiDelegate.getNativeHandle());
            }
        } catch (IllegalArgumentException e) {
            if (hasUnresolvedFlexOp && !hasUnresolvedFlexOp(this.interpreterHandle)) {
                PrintStream printStream = System.err;
                StringBuilder sb = new StringBuilder();
                sb.append("Ignoring failed delegate application: ");
                sb.append(e);
                printStream.println(sb.toString());
                return;
            }
            throw e;
        }
    }

    private static Delegate maybeCreateFlexDelegate(List<Delegate> list) {
        try {
            Class cls = Class.forName("org.tensorflow.lite.flex.FlexDelegate");
            for (Delegate isInstance : list) {
                if (cls.isInstance(isInstance)) {
                    return null;
                }
            }
            return (Delegate) cls.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception unused) {
            return null;
        }
    }
}
