package com.android.systemui.statusbar.notification.collection;

import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifSection;

public abstract class ListEntry {
    int mFirstAddedIteration = -1;
    private final String mKey;
    NotifSection mNotifSection;
    private GroupEntry mParent;
    private GroupEntry mPreviousParent;
    private int mSection = -1;

    public abstract NotificationEntry getRepresentativeEntry();

    ListEntry(String str) {
        this.mKey = str;
    }

    public String getKey() {
        return this.mKey;
    }

    public GroupEntry getParent() {
        return this.mParent;
    }

    /* access modifiers changed from: 0000 */
    public void setParent(GroupEntry groupEntry) {
        this.mParent = groupEntry;
    }

    public GroupEntry getPreviousParent() {
        return this.mPreviousParent;
    }

    /* access modifiers changed from: 0000 */
    public void setPreviousParent(GroupEntry groupEntry) {
        this.mPreviousParent = groupEntry;
    }

    public int getSection() {
        return this.mSection;
    }

    /* access modifiers changed from: 0000 */
    public void setSection(int i) {
        this.mSection = i;
    }
}
