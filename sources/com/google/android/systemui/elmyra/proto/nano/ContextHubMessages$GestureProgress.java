package com.google.android.systemui.elmyra.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public final class ContextHubMessages$GestureProgress extends MessageNano {
    public float progress;

    public ContextHubMessages$GestureProgress() {
        clear();
    }

    public ContextHubMessages$GestureProgress clear() {
        this.progress = 0.0f;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (Float.floatToIntBits(this.progress) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(1, this.progress);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        return Float.floatToIntBits(this.progress) != Float.floatToIntBits(0.0f) ? computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(1, this.progress) : computeSerializedSize;
    }

    public ContextHubMessages$GestureProgress mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 13) {
                this.progress = codedInputByteBufferNano.readFloat();
            } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                return this;
            }
        }
    }

    public static ContextHubMessages$GestureProgress parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        ContextHubMessages$GestureProgress contextHubMessages$GestureProgress = new ContextHubMessages$GestureProgress();
        MessageNano.mergeFrom(contextHubMessages$GestureProgress, bArr);
        return contextHubMessages$GestureProgress;
    }
}
