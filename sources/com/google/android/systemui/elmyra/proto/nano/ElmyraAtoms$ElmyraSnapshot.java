package com.google.android.systemui.elmyra.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public final class ElmyraAtoms$ElmyraSnapshot extends MessageNano {
    public ChassisProtos$Chassis chassis;
    public SnapshotProtos$Snapshot snapshot;

    public ElmyraAtoms$ElmyraSnapshot() {
        clear();
    }

    public ElmyraAtoms$ElmyraSnapshot clear() {
        this.snapshot = null;
        this.chassis = null;
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        SnapshotProtos$Snapshot snapshotProtos$Snapshot = this.snapshot;
        if (snapshotProtos$Snapshot != null) {
            codedOutputByteBufferNano.writeMessage(1, snapshotProtos$Snapshot);
        }
        ChassisProtos$Chassis chassisProtos$Chassis = this.chassis;
        if (chassisProtos$Chassis != null) {
            codedOutputByteBufferNano.writeMessage(2, chassisProtos$Chassis);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        SnapshotProtos$Snapshot snapshotProtos$Snapshot = this.snapshot;
        if (snapshotProtos$Snapshot != null) {
            computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, snapshotProtos$Snapshot);
        }
        ChassisProtos$Chassis chassisProtos$Chassis = this.chassis;
        return chassisProtos$Chassis != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(2, chassisProtos$Chassis) : computeSerializedSize;
    }

    public ElmyraAtoms$ElmyraSnapshot mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 10) {
                if (this.snapshot == null) {
                    this.snapshot = new SnapshotProtos$Snapshot();
                }
                codedInputByteBufferNano.readMessage(this.snapshot);
            } else if (readTag == 18) {
                if (this.chassis == null) {
                    this.chassis = new ChassisProtos$Chassis();
                }
                codedInputByteBufferNano.readMessage(this.chassis);
            } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                return this;
            }
        }
    }
}
