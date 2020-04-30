package com.google.android.systemui.elmyra.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public final class ElmyraFilters$LowpassFilter extends MessageNano {
    public float cutoff;
    public float rate;

    public ElmyraFilters$LowpassFilter() {
        clear();
    }

    public ElmyraFilters$LowpassFilter clear() {
        this.cutoff = 0.0f;
        this.rate = 0.0f;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (Float.floatToIntBits(this.cutoff) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(1, this.cutoff);
        }
        if (Float.floatToIntBits(this.rate) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(2, this.rate);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        if (Float.floatToIntBits(this.cutoff) != Float.floatToIntBits(0.0f)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.cutoff);
        }
        return Float.floatToIntBits(this.rate) != Float.floatToIntBits(0.0f) ? computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(2, this.rate) : computeSerializedSize;
    }

    public ElmyraFilters$LowpassFilter mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 13) {
                this.cutoff = codedInputByteBufferNano.readFloat();
            } else if (readTag == 21) {
                this.rate = codedInputByteBufferNano.readFloat();
            } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                return this;
            }
        }
    }
}
