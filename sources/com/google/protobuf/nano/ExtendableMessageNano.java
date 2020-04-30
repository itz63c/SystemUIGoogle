package com.google.protobuf.nano;

import com.google.protobuf.nano.ExtendableMessageNano;
import java.io.IOException;

public abstract class ExtendableMessageNano<M extends ExtendableMessageNano<M>> extends MessageNano {
    protected FieldArray unknownFieldData;

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        if (this.unknownFieldData == null) {
            return 0;
        }
        int i = 0;
        for (int i2 = 0; i2 < this.unknownFieldData.size(); i2++) {
            i += this.unknownFieldData.dataAt(i2).computeSerializedSize();
        }
        return i;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (this.unknownFieldData != null) {
            for (int i = 0; i < this.unknownFieldData.size(); i++) {
                this.unknownFieldData.dataAt(i).writeTo(codedOutputByteBufferNano);
            }
        }
    }

    public M clone() throws CloneNotSupportedException {
        M m = (ExtendableMessageNano) super.clone();
        InternalNano.cloneUnknownFieldData(this, m);
        return m;
    }
}
