package org.tensorflow.lite;

import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

public final class Tensor {
    private final DataType dtype;
    private long nativeHandle;
    private int[] shapeCopy;

    private static native ByteBuffer buffer(long j);

    private static native long create(long j, int i);

    private static native void delete(long j);

    private static native int dtype(long j);

    private static native boolean hasDelegateBufferHandle(long j);

    private static native int numBytes(long j);

    private static native void readMultiDimensionalArray(long j, Object obj);

    private static native int[] shape(long j);

    private static native void writeDirectBuffer(long j, Buffer buffer);

    private static native void writeMultiDimensionalArray(long j, Object obj);

    static Tensor fromIndex(long j, int i) {
        return new Tensor(create(j, i));
    }

    /* access modifiers changed from: 0000 */
    public void close() {
        delete(this.nativeHandle);
        this.nativeHandle = 0;
    }

    public int numBytes() {
        return numBytes(this.nativeHandle);
    }

    /* access modifiers changed from: 0000 */
    public void setTo(Object obj) {
        if (obj != null) {
            throwIfDataIsIncompatible(obj);
            if (isBuffer(obj)) {
                setTo((Buffer) obj);
            } else {
                writeMultiDimensionalArray(this.nativeHandle, obj);
            }
        } else if (!hasDelegateBufferHandle(this.nativeHandle)) {
            throw new IllegalArgumentException("Null inputs are allowed only if the Tensor is bound to a buffer handle.");
        }
    }

