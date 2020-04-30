package com.google.android.systemui.elmyra.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public final class ContextHubMessages$RecognizerStart extends MessageNano {
    public float progressReportThreshold;
    public float sensitivity;

    public ContextHubMessages$RecognizerStart() {
        clear();
    }

    public ContextHubMessages$RecognizerStart clear() {
        this.progressReportThreshold = 0.0f;
        this.sensitivity = 0.0f;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (Float.floatToIntBits(this.progressReportThreshold) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(1, this.progressReportThreshold);
        }
        if (Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(2, this.sensitivity);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        if (Float.floatToIntBits(this.progressReportThreshold) != Float.floatToIntBits(0.0f)) {
            computeSerializedSize += CodedOutputByteBufferNano.computeFloatSize(1, this.progressReportThreshold);
        }
        return Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f) ? computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(2, this.sensitivity) : computeSerializedSize;
    }

    public ContextHubMessages$RecognizerStart mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 13) {
                this.progressReportThreshold = codedInputByteBufferNano.readFloat();
            } else if (readTag == 21) {
                this.sensitivity = codedInputByteBufferNano.readFloat();
            } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                return this;
            }
        }
    }
}
