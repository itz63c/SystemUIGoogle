package com.google.android.systemui.elmyra.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public final class ElmyraFilters$MedianFilter extends MessageNano {
    public int windowSize;

    public ElmyraFilters$MedianFilter() {
        clear();
    }

    public ElmyraFilters$MedianFilter clear() {
        this.windowSize = 0;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.windowSize;
        if (i != 0) {
            codedOutputByteBufferNano.writeUInt32(1, i);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        int i = this.windowSize;
        return i != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeUInt32Size(1, i) : computeSerializedSize;
    }

    public ElmyraFilters$MedianFilter mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 8) {
                this.windowSize = codedInputByteBufferNano.readUInt32();
            } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                return this;
            }
        }
    }
}