    private void setTo(Buffer buffer) {
        if (buffer instanceof ByteBuffer) {
            ByteBuffer byteBuffer = (ByteBuffer) buffer;
            if (!byteBuffer.isDirect() || byteBuffer.order() != ByteOrder.nativeOrder()) {
                buffer().put(byteBuffer);
            } else {
                writeDirectBuffer(this.nativeHandle, buffer);
            }
        } else if (buffer instanceof LongBuffer) {
            LongBuffer longBuffer = (LongBuffer) buffer;
            if (!longBuffer.isDirect() || longBuffer.order() != ByteOrder.nativeOrder()) {
                buffer().asLongBuffer().put(longBuffer);
            } else {
                writeDirectBuffer(this.nativeHandle, buffer);
            }
        } else if (buffer instanceof FloatBuffer) {
            FloatBuffer floatBuffer = (FloatBuffer) buffer;
            if (!floatBuffer.isDirect() || floatBuffer.order() != ByteOrder.nativeOrder()) {
                buffer().asFloatBuffer().put(floatBuffer);
            } else {
                writeDirectBuffer(this.nativeHandle, buffer);
            }
        } else if (buffer instanceof IntBuffer) {
            IntBuffer intBuffer = (IntBuffer) buffer;
            if (!intBuffer.isDirect() || intBuffer.order() != ByteOrder.nativeOrder()) {
                buffer().asIntBuffer().put(intBuffer);
            } else {
                writeDirectBuffer(this.nativeHandle, buffer);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Unexpected input buffer type: ");
            sb.append(buffer);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    /* access modifiers changed from: 0000 */
    public Object copyTo(Object obj) {
        if (obj != null) {
            throwIfDataIsIncompatible(obj);
            if (isBuffer(obj)) {
                copyTo((Buffer) obj);
            } else {
                readMultiDimensionalArray(this.nativeHandle, obj);
            }
            return obj;
        } else if (hasDelegateBufferHandle(this.nativeHandle)) {
            return obj;
        } else {
            throw new IllegalArgumentException("Null outputs are allowed only if the Tensor is bound to a buffer handle.");
        }
    }

    private void copyTo(Buffer buffer) {
        if (buffer instanceof ByteBuffer) {
            ((ByteBuffer) buffer).put(buffer());
        } else if (buffer instanceof FloatBuffer) {
            ((FloatBuffer) buffer).put(buffer().asFloatBuffer());
        } else if (buffer instanceof LongBuffer) {
            ((LongBuffer) buffer).put(buffer().asLongBuffer());
        } else if (buffer instanceof IntBuffer) {
            ((IntBuffer) buffer).put(buffer().asIntBuffer());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Unexpected output buffer type: ");
            sb.append(buffer);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    /* access modifiers changed from: 0000 */
    public int[] getInputShapeIfDifferent(Object obj) {
        if (obj == null || isBuffer(obj)) {
            return null;
        }
        throwIfTypeIsIncompatible(obj);
        int[] computeShapeOf = computeShapeOf(obj);
        if (Arrays.equals(this.shapeCopy, computeShapeOf)) {
            return null;
        }
        return computeShapeOf;
    }

    /* access modifiers changed from: 0000 */
    public void refreshShape() {
        this.shapeCopy = shape(this.nativeHandle);
    }

    static DataType dataTypeOf(Object obj) {
        if (obj != null) {
            Class cls = obj.getClass();
            while (cls.isArray()) {
                cls = cls.getComponentType();
            }
            if (Float.TYPE.equals(cls) || (obj instanceof FloatBuffer)) {
                return DataType.FLOAT32;
            }
            if (Integer.TYPE.equals(cls) || (obj instanceof IntBuffer)) {
                return DataType.INT32;
            }
            if (Byte.TYPE.equals(cls)) {
                return DataType.UINT8;
            }
            if (Long.TYPE.equals(cls) || (obj instanceof LongBuffer)) {
                return DataType.INT64;
            }
            if (String.class.equals(cls)) {
                return DataType.STRING;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("DataType error: cannot resolve DataType of ");
        sb.append(obj.getClass().getName());
        throw new IllegalArgumentException(sb.toString());
    }

    static int[] computeShapeOf(Object obj) {
        int[] iArr = new int[computeNumDimensions(obj)];
        fillShape(obj, 0, iArr);
        return iArr;
    }

    static int computeNumDimensions(Object obj) {
        if (obj == null || !obj.getClass().isArray()) {
            return 0;
        }
        if (Array.getLength(obj) != 0) {
            return computeNumDimensions(Array.get(obj, 0)) + 1;
        }
        throw new IllegalArgumentException("Array lengths cannot be 0.");
    }

    static void fillShape(Object obj, int i, int[] iArr) {
        if (iArr != null && i != iArr.length) {
            int length = Array.getLength(obj);
            if (iArr[i] == 0) {
                iArr[i] = length;
            } else if (iArr[i] != length) {
                throw new IllegalArgumentException(String.format("Mismatched lengths (%d and %d) in dimension %d", new Object[]{Integer.valueOf(iArr[i]), Integer.valueOf(length), Integer.valueOf(i)}));
            }
            for (int i2 = 0; i2 < length; i2++) {
                fillShape(Array.get(obj, i2), i + 1, iArr);
            }
        }
    }

    private void throwIfDataIsIncompatible(Object obj) {
        throwIfTypeIsIncompatible(obj);
        throwIfShapeIsIncompatible(obj);
    }

    private void throwIfTypeIsIncompatible(Object obj) {
        if (!isByteBuffer(obj)) {
            DataType dataTypeOf = dataTypeOf(obj);
            if (dataTypeOf != this.dtype) {
                throw new IllegalArgumentException(String.format("Cannot convert between a TensorFlowLite tensor with type %s and a Java object of type %s (which is compatible with the TensorFlowLite type %s).", new Object[]{this.dtype, obj.getClass().getName(), dataTypeOf}));
            }
        }
    }

    private void throwIfShapeIsIncompatible(Object obj) {
        int i;
        if (isBuffer(obj)) {
            Buffer buffer = (Buffer) obj;
            int numBytes = numBytes();
            if (isByteBuffer(obj)) {
                i = buffer.capacity();
            } else {
                i = this.dtype.byteSize() * buffer.capacity();
            }
            if (numBytes != i) {
                throw new IllegalArgumentException(String.format("Cannot convert between a TensorFlowLite buffer with %d bytes and a Java Buffer with %d bytes.", new Object[]{Integer.valueOf(numBytes), Integer.valueOf(i)}));
            }
            return;
        }
        int[] computeShapeOf = computeShapeOf(obj);
        if (!Arrays.equals(computeShapeOf, this.shapeCopy)) {
            throw new IllegalArgumentException(String.format("Cannot copy between a TensorFlowLite tensor with shape %s and a Java object with shape %s.", new Object[]{Arrays.toString(this.shapeCopy), Arrays.toString(computeShapeOf)}));
        }
    }

    private static boolean isBuffer(Object obj) {
        return obj instanceof Buffer;
    }

    private static boolean isByteBuffer(Object obj) {
        return obj instanceof ByteBuffer;
    }

    private Tensor(long j) {
        this.nativeHandle = j;
        this.dtype = DataType.fromC(dtype(j));
        this.shapeCopy = shape(j);
    }

    private ByteBuffer buffer() {
        return buffer(this.nativeHandle).order(ByteOrder.nativeOrder());
    }
}
