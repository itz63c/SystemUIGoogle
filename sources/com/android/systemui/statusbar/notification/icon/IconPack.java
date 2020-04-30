package com.android.systemui.statusbar.notification.icon;

import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.statusbar.StatusBarIconView;

public final class IconPack {
    private final StatusBarIconView mAodIcon;
    private final boolean mAreIconsAvailable;
    private final StatusBarIconView mCenteredIcon;
    private boolean mIsImportantConversation;
    private StatusBarIcon mPeopleAvatarDescriptor;
    private final StatusBarIconView mShelfIcon;
    private StatusBarIcon mSmallIconDescriptor;
    private final StatusBarIconView mStatusBarIcon;

    public static IconPack buildEmptyPack(IconPack iconPack) {
        IconPack iconPack2 = new IconPack(false, null, null, null, null, iconPack);
        return iconPack2;
    }

    public static IconPack buildPack(StatusBarIconView statusBarIconView, StatusBarIconView statusBarIconView2, StatusBarIconView statusBarIconView3, StatusBarIconView statusBarIconView4, IconPack iconPack) {
        IconPack iconPack2 = new IconPack(true, statusBarIconView, statusBarIconView2, statusBarIconView3, statusBarIconView4, iconPack);
        return iconPack2;
    }

    private IconPack(boolean z, StatusBarIconView statusBarIconView, StatusBarIconView statusBarIconView2, StatusBarIconView statusBarIconView3, StatusBarIconView statusBarIconView4, IconPack iconPack) {
        this.mAreIconsAvailable = z;
        this.mStatusBarIcon = statusBarIconView;
        this.mShelfIcon = statusBarIconView2;
        this.mCenteredIcon = statusBarIconView4;
        this.mAodIcon = statusBarIconView3;
        if (iconPack != null) {
            this.mIsImportantConversation = iconPack.mIsImportantConversation;
        }
    }

    public StatusBarIconView getStatusBarIcon() {
        return this.mStatusBarIcon;
    }

    public StatusBarIconView getShelfIcon() {
        return this.mShelfIcon;
    }

    public StatusBarIconView getCenteredIcon() {
        return this.mCenteredIcon;
    }

    public StatusBarIconView getAodIcon() {
        return this.mAodIcon;
    }

    /* access modifiers changed from: 0000 */
    public StatusBarIcon getSmallIconDescriptor() {
        return this.mSmallIconDescriptor;
    }

    /* access modifiers changed from: 0000 */
    public void setSmallIconDescriptor(StatusBarIcon statusBarIcon) {
        this.mSmallIconDescriptor = statusBarIcon;
    }

    /* access modifiers changed from: 0000 */
    public StatusBarIcon getPeopleAvatarDescriptor() {
        return this.mPeopleAvatarDescriptor;
    }

    /* access modifiers changed from: 0000 */
    public void setPeopleAvatarDescriptor(StatusBarIcon statusBarIcon) {
        this.mPeopleAvatarDescriptor = statusBarIcon;
    }

    /* access modifiers changed from: 0000 */
    public boolean isImportantConversation() {
        return this.mIsImportantConversation;
    }

    /* access modifiers changed from: 0000 */
    public void setImportantConversation(boolean z) {
        this.mIsImportantConversation = z;
    }

    public boolean getAreIconsAvailable() {
        return this.mAreIconsAvailable;
    }
}
