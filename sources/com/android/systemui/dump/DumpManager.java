package com.android.systemui.dump;

import android.content.Context;
import android.os.SystemClock;
import android.os.Trace;
import android.util.ArrayMap;
import com.android.systemui.C2005R$array;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dumpable;
import com.android.systemui.log.LogBuffer;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DumpManager.kt */
public final class DumpManager {
    private final Map<String, RegisteredDumpable<LogBuffer>> buffers = new ArrayMap();
    private final Context context;
    private final Map<String, RegisteredDumpable<Dumpable>> dumpables = new ArrayMap();

    public DumpManager(Context context2) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        this.context = context2;
    }

    public final synchronized void registerDumpable(String str, Dumpable dumpable) {
        Intrinsics.checkParameterIsNotNull(str, "name");
        Intrinsics.checkParameterIsNotNull(dumpable, "module");
        if (ArraysKt___ArraysKt.contains(DumpManagerKt.RESERVED_NAMES, str)) {
            StringBuilder sb = new StringBuilder();
            sb.append('\'');
            sb.append(str);
            sb.append("' is reserved");
            throw new IllegalArgumentException(sb.toString());
        } else if (canAssignToNameLocked(str, dumpable)) {
            this.dumpables.put(str, new RegisteredDumpable(str, dumpable));
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append('\'');
            sb2.append(str);
            sb2.append("' is already registered");
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    public final synchronized void unregisterDumpable(String str) {
        Intrinsics.checkParameterIsNotNull(str, "name");
        this.dumpables.remove(str);
    }

    public final synchronized void registerBuffer(String str, LogBuffer logBuffer) {
        Intrinsics.checkParameterIsNotNull(str, "name");
        Intrinsics.checkParameterIsNotNull(logBuffer, "buffer");
        if (canAssignToNameLocked(str, logBuffer)) {
            this.buffers.put(str, new RegisteredDumpable(str, logBuffer));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('\'');
            sb.append(str);
            sb.append("' is already registered");
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public final synchronized void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(strArr, "args");
        Trace.beginSection("DumpManager#dump()");
        long uptimeMillis = SystemClock.uptimeMillis();
        try {
            ParsedArgs parseArgs = parseArgs(strArr);
            String dumpPriority = parseArgs.getDumpPriority();
            if (dumpPriority != null) {
                int hashCode = dumpPriority.hashCode();
                if (hashCode != -1986416409) {
                    if (hashCode == -1560189025) {
                        if (dumpPriority.equals("CRITICAL")) {
                            dumpCriticalLocked(fileDescriptor, printWriter, parseArgs);
                            printWriter.println();
                            StringBuilder sb = new StringBuilder();
                            sb.append("Dump took ");
                            sb.append(SystemClock.uptimeMillis() - uptimeMillis);
                            sb.append("ms");
                            printWriter.println(sb.toString());
                            Trace.endSection();
                        }
                    }
                } else if (dumpPriority.equals("NORMAL")) {
                    dumpNormalLocked(printWriter, parseArgs);
                    printWriter.println();
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Dump took ");
                    sb2.append(SystemClock.uptimeMillis() - uptimeMillis);
                    sb2.append("ms");
                    printWriter.println(sb2.toString());
                    Trace.endSection();
                }
            }
            dumpParameterizedLocked(fileDescriptor, printWriter, parseArgs);
            printWriter.println();
            StringBuilder sb22 = new StringBuilder();
            sb22.append("Dump took ");
            sb22.append(SystemClock.uptimeMillis() - uptimeMillis);
            sb22.append("ms");
            printWriter.println(sb22.toString());
            Trace.endSection();
        } catch (ArgParseException e) {
            printWriter.println(e.getMessage());
        }
    }

    private final void dumpCriticalLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, ParsedArgs parsedArgs) {
        dumpDumpablesLocked(fileDescriptor, printWriter, parsedArgs);
        dumpConfig(printWriter);
    }

    private final void dumpNormalLocked(PrintWriter printWriter, ParsedArgs parsedArgs) {
        dumpBuffersLocked(printWriter, parsedArgs);
    }

    private final void dumpParameterizedLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, ParsedArgs parsedArgs) {
        String command = parsedArgs.getCommand();
        if (command != null) {
            switch (command.hashCode()) {
                case -1353714459:
                    if (command.equals("dumpables")) {
                        dumpDumpablesLocked(fileDescriptor, printWriter, parsedArgs);
                        return;
                    }
                    break;
                case -1045369428:
                    if (command.equals("bugreport-normal")) {
                        dumpNormalLocked(printWriter, parsedArgs);
                        return;
                    }
                    break;
                case 227996723:
                    if (command.equals("buffers")) {
                        dumpBuffersLocked(printWriter, parsedArgs);
                        return;
                    }
                    break;
                case 842828580:
                    if (command.equals("bugreport-critical")) {
                        dumpCriticalLocked(fileDescriptor, printWriter, parsedArgs);
                        return;
                    }
                    break;
            }
        }
        dumpTargetsLocked(parsedArgs.getNonFlagArgs(), fileDescriptor, printWriter, parsedArgs);
    }

    private final void dumpTargetsLocked(List<String> list, FileDescriptor fileDescriptor, PrintWriter printWriter, ParsedArgs parsedArgs) {
        if (list.isEmpty()) {
            printWriter.println("Nothing to dump :(");
            return;
        }
        for (String dumpTarget : list) {
            dumpTarget(dumpTarget, fileDescriptor, printWriter, parsedArgs);
        }
    }

    private final void dumpTarget(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, ParsedArgs parsedArgs) {
        if (Intrinsics.areEqual((Object) str, (Object) "config")) {
            dumpConfig(printWriter);
            return;
        }
        for (RegisteredDumpable registeredDumpable : this.dumpables.values()) {
            if (StringsKt__StringsJVMKt.endsWith$default(registeredDumpable.getName(), str, false, 2, null)) {
                dumpDumpable(registeredDumpable, fileDescriptor, printWriter, parsedArgs);
                return;
            }
        }
        Iterator it = this.buffers.values().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            RegisteredDumpable registeredDumpable2 = (RegisteredDumpable) it.next();
            if (StringsKt__StringsJVMKt.endsWith$default(registeredDumpable2.getName(), str, false, 2, null)) {
                dumpBuffer(registeredDumpable2, printWriter, parsedArgs);
                break;
            }
        }
    }

    private final void dumpDumpablesLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, ParsedArgs parsedArgs) {
        for (RegisteredDumpable dumpDumpable : this.dumpables.values()) {
            dumpDumpable(dumpDumpable, fileDescriptor, printWriter, parsedArgs);
        }
    }

    private final void dumpBuffersLocked(PrintWriter printWriter, ParsedArgs parsedArgs) {
        for (RegisteredDumpable dumpBuffer : this.buffers.values()) {
            dumpBuffer(dumpBuffer, printWriter, parsedArgs);
        }
    }

    private final void dumpDumpable(RegisteredDumpable<Dumpable> registeredDumpable, FileDescriptor fileDescriptor, PrintWriter printWriter, ParsedArgs parsedArgs) {
        printWriter.println();
        StringBuilder sb = new StringBuilder();
        sb.append(registeredDumpable.getName());
        sb.append(':');
        printWriter.println(sb.toString());
        printWriter.println("----------------------------------------------------------------------------");
        ((Dumpable) registeredDumpable.getDumpable()).dump(fileDescriptor, printWriter, parsedArgs.getRawArgs());
    }

    private final void dumpBuffer(RegisteredDumpable<LogBuffer> registeredDumpable, PrintWriter printWriter, ParsedArgs parsedArgs) {
        printWriter.println();
        printWriter.println();
        StringBuilder sb = new StringBuilder();
        sb.append("BUFFER ");
        sb.append(registeredDumpable.getName());
        sb.append(':');
        printWriter.println(sb.toString());
        printWriter.println("============================================================================");
        ((LogBuffer) registeredDumpable.getDumpable()).dump(printWriter, parsedArgs.getTailLength());
    }

    private final void dumpConfig(PrintWriter printWriter) {
        printWriter.println("SystemUiServiceComponents configuration:");
        printWriter.print("vendor component: ");
        printWriter.println(this.context.getResources().getString(C2017R$string.config_systemUIVendorServiceComponent));
        dumpServiceList(printWriter, "global", C2005R$array.config_systemUIServiceComponents);
        dumpServiceList(printWriter, "per-user", C2005R$array.config_systemUIServiceComponentsPerUser);
    }

    private final void dumpServiceList(PrintWriter printWriter, String str, int i) {
        String[] stringArray = this.context.getResources().getStringArray(i);
        printWriter.print(str);
        String str2 = ": ";
        printWriter.print(str2);
        if (stringArray == null) {
            printWriter.println("N/A");
            return;
        }
        printWriter.print(stringArray.length);
        printWriter.println(" services");
        int length = stringArray.length;
        for (int i2 = 0; i2 < length; i2++) {
            printWriter.print("  ");
            printWriter.print(i2);
            printWriter.print(str2);
            printWriter.println(stringArray[i2]);
        }
    }

    private final ParsedArgs parseArgs(String[] strArr) {
        List mutableList = ArraysKt___ArraysKt.toMutableList(strArr);
        ParsedArgs parsedArgs = new ParsedArgs(strArr, mutableList);
        Iterator it = mutableList.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (StringsKt__StringsJVMKt.startsWith$default(str, "-", false, 2, null)) {
                it.remove();
                int hashCode = str.hashCode();
                String str2 = "--tail";
                if (hashCode != 1511) {
                    if (hashCode == 1056887741) {
                        String str3 = "--dump-priority";
                        if (str.equals(str3)) {
                            parsedArgs.setDumpPriority((String) readArgument(it, str3, DumpManager$parseArgs$1.INSTANCE));
                        }
                    } else if (hashCode == 1333422576) {
                        if (!str.equals(str2)) {
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unknown flag: ");
                    sb.append(str);
                    throw new ArgParseException(sb.toString());
                } else if (!str.equals("-t")) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Unknown flag: ");
                    sb2.append(str);
                    throw new ArgParseException(sb2.toString());
                }
                parsedArgs.setTailLength(((Number) readArgument(it, str2, DumpManager$parseArgs$2.INSTANCE)).intValue());
            }
        }
        if ((!mutableList.isEmpty()) && ArraysKt___ArraysKt.contains(DumpManagerKt.COMMANDS, mutableList.get(0))) {
            parsedArgs.setCommand((String) mutableList.remove(0));
        }
        return parsedArgs;
    }

    private final <T> T readArgument(Iterator<String> it, String str, Function1<? super String, ? extends T> function1) {
        if (it.hasNext()) {
            String str2 = (String) it.next();
            try {
                T invoke = function1.invoke(str2);
                it.remove();
                return invoke;
            } catch (Exception unused) {
                StringBuilder sb = new StringBuilder();
                sb.append("Invalid argument '");
                sb.append(str2);
                sb.append("' for flag ");
                sb.append(str);
                throw new ArgParseException(sb.toString());
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Missing argument for ");
            sb2.append(str);
            throw new ArgParseException(sb2.toString());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0010, code lost:
        if (r0 != null) goto L_0x0026;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean canAssignToNameLocked(java.lang.String r2, java.lang.Object r3) {
        /*
            r1 = this;
            java.util.Map<java.lang.String, com.android.systemui.dump.RegisteredDumpable<com.android.systemui.Dumpable>> r0 = r1.dumpables
            java.lang.Object r0 = r0.get(r2)
            com.android.systemui.dump.RegisteredDumpable r0 = (com.android.systemui.dump.RegisteredDumpable) r0
            if (r0 == 0) goto L_0x0013
            java.lang.Object r0 = r0.getDumpable()
            com.android.systemui.Dumpable r0 = (com.android.systemui.Dumpable) r0
            if (r0 == 0) goto L_0x0013
            goto L_0x0026
        L_0x0013:
            java.util.Map<java.lang.String, com.android.systemui.dump.RegisteredDumpable<com.android.systemui.log.LogBuffer>> r1 = r1.buffers
            java.lang.Object r1 = r1.get(r2)
            com.android.systemui.dump.RegisteredDumpable r1 = (com.android.systemui.dump.RegisteredDumpable) r1
            if (r1 == 0) goto L_0x0025
            java.lang.Object r1 = r1.getDumpable()
            r0 = r1
            com.android.systemui.log.LogBuffer r0 = (com.android.systemui.log.LogBuffer) r0
            goto L_0x0026
        L_0x0025:
            r0 = 0
        L_0x0026:
            if (r0 == 0) goto L_0x0031
            boolean r1 = kotlin.jvm.internal.Intrinsics.areEqual(r3, r0)
            if (r1 == 0) goto L_0x002f
            goto L_0x0031
        L_0x002f:
            r1 = 0
            goto L_0x0032
        L_0x0031:
            r1 = 1
        L_0x0032:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.dump.DumpManager.canAssignToNameLocked(java.lang.String, java.lang.Object):boolean");
    }
}
