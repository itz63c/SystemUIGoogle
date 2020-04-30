package com.android.settingslib.applications;

import android.app.Application;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageStatsObserver.Stub;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseArray;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.internal.util.ArrayUtils;
import com.android.settingslib.Utils;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class ApplicationsState {
    public static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>() {
        private final Collator sCollator = Collator.getInstance();

        public int compare(AppEntry appEntry, AppEntry appEntry2) {
            int compare = this.sCollator.compare(appEntry.label, appEntry2.label);
            if (compare != 0) {
                return compare;
            }
            ApplicationInfo applicationInfo = appEntry.info;
            if (applicationInfo != null) {
                ApplicationInfo applicationInfo2 = appEntry2.info;
                if (applicationInfo2 != null) {
                    int compare2 = this.sCollator.compare(applicationInfo.packageName, applicationInfo2.packageName);
                    if (compare2 != 0) {
                        return compare2;
                    }
                }
            }
            return appEntry.info.uid - appEntry2.info.uid;
        }
    };
    public static final AppFilter FILTER_AUDIO = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry appEntry) {
            boolean z;
            synchronized (appEntry) {
                z = true;
                if (appEntry.info.category != 1) {
                    z = false;
                }
            }
            return z;
        }
    };
    public static final AppFilter FILTER_DOWNLOADED_AND_LAUNCHER = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry appEntry) {
            if (AppUtils.isInstant(appEntry.info)) {
                return false;
            }
            if (ApplicationsState.hasFlag(appEntry.info.flags, 128) || !ApplicationsState.hasFlag(appEntry.info.flags, 1) || appEntry.hasLauncherEntry) {
                return true;
            }
            if (!ApplicationsState.hasFlag(appEntry.info.flags, 1) || !appEntry.isHomeApp) {
                return false;
            }
            return true;
        }
    };
    public static final AppFilter FILTER_GAMES = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry appEntry) {
            boolean z;
            synchronized (appEntry.info) {
                if (!ApplicationsState.hasFlag(appEntry.info.flags, 33554432)) {
                    if (appEntry.info.category != 0) {
                        z = false;
                    }
                }
                z = true;
            }
            return z;
        }
    };
    public static final AppFilter FILTER_MOVIES = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry appEntry) {
            boolean z;
            synchronized (appEntry) {
                z = appEntry.info.category == 2;
            }
            return z;
        }
    };
    public static final AppFilter FILTER_PHOTOS = new AppFilter() {
        public void init() {
        }

        public boolean filterApp(AppEntry appEntry) {
            boolean z;
            synchronized (appEntry) {
                z = appEntry.info.category == 3;
            }
            return z;
        }
    };
    static ApplicationsState sInstance;
    private static final Object sLock = new Object();
    final ArrayList<WeakReference<Session>> mActiveSessions = new ArrayList<>();
    final int mAdminRetrieveFlags;
    final ArrayList<AppEntry> mAppEntries = new ArrayList<>();
    List<ApplicationInfo> mApplications = new ArrayList();
    final BackgroundHandler mBackgroundHandler;
    final Context mContext;
    String mCurComputingSizePkg;
    int mCurComputingSizeUserId;
    UUID mCurComputingSizeUuid;
    long mCurId = 1;
    final SparseArray<HashMap<String, AppEntry>> mEntriesMap = new SparseArray<>();
    boolean mHaveInstantApps;
    private InterestingConfigChanges mInterestingConfigChanges = new InterestingConfigChanges();
    final IPackageManager mIpm;
    final MainHandler mMainHandler = new MainHandler(Looper.getMainLooper());
    PackageIntentReceiver mPackageIntentReceiver;
    final PackageManager mPm;
    final ArrayList<Session> mRebuildingSessions = new ArrayList<>();
    boolean mResumed;
    final int mRetrieveFlags;
    final ArrayList<Session> mSessions = new ArrayList<>();
    boolean mSessionsChanged;
    final StorageStatsManager mStats;
    final HashMap<String, Boolean> mSystemModules = new HashMap<>();
    final HandlerThread mThread;
    final UserManager mUm;

    public static class AppEntry extends SizeInfo {
        public final File apkFile;
        public long externalSize;
        public String externalSizeStr;
        public boolean hasLauncherEntry;
        public Drawable icon;
        public ApplicationInfo info;
        public long internalSize;
        public String internalSizeStr;
        public boolean isHomeApp;
        public String label;
        public boolean launcherEntryEnabled;
        public boolean mounted;
        public long size = -1;
        public long sizeLoadStart;
        public boolean sizeStale = true;
        public String sizeStr;

        public AppEntry(Context context, ApplicationInfo applicationInfo, long j) {
            this.apkFile = new File(applicationInfo.sourceDir);
            this.info = applicationInfo;
            ensureLabel(context);
        }

        public void ensureLabel(Context context) {
            if (this.label != null && this.mounted) {
                return;
            }
            if (!this.apkFile.exists()) {
                this.mounted = false;
                this.label = this.info.packageName;
                return;
            }
            this.mounted = true;
            CharSequence loadLabel = this.info.loadLabel(context.getPackageManager());
            this.label = loadLabel != null ? loadLabel.toString() : this.info.packageName;
        }

        /* access modifiers changed from: 0000 */
        public boolean ensureIconLocked(Context context) {
            if (this.icon == null) {
                if (this.apkFile.exists()) {
                    this.icon = Utils.getBadgedIcon(context, this.info);
                    return true;
                }
                this.mounted = false;
                this.icon = context.getDrawable(17303642);
            } else if (!this.mounted && this.apkFile.exists()) {
                this.mounted = true;
                this.icon = Utils.getBadgedIcon(context, this.info);
                return true;
            }
            return false;
        }
    }

    public interface AppFilter {
        boolean filterApp(AppEntry appEntry);

        void init();

        void init(Context context) {
            init();
        }
    }

    private class BackgroundHandler extends Handler {
        boolean mRunning;
        final Stub mStatsObserver = new Stub() {
            /* JADX WARNING: Code restructure failed: missing block: B:46:0x0109, code lost:
                return;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onGetStatsCompleted(android.content.pm.PackageStats r13, boolean r14) {
                /*
                    r12 = this;
                    if (r14 != 0) goto L_0x0003
                    return
                L_0x0003:
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r14 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this
                    com.android.settingslib.applications.ApplicationsState r14 = com.android.settingslib.applications.ApplicationsState.this
                    android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r14 = r14.mEntriesMap
                    monitor-enter(r14)
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x010a }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x010a }
                    android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r0 = r0.mEntriesMap     // Catch:{ all -> 0x010a }
                    int r1 = r13.userHandle     // Catch:{ all -> 0x010a }
                    java.lang.Object r0 = r0.get(r1)     // Catch:{ all -> 0x010a }
                    java.util.HashMap r0 = (java.util.HashMap) r0     // Catch:{ all -> 0x010a }
                    if (r0 != 0) goto L_0x001c
                    monitor-exit(r14)     // Catch:{ all -> 0x010a }
                    return
                L_0x001c:
                    java.lang.String r1 = r13.packageName     // Catch:{ all -> 0x010a }
                    java.lang.Object r0 = r0.get(r1)     // Catch:{ all -> 0x010a }
                    com.android.settingslib.applications.ApplicationsState$AppEntry r0 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r0     // Catch:{ all -> 0x010a }
                    if (r0 == 0) goto L_0x00db
                    monitor-enter(r0)     // Catch:{ all -> 0x010a }
                    r1 = 0
                    r0.sizeStale = r1     // Catch:{ all -> 0x00d8 }
                    r2 = 0
                    r0.sizeLoadStart = r2     // Catch:{ all -> 0x00d8 }
                    long r2 = r13.externalCodeSize     // Catch:{ all -> 0x00d8 }
                    long r4 = r13.externalObbSize     // Catch:{ all -> 0x00d8 }
                    long r2 = r2 + r4
                    long r4 = r13.externalDataSize     // Catch:{ all -> 0x00d8 }
                    long r6 = r13.externalMediaSize     // Catch:{ all -> 0x00d8 }
                    long r4 = r4 + r6
                    long r6 = r2 + r4
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r8 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x00d8 }
                    com.android.settingslib.applications.ApplicationsState r8 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00d8 }
                    long r8 = r8.getTotalInternalSize(r13)     // Catch:{ all -> 0x00d8 }
                    long r6 = r6 + r8
                    long r8 = r0.size     // Catch:{ all -> 0x00d8 }
                    int r8 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                    if (r8 != 0) goto L_0x0075
                    long r8 = r0.cacheSize     // Catch:{ all -> 0x00d8 }
                    long r10 = r13.cacheSize     // Catch:{ all -> 0x00d8 }
                    int r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                    if (r8 != 0) goto L_0x0075
                    long r8 = r0.codeSize     // Catch:{ all -> 0x00d8 }
                    long r10 = r13.codeSize     // Catch:{ all -> 0x00d8 }
                    int r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                    if (r8 != 0) goto L_0x0075
                    long r8 = r0.dataSize     // Catch:{ all -> 0x00d8 }
                    long r10 = r13.dataSize     // Catch:{ all -> 0x00d8 }
                    int r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                    if (r8 != 0) goto L_0x0075
                    long r8 = r0.externalCodeSize     // Catch:{ all -> 0x00d8 }
                    int r8 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
                    if (r8 != 0) goto L_0x0075
                    long r8 = r0.externalDataSize     // Catch:{ all -> 0x00d8 }
                    int r8 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
                    if (r8 != 0) goto L_0x0075
                    long r8 = r0.externalCacheSize     // Catch:{ all -> 0x00d8 }
                    long r10 = r13.externalCacheSize     // Catch:{ all -> 0x00d8 }
                    int r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                    if (r8 == 0) goto L_0x00be
                L_0x0075:
                    r0.size = r6     // Catch:{ all -> 0x00d8 }
                    long r8 = r13.cacheSize     // Catch:{ all -> 0x00d8 }
                    r0.cacheSize = r8     // Catch:{ all -> 0x00d8 }
                    long r8 = r13.codeSize     // Catch:{ all -> 0x00d8 }
                    r0.codeSize = r8     // Catch:{ all -> 0x00d8 }
                    long r8 = r13.dataSize     // Catch:{ all -> 0x00d8 }
                    r0.dataSize = r8     // Catch:{ all -> 0x00d8 }
                    r0.externalCodeSize = r2     // Catch:{ all -> 0x00d8 }
                    r0.externalDataSize = r4     // Catch:{ all -> 0x00d8 }
                    long r1 = r13.externalCacheSize     // Catch:{ all -> 0x00d8 }
                    r0.externalCacheSize = r1     // Catch:{ all -> 0x00d8 }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r1 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x00d8 }
                    com.android.settingslib.applications.ApplicationsState r1 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00d8 }
                    java.lang.String r1 = r1.getSizeStr(r6)     // Catch:{ all -> 0x00d8 }
                    r0.sizeStr = r1     // Catch:{ all -> 0x00d8 }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r1 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x00d8 }
                    com.android.settingslib.applications.ApplicationsState r1 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00d8 }
                    long r1 = r1.getTotalInternalSize(r13)     // Catch:{ all -> 0x00d8 }
                    r0.internalSize = r1     // Catch:{ all -> 0x00d8 }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r3 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x00d8 }
                    com.android.settingslib.applications.ApplicationsState r3 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00d8 }
                    java.lang.String r1 = r3.getSizeStr(r1)     // Catch:{ all -> 0x00d8 }
                    r0.internalSizeStr = r1     // Catch:{ all -> 0x00d8 }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r1 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x00d8 }
                    com.android.settingslib.applications.ApplicationsState r1 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00d8 }
                    long r1 = r1.getTotalExternalSize(r13)     // Catch:{ all -> 0x00d8 }
                    r0.externalSize = r1     // Catch:{ all -> 0x00d8 }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r3 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x00d8 }
                    com.android.settingslib.applications.ApplicationsState r3 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x00d8 }
                    java.lang.String r1 = r3.getSizeStr(r1)     // Catch:{ all -> 0x00d8 }
                    r0.externalSizeStr = r1     // Catch:{ all -> 0x00d8 }
                    r1 = 1
                L_0x00be:
                    monitor-exit(r0)     // Catch:{ all -> 0x00d8 }
                    if (r1 == 0) goto L_0x00db
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x010a }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x010a }
                    com.android.settingslib.applications.ApplicationsState$MainHandler r0 = r0.mMainHandler     // Catch:{ all -> 0x010a }
                    r1 = 4
                    java.lang.String r2 = r13.packageName     // Catch:{ all -> 0x010a }
                    android.os.Message r0 = r0.obtainMessage(r1, r2)     // Catch:{ all -> 0x010a }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r1 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x010a }
                    com.android.settingslib.applications.ApplicationsState r1 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x010a }
                    com.android.settingslib.applications.ApplicationsState$MainHandler r1 = r1.mMainHandler     // Catch:{ all -> 0x010a }
                    r1.sendMessage(r0)     // Catch:{ all -> 0x010a }
                    goto L_0x00db
                L_0x00d8:
                    r12 = move-exception
                    monitor-exit(r0)     // Catch:{ all -> 0x00d8 }
                    throw r12     // Catch:{ all -> 0x010a }
                L_0x00db:
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x010a }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x010a }
                    java.lang.String r0 = r0.mCurComputingSizePkg     // Catch:{ all -> 0x010a }
                    if (r0 == 0) goto L_0x0108
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x010a }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x010a }
                    java.lang.String r0 = r0.mCurComputingSizePkg     // Catch:{ all -> 0x010a }
                    java.lang.String r1 = r13.packageName     // Catch:{ all -> 0x010a }
                    boolean r0 = r0.equals(r1)     // Catch:{ all -> 0x010a }
                    if (r0 == 0) goto L_0x0108
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r0 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x010a }
                    com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x010a }
                    int r0 = r0.mCurComputingSizeUserId     // Catch:{ all -> 0x010a }
                    int r13 = r13.userHandle     // Catch:{ all -> 0x010a }
                    if (r0 != r13) goto L_0x0108
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r13 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x010a }
                    com.android.settingslib.applications.ApplicationsState r13 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x010a }
                    r0 = 0
                    r13.mCurComputingSizePkg = r0     // Catch:{ all -> 0x010a }
                    com.android.settingslib.applications.ApplicationsState$BackgroundHandler r12 = com.android.settingslib.applications.ApplicationsState.BackgroundHandler.this     // Catch:{ all -> 0x010a }
                    r13 = 7
                    r12.sendEmptyMessage(r13)     // Catch:{ all -> 0x010a }
                L_0x0108:
                    monitor-exit(r14)     // Catch:{ all -> 0x010a }
                    return
                L_0x010a:
                    r12 = move-exception
                    monitor-exit(r14)     // Catch:{ all -> 0x010a }
                    throw r12
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.applications.ApplicationsState.BackgroundHandler.C06121.onGetStatsCompleted(android.content.pm.PackageStats, boolean):void");
            }
        };

        BackgroundHandler(Looper looper) {
            super(looper);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:42:0x00f0, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r19) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$Session> r2 = r2.mRebuildingSessions
                monitor-enter(r2)
                com.android.settingslib.applications.ApplicationsState r3 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0389 }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$Session> r3 = r3.mRebuildingSessions     // Catch:{ all -> 0x0389 }
                int r3 = r3.size()     // Catch:{ all -> 0x0389 }
                r4 = 0
                if (r3 <= 0) goto L_0x0025
                java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ all -> 0x0389 }
                com.android.settingslib.applications.ApplicationsState r5 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0389 }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$Session> r5 = r5.mRebuildingSessions     // Catch:{ all -> 0x0389 }
                r3.<init>(r5)     // Catch:{ all -> 0x0389 }
                com.android.settingslib.applications.ApplicationsState r5 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0389 }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$Session> r5 = r5.mRebuildingSessions     // Catch:{ all -> 0x0389 }
                r5.clear()     // Catch:{ all -> 0x0389 }
                goto L_0x0026
            L_0x0025:
                r3 = r4
            L_0x0026:
                monitor-exit(r2)     // Catch:{ all -> 0x0389 }
                if (r3 == 0) goto L_0x003d
                java.util.Iterator r2 = r3.iterator()
            L_0x002d:
                boolean r3 = r2.hasNext()
                if (r3 == 0) goto L_0x003d
                java.lang.Object r3 = r2.next()
                com.android.settingslib.applications.ApplicationsState$Session r3 = (com.android.settingslib.applications.ApplicationsState.Session) r3
                r3.handleRebuildList()
                goto L_0x002d
            L_0x003d:
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$Session> r2 = r2.mSessions
                int r2 = r0.getCombinedSessionFlags(r2)
                int r3 = r1.what
                r5 = 8388608(0x800000, float:1.17549435E-38)
                r6 = 3
                r7 = 7
                r8 = 8
                r9 = 5
                r10 = 2
                r11 = 4
                r12 = 6
                r13 = 0
                r14 = 1
                switch(r3) {
                    case 2: goto L_0x02c4;
                    case 3: goto L_0x026a;
                    case 4: goto L_0x019b;
                    case 5: goto L_0x019b;
                    case 6: goto L_0x0121;
                    case 7: goto L_0x0058;
                    default: goto L_0x0056;
                }
            L_0x0056:
                goto L_0x0388
            L_0x0058:
                boolean r1 = com.android.settingslib.applications.ApplicationsState.hasFlag(r2, r11)
                if (r1 == 0) goto L_0x0388
                com.android.settingslib.applications.ApplicationsState r1 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r1 = r1.mEntriesMap
                monitor-enter(r1)
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                java.lang.String r2 = r2.mCurComputingSizePkg     // Catch:{ all -> 0x011e }
                if (r2 == 0) goto L_0x006b
                monitor-exit(r1)     // Catch:{ all -> 0x011e }
                return
            L_0x006b:
                long r2 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x011e }
                r4 = r13
            L_0x0070:
                com.android.settingslib.applications.ApplicationsState r6 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$AppEntry> r6 = r6.mAppEntries     // Catch:{ all -> 0x011e }
                int r6 = r6.size()     // Catch:{ all -> 0x011e }
                if (r4 >= r6) goto L_0x00f5
                com.android.settingslib.applications.ApplicationsState r6 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$AppEntry> r6 = r6.mAppEntries     // Catch:{ all -> 0x011e }
                java.lang.Object r6 = r6.get(r4)     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState$AppEntry r6 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r6     // Catch:{ all -> 0x011e }
                android.content.pm.ApplicationInfo r7 = r6.info     // Catch:{ all -> 0x011e }
                int r7 = r7.flags     // Catch:{ all -> 0x011e }
                boolean r7 = com.android.settingslib.applications.ApplicationsState.hasFlag(r7, r5)     // Catch:{ all -> 0x011e }
                if (r7 == 0) goto L_0x00f1
                long r7 = r6.size     // Catch:{ all -> 0x011e }
                r10 = -1
                int r7 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
                if (r7 == 0) goto L_0x009a
                boolean r7 = r6.sizeStale     // Catch:{ all -> 0x011e }
                if (r7 == 0) goto L_0x00f1
            L_0x009a:
                long r4 = r6.sizeLoadStart     // Catch:{ all -> 0x011e }
                r7 = 0
                int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                if (r4 == 0) goto L_0x00ac
                long r4 = r6.sizeLoadStart     // Catch:{ all -> 0x011e }
                r7 = 20000(0x4e20, double:9.8813E-320)
                long r7 = r2 - r7
                int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                if (r4 >= 0) goto L_0x00ef
            L_0x00ac:
                boolean r4 = r0.mRunning     // Catch:{ all -> 0x011e }
                if (r4 != 0) goto L_0x00c5
                r0.mRunning = r14     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState r4 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState$MainHandler r4 = r4.mMainHandler     // Catch:{ all -> 0x011e }
                java.lang.Integer r5 = java.lang.Integer.valueOf(r14)     // Catch:{ all -> 0x011e }
                android.os.Message r4 = r4.obtainMessage(r12, r5)     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState r5 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState$MainHandler r5 = r5.mMainHandler     // Catch:{ all -> 0x011e }
                r5.sendMessage(r4)     // Catch:{ all -> 0x011e }
            L_0x00c5:
                r6.sizeLoadStart = r2     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                android.content.pm.ApplicationInfo r3 = r6.info     // Catch:{ all -> 0x011e }
                java.util.UUID r3 = r3.storageUuid     // Catch:{ all -> 0x011e }
                r2.mCurComputingSizeUuid = r3     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                android.content.pm.ApplicationInfo r3 = r6.info     // Catch:{ all -> 0x011e }
                java.lang.String r3 = r3.packageName     // Catch:{ all -> 0x011e }
                r2.mCurComputingSizePkg = r3     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                android.content.pm.ApplicationInfo r3 = r6.info     // Catch:{ all -> 0x011e }
                int r3 = r3.uid     // Catch:{ all -> 0x011e }
                int r3 = android.os.UserHandle.getUserId(r3)     // Catch:{ all -> 0x011e }
                r2.mCurComputingSizeUserId = r3     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState$BackgroundHandler r2 = r2.mBackgroundHandler     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.-$$Lambda$ApplicationsState$BackgroundHandler$7jhXQzAcRoT6ACDzmPBTQMi7Ldc r3 = new com.android.settingslib.applications.-$$Lambda$ApplicationsState$BackgroundHandler$7jhXQzAcRoT6ACDzmPBTQMi7Ldc     // Catch:{ all -> 0x011e }
                r3.<init>()     // Catch:{ all -> 0x011e }
                r2.post(r3)     // Catch:{ all -> 0x011e }
            L_0x00ef:
                monitor-exit(r1)     // Catch:{ all -> 0x011e }
                return
            L_0x00f1:
                int r4 = r4 + 1
                goto L_0x0070
            L_0x00f5:
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState$MainHandler r2 = r2.mMainHandler     // Catch:{ all -> 0x011e }
                boolean r2 = r2.hasMessages(r9)     // Catch:{ all -> 0x011e }
                if (r2 != 0) goto L_0x011b
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState$MainHandler r2 = r2.mMainHandler     // Catch:{ all -> 0x011e }
                r2.sendEmptyMessage(r9)     // Catch:{ all -> 0x011e }
                r0.mRunning = r13     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState$MainHandler r2 = r2.mMainHandler     // Catch:{ all -> 0x011e }
                java.lang.Integer r3 = java.lang.Integer.valueOf(r13)     // Catch:{ all -> 0x011e }
                android.os.Message r2 = r2.obtainMessage(r12, r3)     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState r0 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x011e }
                com.android.settingslib.applications.ApplicationsState$MainHandler r0 = r0.mMainHandler     // Catch:{ all -> 0x011e }
                r0.sendMessage(r2)     // Catch:{ all -> 0x011e }
            L_0x011b:
                monitor-exit(r1)     // Catch:{ all -> 0x011e }
                goto L_0x0388
            L_0x011e:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x011e }
                throw r0
            L_0x0121:
                boolean r1 = com.android.settingslib.applications.ApplicationsState.hasFlag(r2, r10)
                if (r1 == 0) goto L_0x0196
                com.android.settingslib.applications.ApplicationsState r1 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r1 = r1.mEntriesMap
                monitor-enter(r1)
                r2 = r13
            L_0x012d:
                com.android.settingslib.applications.ApplicationsState r3 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0193 }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$AppEntry> r3 = r3.mAppEntries     // Catch:{ all -> 0x0193 }
                int r3 = r3.size()     // Catch:{ all -> 0x0193 }
                if (r13 >= r3) goto L_0x0178
                if (r2 >= r10) goto L_0x0178
                com.android.settingslib.applications.ApplicationsState r3 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0193 }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$AppEntry> r3 = r3.mAppEntries     // Catch:{ all -> 0x0193 }
                java.lang.Object r3 = r3.get(r13)     // Catch:{ all -> 0x0193 }
                com.android.settingslib.applications.ApplicationsState$AppEntry r3 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r3     // Catch:{ all -> 0x0193 }
                android.graphics.drawable.Drawable r4 = r3.icon     // Catch:{ all -> 0x0193 }
                if (r4 == 0) goto L_0x014b
                boolean r4 = r3.mounted     // Catch:{ all -> 0x0193 }
                if (r4 != 0) goto L_0x0172
            L_0x014b:
                monitor-enter(r3)     // Catch:{ all -> 0x0193 }
                com.android.settingslib.applications.ApplicationsState r4 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0175 }
                android.content.Context r4 = r4.mContext     // Catch:{ all -> 0x0175 }
                boolean r4 = r3.ensureIconLocked(r4)     // Catch:{ all -> 0x0175 }
                if (r4 == 0) goto L_0x0171
                boolean r4 = r0.mRunning     // Catch:{ all -> 0x0175 }
                if (r4 != 0) goto L_0x016f
                r0.mRunning = r14     // Catch:{ all -> 0x0175 }
                com.android.settingslib.applications.ApplicationsState r4 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0175 }
                com.android.settingslib.applications.ApplicationsState$MainHandler r4 = r4.mMainHandler     // Catch:{ all -> 0x0175 }
                java.lang.Integer r5 = java.lang.Integer.valueOf(r14)     // Catch:{ all -> 0x0175 }
                android.os.Message r4 = r4.obtainMessage(r12, r5)     // Catch:{ all -> 0x0175 }
                com.android.settingslib.applications.ApplicationsState r5 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0175 }
                com.android.settingslib.applications.ApplicationsState$MainHandler r5 = r5.mMainHandler     // Catch:{ all -> 0x0175 }
                r5.sendMessage(r4)     // Catch:{ all -> 0x0175 }
            L_0x016f:
                int r2 = r2 + 1
            L_0x0171:
                monitor-exit(r3)     // Catch:{ all -> 0x0175 }
            L_0x0172:
                int r13 = r13 + 1
                goto L_0x012d
            L_0x0175:
                r0 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x0175 }
                throw r0     // Catch:{ all -> 0x0193 }
            L_0x0178:
                monitor-exit(r1)     // Catch:{ all -> 0x0193 }
                if (r2 <= 0) goto L_0x018c
                com.android.settingslib.applications.ApplicationsState r1 = com.android.settingslib.applications.ApplicationsState.this
                com.android.settingslib.applications.ApplicationsState$MainHandler r1 = r1.mMainHandler
                boolean r1 = r1.hasMessages(r6)
                if (r1 != 0) goto L_0x018c
                com.android.settingslib.applications.ApplicationsState r1 = com.android.settingslib.applications.ApplicationsState.this
                com.android.settingslib.applications.ApplicationsState$MainHandler r1 = r1.mMainHandler
                r1.sendEmptyMessage(r6)
            L_0x018c:
                if (r2 < r10) goto L_0x0196
                r0.sendEmptyMessage(r12)
                goto L_0x0388
            L_0x0193:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0193 }
                throw r0
            L_0x0196:
                r0.sendEmptyMessage(r7)
                goto L_0x0388
            L_0x019b:
                if (r3 != r11) goto L_0x01a3
                boolean r3 = com.android.settingslib.applications.ApplicationsState.hasFlag(r2, r8)
                if (r3 != 0) goto L_0x01af
            L_0x01a3:
                int r3 = r1.what
                if (r3 != r9) goto L_0x025a
                r3 = 16
                boolean r2 = com.android.settingslib.applications.ApplicationsState.hasFlag(r2, r3)
                if (r2 == 0) goto L_0x025a
            L_0x01af:
                android.content.Intent r2 = new android.content.Intent
                java.lang.String r3 = "android.intent.action.MAIN"
                r2.<init>(r3, r4)
                int r3 = r1.what
                if (r3 != r11) goto L_0x01bd
                java.lang.String r3 = "android.intent.category.LAUNCHER"
                goto L_0x01bf
            L_0x01bd:
                java.lang.String r3 = "android.intent.category.LEANBACK_LAUNCHER"
            L_0x01bf:
                r2.addCategory(r3)
                r3 = r13
            L_0x01c3:
                com.android.settingslib.applications.ApplicationsState r4 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r4 = r4.mEntriesMap
                int r4 = r4.size()
                if (r3 >= r4) goto L_0x0249
                com.android.settingslib.applications.ApplicationsState r4 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r4 = r4.mEntriesMap
                int r4 = r4.keyAt(r3)
                com.android.settingslib.applications.ApplicationsState r5 = com.android.settingslib.applications.ApplicationsState.this
                android.content.pm.PackageManager r5 = r5.mPm
                r6 = 786944(0xc0200, float:1.102743E-39)
                java.util.List r5 = r5.queryIntentActivitiesAsUser(r2, r6, r4)
                com.android.settingslib.applications.ApplicationsState r6 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r6 = r6.mEntriesMap
                monitor-enter(r6)
                com.android.settingslib.applications.ApplicationsState r8 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0246 }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r8 = r8.mEntriesMap     // Catch:{ all -> 0x0246 }
                java.lang.Object r8 = r8.valueAt(r3)     // Catch:{ all -> 0x0246 }
                java.util.HashMap r8 = (java.util.HashMap) r8     // Catch:{ all -> 0x0246 }
                int r10 = r5.size()     // Catch:{ all -> 0x0246 }
                r15 = r13
            L_0x01f4:
                if (r15 >= r10) goto L_0x023d
                java.lang.Object r16 = r5.get(r15)     // Catch:{ all -> 0x0246 }
                r13 = r16
                android.content.pm.ResolveInfo r13 = (android.content.pm.ResolveInfo) r13     // Catch:{ all -> 0x0246 }
                android.content.pm.ActivityInfo r12 = r13.activityInfo     // Catch:{ all -> 0x0246 }
                java.lang.String r12 = r12.packageName     // Catch:{ all -> 0x0246 }
                java.lang.Object r17 = r8.get(r12)     // Catch:{ all -> 0x0246 }
                r9 = r17
                com.android.settingslib.applications.ApplicationsState$AppEntry r9 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r9     // Catch:{ all -> 0x0246 }
                if (r9 == 0) goto L_0x0218
                r9.hasLauncherEntry = r14     // Catch:{ all -> 0x0246 }
                boolean r12 = r9.launcherEntryEnabled     // Catch:{ all -> 0x0246 }
                android.content.pm.ActivityInfo r13 = r13.activityInfo     // Catch:{ all -> 0x0246 }
                boolean r13 = r13.enabled     // Catch:{ all -> 0x0246 }
                r12 = r12 | r13
                r9.launcherEntryEnabled = r12     // Catch:{ all -> 0x0246 }
                goto L_0x0236
            L_0x0218:
                java.lang.String r9 = "ApplicationsState"
                java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x0246 }
                r13.<init>()     // Catch:{ all -> 0x0246 }
                java.lang.String r14 = "Cannot find pkg: "
                r13.append(r14)     // Catch:{ all -> 0x0246 }
                r13.append(r12)     // Catch:{ all -> 0x0246 }
                java.lang.String r12 = " on user "
                r13.append(r12)     // Catch:{ all -> 0x0246 }
                r13.append(r4)     // Catch:{ all -> 0x0246 }
                java.lang.String r12 = r13.toString()     // Catch:{ all -> 0x0246 }
                android.util.Log.w(r9, r12)     // Catch:{ all -> 0x0246 }
            L_0x0236:
                int r15 = r15 + 1
                r9 = 5
                r12 = 6
                r13 = 0
                r14 = 1
                goto L_0x01f4
            L_0x023d:
                monitor-exit(r6)     // Catch:{ all -> 0x0246 }
                int r3 = r3 + 1
                r9 = 5
                r12 = 6
                r13 = 0
                r14 = 1
                goto L_0x01c3
            L_0x0246:
                r0 = move-exception
                monitor-exit(r6)     // Catch:{ all -> 0x0246 }
                throw r0
            L_0x0249:
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this
                com.android.settingslib.applications.ApplicationsState$MainHandler r2 = r2.mMainHandler
                boolean r2 = r2.hasMessages(r7)
                if (r2 != 0) goto L_0x025a
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this
                com.android.settingslib.applications.ApplicationsState$MainHandler r2 = r2.mMainHandler
                r2.sendEmptyMessage(r7)
            L_0x025a:
                int r1 = r1.what
                if (r1 != r11) goto L_0x0264
                r1 = 5
                r0.sendEmptyMessage(r1)
                goto L_0x0388
            L_0x0264:
                r1 = 6
                r0.sendEmptyMessage(r1)
                goto L_0x0388
            L_0x026a:
                r1 = r14
                boolean r2 = com.android.settingslib.applications.ApplicationsState.hasFlag(r2, r1)
                if (r2 == 0) goto L_0x02bf
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this
                android.content.pm.PackageManager r2 = r2.mPm
                r2.getHomeActivities(r1)
                com.android.settingslib.applications.ApplicationsState r2 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r2 = r2.mEntriesMap
                monitor-enter(r2)
                com.android.settingslib.applications.ApplicationsState r3 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x02bc }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r3 = r3.mEntriesMap     // Catch:{ all -> 0x02bc }
                int r3 = r3.size()     // Catch:{ all -> 0x02bc }
                r13 = 0
            L_0x028b:
                if (r13 >= r3) goto L_0x02ba
                com.android.settingslib.applications.ApplicationsState r4 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x02bc }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r4 = r4.mEntriesMap     // Catch:{ all -> 0x02bc }
                java.lang.Object r4 = r4.valueAt(r13)     // Catch:{ all -> 0x02bc }
                java.util.HashMap r4 = (java.util.HashMap) r4     // Catch:{ all -> 0x02bc }
                java.util.Iterator r5 = r1.iterator()     // Catch:{ all -> 0x02bc }
            L_0x029b:
                boolean r6 = r5.hasNext()     // Catch:{ all -> 0x02bc }
                if (r6 == 0) goto L_0x02b7
                java.lang.Object r6 = r5.next()     // Catch:{ all -> 0x02bc }
                android.content.pm.ResolveInfo r6 = (android.content.pm.ResolveInfo) r6     // Catch:{ all -> 0x02bc }
                android.content.pm.ActivityInfo r6 = r6.activityInfo     // Catch:{ all -> 0x02bc }
                java.lang.String r6 = r6.packageName     // Catch:{ all -> 0x02bc }
                java.lang.Object r6 = r4.get(r6)     // Catch:{ all -> 0x02bc }
                com.android.settingslib.applications.ApplicationsState$AppEntry r6 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r6     // Catch:{ all -> 0x02bc }
                if (r6 == 0) goto L_0x029b
                r7 = 1
                r6.isHomeApp = r7     // Catch:{ all -> 0x02bc }
                goto L_0x029b
            L_0x02b7:
                int r13 = r13 + 1
                goto L_0x028b
            L_0x02ba:
                monitor-exit(r2)     // Catch:{ all -> 0x02bc }
                goto L_0x02bf
            L_0x02bc:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x02bc }
                throw r0
            L_0x02bf:
                r0.sendEmptyMessage(r11)
                goto L_0x0388
            L_0x02c4:
                com.android.settingslib.applications.ApplicationsState r1 = com.android.settingslib.applications.ApplicationsState.this
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r1 = r1.mEntriesMap
                monitor-enter(r1)
                r2 = 0
                r3 = 0
            L_0x02cb:
                com.android.settingslib.applications.ApplicationsState r4 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0385 }
                java.util.List<android.content.pm.ApplicationInfo> r4 = r4.mApplications     // Catch:{ all -> 0x0385 }
                int r4 = r4.size()     // Catch:{ all -> 0x0385 }
                if (r3 >= r4) goto L_0x0368
                r4 = 6
                if (r2 >= r4) goto L_0x0368
                boolean r4 = r0.mRunning     // Catch:{ all -> 0x0385 }
                if (r4 != 0) goto L_0x02f4
                r4 = 1
                r0.mRunning = r4     // Catch:{ all -> 0x0385 }
                com.android.settingslib.applications.ApplicationsState r7 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0385 }
                com.android.settingslib.applications.ApplicationsState$MainHandler r7 = r7.mMainHandler     // Catch:{ all -> 0x0385 }
                java.lang.Integer r9 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0385 }
                r11 = 6
                android.os.Message r7 = r7.obtainMessage(r11, r9)     // Catch:{ all -> 0x0385 }
                com.android.settingslib.applications.ApplicationsState r9 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0385 }
                com.android.settingslib.applications.ApplicationsState$MainHandler r9 = r9.mMainHandler     // Catch:{ all -> 0x0385 }
                r9.sendMessage(r7)     // Catch:{ all -> 0x0385 }
                goto L_0x02f5
            L_0x02f4:
                r4 = 1
            L_0x02f5:
                com.android.settingslib.applications.ApplicationsState r7 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0385 }
                java.util.List<android.content.pm.ApplicationInfo> r7 = r7.mApplications     // Catch:{ all -> 0x0385 }
                java.lang.Object r7 = r7.get(r3)     // Catch:{ all -> 0x0385 }
                android.content.pm.ApplicationInfo r7 = (android.content.pm.ApplicationInfo) r7     // Catch:{ all -> 0x0385 }
                int r9 = r7.uid     // Catch:{ all -> 0x0385 }
                int r9 = android.os.UserHandle.getUserId(r9)     // Catch:{ all -> 0x0385 }
                com.android.settingslib.applications.ApplicationsState r11 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0385 }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r11 = r11.mEntriesMap     // Catch:{ all -> 0x0385 }
                java.lang.Object r11 = r11.get(r9)     // Catch:{ all -> 0x0385 }
                java.util.HashMap r11 = (java.util.HashMap) r11     // Catch:{ all -> 0x0385 }
                java.lang.String r12 = r7.packageName     // Catch:{ all -> 0x0385 }
                java.lang.Object r11 = r11.get(r12)     // Catch:{ all -> 0x0385 }
                if (r11 != 0) goto L_0x031e
                int r2 = r2 + 1
                com.android.settingslib.applications.ApplicationsState r11 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0385 }
                r11.getEntryLocked(r7)     // Catch:{ all -> 0x0385 }
            L_0x031e:
                if (r9 == 0) goto L_0x0363
                com.android.settingslib.applications.ApplicationsState r9 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0385 }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r9 = r9.mEntriesMap     // Catch:{ all -> 0x0385 }
                r11 = 0
                int r9 = r9.indexOfKey(r11)     // Catch:{ all -> 0x0385 }
                if (r9 < 0) goto L_0x0361
                com.android.settingslib.applications.ApplicationsState r9 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0385 }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r9 = r9.mEntriesMap     // Catch:{ all -> 0x0385 }
                java.lang.Object r9 = r9.get(r11)     // Catch:{ all -> 0x0385 }
                java.util.HashMap r9 = (java.util.HashMap) r9     // Catch:{ all -> 0x0385 }
                java.lang.String r11 = r7.packageName     // Catch:{ all -> 0x0385 }
                java.lang.Object r9 = r9.get(r11)     // Catch:{ all -> 0x0385 }
                com.android.settingslib.applications.ApplicationsState$AppEntry r9 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r9     // Catch:{ all -> 0x0385 }
                if (r9 == 0) goto L_0x0363
                android.content.pm.ApplicationInfo r11 = r9.info     // Catch:{ all -> 0x0385 }
                int r11 = r11.flags     // Catch:{ all -> 0x0385 }
                boolean r11 = com.android.settingslib.applications.ApplicationsState.hasFlag(r11, r5)     // Catch:{ all -> 0x0385 }
                if (r11 != 0) goto L_0x0363
                com.android.settingslib.applications.ApplicationsState r11 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0385 }
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r11 = r11.mEntriesMap     // Catch:{ all -> 0x0385 }
                r12 = 0
                java.lang.Object r11 = r11.get(r12)     // Catch:{ all -> 0x0385 }
                java.util.HashMap r11 = (java.util.HashMap) r11     // Catch:{ all -> 0x0385 }
                java.lang.String r7 = r7.packageName     // Catch:{ all -> 0x0385 }
                r11.remove(r7)     // Catch:{ all -> 0x0385 }
                com.android.settingslib.applications.ApplicationsState r7 = com.android.settingslib.applications.ApplicationsState.this     // Catch:{ all -> 0x0385 }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$AppEntry> r7 = r7.mAppEntries     // Catch:{ all -> 0x0385 }
                r7.remove(r9)     // Catch:{ all -> 0x0385 }
                goto L_0x0364
            L_0x0361:
                r12 = r11
                goto L_0x0364
            L_0x0363:
                r12 = 0
            L_0x0364:
                int r3 = r3 + 1
                goto L_0x02cb
            L_0x0368:
                monitor-exit(r1)     // Catch:{ all -> 0x0385 }
                r1 = 6
                if (r2 < r1) goto L_0x0370
                r0.sendEmptyMessage(r10)
                goto L_0x0388
            L_0x0370:
                com.android.settingslib.applications.ApplicationsState r1 = com.android.settingslib.applications.ApplicationsState.this
                com.android.settingslib.applications.ApplicationsState$MainHandler r1 = r1.mMainHandler
                boolean r1 = r1.hasMessages(r8)
                if (r1 != 0) goto L_0x0381
                com.android.settingslib.applications.ApplicationsState r1 = com.android.settingslib.applications.ApplicationsState.this
                com.android.settingslib.applications.ApplicationsState$MainHandler r1 = r1.mMainHandler
                r1.sendEmptyMessage(r8)
            L_0x0381:
                r0.sendEmptyMessage(r6)
                goto L_0x0388
            L_0x0385:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0385 }
                throw r0
            L_0x0388:
                return
            L_0x0389:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0389 }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.applications.ApplicationsState.BackgroundHandler.handleMessage(android.os.Message):void");
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$handleMessage$0 */
        public /* synthetic */ void lambda$handleMessage$0$ApplicationsState$BackgroundHandler() {
            try {
                StorageStats queryStatsForPackage = ApplicationsState.this.mStats.queryStatsForPackage(ApplicationsState.this.mCurComputingSizeUuid, ApplicationsState.this.mCurComputingSizePkg, UserHandle.of(ApplicationsState.this.mCurComputingSizeUserId));
                PackageStats packageStats = new PackageStats(ApplicationsState.this.mCurComputingSizePkg, ApplicationsState.this.mCurComputingSizeUserId);
                packageStats.codeSize = queryStatsForPackage.getCodeBytes();
                packageStats.dataSize = queryStatsForPackage.getDataBytes();
                packageStats.cacheSize = queryStatsForPackage.getCacheBytes();
                this.mStatsObserver.onGetStatsCompleted(packageStats, true);
            } catch (NameNotFoundException | IOException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to query stats: ");
                sb.append(e);
                Log.w("ApplicationsState", sb.toString());
                try {
                    this.mStatsObserver.onGetStatsCompleted(null, false);
                } catch (RemoteException unused) {
                }
            }
        }

        private int getCombinedSessionFlags(List<Session> list) {
            int i;
            synchronized (ApplicationsState.this.mEntriesMap) {
                i = 0;
                for (Session access$300 : list) {
                    i |= access$300.mFlags;
                }
            }
            return i;
        }
    }

    public interface Callbacks {
        void onAllSizesComputed();

        void onLauncherInfoChanged();

        void onLoadEntriesCompleted();

        void onPackageIconChanged();

        void onPackageListChanged();

        void onPackageSizeChanged(String str);

        void onRebuildComplete(ArrayList<AppEntry> arrayList);

        void onRunningStateChanged(boolean z);
    }

    class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            ApplicationsState.this.rebuildActiveSessions();
            switch (message.what) {
                case 1:
                    Session session = (Session) message.obj;
                    Iterator it = ApplicationsState.this.mActiveSessions.iterator();
                    while (it.hasNext()) {
                        Session session2 = (Session) ((WeakReference) it.next()).get();
                        if (session2 != null && session2 == session) {
                            session.mCallbacks.onRebuildComplete(session.mLastAppList);
                        }
                    }
                    return;
                case 2:
                    Iterator it2 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it2.hasNext()) {
                        Session session3 = (Session) ((WeakReference) it2.next()).get();
                        if (session3 != null) {
                            session3.mCallbacks.onPackageListChanged();
                        }
                    }
                    return;
                case 3:
                    Iterator it3 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it3.hasNext()) {
                        Session session4 = (Session) ((WeakReference) it3.next()).get();
                        if (session4 != null) {
                            session4.mCallbacks.onPackageIconChanged();
                        }
                    }
                    return;
                case 4:
                    Iterator it4 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it4.hasNext()) {
                        Session session5 = (Session) ((WeakReference) it4.next()).get();
                        if (session5 != null) {
                            session5.mCallbacks.onPackageSizeChanged((String) message.obj);
                        }
                    }
                    return;
                case 5:
                    Iterator it5 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it5.hasNext()) {
                        Session session6 = (Session) ((WeakReference) it5.next()).get();
                        if (session6 != null) {
                            session6.mCallbacks.onAllSizesComputed();
                        }
                    }
                    return;
                case 6:
                    Iterator it6 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it6.hasNext()) {
                        Session session7 = (Session) ((WeakReference) it6.next()).get();
                        if (session7 != null) {
                            session7.mCallbacks.onRunningStateChanged(message.arg1 != 0);
                        }
                    }
                    return;
                case 7:
                    Iterator it7 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it7.hasNext()) {
                        Session session8 = (Session) ((WeakReference) it7.next()).get();
                        if (session8 != null) {
                            session8.mCallbacks.onLauncherInfoChanged();
                        }
                    }
                    return;
                case 8:
                    Iterator it8 = ApplicationsState.this.mActiveSessions.iterator();
                    while (it8.hasNext()) {
                        Session session9 = (Session) ((WeakReference) it8.next()).get();
                        if (session9 != null) {
                            session9.mCallbacks.onLoadEntriesCompleted();
                        }
                    }
                    return;
                default:
                    return;
            }
        }
    }

    private class PackageIntentReceiver extends BroadcastReceiver {
        private PackageIntentReceiver() {
        }

        /* access modifiers changed from: 0000 */
        public void registerReceiver() {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
            intentFilter.addDataScheme("package");
            ApplicationsState.this.mContext.registerReceiver(this, intentFilter);
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
            intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
            ApplicationsState.this.mContext.registerReceiver(this, intentFilter2);
            IntentFilter intentFilter3 = new IntentFilter();
            intentFilter3.addAction("android.intent.action.USER_ADDED");
            intentFilter3.addAction("android.intent.action.USER_REMOVED");
            ApplicationsState.this.mContext.registerReceiver(this, intentFilter3);
        }

        /* access modifiers changed from: 0000 */
        public void unregisterReceiver() {
            ApplicationsState.this.mContext.unregisterReceiver(this);
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int i = 0;
            if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                String encodedSchemeSpecificPart = intent.getData().getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    ApplicationsState applicationsState = ApplicationsState.this;
                    applicationsState.addPackage(encodedSchemeSpecificPart, applicationsState.mEntriesMap.keyAt(i));
                    i++;
                }
            } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                String encodedSchemeSpecificPart2 = intent.getData().getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    ApplicationsState applicationsState2 = ApplicationsState.this;
                    applicationsState2.removePackage(encodedSchemeSpecificPart2, applicationsState2.mEntriesMap.keyAt(i));
                    i++;
                }
            } else if ("android.intent.action.PACKAGE_CHANGED".equals(action)) {
                String encodedSchemeSpecificPart3 = intent.getData().getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    ApplicationsState applicationsState3 = ApplicationsState.this;
                    applicationsState3.invalidatePackage(encodedSchemeSpecificPart3, applicationsState3.mEntriesMap.keyAt(i));
                    i++;
                }
            } else {
                String str = "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE";
                if (str.equals(action) || "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(action)) {
                    String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                    if (stringArrayExtra != null && stringArrayExtra.length != 0 && str.equals(action)) {
                        for (String str2 : stringArrayExtra) {
                            for (int i2 = 0; i2 < ApplicationsState.this.mEntriesMap.size(); i2++) {
                                ApplicationsState applicationsState4 = ApplicationsState.this;
                                applicationsState4.invalidatePackage(str2, applicationsState4.mEntriesMap.keyAt(i2));
                            }
                        }
                        return;
                    }
                    return;
                }
                String str3 = "android.intent.extra.user_handle";
                if ("android.intent.action.USER_ADDED".equals(action)) {
                    ApplicationsState.this.addUser(intent.getIntExtra(str3, -10000));
                } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                    ApplicationsState.this.removeUser(intent.getIntExtra(str3, -10000));
                }
            }
        }
    }

    public class Session implements LifecycleObserver {
        final Callbacks mCallbacks;
        /* access modifiers changed from: private */
        public int mFlags;
        private final boolean mHasLifecycle;
        ArrayList<AppEntry> mLastAppList;
        boolean mRebuildAsync;
        Comparator<AppEntry> mRebuildComparator;
        AppFilter mRebuildFilter;
        boolean mRebuildForeground;
        boolean mRebuildRequested;
        final Object mRebuildSync;
        boolean mResumed;
        final /* synthetic */ ApplicationsState this$0;

        @OnLifecycleEvent(Event.ON_RESUME)
        public void onResume() {
            synchronized (this.this$0.mEntriesMap) {
                if (!this.mResumed) {
                    this.mResumed = true;
                    this.this$0.mSessionsChanged = true;
                    this.this$0.doPauseLocked();
                    this.this$0.doResumeIfNeededLocked();
                }
            }
        }

        @OnLifecycleEvent(Event.ON_PAUSE)
        public void onPause() {
            synchronized (this.this$0.mEntriesMap) {
                if (this.mResumed) {
                    this.mResumed = false;
                    this.this$0.mSessionsChanged = true;
                    this.this$0.mBackgroundHandler.removeMessages(1, this);
                    this.this$0.doPauseIfNeededLocked();
                }
            }
        }

        /* access modifiers changed from: 0000 */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0025, code lost:
            if (r1 == null) goto L_0x002e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0027, code lost:
            r1.init(r7.this$0.mContext);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x002e, code lost:
            r3 = r7.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0032, code lost:
            monitor-enter(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            r0 = new java.util.ArrayList(r7.this$0.mAppEntries);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x003c, code lost:
            monitor-exit(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x003d, code lost:
            r3 = new java.util.ArrayList<>();
            r0 = r0.iterator();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x004a, code lost:
            if (r0.hasNext() == false) goto L_0x0072;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x004c, code lost:
            r4 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r0.next();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0052, code lost:
            if (r4 == null) goto L_0x0046;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0054, code lost:
            if (r1 == null) goto L_0x005c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x005a, code lost:
            if (r1.filterApp(r4) == false) goto L_0x0046;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x005c, code lost:
            r5 = r7.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x0060, code lost:
            monitor-enter(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x0061, code lost:
            if (r2 == null) goto L_0x006a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
            r4.ensureLabel(r7.this$0.mContext);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x006a, code lost:
            r3.add(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x006d, code lost:
            monitor-exit(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:0x0072, code lost:
            if (r2 == null) goto L_0x0081;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x0074, code lost:
            r0 = r7.this$0.mEntriesMap;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x0078, code lost:
            monitor-enter(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
            java.util.Collections.sort(r3, r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:45:0x007c, code lost:
            monitor-exit(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:50:0x0081, code lost:
            r0 = r7.mRebuildSync;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:51:0x0083, code lost:
            monitor-enter(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x0086, code lost:
            if (r7.mRebuildRequested != false) goto L_0x00ae;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x0088, code lost:
            r7.mLastAppList = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x008c, code lost:
            if (r7.mRebuildAsync != false) goto L_0x0094;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:0x008e, code lost:
            r7.mRebuildSync.notifyAll();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x009d, code lost:
            if (r7.this$0.mMainHandler.hasMessages(1, r7) != false) goto L_0x00ae;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:60:0x009f, code lost:
            r7.this$0.mMainHandler.sendMessage(r7.this$0.mMainHandler.obtainMessage(1, r7));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:61:0x00ae, code lost:
            monitor-exit(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x00af, code lost:
            android.os.Process.setThreadPriority(10);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:63:0x00b4, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleRebuildList() {
            /*
                r7 = this;
                boolean r0 = r7.mResumed
                if (r0 != 0) goto L_0x0005
                return
            L_0x0005:
                java.lang.Object r0 = r7.mRebuildSync
                monitor-enter(r0)
                boolean r1 = r7.mRebuildRequested     // Catch:{ all -> 0x00bb }
                if (r1 != 0) goto L_0x000e
                monitor-exit(r0)     // Catch:{ all -> 0x00bb }
                return
            L_0x000e:
                com.android.settingslib.applications.ApplicationsState$AppFilter r1 = r7.mRebuildFilter     // Catch:{ all -> 0x00bb }
                java.util.Comparator<com.android.settingslib.applications.ApplicationsState$AppEntry> r2 = r7.mRebuildComparator     // Catch:{ all -> 0x00bb }
                r3 = 0
                r7.mRebuildRequested = r3     // Catch:{ all -> 0x00bb }
                r4 = 0
                r7.mRebuildFilter = r4     // Catch:{ all -> 0x00bb }
                r7.mRebuildComparator = r4     // Catch:{ all -> 0x00bb }
                boolean r4 = r7.mRebuildForeground     // Catch:{ all -> 0x00bb }
                if (r4 == 0) goto L_0x0024
                r4 = -2
                android.os.Process.setThreadPriority(r4)     // Catch:{ all -> 0x00bb }
                r7.mRebuildForeground = r3     // Catch:{ all -> 0x00bb }
            L_0x0024:
                monitor-exit(r0)     // Catch:{ all -> 0x00bb }
                if (r1 == 0) goto L_0x002e
                com.android.settingslib.applications.ApplicationsState r0 = r7.this$0
                android.content.Context r0 = r0.mContext
                r1.init(r0)
            L_0x002e:
                com.android.settingslib.applications.ApplicationsState r0 = r7.this$0
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r3 = r0.mEntriesMap
                monitor-enter(r3)
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x00b8 }
                com.android.settingslib.applications.ApplicationsState r4 = r7.this$0     // Catch:{ all -> 0x00b8 }
                java.util.ArrayList<com.android.settingslib.applications.ApplicationsState$AppEntry> r4 = r4.mAppEntries     // Catch:{ all -> 0x00b8 }
                r0.<init>(r4)     // Catch:{ all -> 0x00b8 }
                monitor-exit(r3)     // Catch:{ all -> 0x00b8 }
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                java.util.Iterator r0 = r0.iterator()
            L_0x0046:
                boolean r4 = r0.hasNext()
                if (r4 == 0) goto L_0x0072
                java.lang.Object r4 = r0.next()
                com.android.settingslib.applications.ApplicationsState$AppEntry r4 = (com.android.settingslib.applications.ApplicationsState.AppEntry) r4
                if (r4 == 0) goto L_0x0046
                if (r1 == 0) goto L_0x005c
                boolean r5 = r1.filterApp(r4)
                if (r5 == 0) goto L_0x0046
            L_0x005c:
                com.android.settingslib.applications.ApplicationsState r5 = r7.this$0
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r5 = r5.mEntriesMap
                monitor-enter(r5)
                if (r2 == 0) goto L_0x006a
                com.android.settingslib.applications.ApplicationsState r6 = r7.this$0     // Catch:{ all -> 0x006f }
                android.content.Context r6 = r6.mContext     // Catch:{ all -> 0x006f }
                r4.ensureLabel(r6)     // Catch:{ all -> 0x006f }
            L_0x006a:
                r3.add(r4)     // Catch:{ all -> 0x006f }
                monitor-exit(r5)     // Catch:{ all -> 0x006f }
                goto L_0x0046
            L_0x006f:
                r7 = move-exception
                monitor-exit(r5)     // Catch:{ all -> 0x006f }
                throw r7
            L_0x0072:
                if (r2 == 0) goto L_0x0081
                com.android.settingslib.applications.ApplicationsState r0 = r7.this$0
                android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r0 = r0.mEntriesMap
                monitor-enter(r0)
                java.util.Collections.sort(r3, r2)     // Catch:{ all -> 0x007e }
                monitor-exit(r0)     // Catch:{ all -> 0x007e }
                goto L_0x0081
            L_0x007e:
                r7 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x007e }
                throw r7
            L_0x0081:
                java.lang.Object r0 = r7.mRebuildSync
                monitor-enter(r0)
                boolean r1 = r7.mRebuildRequested     // Catch:{ all -> 0x00b5 }
                if (r1 != 0) goto L_0x00ae
                r7.mLastAppList = r3     // Catch:{ all -> 0x00b5 }
                boolean r1 = r7.mRebuildAsync     // Catch:{ all -> 0x00b5 }
                if (r1 != 0) goto L_0x0094
                java.lang.Object r7 = r7.mRebuildSync     // Catch:{ all -> 0x00b5 }
                r7.notifyAll()     // Catch:{ all -> 0x00b5 }
                goto L_0x00ae
            L_0x0094:
                com.android.settingslib.applications.ApplicationsState r1 = r7.this$0     // Catch:{ all -> 0x00b5 }
                com.android.settingslib.applications.ApplicationsState$MainHandler r1 = r1.mMainHandler     // Catch:{ all -> 0x00b5 }
                r2 = 1
                boolean r1 = r1.hasMessages(r2, r7)     // Catch:{ all -> 0x00b5 }
                if (r1 != 0) goto L_0x00ae
                com.android.settingslib.applications.ApplicationsState r1 = r7.this$0     // Catch:{ all -> 0x00b5 }
                com.android.settingslib.applications.ApplicationsState$MainHandler r1 = r1.mMainHandler     // Catch:{ all -> 0x00b5 }
                android.os.Message r1 = r1.obtainMessage(r2, r7)     // Catch:{ all -> 0x00b5 }
                com.android.settingslib.applications.ApplicationsState r7 = r7.this$0     // Catch:{ all -> 0x00b5 }
                com.android.settingslib.applications.ApplicationsState$MainHandler r7 = r7.mMainHandler     // Catch:{ all -> 0x00b5 }
                r7.sendMessage(r1)     // Catch:{ all -> 0x00b5 }
            L_0x00ae:
                monitor-exit(r0)     // Catch:{ all -> 0x00b5 }
                r7 = 10
                android.os.Process.setThreadPriority(r7)
                return
            L_0x00b5:
                r7 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x00b5 }
                throw r7
            L_0x00b8:
                r7 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x00b8 }
                throw r7
            L_0x00bb:
                r7 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x00bb }
                throw r7
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.applications.ApplicationsState.Session.handleRebuildList():void");
        }

        @OnLifecycleEvent(Event.ON_DESTROY)
        public void onDestroy() {
            if (!this.mHasLifecycle) {
                onPause();
            }
            synchronized (this.this$0.mEntriesMap) {
                this.this$0.mSessions.remove(this);
            }
        }
    }

    public static class SizeInfo {
        public long cacheSize;
        public long codeSize;
        public long dataSize;
        public long externalCacheSize;
        public long externalCodeSize;
        public long externalDataSize;
    }

    /* access modifiers changed from: private */
    public static boolean hasFlag(int i, int i2) {
        return (i & i2) != 0;
    }

    static {
        Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    }

    static ApplicationsState getInstance(Application application, IPackageManager iPackageManager) {
        ApplicationsState applicationsState;
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new ApplicationsState(application, iPackageManager);
            }
            applicationsState = sInstance;
        }
        return applicationsState;
    }

    /* access modifiers changed from: 0000 */
    public void setInterestingConfigChanges(InterestingConfigChanges interestingConfigChanges) {
        this.mInterestingConfigChanges = interestingConfigChanges;
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x00e5 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private ApplicationsState(android.app.Application r8, android.content.pm.IPackageManager r9) {
        /*
            r7 = this;
            r7.<init>()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.mSessions = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.mRebuildingSessions = r0
            com.android.settingslib.applications.InterestingConfigChanges r0 = new com.android.settingslib.applications.InterestingConfigChanges
            r0.<init>()
            r7.mInterestingConfigChanges = r0
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r7.mEntriesMap = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.mAppEntries = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.mApplications = r0
            r0 = 1
            r7.mCurId = r0
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r7.mSystemModules = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r7.mActiveSessions = r2
            com.android.settingslib.applications.ApplicationsState$MainHandler r2 = new com.android.settingslib.applications.ApplicationsState$MainHandler
            android.os.Looper r3 = android.os.Looper.getMainLooper()
            r2.<init>(r3)
            r7.mMainHandler = r2
            r7.mContext = r8
            android.content.pm.PackageManager r8 = r8.getPackageManager()
            r7.mPm = r8
            android.content.Context r8 = r7.mContext
            android.util.IconDrawableFactory.newInstance(r8)
            r7.mIpm = r9
            android.content.Context r8 = r7.mContext
            java.lang.Class<android.os.UserManager> r9 = android.os.UserManager.class
            java.lang.Object r8 = r8.getSystemService(r9)
            android.os.UserManager r8 = (android.os.UserManager) r8
            r7.mUm = r8
            android.content.Context r8 = r7.mContext
            java.lang.Class<android.app.usage.StorageStatsManager> r9 = android.app.usage.StorageStatsManager.class
            java.lang.Object r8 = r8.getSystemService(r9)
            android.app.usage.StorageStatsManager r8 = (android.app.usage.StorageStatsManager) r8
            r7.mStats = r8
            android.os.UserManager r8 = r7.mUm
            int r9 = android.os.UserHandle.myUserId()
            int[] r8 = r8.getProfileIdsWithDisabled(r9)
            int r9 = r8.length
            r2 = 0
            r3 = r2
        L_0x007e:
            if (r3 >= r9) goto L_0x008f
            r4 = r8[r3]
            android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r5 = r7.mEntriesMap
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            r5.put(r4, r6)
            int r3 = r3 + 1
            goto L_0x007e
        L_0x008f:
            android.os.HandlerThread r8 = new android.os.HandlerThread
            java.lang.String r9 = "ApplicationsState.Loader"
            r8.<init>(r9)
            r7.mThread = r8
            r8.start()
            com.android.settingslib.applications.ApplicationsState$BackgroundHandler r8 = new com.android.settingslib.applications.ApplicationsState$BackgroundHandler
            android.os.HandlerThread r9 = r7.mThread
            android.os.Looper r9 = r9.getLooper()
            r8.<init>(r9)
            r7.mBackgroundHandler = r8
            r8 = 4227584(0x408200, float:5.924107E-39)
            r7.mAdminRetrieveFlags = r8
            r8 = 33280(0x8200, float:4.6635E-41)
            r7.mRetrieveFlags = r8
            android.content.pm.PackageManager r8 = r7.mPm
            java.util.List r8 = r8.getInstalledModules(r2)
            java.util.Iterator r8 = r8.iterator()
        L_0x00bc:
            boolean r9 = r8.hasNext()
            if (r9 == 0) goto L_0x00da
            java.lang.Object r9 = r8.next()
            android.content.pm.ModuleInfo r9 = (android.content.pm.ModuleInfo) r9
            java.util.HashMap<java.lang.String, java.lang.Boolean> r2 = r7.mSystemModules
            java.lang.String r3 = r9.getPackageName()
            boolean r9 = r9.isHidden()
            java.lang.Boolean r9 = java.lang.Boolean.valueOf(r9)
            r2.put(r3, r9)
            goto L_0x00bc
        L_0x00da:
            android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r8 = r7.mEntriesMap
            monitor-enter(r8)
            android.util.SparseArray<java.util.HashMap<java.lang.String, com.android.settingslib.applications.ApplicationsState$AppEntry>> r7 = r7.mEntriesMap     // Catch:{ InterruptedException -> 0x00e5 }
            r7.wait(r0)     // Catch:{ InterruptedException -> 0x00e5 }
            goto L_0x00e5
        L_0x00e3:
            r7 = move-exception
            goto L_0x00e7
        L_0x00e5:
            monitor-exit(r8)     // Catch:{ all -> 0x00e3 }
            return
        L_0x00e7:
            monitor-exit(r8)     // Catch:{ all -> 0x00e3 }
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.applications.ApplicationsState.<init>(android.app.Application, android.content.pm.IPackageManager):void");
    }

    /* access modifiers changed from: 0000 */
    public void doResumeIfNeededLocked() {
        if (!this.mResumed) {
            this.mResumed = true;
            if (this.mPackageIntentReceiver == null) {
                PackageIntentReceiver packageIntentReceiver = new PackageIntentReceiver();
                this.mPackageIntentReceiver = packageIntentReceiver;
                packageIntentReceiver.registerReceiver();
            }
            List<ApplicationInfo> list = this.mApplications;
            this.mApplications = new ArrayList();
            for (UserInfo userInfo : this.mUm.getProfiles(UserHandle.myUserId())) {
                try {
                    if (this.mEntriesMap.indexOfKey(userInfo.id) < 0) {
                        this.mEntriesMap.put(userInfo.id, new HashMap());
                    }
                    this.mApplications.addAll(this.mIpm.getInstalledApplications(userInfo.isAdmin() ? this.mAdminRetrieveFlags : this.mRetrieveFlags, userInfo.id).getList());
                } catch (Exception e) {
                    Log.e("ApplicationsState", "Error during doResumeIfNeededLocked", e);
                }
            }
            int i = 0;
            if (this.mInterestingConfigChanges.applyNewConfig(this.mContext.getResources())) {
                clearEntries();
            } else {
                for (int i2 = 0; i2 < this.mAppEntries.size(); i2++) {
                    ((AppEntry) this.mAppEntries.get(i2)).sizeStale = true;
                }
            }
            this.mHaveInstantApps = false;
            while (i < this.mApplications.size()) {
                ApplicationInfo applicationInfo = (ApplicationInfo) this.mApplications.get(i);
                if (!applicationInfo.enabled && applicationInfo.enabledSetting != 3) {
                    this.mApplications.remove(i);
                    i--;
                } else if (isHiddenModule(applicationInfo.packageName)) {
                    int i3 = i - 1;
                    this.mApplications.remove(i);
                    i = i3;
                } else {
                    if (!this.mHaveInstantApps && AppUtils.isInstant(applicationInfo)) {
                        this.mHaveInstantApps = true;
                    }
                    AppEntry appEntry = (AppEntry) ((HashMap) this.mEntriesMap.get(UserHandle.getUserId(applicationInfo.uid))).get(applicationInfo.packageName);
                    if (appEntry != null) {
                        appEntry.info = applicationInfo;
                    }
                }
                i++;
            }
            if (anyAppIsRemoved(list, this.mApplications)) {
                clearEntries();
            }
            this.mCurComputingSizePkg = null;
            if (!this.mBackgroundHandler.hasMessages(2)) {
                this.mBackgroundHandler.sendEmptyMessage(2);
            }
        }
    }

    private static boolean anyAppIsRemoved(List<ApplicationInfo> list, List<ApplicationInfo> list2) {
        if (list.size() == 0) {
            return false;
        }
        if (list2.size() < list.size()) {
            return true;
        }
        HashMap hashMap = new HashMap();
        for (ApplicationInfo applicationInfo : list2) {
            String valueOf = String.valueOf(UserHandle.getUserId(applicationInfo.uid));
            HashSet hashSet = (HashSet) hashMap.get(valueOf);
            if (hashSet == null) {
                hashSet = new HashSet();
                hashMap.put(valueOf, hashSet);
            }
            if (hasFlag(applicationInfo.flags, 8388608)) {
                hashSet.add(applicationInfo.packageName);
            }
        }
        for (ApplicationInfo applicationInfo2 : list) {
            if (hasFlag(applicationInfo2.flags, 8388608)) {
                HashSet hashSet2 = (HashSet) hashMap.get(String.valueOf(UserHandle.getUserId(applicationInfo2.uid)));
                if (hashSet2 == null || !hashSet2.remove(applicationInfo2.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public void clearEntries() {
        for (int i = 0; i < this.mEntriesMap.size(); i++) {
            ((HashMap) this.mEntriesMap.valueAt(i)).clear();
        }
        this.mAppEntries.clear();
    }

    /* access modifiers changed from: 0000 */
    public boolean isHiddenModule(String str) {
        Boolean bool = (Boolean) this.mSystemModules.get(str);
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    /* access modifiers changed from: 0000 */
    public void doPauseIfNeededLocked() {
        if (this.mResumed) {
            int i = 0;
            while (i < this.mSessions.size()) {
                if (!((Session) this.mSessions.get(i)).mResumed) {
                    i++;
                } else {
                    return;
                }
            }
            doPauseLocked();
        }
    }

    /* access modifiers changed from: 0000 */
    public void doPauseLocked() {
        this.mResumed = false;
        PackageIntentReceiver packageIntentReceiver = this.mPackageIntentReceiver;
        if (packageIntentReceiver != null) {
            packageIntentReceiver.unregisterReceiver();
            this.mPackageIntentReceiver = null;
        }
    }

    /* access modifiers changed from: 0000 */
    public int indexOfApplicationInfoLocked(String str, int i) {
        for (int size = this.mApplications.size() - 1; size >= 0; size--) {
            ApplicationInfo applicationInfo = (ApplicationInfo) this.mApplications.get(size);
            if (applicationInfo.packageName.equals(str) && UserHandle.getUserId(applicationInfo.uid) == i) {
                return size;
            }
        }
        return -1;
    }

    /* access modifiers changed from: 0000 */
    public void addPackage(String str, int i) {
        try {
            synchronized (this.mEntriesMap) {
                if (this.mResumed) {
                    if (indexOfApplicationInfoLocked(str, i) < 0) {
                        ApplicationInfo applicationInfo = this.mIpm.getApplicationInfo(str, this.mUm.isUserAdmin(i) ? this.mAdminRetrieveFlags : this.mRetrieveFlags, i);
                        if (applicationInfo != null) {
                            if (applicationInfo.enabled || applicationInfo.enabledSetting == 3) {
                                if (AppUtils.isInstant(applicationInfo)) {
                                    this.mHaveInstantApps = true;
                                }
                                this.mApplications.add(applicationInfo);
                                if (!this.mBackgroundHandler.hasMessages(2)) {
                                    this.mBackgroundHandler.sendEmptyMessage(2);
                                }
                                if (!this.mMainHandler.hasMessages(2)) {
                                    this.mMainHandler.sendEmptyMessage(2);
                                }
                            }
                        }
                    }
                }
            }
        } catch (RemoteException unused) {
        }
    }

    public void removePackage(String str, int i) {
        synchronized (this.mEntriesMap) {
            int indexOfApplicationInfoLocked = indexOfApplicationInfoLocked(str, i);
            if (indexOfApplicationInfoLocked >= 0) {
                AppEntry appEntry = (AppEntry) ((HashMap) this.mEntriesMap.get(i)).get(str);
                if (appEntry != null) {
                    ((HashMap) this.mEntriesMap.get(i)).remove(str);
                    this.mAppEntries.remove(appEntry);
                }
                ApplicationInfo applicationInfo = (ApplicationInfo) this.mApplications.get(indexOfApplicationInfoLocked);
                this.mApplications.remove(indexOfApplicationInfoLocked);
                if (!applicationInfo.enabled) {
                    for (ApplicationInfo applicationInfo2 : this.mApplications) {
                        if (!applicationInfo2.enabled) {
                            break;
                        }
                    }
                }
                if (AppUtils.isInstant(applicationInfo)) {
                    this.mHaveInstantApps = false;
                    Iterator it = this.mApplications.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (AppUtils.isInstant((ApplicationInfo) it.next())) {
                                this.mHaveInstantApps = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    public void invalidatePackage(String str, int i) {
        removePackage(str, i);
        addPackage(str, i);
    }

    /* access modifiers changed from: private */
    public void addUser(int i) {
        if (ArrayUtils.contains(this.mUm.getProfileIdsWithDisabled(UserHandle.myUserId()), i)) {
            synchronized (this.mEntriesMap) {
                this.mEntriesMap.put(i, new HashMap());
                if (this.mResumed) {
                    doPauseLocked();
                    doResumeIfNeededLocked();
                }
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void removeUser(int i) {
        synchronized (this.mEntriesMap) {
            HashMap hashMap = (HashMap) this.mEntriesMap.get(i);
            if (hashMap != null) {
                for (AppEntry appEntry : hashMap.values()) {
                    this.mAppEntries.remove(appEntry);
                    this.mApplications.remove(appEntry.info);
                }
                this.mEntriesMap.remove(i);
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public AppEntry getEntryLocked(ApplicationInfo applicationInfo) {
        int userId = UserHandle.getUserId(applicationInfo.uid);
        AppEntry appEntry = (AppEntry) ((HashMap) this.mEntriesMap.get(userId)).get(applicationInfo.packageName);
        if (appEntry == null) {
            if (isHiddenModule(applicationInfo.packageName)) {
                return null;
            }
            Context context = this.mContext;
            long j = this.mCurId;
            this.mCurId = 1 + j;
            appEntry = new AppEntry(context, applicationInfo, j);
            ((HashMap) this.mEntriesMap.get(userId)).put(applicationInfo.packageName, appEntry);
            this.mAppEntries.add(appEntry);
        } else if (appEntry.info != applicationInfo) {
            appEntry.info = applicationInfo;
        }
        return appEntry;
    }

    /* access modifiers changed from: private */
    public long getTotalInternalSize(PackageStats packageStats) {
        if (packageStats != null) {
            return (packageStats.codeSize + packageStats.dataSize) - packageStats.cacheSize;
        }
        return -2;
    }

    /* access modifiers changed from: private */
    public long getTotalExternalSize(PackageStats packageStats) {
        if (packageStats != null) {
            return packageStats.externalCodeSize + packageStats.externalDataSize + packageStats.externalCacheSize + packageStats.externalMediaSize + packageStats.externalObbSize;
        }
        return -2;
    }

    /* access modifiers changed from: private */
    public String getSizeStr(long j) {
        if (j >= 0) {
            return Formatter.formatFileSize(this.mContext, j);
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void rebuildActiveSessions() {
        synchronized (this.mEntriesMap) {
            if (this.mSessionsChanged) {
                this.mActiveSessions.clear();
                for (int i = 0; i < this.mSessions.size(); i++) {
                    Session session = (Session) this.mSessions.get(i);
                    if (session.mResumed) {
                        this.mActiveSessions.add(new WeakReference(session));
                    }
                }
            }
        }
    }
}
