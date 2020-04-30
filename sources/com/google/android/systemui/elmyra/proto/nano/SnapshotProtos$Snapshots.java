package com.google.android.systemui.elmyra.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public final class SnapshotProtos$Snapshots extends MessageNano {
    public SnapshotProtos$Snapshot[] snapshots;

    public SnapshotProtos$Snapshots() {
        clear();
    }

    public SnapshotProtos$Snapshots clear() {
        this.snapshots = SnapshotProtos$Snapshot.emptyArray();
        this.cachedSize = -1;
        return this;
    }

    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        SnapshotProtos$Snapshot[] snapshotProtos$SnapshotArr = this.snapshots;
        if (snapshotProtos$SnapshotArr != null && snapshotProtos$SnapshotArr.length > 0) {
            int i = 0;
            while (true) {
                SnapshotProtos$Snapshot[] snapshotProtos$SnapshotArr2 = this.snapshots;
                if (i >= snapshotProtos$SnapshotArr2.length) {
                    break;
                }
                SnapshotProtos$Snapshot snapshotProtos$Snapshot = snapshotProtos$SnapshotArr2[i];
                if (snapshotProtos$Snapshot != null) {
                    codedOutputByteBufferNano.writeMessage(1, snapshotProtos$Snapshot);
                }
                i++;
            }
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* access modifiers changed from: protected */
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        SnapshotProtos$Snapshot[] snapshotProtos$SnapshotArr = this.snapshots;
        if (snapshotProtos$SnapshotArr != null && snapshotProtos$SnapshotArr.length > 0) {
            int i = 0;
            while (true) {
                SnapshotProtos$Snapshot[] snapshotProtos$SnapshotArr2 = this.snapshots;
                if (i >= snapshotProtos$SnapshotArr2.length) {
                    break;
                }
                SnapshotProtos$Snapshot snapshotProtos$Snapshot = snapshotProtos$SnapshotArr2[i];
                if (snapshotProtos$Snapshot != null) {
                    computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, snapshotProtos$Snapshot);
                }
                i++;
            }
        }
        return computeSerializedSize;
    }

    public SnapshotProtos$Snapshots mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            if (readTag == 0) {
                return this;
            }
            if (readTag == 10) {
                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                SnapshotProtos$Snapshot[] snapshotProtos$SnapshotArr = this.snapshots;
                int length = snapshotProtos$SnapshotArr == null ? 0 : snapshotProtos$SnapshotArr.length;
                int i = repeatedFieldArrayLength + length;
                SnapshotProtos$Snapshot[] snapshotProtos$SnapshotArr2 = new SnapshotProtos$Snapshot[i];
                if (length != 0) {
                    System.arraycopy(this.snapshots, 0, snapshotProtos$SnapshotArr2, 0, length);
                }
                while (length < i - 1) {
                    snapshotProtos$SnapshotArr2[length] = new SnapshotProtos$Snapshot();
                    codedInputByteBufferNano.readMessage(snapshotProtos$SnapshotArr2[length]);
                    codedInputByteBufferNano.readTag();
                    length++;
                }
                snapshotProtos$SnapshotArr2[length] = new SnapshotProtos$Snapshot();
                codedInputByteBufferNano.readMessage(snapshotProtos$SnapshotArr2[length]);
                this.snapshots = snapshotProtos$SnapshotArr2;
            } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                return this;
            }
        }
    }
}
