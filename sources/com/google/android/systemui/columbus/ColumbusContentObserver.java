package com.google.android.systemui.columbus;

import android.app.IActivityManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ColumbusContentObserver.kt */
public final class ColumbusContentObserver extends ContentObserver {
    private final IActivityManager activityManagerService;
    /* access modifiers changed from: private */
    public final Function1<Uri, Unit> callback;
    private final ContentResolverWrapper contentResolver;
    /* access modifiers changed from: private */
    public final Uri settingsUri;
    private final ColumbusContentObserver$userSwitchCallback$1 userSwitchCallback;

    /* compiled from: ColumbusContentObserver.kt */
    public static final class Factory {
        private final IActivityManager activityManagerService;
        private final ContentResolverWrapper contentResolver;

        public Factory(ContentResolverWrapper contentResolverWrapper, IActivityManager iActivityManager) {
            Intrinsics.checkParameterIsNotNull(contentResolverWrapper, "contentResolver");
            Intrinsics.checkParameterIsNotNull(iActivityManager, "activityManagerService");
            this.contentResolver = contentResolverWrapper;
            this.activityManagerService = iActivityManager;
        }

        public final ColumbusContentObserver create(Uri uri, Function1<? super Uri, Unit> function1) {
            Intrinsics.checkParameterIsNotNull(uri, "settingsUri");
            Intrinsics.checkParameterIsNotNull(function1, "callback");
            ColumbusContentObserver columbusContentObserver = new ColumbusContentObserver(this.contentResolver, uri, function1, this.activityManagerService, null);
            return columbusContentObserver;
        }
    }

    public /* synthetic */ ColumbusContentObserver(ContentResolverWrapper contentResolverWrapper, Uri uri, Function1 function1, IActivityManager iActivityManager, DefaultConstructorMarker defaultConstructorMarker) {
        this(contentResolverWrapper, uri, function1, iActivityManager);
    }

    private ColumbusContentObserver(ContentResolverWrapper contentResolverWrapper, Uri uri, Function1<? super Uri, Unit> function1, IActivityManager iActivityManager) {
        super(new Handler(Looper.getMainLooper()));
        this.contentResolver = contentResolverWrapper;
        this.settingsUri = uri;
        this.callback = function1;
        this.activityManagerService = iActivityManager;
        this.userSwitchCallback = new ColumbusContentObserver$userSwitchCallback$1(this);
    }

    public final void activate() {
        String str = "Columbus/ColumbusContentObserver";
        updateContentObserver();
        try {
            this.activityManagerService.registerUserSwitchObserver(this.userSwitchCallback, str);
        } catch (RemoteException e) {
            Log.e(str, "Failed to register user switch observer", e);
        }
    }

    public final void deactivate() {
        this.contentResolver.unregisterContentObserver(this);
        try {
            this.activityManagerService.unregisterUserSwitchObserver(this.userSwitchCallback);
        } catch (RemoteException e) {
            Log.e("Columbus/ColumbusContentObserver", "Failed to unregister user switch observer", e);
        }
    }

    /* access modifiers changed from: private */
    public final void updateContentObserver() {
        this.contentResolver.unregisterContentObserver(this);
        this.contentResolver.registerContentObserver(this.settingsUri, false, this, -2);
    }

    public void onChange(boolean z, Uri uri) {
        Intrinsics.checkParameterIsNotNull(uri, "uri");
        this.callback.invoke(uri);
    }
}
