package com.android.systemui.stackdivider;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityTaskManager;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Log;
import android.view.IWindowContainer;
import android.view.WindowContainerTransaction;
import android.view.WindowManagerGlobal;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WindowManagerProxy {
    private static final int[] HOME_AND_RECENTS = {2, 3};
    private static final WindowManagerProxy sInstance = new WindowManagerProxy();
    /* access modifiers changed from: private */
    @GuardedBy({"mDockedRect"})
    public final Rect mDockedRect = new Rect();
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final Runnable mSetTouchableRegionRunnable = new Runnable() {
        public void run() {
            try {
                synchronized (WindowManagerProxy.this.mDockedRect) {
                    WindowManagerProxy.this.mTmpRect1.set(WindowManagerProxy.this.mTouchableRegion);
                }
                WindowManagerGlobal.getWindowManagerService().setDockedStackDividerTouchRegion(WindowManagerProxy.this.mTmpRect1);
            } catch (RemoteException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to set touchable region: ");
                sb.append(e);
                Log.w("WindowManagerProxy", sb.toString());
            }
        }
    };
    /* access modifiers changed from: private */
    public final Rect mTmpRect1 = new Rect();
    /* access modifiers changed from: private */
    @GuardedBy({"mDockedRect"})
    public final Rect mTouchableRegion = new Rect();

    private WindowManagerProxy() {
    }

    public static WindowManagerProxy getInstance() {
        return sInstance;
    }

    /* access modifiers changed from: 0000 */
    public void dismissOrMaximizeDocked(SplitScreenTaskOrganizer splitScreenTaskOrganizer, boolean z) {
        this.mExecutor.execute(new Runnable(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WindowManagerProxy.applyDismissSplit(SplitScreenTaskOrganizer.this, this.f$1);
            }
        });
    }

    public void setResizing(final boolean z) {
        this.mExecutor.execute(new Runnable(this) {
            public void run() {
                try {
                    ActivityTaskManager.getService().setSplitScreenResizing(z);
                } catch (RemoteException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Error calling setDockedStackResizing: ");
                    sb.append(e);
                    Log.w("WindowManagerProxy", sb.toString());
                }
            }
        });
    }

    public void setTouchRegion(Rect rect) {
        synchronized (this.mDockedRect) {
            this.mTouchableRegion.set(rect);
        }
        this.mExecutor.execute(this.mSetTouchableRegionRunnable);
    }

    static void applyResizeSplits(int i, SplitDisplayLayout splitDisplayLayout) {
        WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
        splitDisplayLayout.resizeSplits(i, windowContainerTransaction);
        try {
            ActivityTaskManager.getTaskOrganizerController().applyContainerTransaction(windowContainerTransaction, null);
        } catch (RemoteException unused) {
        }
    }

    /* JADX WARNING: type inference failed for: r1v1 */
    /* JADX WARNING: type inference failed for: r2v0, types: [boolean] */
    /* JADX WARNING: type inference failed for: r2v1 */
    /* JADX WARNING: type inference failed for: r2v2, types: [boolean] */
    /* JADX WARNING: type inference failed for: r1v3 */
    /* JADX WARNING: type inference failed for: r2v3 */
    /* JADX WARNING: type inference failed for: r2v4, types: [boolean] */
    /* JADX WARNING: type inference failed for: r2v6 */
    /* JADX WARNING: type inference failed for: r2v7 */
    /* JADX WARNING: type inference failed for: r2v8 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r2v3
      assigns: []
      uses: []
      mth insns count: 28
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 4 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean getHomeAndRecentsTasks(java.util.List<android.view.IWindowContainer> r6, android.view.IWindowContainer r7) {
        /*
            int[] r0 = HOME_AND_RECENTS
            r1 = 0
            if (r7 != 0) goto L_0x000e
            android.app.ITaskOrganizerController r7 = android.app.ActivityTaskManager.getTaskOrganizerController()     // Catch:{ RemoteException -> 0x0035 }
            java.util.List r7 = r7.getRootTasks(r1, r0)     // Catch:{ RemoteException -> 0x0035 }
            goto L_0x0016
        L_0x000e:
            android.app.ITaskOrganizerController r2 = android.app.ActivityTaskManager.getTaskOrganizerController()     // Catch:{ RemoteException -> 0x0035 }
            java.util.List r7 = r2.getChildTasks(r7, r0)     // Catch:{ RemoteException -> 0x0035 }
        L_0x0016:
            int r0 = r7.size()     // Catch:{ RemoteException -> 0x0035 }
            r2 = r1
        L_0x001b:
            if (r1 >= r0) goto L_0x0036
            java.lang.Object r3 = r7.get(r1)     // Catch:{ RemoteException -> 0x0034 }
            android.app.ActivityManager$RunningTaskInfo r3 = (android.app.ActivityManager.RunningTaskInfo) r3     // Catch:{ RemoteException -> 0x0034 }
            android.view.IWindowContainer r4 = r3.token     // Catch:{ RemoteException -> 0x0034 }
            r6.add(r4)     // Catch:{ RemoteException -> 0x0034 }
            int r4 = r3.topActivityType     // Catch:{ RemoteException -> 0x0034 }
            r5 = 2
            if (r4 != r5) goto L_0x0031
            boolean r2 = r3.isResizable()     // Catch:{ RemoteException -> 0x0034 }
        L_0x0031:
            int r1 = r1 + 1
            goto L_0x001b
        L_0x0034:
            r1 = r2
        L_0x0035:
            r2 = r1
        L_0x0036:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.stackdivider.WindowManagerProxy.getHomeAndRecentsTasks(java.util.List, android.view.IWindowContainer):boolean");
    }

    static boolean applyHomeTasksMinimized(SplitDisplayLayout splitDisplayLayout, IWindowContainer iWindowContainer, WindowContainerTransaction windowContainerTransaction) {
        Rect rect;
        ArrayList arrayList = new ArrayList();
        boolean homeAndRecentsTasks = getHomeAndRecentsTasks(arrayList, iWindowContainer);
        if (homeAndRecentsTasks) {
            rect = splitDisplayLayout.calcMinimizedHomeStackBounds();
        } else {
            rect = new Rect(0, 0, splitDisplayLayout.mDisplayLayout.width(), splitDisplayLayout.mDisplayLayout.height());
        }
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            windowContainerTransaction.setBounds((IWindowContainer) arrayList.get(size), rect);
        }
        return homeAndRecentsTasks;
    }

    static boolean applyEnterSplit(SplitScreenTaskOrganizer splitScreenTaskOrganizer, SplitDisplayLayout splitDisplayLayout) {
        try {
            ActivityTaskManager.getTaskOrganizerController().setLaunchRoot(0, splitScreenTaskOrganizer.mSecondary.token);
            List rootTasks = ActivityTaskManager.getTaskOrganizerController().getRootTasks(0, null);
            WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
            if (rootTasks.isEmpty()) {
                return false;
            }
            for (int size = rootTasks.size() - 1; size >= 0; size--) {
                if (((RunningTaskInfo) rootTasks.get(size)).configuration.windowConfiguration.getWindowingMode() == 1) {
                    windowContainerTransaction.reparent(((RunningTaskInfo) rootTasks.get(size)).token, splitScreenTaskOrganizer.mSecondary.token, true);
                }
            }
            boolean applyHomeTasksMinimized = applyHomeTasksMinimized(splitDisplayLayout, null, windowContainerTransaction);
            ActivityTaskManager.getTaskOrganizerController().applyContainerTransaction(windowContainerTransaction, null);
            return applyHomeTasksMinimized;
        } catch (RemoteException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error moving fullscreen tasks to secondary split: ");
            sb.append(e);
            Log.w("WindowManagerProxy", sb.toString());
            return false;
        }
    }

    private static boolean isHomeOrRecentTask(RunningTaskInfo runningTaskInfo) {
        int activityType = runningTaskInfo.configuration.windowConfiguration.getActivityType();
        return activityType == 2 || activityType == 3;
    }

    /* access modifiers changed from: 0000 */
    public static void applyDismissSplit(SplitScreenTaskOrganizer splitScreenTaskOrganizer, boolean z) {
        try {
            ActivityTaskManager.getTaskOrganizerController().setLaunchRoot(0, null);
            List childTasks = ActivityTaskManager.getTaskOrganizerController().getChildTasks(splitScreenTaskOrganizer.mPrimary.token, null);
            List childTasks2 = ActivityTaskManager.getTaskOrganizerController().getChildTasks(splitScreenTaskOrganizer.mSecondary.token, null);
            List rootTasks = ActivityTaskManager.getTaskOrganizerController().getRootTasks(0, HOME_AND_RECENTS);
            if (!childTasks.isEmpty() || !childTasks2.isEmpty() || !rootTasks.isEmpty()) {
                WindowContainerTransaction windowContainerTransaction = new WindowContainerTransaction();
                if (z) {
                    for (int size = childTasks.size() - 1; size >= 0; size--) {
                        windowContainerTransaction.reparent(((RunningTaskInfo) childTasks.get(size)).token, null, true);
                    }
                    for (int size2 = childTasks2.size() - 1; size2 >= 0; size2--) {
                        RunningTaskInfo runningTaskInfo = (RunningTaskInfo) childTasks2.get(size2);
                        windowContainerTransaction.reparent(runningTaskInfo.token, null, true);
                        if (isHomeOrRecentTask(runningTaskInfo)) {
                            windowContainerTransaction.setBounds(runningTaskInfo.token, null);
                        }
                    }
                } else {
                    for (int size3 = childTasks2.size() - 1; size3 >= 0; size3--) {
                        if (!isHomeOrRecentTask((RunningTaskInfo) childTasks2.get(size3))) {
                            windowContainerTransaction.reparent(((RunningTaskInfo) childTasks2.get(size3)).token, null, true);
                        }
                    }
                    for (int size4 = childTasks2.size() - 1; size4 >= 0; size4--) {
                        RunningTaskInfo runningTaskInfo2 = (RunningTaskInfo) childTasks2.get(size4);
                        if (isHomeOrRecentTask(runningTaskInfo2)) {
                            windowContainerTransaction.reparent(runningTaskInfo2.token, null, true);
                            windowContainerTransaction.setBounds(runningTaskInfo2.token, null);
                        }
                    }
                    for (int size5 = childTasks.size() - 1; size5 >= 0; size5--) {
                        windowContainerTransaction.reparent(((RunningTaskInfo) childTasks.get(size5)).token, null, true);
                    }
                }
                for (int size6 = rootTasks.size() - 1; size6 >= 0; size6--) {
                    windowContainerTransaction.setBounds(((RunningTaskInfo) rootTasks.get(size6)).token, null);
                }
                windowContainerTransaction.setFocusable(splitScreenTaskOrganizer.mPrimary.token, true);
                ActivityTaskManager.getTaskOrganizerController().applyContainerTransaction(windowContainerTransaction, null);
            }
        } catch (RemoteException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to remove stack: ");
            sb.append(e);
            Log.w("WindowManagerProxy", sb.toString());
        }
    }

    static void applyContainerTransaction(WindowContainerTransaction windowContainerTransaction) {
        try {
            ActivityTaskManager.getTaskOrganizerController().applyContainerTransaction(windowContainerTransaction, null);
        } catch (RemoteException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error setting focusability: ");
            sb.append(e);
            Log.w("WindowManagerProxy", sb.toString());
        }
    }
}
