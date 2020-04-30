package com.android.systemui.statusbar.phone.nano;

import android.support.constraint.R$styleable;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public final class TouchAnalyticsProto$Session extends MessageNano {
    public String build;
    public String deviceId;
    public long durationMillis;
    public PhoneEvent[] phoneEvents;
    public int result;
    public SensorEvent[] sensorEvents;
    public long startTimestampMillis;
    public int touchAreaHeight;
    public int touchAreaWidth;
    public TouchEvent[] touchEvents;
    public int type;

    public static final class PhoneEvent extends MessageNano {
        private static volatile PhoneEvent[] _emptyArray;
        public long timeOffsetNanos;
        public int type;

        public static PhoneEvent[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new PhoneEvent[0];
                    }
                }
            }
            return _emptyArray;
        }

        public PhoneEvent() {
            clear();
        }

        public PhoneEvent clear() {
            this.type = 0;
            this.timeOffsetNanos = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.type;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            long j = this.timeOffsetNanos;
            if (j != 0) {
                codedOutputByteBufferNano.writeUInt64(2, j);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            int i = this.type;
            if (i != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            long j = this.timeOffsetNanos;
            return j != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeUInt64Size(2, j) : computeSerializedSize;
        }

        public PhoneEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag != 0) {
                    if (readTag == 8) {
                        int readInt32 = codedInputByteBufferNano.readInt32();
                        switch (readInt32) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                            case 18:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                            case 25:
                            case 26:
                            case 27:
                            case 28:
                                this.type = readInt32;
                                break;
                        }
                    } else if (readTag == 16) {
                        this.timeOffsetNanos = codedInputByteBufferNano.readUInt64();
                    } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    return this;
                }
            }
        }
    }

    public static final class SensorEvent extends MessageNano {
        private static volatile SensorEvent[] _emptyArray;
        public long timeOffsetNanos;
        public long timestamp;
        public int type;
        public float[] values;

        public static SensorEvent[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new SensorEvent[0];
                    }
                }
            }
            return _emptyArray;
        }

        public SensorEvent() {
            clear();
        }

        public SensorEvent clear() {
            this.type = 1;
            this.timeOffsetNanos = 0;
            this.values = WireFormatNano.EMPTY_FLOAT_ARRAY;
            this.timestamp = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            int i = this.type;
            if (i != 1) {
                codedOutputByteBufferNano.writeInt32(1, i);
            }
            long j = this.timeOffsetNanos;
            if (j != 0) {
                codedOutputByteBufferNano.writeUInt64(2, j);
            }
            float[] fArr = this.values;
            if (fArr != null && fArr.length > 0) {
                int i2 = 0;
                while (true) {
                    float[] fArr2 = this.values;
                    if (i2 >= fArr2.length) {
                        break;
                    }
                    codedOutputByteBufferNano.writeFloat(3, fArr2[i2]);
                    i2++;
                }
            }
            long j2 = this.timestamp;
            if (j2 != 0) {
                codedOutputByteBufferNano.writeUInt64(4, j2);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            int i = this.type;
            if (i != 1) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            long j = this.timeOffsetNanos;
            if (j != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeUInt64Size(2, j);
            }
            float[] fArr = this.values;
            if (fArr != null && fArr.length > 0) {
                computeSerializedSize = computeSerializedSize + (fArr.length * 4) + (fArr.length * 1);
            }
            long j2 = this.timestamp;
            return j2 != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeUInt64Size(4, j2) : computeSerializedSize;
        }

        public SensorEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 8) {
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    if (readInt32 == 1 || readInt32 == 8 || readInt32 == 11 || readInt32 == 4 || readInt32 == 5) {
                        this.type = readInt32;
                    }
                } else if (readTag == 16) {
                    this.timeOffsetNanos = codedInputByteBufferNano.readUInt64();
                } else if (readTag == 26) {
                    int readRawVarint32 = codedInputByteBufferNano.readRawVarint32();
                    int pushLimit = codedInputByteBufferNano.pushLimit(readRawVarint32);
                    int i = readRawVarint32 / 4;
                    float[] fArr = this.values;
                    int length = fArr == null ? 0 : fArr.length;
                    int i2 = i + length;
                    float[] fArr2 = new float[i2];
                    if (length != 0) {
                        System.arraycopy(this.values, 0, fArr2, 0, length);
                    }
                    while (length < i2) {
                        fArr2[length] = codedInputByteBufferNano.readFloat();
                        length++;
                    }
                    this.values = fArr2;
                    codedInputByteBufferNano.popLimit(pushLimit);
                } else if (readTag == 29) {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 29);
                    float[] fArr3 = this.values;
                    int length2 = fArr3 == null ? 0 : fArr3.length;
                    int i3 = repeatedFieldArrayLength + length2;
                    float[] fArr4 = new float[i3];
                    if (length2 != 0) {
                        System.arraycopy(this.values, 0, fArr4, 0, length2);
                    }
                    while (length2 < i3 - 1) {
                        fArr4[length2] = codedInputByteBufferNano.readFloat();
                        codedInputByteBufferNano.readTag();
                        length2++;
                    }
                    fArr4[length2] = codedInputByteBufferNano.readFloat();
                    this.values = fArr4;
                } else if (readTag == 32) {
                    this.timestamp = codedInputByteBufferNano.readUInt64();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }
    }

    public static final class TouchEvent extends MessageNano {
        private static volatile TouchEvent[] _emptyArray;
        public int action;
        public int actionIndex;
        public Pointer[] pointers;
        public BoundingBox removedBoundingBox;
        public boolean removedRedacted;
        public long timeOffsetNanos;

        public static final class BoundingBox extends MessageNano {
            public float height;
            public float width;

            public BoundingBox() {
                clear();
            }

            public BoundingBox clear() {
                this.width = 0.0f;
                this.height = 0.0f;
                this.cachedSize = -1;
                return this;
            }

            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (Float.floatToIntBits(this.width) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(1, this.width);
                }
                if (Float.floatToIntBits(this.height) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(2, this.height);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            /* access modifiers changed from: protected */
            public int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (Float.floatToIntBits(this.width) != Float.floatToIntBits(0.0f)) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.width);
                }
                return Float.floatToIntBits(this.height) != Float.floatToIntBits(0.0f) ? computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(2, this.height) : computeSerializedSize;
            }

            public BoundingBox mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    if (readTag == 0) {
                        return this;
                    }
                    if (readTag == 13) {
                        this.width = codedInputByteBufferNano.readFloat();
                    } else if (readTag == 21) {
                        this.height = codedInputByteBufferNano.readFloat();
                    } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                }
            }
        }

        public static final class Pointer extends MessageNano {
            private static volatile Pointer[] _emptyArray;

            /* renamed from: id */
            public int f76id;
            public float pressure;
            public BoundingBox removedBoundingBox;
            public float removedLength;
            public float size;

            /* renamed from: x */
            public float f77x;

            /* renamed from: y */
            public float f78y;

            public static Pointer[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new Pointer[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public Pointer() {
                clear();
            }

            public Pointer clear() {
                this.f77x = 0.0f;
                this.f78y = 0.0f;
                this.size = 0.0f;
                this.pressure = 0.0f;
                this.f76id = 0;
                this.removedLength = 0.0f;
                this.removedBoundingBox = null;
                this.cachedSize = -1;
                return this;
            }

            public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
                if (Float.floatToIntBits(this.f77x) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(1, this.f77x);
                }
                if (Float.floatToIntBits(this.f78y) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(2, this.f78y);
                }
                if (Float.floatToIntBits(this.size) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(3, this.size);
                }
                if (Float.floatToIntBits(this.pressure) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(4, this.pressure);
                }
                int i = this.f76id;
                if (i != 0) {
                    codedOutputByteBufferNano.writeInt32(5, i);
                }
                if (Float.floatToIntBits(this.removedLength) != Float.floatToIntBits(0.0f)) {
                    codedOutputByteBufferNano.writeFloat(6, this.removedLength);
                }
                BoundingBox boundingBox = this.removedBoundingBox;
                if (boundingBox != null) {
                    codedOutputByteBufferNano.writeMessage(7, boundingBox);
                }
                super.writeTo(codedOutputByteBufferNano);
            }

            /* access modifiers changed from: protected */
            public int computeSerializedSize() {
                int computeSerializedSize = super.computeSerializedSize();
                if (Float.floatToIntBits(this.f77x) != Float.floatToIntBits(0.0f)) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.f77x);
                }
                if (Float.floatToIntBits(this.f78y) != Float.floatToIntBits(0.0f)) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(2, this.f78y);
                }
                if (Float.floatToIntBits(this.size) != Float.floatToIntBits(0.0f)) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(3, this.size);
                }
                if (Float.floatToIntBits(this.pressure) != Float.floatToIntBits(0.0f)) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(4, this.pressure);
                }
                int i = this.f76id;
                if (i != 0) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i);
                }
                if (Float.floatToIntBits(this.removedLength) != Float.floatToIntBits(0.0f)) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(6, this.removedLength);
                }
                BoundingBox boundingBox = this.removedBoundingBox;
                return boundingBox != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(7, boundingBox) : computeSerializedSize;
            }

            public Pointer mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
                while (true) {
                    int readTag = codedInputByteBufferNano.readTag();
                    if (readTag == 0) {
                        return this;
                    }
                    if (readTag == 13) {
                        this.f77x = codedInputByteBufferNano.readFloat();
                    } else if (readTag == 21) {
                        this.f78y = codedInputByteBufferNano.readFloat();
                    } else if (readTag == 29) {
                        this.size = codedInputByteBufferNano.readFloat();
                    } else if (readTag == 37) {
                        this.pressure = codedInputByteBufferNano.readFloat();
                    } else if (readTag == 40) {
                        this.f76id = codedInputByteBufferNano.readInt32();
                    } else if (readTag == 53) {
                        this.removedLength = codedInputByteBufferNano.readFloat();
                    } else if (readTag == 58) {
                        if (this.removedBoundingBox == null) {
                            this.removedBoundingBox = new BoundingBox();
                        }
                        codedInputByteBufferNano.readMessage(this.removedBoundingBox);
                    } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                }
            }
        }

        public static TouchEvent[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new TouchEvent[0];
                    }
                }
            }
            return _emptyArray;
        }

        public TouchEvent() {
            clear();
        }

        public TouchEvent clear() {
            this.timeOffsetNanos = 0;
            this.action = 0;
            this.actionIndex = 0;
            this.pointers = Pointer.emptyArray();
            this.removedRedacted = false;
            this.removedBoundingBox = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            long j = this.timeOffsetNanos;
            if (j != 0) {
                codedOutputByteBufferNano.writeUInt64(1, j);
            }
            int i = this.action;
            if (i != 0) {
                codedOutputByteBufferNano.writeInt32(2, i);
            }
            int i2 = this.actionIndex;
            if (i2 != 0) {
                codedOutputByteBufferNano.writeInt32(3, i2);
            }
            Pointer[] pointerArr = this.pointers;
            if (pointerArr != null && pointerArr.length > 0) {
                int i3 = 0;
                while (true) {
                    Pointer[] pointerArr2 = this.pointers;
                    if (i3 >= pointerArr2.length) {
                        break;
                    }
                    Pointer pointer = pointerArr2[i3];
                    if (pointer != null) {
                        codedOutputByteBufferNano.writeMessage(4, pointer);
                    }
                    i3++;
                }
            }
            boolean z = this.removedRedacted;
            if (z) {
                codedOutputByteBufferNano.writeBool(5, z);
            }
            BoundingBox boundingBox = this.removedBoundingBox;
            if (boundingBox != null) {
                codedOutputByteBufferNano.writeMessage(6, boundingBox);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            long j = this.timeOffsetNanos;
            if (j != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeUInt64Size(1, j);
            }
            int i = this.action;
            if (i != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i);
            }
            int i2 = this.actionIndex;
            if (i2 != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i2);
            }
            Pointer[] pointerArr = this.pointers;
            if (pointerArr != null && pointerArr.length > 0) {
                int i3 = 0;
                while (true) {
                    Pointer[] pointerArr2 = this.pointers;
                    if (i3 >= pointerArr2.length) {
                        break;
                    }
                    Pointer pointer = pointerArr2[i3];
                    if (pointer != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, pointer);
                    }
                    i3++;
                }
            }
            boolean z = this.removedRedacted;
            if (z) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, z);
            }
            BoundingBox boundingBox = this.removedBoundingBox;
            return boundingBox != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(6, boundingBox) : computeSerializedSize;
        }

        public TouchEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag != 8) {
                    if (readTag == 16) {
                        int readInt32 = codedInputByteBufferNano.readInt32();
                        switch (readInt32) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.action = readInt32;
                                break;
                        }
                    } else if (readTag == 24) {
                        this.actionIndex = codedInputByteBufferNano.readInt32();
                    } else if (readTag == 34) {
                        int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 34);
                        Pointer[] pointerArr = this.pointers;
                        int length = pointerArr == null ? 0 : pointerArr.length;
                        int i = repeatedFieldArrayLength + length;
                        Pointer[] pointerArr2 = new Pointer[i];
                        if (length != 0) {
                            System.arraycopy(this.pointers, 0, pointerArr2, 0, length);
                        }
                        while (length < i - 1) {
                            pointerArr2[length] = new Pointer();
                            codedInputByteBufferNano.readMessage(pointerArr2[length]);
                            codedInputByteBufferNano.readTag();
                            length++;
                        }
                        pointerArr2[length] = new Pointer();
                        codedInputByteBufferNano.readMessage(pointerArr2[length]);
                        this.pointers = pointerArr2;
                    } else if (readTag == 40) {
                        this.removedRedacted = codedInputByteBufferNano.readBool();
                    } else if (readTag == 50) {
                        if (this.removedBoundingBox == null) {
                            this.removedBoundingBox = new BoundingBox();
                        }
                        codedInputByteBufferNano.readMessage(this.removedBoundingBox);
                    } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                        return this;
                    }
                } else {
                    this.timeOffsetNanos = codedInputByteBufferNano.readUInt64();
                }
            }
        }
    }

    public TouchAnalyticsProto$Session() {
        clear();
    }

    public TouchAnalyticsProto$Session clear() {
        this.startTimestampMillis = 0;
        this.durationMillis = 0;
        String str = "";
        this.build = str;
        this.result = 0;
        this.touchEvents = TouchEvent.emptyArray();
        this.sensorEvents = SensorEvent.emptyArray();
        this.touchAreaWidth = 0;
        this.touchAreaHeight = 0;
        this.type = 0;
        this.phoneEvents = PhoneEvent.emptyArray();
        this.deviceId = str;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        long j = this.startTimestampMillis;
        if (j != 0) {
            codedOutputByteBufferNano.writeUInt64(1, j);
        }
        long j2 = this.durationMillis;
        if (j2 != 0) {
            codedOutputByteBufferNano.writeUInt64(2, j2);
        }
        String str = "";
        if (!this.build.equals(str)) {
            codedOutputByteBufferNano.writeString(3, this.build);
        }
        int i = this.result;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(4, i);
        }
        TouchEvent[] touchEventArr = this.touchEvents;
        int i2 = 0;
        if (touchEventArr != null && touchEventArr.length > 0) {
            int i3 = 0;
            while (true) {
                TouchEvent[] touchEventArr2 = this.touchEvents;
                if (i3 >= touchEventArr2.length) {
                    break;
                }
                TouchEvent touchEvent = touchEventArr2[i3];
                if (touchEvent != null) {
                    codedOutputByteBufferNano.writeMessage(5, touchEvent);
                }
                i3++;
            }
        }
        SensorEvent[] sensorEventArr = this.sensorEvents;
        if (sensorEventArr != null && sensorEventArr.length > 0) {
            int i4 = 0;
            while (true) {
                SensorEvent[] sensorEventArr2 = this.sensorEvents;
                if (i4 >= sensorEventArr2.length) {
                    break;
                }
                SensorEvent sensorEvent = sensorEventArr2[i4];
                if (sensorEvent != null) {
                    codedOutputByteBufferNano.writeMessage(6, sensorEvent);
                }
                i4++;
            }
        }
        int i5 = this.touchAreaWidth;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(9, i5);
        }
        int i6 = this.touchAreaHeight;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(10, i6);
        }
        int i7 = this.type;
        if (i7 != 0) {
            codedOutputByteBufferNano.writeInt32(11, i7);
        }
        PhoneEvent[] phoneEventArr = this.phoneEvents;
        if (phoneEventArr != null && phoneEventArr.length > 0) {
            while (true) {
                PhoneEvent[] phoneEventArr2 = this.phoneEvents;
                if (i2 >= phoneEventArr2.length) {
                    break;
                }
                PhoneEvent phoneEvent = phoneEventArr2[i2];
                if (phoneEvent != null) {
                    codedOutputByteBufferNano.writeMessage(12, phoneEvent);
                }
                i2++;
            }
        }
        if (!this.deviceId.equals(str)) {
            codedOutputByteBufferNano.writeString(13, this.deviceId);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        long j = this.startTimestampMillis;
        if (j != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeUInt64Size(1, j);
        }
        long j2 = this.durationMillis;
        if (j2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeUInt64Size(2, j2);
        }
        String str = "";
        if (!this.build.equals(str)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.build);
        }
        int i = this.result;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i);
        }
        TouchEvent[] touchEventArr = this.touchEvents;
        int i2 = 0;
        if (touchEventArr != null && touchEventArr.length > 0) {
            int i3 = 0;
            while (true) {
                TouchEvent[] touchEventArr2 = this.touchEvents;
                if (i3 >= touchEventArr2.length) {
                    break;
                }
                TouchEvent touchEvent = touchEventArr2[i3];
                if (touchEvent != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, touchEvent);
                }
                i3++;
            }
        }
        SensorEvent[] sensorEventArr = this.sensorEvents;
        if (sensorEventArr != null && sensorEventArr.length > 0) {
            int i4 = 0;
            while (true) {
                SensorEvent[] sensorEventArr2 = this.sensorEvents;
                if (i4 >= sensorEventArr2.length) {
                    break;
                }
                SensorEvent sensorEvent = sensorEventArr2[i4];
                if (sensorEvent != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(6, sensorEvent);
                }
                i4++;
            }
        }
        int i5 = this.touchAreaWidth;
        if (i5 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, i5);
        }
        int i6 = this.touchAreaHeight;
        if (i6 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i6);
        }
        int i7 = this.type;
        if (i7 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, i7);
        }
        PhoneEvent[] phoneEventArr = this.phoneEvents;
        if (phoneEventArr != null && phoneEventArr.length > 0) {
            while (true) {
                PhoneEvent[] phoneEventArr2 = this.phoneEvents;
                if (i2 >= phoneEventArr2.length) {
                    break;
                }
                PhoneEvent phoneEvent = phoneEventArr2[i2];
                if (phoneEvent != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(12, phoneEvent);
                }
                i2++;
            }
        }
        return !this.deviceId.equals(str) ? computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(13, this.deviceId) : computeSerializedSize;
    }

    public TouchAnalyticsProto$Session mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            switch (readTag) {
                case 0:
                    return this;
                case 8:
                    this.startTimestampMillis = codedInputByteBufferNano.readUInt64();
                    break;
                case 16:
                    this.durationMillis = codedInputByteBufferNano.readUInt64();
                    break;
                case 26:
                    this.build = codedInputByteBufferNano.readString();
                    break;
                case 32:
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    if (readInt32 != 0 && readInt32 != 1 && readInt32 != 2) {
                        break;
                    } else {
                        this.result = readInt32;
                        break;
                    }
                case R$styleable.ConstraintLayout_Layout_layout_constraintTop_toBottomOf /*42*/:
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 42);
                    TouchEvent[] touchEventArr = this.touchEvents;
                    int length = touchEventArr == null ? 0 : touchEventArr.length;
                    int i = repeatedFieldArrayLength + length;
                    TouchEvent[] touchEventArr2 = new TouchEvent[i];
                    if (length != 0) {
                        System.arraycopy(this.touchEvents, 0, touchEventArr2, 0, length);
                    }
                    while (length < i - 1) {
                        touchEventArr2[length] = new TouchEvent();
                        codedInputByteBufferNano.readMessage(touchEventArr2[length]);
                        codedInputByteBufferNano.readTag();
                        length++;
                    }
                    touchEventArr2[length] = new TouchEvent();
                    codedInputByteBufferNano.readMessage(touchEventArr2[length]);
                    this.touchEvents = touchEventArr2;
                    break;
                case 50:
                    int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 50);
                    SensorEvent[] sensorEventArr = this.sensorEvents;
                    int length2 = sensorEventArr == null ? 0 : sensorEventArr.length;
                    int i2 = repeatedFieldArrayLength2 + length2;
                    SensorEvent[] sensorEventArr2 = new SensorEvent[i2];
                    if (length2 != 0) {
                        System.arraycopy(this.sensorEvents, 0, sensorEventArr2, 0, length2);
                    }
                    while (length2 < i2 - 1) {
                        sensorEventArr2[length2] = new SensorEvent();
                        codedInputByteBufferNano.readMessage(sensorEventArr2[length2]);
                        codedInputByteBufferNano.readTag();
                        length2++;
                    }
                    sensorEventArr2[length2] = new SensorEvent();
                    codedInputByteBufferNano.readMessage(sensorEventArr2[length2]);
                    this.sensorEvents = sensorEventArr2;
                    break;
                case 72:
                    this.touchAreaWidth = codedInputByteBufferNano.readInt32();
                    break;
                case 80:
                    this.touchAreaHeight = codedInputByteBufferNano.readInt32();
                    break;
                case 88:
                    int readInt322 = codedInputByteBufferNano.readInt32();
                    if (readInt322 != 0 && readInt322 != 1 && readInt322 != 2 && readInt322 != 3 && readInt322 != 4) {
                        break;
                    } else {
                        this.type = readInt322;
                        break;
                    }
                case 98:
                    int repeatedFieldArrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 98);
                    PhoneEvent[] phoneEventArr = this.phoneEvents;
                    int length3 = phoneEventArr == null ? 0 : phoneEventArr.length;
                    int i3 = repeatedFieldArrayLength3 + length3;
                    PhoneEvent[] phoneEventArr2 = new PhoneEvent[i3];
                    if (length3 != 0) {
                        System.arraycopy(this.phoneEvents, 0, phoneEventArr2, 0, length3);
                    }
                    while (length3 < i3 - 1) {
                        phoneEventArr2[length3] = new PhoneEvent();
                        codedInputByteBufferNano.readMessage(phoneEventArr2[length3]);
                        codedInputByteBufferNano.readTag();
                        length3++;
                    }
                    phoneEventArr2[length3] = new PhoneEvent();
                    codedInputByteBufferNano.readMessage(phoneEventArr2[length3]);
                    this.phoneEvents = phoneEventArr2;
                    break;
                case 106:
                    this.deviceId = codedInputByteBufferNano.readString();
                    break;
                default:
                    if (WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                        break;
                    } else {
                        return this;
                    }
            }
        }
    }
}
