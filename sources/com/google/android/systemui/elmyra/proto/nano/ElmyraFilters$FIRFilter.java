package com.google.android.systemui.elmyra.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public final class ElmyraFilters$FIRFilter extends MessageNano {
    public float[] coefficients;

    public ElmyraFilters$FIRFilter() {
        clear();
    }

    public ElmyraFilters$FIRFilter clear() {
        this.coefficients = WireFormatNano.EMPTY_FLOAT_ARRAY;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        float[] fArr = this.coefficients;
        if (fArr != null && fArr.length > 0) {
            int i = 0;
            while (true) {
                float[] fArr2 = this.coefficients;
                if (i >= fArr2.length) {
                    break;
                }
                codedOutputByteBufferNano.writeFloat(1, fArr2[i]);
                i++;
            }
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        float[] fArr = this.coefficients;
        return (fArr == null || fArr.length <= 0) ? computeSerializedSize : computeSerializedSize + (fArr.length * 4) + (fArr.length * 1);
    }

    public ElmyraFilters$FIRFilter mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 10) {
                int readRawVarint32 = codedInputByteBufferNano.readRawVarint32();
                int pushLimit = codedInputByteBufferNano.pushLimit(readRawVarint32);
                int i = readRawVarint32 / 4;
                float[] fArr = this.coefficients;
                int length = fArr == null ? 0 : fArr.length;
                int i2 = i + length;
                float[] fArr2 = new float[i2];
                if (length != 0) {
                    System.arraycopy(this.coefficients, 0, fArr2, 0, length);
                }
                while (length < i2) {
                    fArr2[length] = codedInputByteBufferNano.readFloat();
                    length++;
                }
                this.coefficients = fArr2;
                codedInputByteBufferNano.popLimit(pushLimit);
            } else if (readTag == 13) {
                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 13);
                float[] fArr3 = this.coefficients;
                int length2 = fArr3 == null ? 0 : fArr3.length;
                int i3 = repeatedFieldArrayLength + length2;
                float[] fArr4 = new float[i3];
                if (length2 != 0) {
                    System.arraycopy(this.coefficients, 0, fArr4, 0, length2);
                }
                while (length2 < i3 - 1) {
                    fArr4[length2] = codedInputByteBufferNano.readFloat();
                    codedInputByteBufferNano.readTag();
                    length2++;
                }
                fArr4[length2] = codedInputByteBufferNano.readFloat();
                this.coefficients = fArr4;
            } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                return this;
            }
        }
    }
}
