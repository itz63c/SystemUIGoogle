package com.google.android.systemui.elmyra.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public final class ElmyraFilters$Filter extends MessageNano {
    private static volatile ElmyraFilters$Filter[] _emptyArray;
    private int parametersCase_ = 0;
    private Object parameters_;

    public ElmyraFilters$Filter clearParameters() {
        this.parametersCase_ = 0;
        this.parameters_ = null;
        return this;
    }

    public static ElmyraFilters$Filter[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new ElmyraFilters$Filter[0];
                }
            }
        }
        return _emptyArray;
    }

    public ElmyraFilters$Filter() {
        clear();
    }

    public ElmyraFilters$Filter clear() {
        clearParameters();
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        if (this.parametersCase_ == 1) {
            codedOutputByteBufferNano.writeMessage(1, (MessageNano) this.parameters_);
        }
        if (this.parametersCase_ == 2) {
            codedOutputByteBufferNano.writeMessage(2, (MessageNano) this.parameters_);
        }
        if (this.parametersCase_ == 3) {
            codedOutputByteBufferNano.writeMessage(3, (MessageNano) this.parameters_);
        }
        if (this.parametersCase_ == 4) {
            codedOutputByteBufferNano.writeMessage(4, (MessageNano) this.parameters_);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        if (this.parametersCase_ == 1) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, (MessageNano) this.parameters_);
        }
        if (this.parametersCase_ == 2) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, (MessageNano) this.parameters_);
        }
        if (this.parametersCase_ == 3) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, (MessageNano) this.parameters_);
        }
        return this.parametersCase_ == 4 ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(4, (MessageNano) this.parameters_) : computeSerializedSize;
    }

    public ElmyraFilters$Filter mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 10) {
                if (this.parametersCase_ != 1) {
                    this.parameters_ = new ElmyraFilters$FIRFilter();
                }
                codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                this.parametersCase_ = 1;
            } else if (readTag == 18) {
                if (this.parametersCase_ != 2) {
                    this.parameters_ = new ElmyraFilters$HighpassFilter();
                }
                codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                this.parametersCase_ = 2;
            } else if (readTag == 26) {
                if (this.parametersCase_ != 3) {
                    this.parameters_ = new ElmyraFilters$LowpassFilter();
                }
                codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                this.parametersCase_ = 3;
            } else if (readTag == 34) {
                if (this.parametersCase_ != 4) {
                    this.parameters_ = new ElmyraFilters$MedianFilter();
                }
                codedInputByteBufferNano.readMessage((MessageNano) this.parameters_);
                this.parametersCase_ = 4;
            } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                return this;
            }
        }
    }
}
