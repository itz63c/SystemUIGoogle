package com.android.systemui.bubbles;

import android.app.Notification.BubbleMetadata;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.systemui.C2012R$integer;
import com.android.systemui.bubbles.BubbleController.NotificationSuppressionChangedListener;
import com.android.systemui.bubbles.BubbleViewInfoTask.Callback;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BubbleData {
    private static final Comparator<Bubble> BUBBLES_BY_SORT_KEY_DESCENDING = Comparator.comparing($$Lambda$BubbleData$vPZCImnk7rTPTX1c7nr0PX7FO2o.INSTANCE).reversed();
    private static final Comparator<Entry<String, Long>> GROUPS_BY_MAX_SORT_KEY_DESCENDING = Comparator.comparing($$Lambda$JmVHPWbzq5woEs3Hauzhf2I3Jc.INSTANCE).reversed();
    private final List<Bubble> mBubbles;
    private final Context mContext;
    private boolean mExpanded;
    private Listener mListener;
    private final int mMaxBubbles;
    private final int mMaxOverflowBubbles;
    private final List<Bubble> mOverflowBubbles;
    private final List<Bubble> mPendingBubbles;
    private Bubble mSelectedBubble;
    private Update mStateChange;
    private HashMap<String, String> mSuppressedGroupKeys = new HashMap<>();
    private NotificationSuppressionChangedListener mSuppressionListener;
    private TimeSource mTimeSource = $$Lambda$0E0fwzH9SS6aB9lL5npMzupI4Q.INSTANCE;

    interface Listener {
        void applyUpdate(Update update);
    }

    interface TimeSource {
        long currentTimeMillis();
    }

    static final class Update {
        Bubble addedBubble;
        final List<Bubble> bubbles;
        boolean expanded;
        boolean expandedChanged;
        boolean orderChanged;
        final List<Pair<Bubble, Integer>> removedBubbles;
        Bubble selectedBubble;
        boolean selectionChanged;
        Bubble updatedBubble;

        private Update(List<Bubble> list, List<Bubble> list2) {
            this.removedBubbles = new ArrayList();
            this.bubbles = Collections.unmodifiableList(list);
            Collections.unmodifiableList(list2);
        }

        /* access modifiers changed from: 0000 */
        public boolean anythingChanged() {
            return this.expandedChanged || this.selectionChanged || this.addedBubble != null || this.updatedBubble != null || !this.removedBubbles.isEmpty() || this.orderChanged;
        }

        /* access modifiers changed from: 0000 */
        public void bubbleRemoved(Bubble bubble, int i) {
            this.removedBubbles.add(new Pair(bubble, Integer.valueOf(i)));
        }
    }

    public BubbleData(Context context) {
        this.mContext = context;
        this.mBubbles = new ArrayList();
        this.mOverflowBubbles = new ArrayList();
        this.mPendingBubbles = new ArrayList();
        this.mStateChange = new Update(this.mBubbles, this.mOverflowBubbles);
        this.mMaxBubbles = this.mContext.getResources().getInteger(C2012R$integer.bubbles_max_rendered);
        this.mMaxOverflowBubbles = this.mContext.getResources().getInteger(C2012R$integer.bubbles_max_overflow);
    }

    public void setSuppressionChangedListener(NotificationSuppressionChangedListener notificationSuppressionChangedListener) {
        this.mSuppressionListener = notificationSuppressionChangedListener;
    }

    public boolean hasBubbles() {
        return !this.mBubbles.isEmpty();
    }

    public boolean isExpanded() {
        return this.mExpanded;
    }

    public boolean hasBubbleWithKey(String str) {
        return getBubbleWithKey(str) != null;
    }

    public Bubble getSelectedBubble() {
        return this.mSelectedBubble;
    }

    public void setExpanded(boolean z) {
        setExpandedInternal(z);
        dispatchPendingChanges();
    }

    public void setSelectedBubble(Bubble bubble) {
        setSelectedBubbleInternal(bubble);
        dispatchPendingChanges();
    }

    public void promoteBubbleFromOverflow(Bubble bubble, BubbleStackView bubbleStackView, BubbleIconFactory bubbleIconFactory) {
        bubble.markUpdatedAt(this.mTimeSource.currentTimeMillis());
        this.mOverflowBubbles.remove(bubble);
        bubble.inflate(new Callback(bubble) {
            public final /* synthetic */ Bubble f$1;

            {
                this.f$1 = r2;
            }

            public final void onBubbleViewsReady(Bubble bubble) {
                BubbleData.this.lambda$promoteBubbleFromOverflow$0$BubbleData(this.f$1, bubble);
            }
        }, this.mContext, bubbleStackView, bubbleIconFactory);
        dispatchPendingChanges();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$promoteBubbleFromOverflow$0 */
    public /* synthetic */ void lambda$promoteBubbleFromOverflow$0$BubbleData(Bubble bubble, Bubble bubble2) {
        notificationEntryUpdated(bubble, false, true);
        setSelectedBubbleInternal(bubble);
    }

    /* access modifiers changed from: 0000 */
    public Bubble getOrCreateBubble(NotificationEntry notificationEntry) {
        Bubble bubbleWithKey = getBubbleWithKey(notificationEntry.getKey());
        if (bubbleWithKey == null) {
            for (int i = 0; i < this.mOverflowBubbles.size(); i++) {
                Bubble bubble = (Bubble) this.mOverflowBubbles.get(i);
                if (bubble.getKey().equals(notificationEntry.getKey())) {
                    this.mOverflowBubbles.remove(bubble);
                    this.mPendingBubbles.add(bubble);
                    return bubble;
                }
            }
            for (int i2 = 0; i2 < this.mPendingBubbles.size(); i2++) {
                Bubble bubble2 = (Bubble) this.mPendingBubbles.get(i2);
                if (bubble2.getKey().equals(notificationEntry.getKey())) {
                    return bubble2;
                }
            }
            bubbleWithKey = new Bubble(notificationEntry, this.mSuppressionListener);
            this.mPendingBubbles.add(bubbleWithKey);
        } else {
            bubbleWithKey.setEntry(notificationEntry);
        }
        return bubbleWithKey;
    }

    /* access modifiers changed from: 0000 */
    public void notificationEntryUpdated(Bubble bubble, boolean z, boolean z2) {
        this.mPendingBubbles.remove(bubble);
        boolean z3 = z | (!bubble.getEntry().getRanking().visuallyInterruptive());
        if (getBubbleWithKey(bubble.getKey()) == null) {
            bubble.setSuppressFlyout(z3);
            doAdd(bubble);
            trim();
        } else {
            bubble.setSuppressFlyout(z3);
            doUpdate(bubble);
        }
        if (bubble.shouldAutoExpand()) {
            setSelectedBubbleInternal(bubble);
            if (!this.mExpanded) {
                setExpandedInternal(true);
            }
        } else if (this.mSelectedBubble == null) {
            setSelectedBubbleInternal(bubble);
        }
        boolean z4 = false;
        boolean z5 = this.mExpanded && this.mSelectedBubble == bubble;
        if (z5 || !z2 || !bubble.showInShade()) {
            z4 = true;
        }
        bubble.setSuppressNotification(z4);
        bubble.setShowDot(!z5, true);
        dispatchPendingChanges();
    }

    public void notificationEntryRemoved(NotificationEntry notificationEntry, int i) {
        doRemove(notificationEntry.getKey(), i);
        dispatchPendingChanges();
    }

    /* access modifiers changed from: 0000 */
    public void addSummaryToSuppress(String str, String str2) {
        this.mSuppressedGroupKeys.put(str, str2);
    }

    /* access modifiers changed from: 0000 */
    public String getSummaryKey(String str) {
        return (String) this.mSuppressedGroupKeys.get(str);
    }

    /* access modifiers changed from: 0000 */
    public void removeSuppressedSummary(String str) {
        this.mSuppressedGroupKeys.remove(str);
    }

    /* access modifiers changed from: 0000 */
    public boolean isSummarySuppressed(String str) {
        return this.mSuppressedGroupKeys.containsKey(str);
    }

    /* access modifiers changed from: 0000 */
    public ArrayList<Bubble> getBubblesInGroup(String str) {
        ArrayList<Bubble> arrayList = new ArrayList<>();
        if (str == null) {
            return arrayList;
        }
        for (Bubble bubble : this.mBubbles) {
            if (str.equals(bubble.getEntry().getSbn().getGroupKey())) {
                arrayList.add(bubble);
            }
        }
        return arrayList;
    }

    private void doAdd(Bubble bubble) {
        if (insertBubble((!isExpanded() || (hasBubbleWithGroupId(bubble.getGroupId()) ^ true)) ? 0 : findFirstIndexForGroup(bubble.getGroupId()), bubble) < this.mBubbles.size() - 1) {
            this.mStateChange.orderChanged = true;
        }
        this.mStateChange.addedBubble = bubble;
        if (!isExpanded()) {
            Update update = this.mStateChange;
            update.orderChanged = packGroup(findFirstIndexForGroup(bubble.getGroupId())) | update.orderChanged;
            setSelectedBubbleInternal((Bubble) this.mBubbles.get(0));
        }
    }

    private void trim() {
        if (this.mBubbles.size() > this.mMaxBubbles) {
            this.mBubbles.stream().sorted(Comparator.comparingLong($$Lambda$x9O8XLDgnXklCbpbq_xgakOvcgY.INSTANCE)).filter(new Predicate() {
                public final boolean test(Object obj) {
                    return BubbleData.this.lambda$trim$1$BubbleData((Bubble) obj);
                }
            }).findFirst().ifPresent(new Consumer() {
                public final void accept(Object obj) {
                    BubbleData.this.lambda$trim$2$BubbleData((Bubble) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$trim$1 */
    public /* synthetic */ boolean lambda$trim$1$BubbleData(Bubble bubble) {
        return !bubble.equals(this.mSelectedBubble);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$trim$2 */
    public /* synthetic */ void lambda$trim$2$BubbleData(Bubble bubble) {
        doRemove(bubble.getKey(), 2);
    }

    private void doUpdate(Bubble bubble) {
        this.mStateChange.updatedBubble = bubble;
        if (!isExpanded()) {
            int indexOf = this.mBubbles.indexOf(bubble);
            this.mBubbles.remove(bubble);
            int insertBubble = insertBubble(0, bubble);
            if (indexOf != insertBubble) {
                packGroup(insertBubble);
                this.mStateChange.orderChanged = true;
            }
            setSelectedBubbleInternal((Bubble) this.mBubbles.get(0));
        }
    }

    private void doRemove(String str, int i) {
        for (int i2 = 0; i2 < this.mPendingBubbles.size(); i2++) {
            if (((Bubble) this.mPendingBubbles.get(i2)).getKey().equals(str)) {
                List<Bubble> list = this.mPendingBubbles;
                list.remove(list.get(i2));
            }
        }
        int indexForKey = indexForKey(str);
        if (indexForKey != -1) {
            Bubble bubble = (Bubble) this.mBubbles.get(indexForKey);
            if (this.mBubbles.size() == 1) {
                setExpandedInternal(false);
                setSelectedBubbleInternal(null);
            }
            if (indexForKey < this.mBubbles.size() - 1) {
                this.mStateChange.orderChanged = true;
            }
            this.mBubbles.remove(indexForKey);
            this.mStateChange.bubbleRemoved(bubble, i);
            if (!isExpanded()) {
                this.mStateChange.orderChanged |= repackAll();
            }
            overflowBubble(i, bubble);
            if (Objects.equals(this.mSelectedBubble, bubble)) {
                setSelectedBubbleInternal((Bubble) this.mBubbles.get(Math.min(indexForKey, this.mBubbles.size() - 1)));
            }
            maybeSendDeleteIntent(i, bubble.getEntry());
        }
    }

    /* access modifiers changed from: 0000 */
    public void overflowBubble(int i, Bubble bubble) {
        if (i == 2 || i == 1) {
            this.mOverflowBubbles.add(0, bubble);
            if (this.mOverflowBubbles.size() == this.mMaxOverflowBubbles + 1) {
                List<Bubble> list = this.mOverflowBubbles;
                list.remove(list.size() - 1);
            }
        }
    }

    public void dismissAll(int i) {
        if (!this.mBubbles.isEmpty()) {
            setExpandedInternal(false);
            setSelectedBubbleInternal(null);
            while (!this.mBubbles.isEmpty()) {
                doRemove(((Bubble) this.mBubbles.get(0)).getKey(), i);
            }
            dispatchPendingChanges();
        }
    }

    /* access modifiers changed from: 0000 */
    public void notifyDisplayEmpty(int i) {
        for (Bubble bubble : this.mBubbles) {
            if (bubble.getDisplayId() == i) {
                if (bubble.getExpandedView() != null) {
                    bubble.getExpandedView().notifyDisplayEmpty();
                    return;
                }
                return;
            }
        }
    }

    private void dispatchPendingChanges() {
        if (this.mListener != null && this.mStateChange.anythingChanged()) {
            this.mListener.applyUpdate(this.mStateChange);
        }
        this.mStateChange = new Update(this.mBubbles, this.mOverflowBubbles);
    }

    private void setSelectedBubbleInternal(Bubble bubble) {
        if (!Objects.equals(bubble, this.mSelectedBubble)) {
            if (bubble == null || this.mBubbles.contains(bubble) || this.mOverflowBubbles.contains(bubble)) {
                if (this.mExpanded && bubble != null) {
                    bubble.markAsAccessedAt(this.mTimeSource.currentTimeMillis());
                }
                this.mSelectedBubble = bubble;
                Update update = this.mStateChange;
                update.selectedBubble = bubble;
                update.selectionChanged = true;
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot select bubble which doesn't exist! (");
            sb.append(bubble);
            sb.append(") bubbles=");
            sb.append(this.mBubbles);
            Log.e("Bubbles", sb.toString());
        }
    }

    private void setExpandedInternal(boolean z) {
        if (this.mExpanded != z) {
            if (z) {
                String str = "Bubbles";
                if (this.mBubbles.isEmpty()) {
                    Log.e(str, "Attempt to expand stack when empty!");
                    return;
                }
                Bubble bubble = this.mSelectedBubble;
                if (bubble == null) {
                    Log.e(str, "Attempt to expand stack without selected bubble!");
                    return;
                }
                bubble.markAsAccessedAt(this.mTimeSource.currentTimeMillis());
                this.mStateChange.orderChanged |= repackAll();
            } else if (!this.mBubbles.isEmpty()) {
                this.mStateChange.orderChanged |= repackAll();
                if (this.mBubbles.indexOf(this.mSelectedBubble) > 0) {
                    if (this.mSelectedBubble.isOngoing() || !((Bubble) this.mBubbles.get(0)).isOngoing()) {
                        this.mBubbles.remove(this.mSelectedBubble);
                        this.mBubbles.add(0, this.mSelectedBubble);
                        Update update = this.mStateChange;
                        update.orderChanged = packGroup(0) | update.orderChanged;
                    } else {
                        setSelectedBubbleInternal((Bubble) this.mBubbles.get(0));
                    }
                }
            }
            this.mExpanded = z;
            Update update2 = this.mStateChange;
            update2.expanded = z;
            update2.expandedChanged = true;
        }
    }

    /* access modifiers changed from: private */
    public static long sortKey(Bubble bubble) {
        long lastUpdateTime = bubble.getLastUpdateTime();
        return bubble.isOngoing() ? lastUpdateTime | 4611686018427387904L : lastUpdateTime;
    }

    private int insertBubble(int i, Bubble bubble) {
        long sortKey = sortKey(bubble);
        String str = null;
        while (i < this.mBubbles.size()) {
            Bubble bubble2 = (Bubble) this.mBubbles.get(i);
            String groupId = bubble2.getGroupId();
            if (!(!groupId.equals(str)) || sortKey <= sortKey(bubble2)) {
                i++;
                str = groupId;
            } else {
                this.mBubbles.add(i, bubble);
                return i;
            }
        }
        this.mBubbles.add(bubble);
        return this.mBubbles.size() - 1;
    }

    private boolean hasBubbleWithGroupId(String str) {
        return this.mBubbles.stream().anyMatch(new Predicate(str) {
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ((Bubble) obj).getGroupId().equals(this.f$0);
            }
        });
    }

    private int findFirstIndexForGroup(String str) {
        for (int i = 0; i < this.mBubbles.size(); i++) {
            if (((Bubble) this.mBubbles.get(i)).getGroupId().equals(str)) {
                return i;
            }
        }
        return 0;
    }

    private boolean packGroup(int i) {
        String groupId = ((Bubble) this.mBubbles.get(i)).getGroupId();
        ArrayList arrayList = new ArrayList();
        for (int size = this.mBubbles.size() - 1; size > i; size--) {
            if (((Bubble) this.mBubbles.get(size)).getGroupId().equals(groupId)) {
                arrayList.add(0, (Bubble) this.mBubbles.get(size));
            }
        }
        if (arrayList.isEmpty()) {
            return false;
        }
        this.mBubbles.removeAll(arrayList);
        this.mBubbles.addAll(i + 1, arrayList);
        return true;
    }

    private boolean repackAll() {
        if (this.mBubbles.isEmpty()) {
            return false;
        }
        HashMap hashMap = new HashMap();
        for (Bubble bubble : this.mBubbles) {
            long longValue = ((Long) hashMap.getOrDefault(bubble.getGroupId(), Long.valueOf(0))).longValue();
            long sortKey = sortKey(bubble);
            if (sortKey > longValue) {
                hashMap.put(bubble.getGroupId(), Long.valueOf(sortKey));
            }
        }
        List<String> list = (List) hashMap.entrySet().stream().sorted(GROUPS_BY_MAX_SORT_KEY_DESCENDING).map($$Lambda$CSz_ibwXhtkKNl72Q8tR5oBgkWk.INSTANCE).collect(Collectors.toList());
        ArrayList arrayList = new ArrayList(this.mBubbles.size());
        for (String r3 : list) {
            this.mBubbles.stream().filter(new Predicate(r3) {
                public final /* synthetic */ String f$0;

                {
                    this.f$0 = r1;
                }

                public final boolean test(Object obj) {
                    return ((Bubble) obj).getGroupId().equals(this.f$0);
                }
            }).sorted(BUBBLES_BY_SORT_KEY_DESCENDING).forEachOrdered(new Consumer(arrayList) {
                public final /* synthetic */ List f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    this.f$0.add((Bubble) obj);
                }
            });
        }
        if (arrayList.equals(this.mBubbles)) {
            return false;
        }
        this.mBubbles.clear();
        this.mBubbles.addAll(arrayList);
        return true;
    }

    private void maybeSendDeleteIntent(int i, NotificationEntry notificationEntry) {
        if (i == 1) {
            BubbleMetadata bubbleMetadata = notificationEntry.getBubbleMetadata();
            PendingIntent deleteIntent = bubbleMetadata != null ? bubbleMetadata.getDeleteIntent() : null;
            if (deleteIntent != null) {
                try {
                    deleteIntent.send();
                } catch (CanceledException unused) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed to send delete intent for bubble with key: ");
                    sb.append(notificationEntry.getKey());
                    Log.w("Bubbles", sb.toString());
                }
            }
        }
    }

    private int indexForKey(String str) {
        for (int i = 0; i < this.mBubbles.size(); i++) {
            if (((Bubble) this.mBubbles.get(i)).getKey().equals(str)) {
                return i;
            }
        }
        return -1;
    }

    @VisibleForTesting(visibility = Visibility.PRIVATE)
    public List<Bubble> getBubbles() {
        return Collections.unmodifiableList(this.mBubbles);
    }

    @VisibleForTesting(visibility = Visibility.PRIVATE)
    public List<Bubble> getOverflowBubbles() {
        return Collections.unmodifiableList(this.mOverflowBubbles);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting(visibility = Visibility.PRIVATE)
    public Bubble getBubbleWithKey(String str) {
        for (int i = 0; i < this.mBubbles.size(); i++) {
            Bubble bubble = (Bubble) this.mBubbles.get(i);
            if (bubble.getKey().equals(str)) {
                return bubble;
            }
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting(visibility = Visibility.PRIVATE)
    public void setTimeSource(TimeSource timeSource) {
        this.mTimeSource = timeSource;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print("selected: ");
        Bubble bubble = this.mSelectedBubble;
        printWriter.println(bubble != null ? bubble.getKey() : "null");
        printWriter.print("expanded: ");
        printWriter.println(this.mExpanded);
        printWriter.print("count:    ");
        printWriter.println(this.mBubbles.size());
        for (Bubble dump : this.mBubbles) {
            dump.dump(fileDescriptor, printWriter, strArr);
        }
        printWriter.print("summaryKeys: ");
        printWriter.println(this.mSuppressedGroupKeys.size());
        for (String str : this.mSuppressedGroupKeys.keySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append("   suppressing: ");
            sb.append(str);
            printWriter.println(sb.toString());
        }
    }
}
