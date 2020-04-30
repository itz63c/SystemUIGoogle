package com.android.systemui.statusbar;

import android.app.Notification.Action;
import android.app.RemoteInput;
import android.graphics.drawable.Icon;
import android.text.TextUtils;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NotificationUiAdjustment {
    public final List<Action> smartActions;
    public final List<CharSequence> smartReplies;

    NotificationUiAdjustment(String str, List<Action> list, List<CharSequence> list2) {
        if (list == null) {
            list = Collections.emptyList();
        }
        this.smartActions = list;
        if (list2 == null) {
            list2 = Collections.emptyList();
        }
        this.smartReplies = list2;
    }

    public static NotificationUiAdjustment extractFromNotificationEntry(NotificationEntry notificationEntry) {
        return new NotificationUiAdjustment(notificationEntry.getKey(), notificationEntry.getSmartActions(), notificationEntry.getSmartReplies());
    }

    public static boolean needReinflate(NotificationUiAdjustment notificationUiAdjustment, NotificationUiAdjustment notificationUiAdjustment2) {
        if (notificationUiAdjustment == notificationUiAdjustment2) {
            return false;
        }
        return areDifferent(notificationUiAdjustment.smartActions, notificationUiAdjustment2.smartActions) || !notificationUiAdjustment2.smartReplies.equals(notificationUiAdjustment.smartReplies);
    }

    public static boolean areDifferent(List<Action> list, List<Action> list2) {
        if (list == list2) {
            return false;
        }
        if (list == null || list2 == null || list.size() != list2.size()) {
            return true;
        }
        for (int i = 0; i < list.size(); i++) {
            Action action = (Action) list.get(i);
            Action action2 = (Action) list2.get(i);
            if (!TextUtils.equals(action.title, action2.title) || areDifferent(action.getIcon(), action2.getIcon()) || !Objects.equals(action.actionIntent, action2.actionIntent) || areDifferent(action.getRemoteInputs(), action2.getRemoteInputs())) {
                return true;
            }
        }
        return false;
    }

    private static boolean areDifferent(Icon icon, Icon icon2) {
        if (icon == icon2) {
            return false;
        }
        if (icon == null || icon2 == null) {
            return true;
        }
        return !icon.sameAs(icon2);
    }

    private static boolean areDifferent(RemoteInput[] remoteInputArr, RemoteInput[] remoteInputArr2) {
        if (remoteInputArr == remoteInputArr2) {
            return false;
        }
        if (remoteInputArr == null || remoteInputArr2 == null || remoteInputArr.length != remoteInputArr2.length) {
            return true;
        }
        for (int i = 0; i < remoteInputArr.length; i++) {
            RemoteInput remoteInput = remoteInputArr[i];
            RemoteInput remoteInput2 = remoteInputArr2[i];
            if (!TextUtils.equals(remoteInput.getLabel(), remoteInput2.getLabel()) || areDifferent(remoteInput.getChoices(), remoteInput2.getChoices())) {
                return true;
            }
        }
        return false;
    }

    private static boolean areDifferent(CharSequence[] charSequenceArr, CharSequence[] charSequenceArr2) {
        if (charSequenceArr == charSequenceArr2) {
            return false;
        }
        if (charSequenceArr == null || charSequenceArr2 == null || charSequenceArr.length != charSequenceArr2.length) {
            return true;
        }
        for (int i = 0; i < charSequenceArr.length; i++) {
            if (!TextUtils.equals(charSequenceArr[i], charSequenceArr2[i])) {
                return true;
            }
        }
        return false;
    }
}
