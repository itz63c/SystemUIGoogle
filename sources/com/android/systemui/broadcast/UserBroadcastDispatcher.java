package com.android.systemui.broadcast;

import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver.PendingResult;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.util.Preconditions;
import com.android.systemui.Dumpable;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$BooleanRef;
import kotlin.sequences.Sequence;

/* compiled from: UserBroadcastDispatcher.kt */
public final class UserBroadcastDispatcher extends BroadcastReceiver implements Dumpable {
    private final ArrayMap<String, Set<ReceiverData>> actionsToReceivers = new ArrayMap<>();
    /* access modifiers changed from: private */
    public final UserBroadcastDispatcher$bgHandler$1 bgHandler = new UserBroadcastDispatcher$bgHandler$1(this, this.bgLooper);
    private final Looper bgLooper;
    /* access modifiers changed from: private */
    public final Context context;
    private final Handler mainHandler;
    private final ArrayMap<BroadcastReceiver, Set<ReceiverData>> receiverToReceiverData = new ArrayMap<>();
    /* access modifiers changed from: private */
    public final AtomicBoolean registered = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public final int userId;

    /* compiled from: UserBroadcastDispatcher.kt */
    private static final class HandleBroadcastRunnable implements Runnable {
        private final Map<String, Set<ReceiverData>> actionsToReceivers;
        private final Context context;
        private final Intent intent;
        private final PendingResult pendingResult;

        public HandleBroadcastRunnable(Map<String, ? extends Set<ReceiverData>> map, Context context2, Intent intent2, PendingResult pendingResult2, int i) {
            Intrinsics.checkParameterIsNotNull(map, "actionsToReceivers");
            Intrinsics.checkParameterIsNotNull(context2, "context");
            Intrinsics.checkParameterIsNotNull(intent2, "intent");
            Intrinsics.checkParameterIsNotNull(pendingResult2, "pendingResult");
            this.actionsToReceivers = map;
            this.context = context2;
            this.intent = intent2;
            this.pendingResult = pendingResult2;
        }

        public final Context getContext() {
            return this.context;
        }

        public final Intent getIntent() {
            return this.intent;
        }

        public final PendingResult getPendingResult() {
            return this.pendingResult;
        }

        public void run() {
            Set set = (Set) this.actionsToReceivers.get(this.intent.getAction());
            if (set != null) {
                ArrayList<ReceiverData> arrayList = new ArrayList<>();
                for (Object next : set) {
                    ReceiverData receiverData = (ReceiverData) next;
                    if (receiverData.getFilter().hasAction(this.intent.getAction()) && receiverData.getFilter().matchCategories(this.intent.getCategories()) == null) {
                        arrayList.add(next);
                    }
                }
                for (ReceiverData receiverData2 : arrayList) {
                    receiverData2.getExecutor().execute(new C0755x519e51dd(receiverData2, this));
                }
            }
        }
    }

    /* compiled from: UserBroadcastDispatcher.kt */
    private final class RegisterReceiverRunnable implements Runnable {
        private final IntentFilter intentFilter;
        final /* synthetic */ UserBroadcastDispatcher this$0;

        public RegisterReceiverRunnable(UserBroadcastDispatcher userBroadcastDispatcher, IntentFilter intentFilter2) {
            Intrinsics.checkParameterIsNotNull(intentFilter2, "intentFilter");
            this.this$0 = userBroadcastDispatcher;
            this.intentFilter = intentFilter2;
        }

        public void run() {
            if (this.this$0.registered.get()) {
                this.this$0.context.unregisterReceiver(this.this$0);
                this.this$0.registered.set(false);
            }
            if (this.intentFilter.countActions() > 0 && !this.this$0.registered.get()) {
                Context access$getContext$p = this.this$0.context;
                UserBroadcastDispatcher userBroadcastDispatcher = this.this$0;
                access$getContext$p.registerReceiverAsUser(userBroadcastDispatcher, UserHandle.of(userBroadcastDispatcher.userId), this.intentFilter, null, this.this$0.bgHandler);
                this.this$0.registered.set(true);
            }
        }
    }

