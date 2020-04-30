package com.google.android.systemui.elmyra.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public final class ContextHubMessages$SensitivityUpdate extends MessageNano {
    public float sensitivity;

    public ContextHubMessages$SensitivityUpdate() {
        clear();
    }

    public ContextHubMessages$SensitivityUpdate clear() {
        this.sensitivity = 0.0f;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f)) {
            codedOutputByteBufferNano.writeFloat(1, this.sensitivity);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        return Float.floatToIntBits(this.sensitivity) != Float.floatToIntBits(0.0f) ? computeSerializedSize + CodedOutputByteBufferNano.computeFloatSize(1, this.sensitivity) : computeSerializedSize;
    }

    public ContextHubMessages$SensitivityUpdate mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 13) {
                this.sensitivity = codedInputByteBufferNano.readFloat();
            } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                return this;
            }
        }
    }
}
