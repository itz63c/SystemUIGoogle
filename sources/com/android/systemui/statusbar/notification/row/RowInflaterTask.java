package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater.OnInflateFinishedListener;
import com.android.systemui.C2013R$layout;
import com.android.systemui.statusbar.InflationTask;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public class RowInflaterTask implements InflationTask, OnInflateFinishedListener {
    private boolean mCancelled;
    private NotificationEntry mEntry;
    private Throwable mInflateOrigin;
    private RowInflationFinishedListener mListener;

    public interface RowInflationFinishedListener {
        void onInflationFinished(ExpandableNotificationRow expandableNotificationRow);
    }

    public void inflate(Context context, ViewGroup viewGroup, NotificationEntry notificationEntry, RowInflationFinishedListener rowInflationFinishedListener) {
        this.mInflateOrigin = new Throwable("inflate requested here");
        this.mListener = rowInflationFinishedListener;
        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        this.mEntry = notificationEntry;
        notificationEntry.setInflationTask(this);
        asyncLayoutInflater.inflate(C2013R$layout.status_bar_notification_row, viewGroup, this);
    }

    public void abort() {
        this.mCancelled = true;
    }

    public void onInflateFinished(View view, int i, ViewGroup viewGroup) {
        if (!this.mCancelled) {
            try {
                this.mEntry.onInflationTaskFinished();
                this.mListener.onInflationFinished((ExpandableNotificationRow) view);
            } catch (Throwable th) {
                if (this.mInflateOrigin != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Error in inflation finished listener: ");
                    sb.append(th);
                    Log.e("RowInflaterTask", sb.toString(), this.mInflateOrigin);
                    th.addSuppressed(this.mInflateOrigin);
                }
                throw th;
            }
        }
    }
}
