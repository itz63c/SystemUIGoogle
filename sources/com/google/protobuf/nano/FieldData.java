package com.google.protobuf.nano;

import java.io.IOException;

class FieldData implements Cloneable {
    /* access modifiers changed from: 0000 */
    public abstract int computeSerializedSize();

    /* access modifiers changed from: 0000 */
    public abstract void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException;
}