    public UserBroadcastDispatcher(Context context2, int i, Handler handler, Looper looper) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(handler, "mainHandler");
        Intrinsics.checkParameterIsNotNull(looper, "bgLooper");
        this.context = context2;
        this.userId = i;
        this.mainHandler = handler;
        this.bgLooper = looper;
    }

    static {
        new AtomicInteger(0);
    }

    /* renamed from: isReceiverReferenceHeld$frameworks__base__packages__SystemUI__android_common__SystemUI_core */
    public final boolean mo10182xbd20662d(BroadcastReceiver broadcastReceiver) {
        boolean z;
        boolean z2;
        Intrinsics.checkParameterIsNotNull(broadcastReceiver, "receiver");
        if (this.receiverToReceiverData.containsKey(broadcastReceiver)) {
            return true;
        }
        ArrayMap<String, Set<ReceiverData>> arrayMap = this.actionsToReceivers;
        if (!arrayMap.isEmpty()) {
            Iterator it = arrayMap.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Object value = ((Entry) it.next()).getValue();
                Intrinsics.checkExpressionValueIsNotNull(value, "it.value");
                Iterable iterable = (Iterable) value;
                if (!(iterable instanceof Collection) || !((Collection) iterable).isEmpty()) {
                    Iterator it2 = iterable.iterator();
                    while (true) {
                        if (it2.hasNext()) {
                            if (Intrinsics.areEqual((Object) ((ReceiverData) it2.next()).getReceiver(), (Object) broadcastReceiver)) {
                                z2 = true;
                                continue;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                z2 = false;
                continue;
                if (z2) {
                    z = true;
                    break;
                }
            }
        }
        z = false;
        if (z) {
            return true;
        }
        return false;
    }

    private final IntentFilter createFilter() {
        Looper looper = this.bgHandler.getLooper();
        Intrinsics.checkExpressionValueIsNotNull(looper, "bgHandler.looper");
        Preconditions.checkState(looper.isCurrentThread(), "This method should only be called from BG thread");
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        Collection values = this.receiverToReceiverData.values();
        Intrinsics.checkExpressionValueIsNotNull(values, "receiverToReceiverData.values");
        for (ReceiverData filter : CollectionsKt__IterablesKt.flatten(values)) {
            Iterator categoriesIterator = filter.getFilter().categoriesIterator();
            if (categoriesIterator != null) {
                Sequence asSequence = SequencesKt__SequencesKt.asSequence(categoriesIterator);
                if (asSequence != null) {
                    CollectionsKt__MutableCollectionsKt.addAll((Collection) linkedHashSet, asSequence);
                }
            }
        }
        IntentFilter intentFilter = new IntentFilter();
        Set<String> keySet = this.actionsToReceivers.keySet();
        Intrinsics.checkExpressionValueIsNotNull(keySet, "actionsToReceivers.keys");
        for (String str : keySet) {
            if (str != null) {
                intentFilter.addAction(str);
            }
        }
        for (String addCategory : linkedHashSet) {
            intentFilter.addCategory(addCategory);
        }
        return intentFilter;
    }

    public void onReceive(Context context2, Intent intent) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        UserBroadcastDispatcher$bgHandler$1 userBroadcastDispatcher$bgHandler$1 = this.bgHandler;
        ArrayMap<String, Set<ReceiverData>> arrayMap = this.actionsToReceivers;
        PendingResult pendingResult = getPendingResult();
        Intrinsics.checkExpressionValueIsNotNull(pendingResult, "pendingResult");
        HandleBroadcastRunnable handleBroadcastRunnable = new HandleBroadcastRunnable(arrayMap, context2, intent, pendingResult, 0);
        userBroadcastDispatcher$bgHandler$1.post(handleBroadcastRunnable);
    }

    public final void registerReceiver(ReceiverData receiverData) {
        Intrinsics.checkParameterIsNotNull(receiverData, "receiverData");
        this.bgHandler.obtainMessage(0, receiverData).sendToTarget();
    }

    public final void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        Intrinsics.checkParameterIsNotNull(broadcastReceiver, "receiver");
        this.bgHandler.obtainMessage(1, broadcastReceiver).sendToTarget();
    }

    /* access modifiers changed from: private */
    public final void handleRegisterReceiver(ReceiverData receiverData) {
        Looper looper = this.bgHandler.getLooper();
        Intrinsics.checkExpressionValueIsNotNull(looper, "bgHandler.looper");
        Preconditions.checkState(looper.isCurrentThread(), "This method should only be called from BG thread");
        ArrayMap<BroadcastReceiver, Set<ReceiverData>> arrayMap = this.receiverToReceiverData;
        BroadcastReceiver receiver = receiverData.getReceiver();
        Object obj = arrayMap.get(receiver);
        if (obj == null) {
            obj = new ArraySet();
            arrayMap.put(receiver, obj);
        }
        ((Set) obj).add(receiverData);
        boolean z = false;
        Iterator actionsIterator = receiverData.getFilter().actionsIterator();
        Intrinsics.checkExpressionValueIsNotNull(actionsIterator, "receiverData.filter.actionsIterator()");
        while (actionsIterator.hasNext()) {
            String str = (String) actionsIterator.next();
            ArrayMap<String, Set<ReceiverData>> arrayMap2 = this.actionsToReceivers;
            Object obj2 = arrayMap2.get(str);
            if (obj2 == null) {
                z = true;
                obj2 = new ArraySet();
                arrayMap2.put(str, obj2);
            }
            ((Set) obj2).add(receiverData);
        }
        if (z) {
            createFilterAndRegisterReceiverBG();
        }
    }

    /* access modifiers changed from: private */
    public final void handleUnregisterReceiver(BroadcastReceiver broadcastReceiver) {
        Looper looper = this.bgHandler.getLooper();
        Intrinsics.checkExpressionValueIsNotNull(looper, "bgHandler.looper");
        Preconditions.checkState(looper.isCurrentThread(), "This method should only be called from BG thread");
        Object obj = this.receiverToReceiverData.get(broadcastReceiver);
        if (obj != null) {
            Intrinsics.checkExpressionValueIsNotNull(obj, "receiverToReceiverData.g…Else(receiver) { return }");
            Iterable<ReceiverData> iterable = (Iterable) obj;
            ArrayList arrayList = new ArrayList();
            for (ReceiverData filter : iterable) {
                Iterator actionsIterator = filter.getFilter().actionsIterator();
                Intrinsics.checkExpressionValueIsNotNull(actionsIterator, "it.filter.actionsIterator()");
                CollectionsKt__MutableCollectionsKt.addAll((Collection) arrayList, SequencesKt___SequencesKt.asIterable(SequencesKt__SequencesKt.asSequence(actionsIterator)));
            }
            Set<String> set = CollectionsKt___CollectionsKt.toSet(arrayList);
            Set set2 = (Set) this.receiverToReceiverData.remove(broadcastReceiver);
            if (set2 != null) {
                set2.clear();
            }
            Ref$BooleanRef ref$BooleanRef = new Ref$BooleanRef();
            ref$BooleanRef.element = false;
            for (String str : set) {
                Set set3 = (Set) this.actionsToReceivers.get(str);
                if (set3 != null) {
                    set3.removeIf(new C0756xa20bc321(this, broadcastReceiver, ref$BooleanRef));
                }
                Set set4 = (Set) this.actionsToReceivers.get(str);
                if (set4 != null ? set4.isEmpty() : false) {
                    ref$BooleanRef.element = true;
                    this.actionsToReceivers.remove(str);
                }
            }
            if (ref$BooleanRef.element) {
                createFilterAndRegisterReceiverBG();
            }
        }
    }

    private final void createFilterAndRegisterReceiverBG() {
        this.mainHandler.post(new RegisterReceiverRunnable(this, createFilter()));
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(strArr, "args");
        StringBuilder sb = new StringBuilder();
        sb.append("  Registered=");
        sb.append(this.registered.get());
        printWriter.println(sb.toString());
        for (Entry entry : this.actionsToReceivers.entrySet()) {
            String str = (String) entry.getKey();
            Set<ReceiverData> set = (Set) entry.getValue();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("    ");
            sb2.append(str);
            sb2.append(':');
            printWriter.println(sb2.toString());
            Intrinsics.checkExpressionValueIsNotNull(set, "list");
            for (ReceiverData receiverData : set) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("      ");
                sb3.append(receiverData.getReceiver());
                printWriter.println(sb3.toString());
            }
        }
    }
}
