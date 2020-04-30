package com.android.systemui.tracing;

import android.content.Context;
import android.os.SystemClock;
import com.android.systemui.Dumpable;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.shared.tracing.FrameProtoTracer;
import com.android.systemui.shared.tracing.FrameProtoTracer.ProtoTraceParams;
import com.android.systemui.shared.tracing.ProtoTraceable;
import com.android.systemui.tracing.nano.SystemUiTraceEntryProto;
import com.android.systemui.tracing.nano.SystemUiTraceFileProto;
import com.android.systemui.tracing.nano.SystemUiTraceProto;
import com.google.protobuf.nano.MessageNano;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;

public class ProtoTracer implements Dumpable, ProtoTraceParams<MessageNano, SystemUiTraceFileProto, SystemUiTraceEntryProto, SystemUiTraceProto> {
    private final Context mContext;
    private final FrameProtoTracer<MessageNano, SystemUiTraceFileProto, SystemUiTraceEntryProto, SystemUiTraceProto> mProtoTracer = new FrameProtoTracer<>(this);

    public ProtoTracer(Context context, DumpManager dumpManager) {
        this.mContext = context;
        dumpManager.registerDumpable(ProtoTracer.class.getName(), this);
    }

    public File getTraceFile() {
        return new File(this.mContext.getFilesDir(), "sysui_trace.pb");
    }

    public SystemUiTraceFileProto getEncapsulatingTraceProto() {
        return new SystemUiTraceFileProto();
    }

    public SystemUiTraceEntryProto updateBufferProto(SystemUiTraceEntryProto systemUiTraceEntryProto, ArrayList<ProtoTraceable<SystemUiTraceProto>> arrayList) {
        if (systemUiTraceEntryProto == null) {
            systemUiTraceEntryProto = new SystemUiTraceEntryProto();
        }
        systemUiTraceEntryProto.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        SystemUiTraceProto systemUiTraceProto = systemUiTraceEntryProto.systemUi;
        if (systemUiTraceProto == null) {
            systemUiTraceProto = new SystemUiTraceProto();
        }
        systemUiTraceEntryProto.systemUi = systemUiTraceProto;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((ProtoTraceable) it.next()).writeToProto(systemUiTraceEntryProto.systemUi);
        }
        return systemUiTraceEntryProto;
    }

    public byte[] serializeEncapsulatingProto(SystemUiTraceFileProto systemUiTraceFileProto, Queue<SystemUiTraceEntryProto> queue) {
        systemUiTraceFileProto.magicNumber = 4851032422572317011L;
        systemUiTraceFileProto.entry = (SystemUiTraceEntryProto[]) queue.toArray(new SystemUiTraceEntryProto[0]);
        return MessageNano.toByteArray(systemUiTraceFileProto);
    }

    public byte[] getProtoBytes(MessageNano messageNano) {
        return MessageNano.toByteArray(messageNano);
    }

    public int getProtoSize(MessageNano messageNano) {
        return messageNano.getCachedSize();
    }

    public void start() {
        this.mProtoTracer.start();
    }

    public void stop() {
        this.mProtoTracer.stop();
    }

    public void add(ProtoTraceable<SystemUiTraceProto> protoTraceable) {
        this.mProtoTracer.add(protoTraceable);
    }

    public void update() {
        this.mProtoTracer.update();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("ProtoTracer:");
        String str = "    ";
        printWriter.print(str);
        StringBuilder sb = new StringBuilder();
        sb.append("enabled: ");
        sb.append(this.mProtoTracer.isEnabled());
        printWriter.println(sb.toString());
        printWriter.print(str);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("usagePct: ");
        sb2.append(this.mProtoTracer.getBufferUsagePct());
        printWriter.println(sb2.toString());
        printWriter.print(str);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("file: ");
        sb3.append(getTraceFile());
        printWriter.println(sb3.toString());
    }
}
