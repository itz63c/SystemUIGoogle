package com.android.systemui.shared.recents.model;

import android.app.ActivityManager.TaskSnapshot;
import android.graphics.Bitmap;
import android.graphics.Rect;

public class ThumbnailData {
    public final Bitmap thumbnail;

    public ThumbnailData(TaskSnapshot taskSnapshot) {
        this.thumbnail = Bitmap.wrapHardwareBuffer(taskSnapshot.getSnapshot(), taskSnapshot.getColorSpace());
        new Rect(taskSnapshot.getContentInsets());
        taskSnapshot.getOrientation();
        taskSnapshot.getRotation();
        taskSnapshot.isLowResolution();
        this.thumbnail.getWidth();
        int i = taskSnapshot.getTaskSize().x;
        taskSnapshot.isRealSnapshot();
        taskSnapshot.isTranslucent();
        taskSnapshot.getWindowingMode();
        taskSnapshot.getSystemUiVisibility();
        taskSnapshot.getId();
    }
}
