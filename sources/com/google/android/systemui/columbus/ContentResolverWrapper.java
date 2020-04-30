package com.google.android.systemui.columbus;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ContentResolverWrapper.kt */
public class ContentResolverWrapper {
    private final ContentResolver contentResolver;

    public ContentResolverWrapper(Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.contentResolver = context.getContentResolver();
    }

    public void registerContentObserver(Uri uri, boolean z, ContentObserver contentObserver, int i) {
        Intrinsics.checkParameterIsNotNull(uri, "uri");
        Intrinsics.checkParameterIsNotNull(contentObserver, "observer");
        this.contentResolver.registerContentObserver(uri, z, contentObserver, i);
    }

    public void unregisterContentObserver(ContentObserver contentObserver) {
        Intrinsics.checkParameterIsNotNull(contentObserver, "observer");
        this.contentResolver.unregisterContentObserver(contentObserver);
    }
}
