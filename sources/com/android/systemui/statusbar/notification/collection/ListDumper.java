package com.android.systemui.statusbar.notification.collection;

import com.android.systemui.statusbar.notification.collection.NotificationEntry.DismissState;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifDismissInterceptor;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender;
import java.util.Arrays;
import java.util.List;

public class ListDumper {
    public static String dumpTree(List<ListEntry> list, boolean z, String str) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append("  ");
        String sb3 = sb2.toString();
        for (int i = 0; i < list.size(); i++) {
            ListEntry listEntry = (ListEntry) list.get(i);
            dumpEntry(listEntry, Integer.toString(i), str, sb, true, z);
            if (listEntry instanceof GroupEntry) {
                List children = ((GroupEntry) listEntry).getChildren();
                for (int i2 = 0; i2 < children.size(); i2++) {
                    ListEntry listEntry2 = (ListEntry) children.get(i2);
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append(Integer.toString(i));
                    sb4.append(".");
                    sb4.append(Integer.toString(i2));
                    dumpEntry(listEntry2, sb4.toString(), sb3, sb, true, z);
                }
            }
        }
        return sb.toString();
    }

    public static String dumpList(List<NotificationEntry> list, boolean z, String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            dumpEntry((ListEntry) list.get(i), Integer.toString(i), str, sb, false, z);
        }
        return sb.toString();
    }

    private static void dumpEntry(ListEntry listEntry, String str, String str2, StringBuilder sb, boolean z, boolean z2) {
        sb.append(str2);
        sb.append("[");
        sb.append(str);
        sb.append("] ");
        sb.append(listEntry.getKey());
        if (z) {
            sb.append(" (parent=");
            sb.append(listEntry.getParent() != null ? listEntry.getParent().getKey() : null);
            sb.append(")");
        }
        if (listEntry.mNotifSection != null) {
            sb.append(" sectionIndex=");
            sb.append(listEntry.getSection());
            sb.append(" sectionName=");
            sb.append(listEntry.mNotifSection.getName());
        }
        if (z2) {
            NotificationEntry representativeEntry = listEntry.getRepresentativeEntry();
            StringBuilder sb2 = new StringBuilder();
            String str3 = " ";
            if (!representativeEntry.mLifetimeExtenders.isEmpty()) {
                int size = representativeEntry.mLifetimeExtenders.size();
                String[] strArr = new String[size];
                for (int i = 0; i < size; i++) {
                    strArr[i] = ((NotifLifetimeExtender) representativeEntry.mLifetimeExtenders.get(i)).getName();
                }
                sb2.append("lifetimeExtenders=");
                sb2.append(Arrays.toString(strArr));
                sb2.append(str3);
            }
            if (!representativeEntry.mDismissInterceptors.isEmpty()) {
                int size2 = representativeEntry.mDismissInterceptors.size();
                String[] strArr2 = new String[size2];
                for (int i2 = 0; i2 < size2; i2++) {
                    strArr2[i2] = ((NotifDismissInterceptor) representativeEntry.mDismissInterceptors.get(i2)).getName();
                }
                sb2.append("dismissInterceptors=");
                sb2.append(Arrays.toString(strArr2));
                sb2.append(str3);
            }
            if (representativeEntry.mExcludingFilter != null) {
                sb2.append("filter=");
                sb2.append(representativeEntry.mExcludingFilter);
                sb2.append(str3);
            }
            if (representativeEntry.mNotifPromoter != null) {
                sb2.append("promoter=");
                sb2.append(representativeEntry.mNotifPromoter);
                sb2.append(str3);
            }
            if (representativeEntry.mCancellationReason != -1) {
                sb2.append("cancellationReason=");
                sb2.append(representativeEntry.mCancellationReason);
                sb2.append(str3);
            }
            if (representativeEntry.getDismissState() != DismissState.NOT_DISMISSED) {
                sb2.append("dismissState=");
                sb2.append(representativeEntry.getDismissState());
                sb2.append(str3);
            }
            String sb3 = sb2.toString();
            if (!sb3.isEmpty()) {
                sb.append("\n\t");
                sb.append(str2);
                sb.append(sb3);
            }
        }
        sb.append("\n");
    }
}
