package com.android.systemui.util.leak;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.LongSparseArray;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2012R$integer;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dumpable;
import com.android.systemui.SystemUI;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.plugins.p006qs.QSTile.State;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GarbageMonitor implements Dumpable {
    private static final boolean DEBUG = Log.isLoggable("GarbageMonitor", 3);
    private static final boolean ENABLE_AM_HEAP_LIMIT;
    /* access modifiers changed from: private */
    public static final boolean HEAP_TRACKING_ENABLED;
    /* access modifiers changed from: private */
    public static final boolean LEAK_REPORTING_ENABLED;
    private final Context mContext;
    private final LongSparseArray<ProcessMemInfo> mData = new LongSparseArray<>();
    private DumpTruck mDumpTruck;
    private final Handler mHandler;
    /* access modifiers changed from: private */
    public long mHeapLimit;
    private final LeakReporter mLeakReporter;
    private final ArrayList<Long> mPids = new ArrayList<>();
    private MemoryTile mQSTile;
    private final TrackedGarbage mTrackedGarbage;

    private class BackgroundHeapCheckHandler extends Handler {
        BackgroundHeapCheckHandler(Looper looper) {
            super(looper);
            if (Looper.getMainLooper().equals(looper)) {
                throw new RuntimeException("BackgroundHeapCheckHandler may not run on the ui thread");
            }
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1000) {
                if (GarbageMonitor.this.gcAndCheckGarbage()) {
                    postDelayed(new Runnable() {
                        public final void run() {
                            GarbageMonitor.this.reinspectGarbageAfterGc();
                        }
                    }, 100);
                }
                removeMessages(1000);
                sendEmptyMessageDelayed(1000, 900000);
            } else if (i == 3000) {
                GarbageMonitor.this.update();
                removeMessages(3000);
                sendEmptyMessageDelayed(3000, 60000);
            }
        }
    }

    private static class MemoryGraphIcon extends Icon {
        long limit;
        long rss;

        private MemoryGraphIcon() {
        }

        public void setRss(long j) {
            this.rss = j;
        }

        public void setHeapLimit(long j) {
            this.limit = j;
        }

        public Drawable getDrawable(Context context) {
            MemoryIconDrawable memoryIconDrawable = new MemoryIconDrawable(context);
            memoryIconDrawable.setRss(this.rss);
            memoryIconDrawable.setLimit(this.limit);
            return memoryIconDrawable;
        }
    }

    private static class MemoryIconDrawable extends Drawable {
        final Drawable baseIcon;

        /* renamed from: dp */
        final float f84dp;
        long limit;
        final Paint paint = new Paint();
        long rss;

        public int getOpacity() {
            return -3;
        }

        MemoryIconDrawable(Context context) {
            this.baseIcon = context.getDrawable(C2010R$drawable.ic_memory).mutate();
            this.f84dp = context.getResources().getDisplayMetrics().density;
            this.paint.setColor(QSTileImpl.getColorForState(context, 2));
        }

        public void setRss(long j) {
            if (j != this.rss) {
                this.rss = j;
                invalidateSelf();
            }
        }

        public void setLimit(long j) {
            if (j != this.limit) {
                this.limit = j;
                invalidateSelf();
            }
        }

        public void draw(Canvas canvas) {
            this.baseIcon.draw(canvas);
            long j = this.limit;
            if (j > 0) {
                long j2 = this.rss;
                if (j2 > 0) {
                    float min = Math.min(1.0f, ((float) j2) / ((float) j));
                    Rect bounds = getBounds();
                    float f = (float) bounds.left;
                    float f2 = this.f84dp;
                    canvas.translate(f + (f2 * 8.0f), ((float) bounds.top) + (f2 * 5.0f));
                    float f3 = this.f84dp;
                    canvas.drawRect(0.0f, f3 * 14.0f * (1.0f - min), (8.0f * f3) + 1.0f, (f3 * 14.0f) + 1.0f, this.paint);
                }
            }
        }

        public void setBounds(int i, int i2, int i3, int i4) {
            super.setBounds(i, i2, i3, i4);
            this.baseIcon.setBounds(i, i2, i3, i4);
        }

        public int getIntrinsicHeight() {
            return this.baseIcon.getIntrinsicHeight();
        }

        public int getIntrinsicWidth() {
            return this.baseIcon.getIntrinsicWidth();
        }

        public void setAlpha(int i) {
            this.baseIcon.setAlpha(i);
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.baseIcon.setColorFilter(colorFilter);
            this.paint.setColorFilter(colorFilter);
        }

        public void setTint(int i) {
            super.setTint(i);
            this.baseIcon.setTint(i);
        }

        public void setTintList(ColorStateList colorStateList) {
            super.setTintList(colorStateList);
            this.baseIcon.setTintList(colorStateList);
        }

        public void setTintMode(Mode mode) {
            super.setTintMode(mode);
            this.baseIcon.setTintMode(mode);
        }
    }

    public static class MemoryTile extends QSTileImpl<State> {
        /* access modifiers changed from: private */
        public boolean dumpInProgress;
        /* access modifiers changed from: private */

        /* renamed from: gm */
        public final GarbageMonitor f85gm;
        /* access modifiers changed from: private */
        public final ActivityStarter mActivityStarter;
        private ProcessMemInfo pmi;

        public int getMetricsCategory() {
            return 0;
        }

        public MemoryTile(QSHost qSHost, GarbageMonitor garbageMonitor, ActivityStarter activityStarter) {
            super(qSHost);
            this.f85gm = garbageMonitor;
            this.mActivityStarter = activityStarter;
        }

        public State newTileState() {
            return new State();
        }

        public Intent getLongClickIntent() {
            return new Intent();
        }

        /* access modifiers changed from: protected */
        public void handleClick() {
            if (!this.dumpInProgress) {
                this.dumpInProgress = true;
                refreshState();
                new Thread("HeapDumpThread") {
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException unused) {
                        }
                        MemoryTile.this.mHandler.post(new Runnable(this, MemoryTile.this.f85gm.dumpHprofAndGetShareIntent()) {
                            public final /* synthetic */ C17421 f$0;
                            public final /* synthetic */ Intent f$1;

                            public final 
/*
Method generation error in method: com.android.systemui.util.leak.-$$Lambda$GarbageMonitor$MemoryTile$1$cmBeuqKr1b9hrY1trlao7X6pfIc.run():null, dex: classes.dex
                            java.lang.NullPointerException
                            	at jadx.core.codegen.ClassGen.useType(ClassGen.java:442)
                            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:109)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:311)
                            	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:262)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:225)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:661)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:595)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:353)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:223)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:105)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:773)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:713)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:357)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:239)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:213)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:210)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:203)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:316)
                            	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:262)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:225)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:661)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:595)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:353)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:223)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:105)
                            	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:88)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:682)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:357)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:239)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:213)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:138)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:210)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:203)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:316)
                            	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:262)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:225)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
                            	at jadx.core.codegen.ClassGen.addInnerClasses(ClassGen.java:237)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:224)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:76)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:32)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:20)
                            	at jadx.core.ProcessClass.process(ProcessClass.java:36)
                            	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
                            	at jadx.api.JavaClass.decompile(JavaClass.java:62)
                            	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
                            
*/
                        });
                    }

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$run$0 */
                    public /* synthetic */ void lambda$run$0$GarbageMonitor$MemoryTile$1(Intent intent) {
                        MemoryTile.this.dumpInProgress = false;
                        MemoryTile.this.refreshState();
                        MemoryTile.this.getHost().collapsePanels();
                        MemoryTile.this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
                    }
                }.start();
            }
        }

        public void handleSetListening(boolean z) {
            super.handleSetListening(z);
            GarbageMonitor garbageMonitor = this.f85gm;
            if (garbageMonitor != null) {
                garbageMonitor.setTile(z ? this : null);
            }
            ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService(ActivityManager.class);
            if (!z || this.f85gm.mHeapLimit <= 0) {
                activityManager.clearWatchHeapLimit();
            } else {
                activityManager.setWatchHeapLimit(this.f85gm.mHeapLimit * 1024);
            }
        }

        public CharSequence getTileLabel() {
            return getState().label;
        }

        /* access modifiers changed from: protected */
        public void handleUpdateState(State state, Object obj) {
            String str;
            this.pmi = this.f85gm.getMemInfo(Process.myPid());
            MemoryGraphIcon memoryGraphIcon = new MemoryGraphIcon();
            memoryGraphIcon.setHeapLimit(this.f85gm.mHeapLimit);
            state.state = this.dumpInProgress ? 0 : 2;
            if (this.dumpInProgress) {
                str = "Dumping...";
            } else {
                str = this.mContext.getString(C2017R$string.heap_dump_tile_name);
            }
            state.label = str;
            ProcessMemInfo processMemInfo = this.pmi;
            if (processMemInfo != null) {
                memoryGraphIcon.setRss(processMemInfo.currentRss);
                state.secondaryLabel = String.format("rss: %s / %s", new Object[]{GarbageMonitor.formatBytes(this.pmi.currentRss * 1024), GarbageMonitor.formatBytes(this.f85gm.mHeapLimit * 1024)});
            } else {
                memoryGraphIcon.setRss(0);
                state.secondaryLabel = null;
            }
            state.icon = memoryGraphIcon;
        }

        public void update() {
            refreshState();
        }
    }

    public static class ProcessMemInfo implements Dumpable {
        public long currentRss;
        public int head = 0;
        public long max = 1;
        public String name;
        public long pid;
        public long[] rss = new long[720];
        public long startTime;

        public ProcessMemInfo(long j, String str, long j2) {
            this.pid = j;
            this.name = str;
            this.startTime = j2;
        }

        public long getUptime() {
            return System.currentTimeMillis() - this.startTime;
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            printWriter.print("{ \"pid\": ");
            printWriter.print(this.pid);
            printWriter.print(", \"name\": \"");
            printWriter.print(this.name.replace('\"', '-'));
            printWriter.print("\", \"start\": ");
            printWriter.print(this.startTime);
            printWriter.print(", \"rss\": [");
            for (int i = 0; i < this.rss.length; i++) {
                if (i > 0) {
                    printWriter.print(",");
                }
                long[] jArr = this.rss;
                printWriter.print(jArr[(this.head + i) % jArr.length]);
            }
            printWriter.println("] }");
        }
    }

    public static class Service extends SystemUI implements Dumpable {
        private final GarbageMonitor mGarbageMonitor;

        public Service(Context context, GarbageMonitor garbageMonitor) {
            super(context);
            this.mGarbageMonitor = garbageMonitor;
        }

        public void start() {
            boolean z = false;
            if (Secure.getInt(this.mContext.getContentResolver(), "sysui_force_enable_leak_reporting", 0) != 0) {
                z = true;
            }
            if (GarbageMonitor.LEAK_REPORTING_ENABLED || z) {
                this.mGarbageMonitor.startLeakMonitor();
            }
            if (GarbageMonitor.HEAP_TRACKING_ENABLED || z) {
                this.mGarbageMonitor.startHeapTracking();
            }
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            GarbageMonitor garbageMonitor = this.mGarbageMonitor;
            if (garbageMonitor != null) {
                garbageMonitor.dump(fileDescriptor, printWriter, strArr);
            }
        }
    }

    static {
        boolean z = false;
        if (Build.IS_DEBUGGABLE && SystemProperties.getBoolean("debug.enable_leak_reporting", false)) {
            z = true;
        }
        LEAK_REPORTING_ENABLED = z;
        boolean z2 = Build.IS_DEBUGGABLE;
        HEAP_TRACKING_ENABLED = z2;
        ENABLE_AM_HEAP_LIMIT = z2;
    }

    public GarbageMonitor(Context context, Looper looper, LeakDetector leakDetector, LeakReporter leakReporter) {
        this.mContext = context.getApplicationContext();
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        this.mHandler = new BackgroundHeapCheckHandler(looper);
        this.mTrackedGarbage = leakDetector.getTrackedGarbage();
        this.mLeakReporter = leakReporter;
        this.mDumpTruck = new DumpTruck(this.mContext);
        if (ENABLE_AM_HEAP_LIMIT) {
            this.mHeapLimit = (long) Global.getInt(context.getContentResolver(), "systemui_am_heap_limit", this.mContext.getResources().getInteger(C2012R$integer.watch_heap_limit));
        }
    }

    public void startLeakMonitor() {
        if (this.mTrackedGarbage != null) {
            this.mHandler.sendEmptyMessage(1000);
        }
    }

    public void startHeapTracking() {
        startTrackingProcess((long) Process.myPid(), this.mContext.getPackageName(), System.currentTimeMillis());
        this.mHandler.sendEmptyMessage(3000);
    }

    /* access modifiers changed from: private */
    public boolean gcAndCheckGarbage() {
        if (this.mTrackedGarbage.countOldGarbage() <= 5) {
            return false;
        }
        Runtime.getRuntime().gc();
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void reinspectGarbageAfterGc() {
        int countOldGarbage = this.mTrackedGarbage.countOldGarbage();
        if (countOldGarbage > 5) {
            this.mLeakReporter.dumpLeak(countOldGarbage);
        }
    }

    public ProcessMemInfo getMemInfo(int i) {
        return (ProcessMemInfo) this.mData.get((long) i);
    }

    public List<Long> getTrackedProcesses() {
        return this.mPids;
    }

    public void startTrackingProcess(long j, String str, long j2) {
        synchronized (this.mPids) {
            if (!this.mPids.contains(Long.valueOf(j))) {
                this.mPids.add(Long.valueOf(j));
                logPids();
                LongSparseArray<ProcessMemInfo> longSparseArray = this.mData;
                ProcessMemInfo processMemInfo = new ProcessMemInfo(j, str, j2);
                longSparseArray.put(j, processMemInfo);
            }
        }
    }

    private void logPids() {
        if (DEBUG) {
            StringBuffer stringBuffer = new StringBuffer("Now tracking processes: ");
            for (int i = 0; i < this.mPids.size(); i++) {
                ((Long) this.mPids.get(i)).intValue();
                stringBuffer.append(" ");
            }
            Log.v("GarbageMonitor", stringBuffer.toString());
        }
    }

    /* access modifiers changed from: private */
    public void update() {
        synchronized (this.mPids) {
            int i = 0;
            while (true) {
                if (i >= this.mPids.size()) {
                    break;
                }
                int intValue = ((Long) this.mPids.get(i)).intValue();
                long[] rss = Process.getRss(intValue);
                if (rss != null || rss.length != 0) {
                    long j = rss[0];
                    long j2 = (long) intValue;
                    ProcessMemInfo processMemInfo = (ProcessMemInfo) this.mData.get(j2);
                    long[] jArr = processMemInfo.rss;
                    int i2 = processMemInfo.head;
                    processMemInfo.currentRss = j;
                    jArr[i2] = j;
                    processMemInfo.head = (processMemInfo.head + 1) % processMemInfo.rss.length;
                    if (j > processMemInfo.max) {
                        processMemInfo.max = j;
                    }
                    if (processMemInfo.currentRss == 0) {
                        if (DEBUG) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("update: pid ");
                            sb.append(intValue);
                            sb.append(" has rss=0, it probably died");
                            Log.v("GarbageMonitor", sb.toString());
                        }
                        this.mData.remove(j2);
                    }
                    i++;
                } else if (DEBUG) {
                    Log.e("GarbageMonitor", "update: Process.getRss() didn't provide any values.");
                }
            }
            for (int size = this.mPids.size() - 1; size >= 0; size--) {
                if (this.mData.get((long) ((Long) this.mPids.get(size)).intValue()) == null) {
                    this.mPids.remove(size);
                    logPids();
                }
            }
        }
        MemoryTile memoryTile = this.mQSTile;
        if (memoryTile != null) {
            memoryTile.update();
        }
    }

    /* access modifiers changed from: private */
    public void setTile(MemoryTile memoryTile) {
        this.mQSTile = memoryTile;
        if (memoryTile != null) {
            memoryTile.update();
        }
    }

    /* access modifiers changed from: private */
    public static String formatBytes(long j) {
        String[] strArr = {"B", "K", "M", "G", "T"};
        int i = 0;
        while (i < 5 && j >= 1024) {
            j /= 1024;
            i++;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(j);
        sb.append(strArr[i]);
        return sb.toString();
    }

    /* access modifiers changed from: private */
    public Intent dumpHprofAndGetShareIntent() {
        DumpTruck dumpTruck = this.mDumpTruck;
        dumpTruck.captureHeaps(getTrackedProcesses());
        return dumpTruck.createShareIntent();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("GarbageMonitor params:");
        printWriter.println(String.format("   mHeapLimit=%d KB", new Object[]{Long.valueOf(this.mHeapLimit)}));
        printWriter.println(String.format("   GARBAGE_INSPECTION_INTERVAL=%d (%.1f mins)", new Object[]{Long.valueOf(900000), Float.valueOf(15.0f)}));
        printWriter.println(String.format("   HEAP_TRACK_INTERVAL=%d (%.1f mins)", new Object[]{Long.valueOf(60000), Float.valueOf(1.0f)}));
        printWriter.println(String.format("   HEAP_TRACK_HISTORY_LEN=%d (%.1f hr total)", new Object[]{Integer.valueOf(720), Float.valueOf(12.0f)}));
        printWriter.println("GarbageMonitor tracked processes:");
        Iterator it = this.mPids.iterator();
        while (it.hasNext()) {
            ProcessMemInfo processMemInfo = (ProcessMemInfo) this.mData.get(((Long) it.next()).longValue());
            if (processMemInfo != null) {
                processMemInfo.dump(fileDescriptor, printWriter, strArr);
            }
        }
    }
}
