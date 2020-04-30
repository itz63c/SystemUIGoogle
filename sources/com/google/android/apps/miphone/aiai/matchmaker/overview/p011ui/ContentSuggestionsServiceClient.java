package com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui;

import android.app.Notification.Action;
import android.app.Notification.Action.Builder;
import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.EntitiesData;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$ScreenshotOp;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$ScreenshotOpStatus;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.InteractionContextParcelables$InteractionContext;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.ParserParcelables$ParsedViewHierarchy;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.SuggestParcelables$Action;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.SuggestParcelables$ActionGroup;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.SuggestParcelables$Entities;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.SuggestParcelables$Entity;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.SuggestParcelables$InteractionType;
import com.google.android.apps.miphone.aiai.matchmaker.overview.common.BundleUtils;
import com.google.android.apps.miphone.aiai.matchmaker.overview.common.BundleUtils.FeedbackBundle;
import com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.ContentSuggestionsServiceWrapper.BundleCallback;
import com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.utils.LogUtils;
import com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;

/* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.ContentSuggestionsServiceClient */
public class ContentSuggestionsServiceClient {
    private static final Random random = new Random();
    private final boolean isAiAiVersionSupported;
    /* access modifiers changed from: private */
    public final ContentSuggestionsServiceWrapper serviceWrapper;

    public ContentSuggestionsServiceClient(Context context, Executor executor, Handler handler) {
        this.serviceWrapper = SuggestController.create(context, context, executor, handler).getWrapper();
        this.isAiAiVersionSupported = isVersionCodeSupported(context);
    }

    private static boolean isVersionCodeSupported(Context context) {
        try {
            if (context.getPackageManager().getPackageInfo("com.google.android.as", 0).getLongVersionCode() >= 660780) {
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            LogUtils.m118e("Error obtaining package info: ", e);
            return false;
        }
    }

    @WorkerThread
    public void provideScreenshotActions(Bitmap bitmap, String str, String str2, boolean z, BundleCallback bundleCallback) {
        if (!this.isAiAiVersionSupported) {
            bundleCallback.onResult(Bundle.EMPTY);
            return;
        }
        BundleCallback bundleCallback2 = bundleCallback;
        int nextInt = random.nextInt();
        long currentTimeMillis = System.currentTimeMillis();
        String str3 = str;
        String str4 = str2;
        Bundle obtainContextImageBundle = BundleUtils.obtainContextImageBundle(true, str, str4, currentTimeMillis);
        Bitmap bitmap2 = bitmap;
        obtainContextImageBundle.putParcelable("android.contentsuggestions.extra.BITMAP", bitmap);
        InteractionContextParcelables$InteractionContext create = InteractionContextParcelables$InteractionContext.create();
        create.interactionType = SuggestParcelables$InteractionType.SCREENSHOT_NOTIFICATION;
        create.disallowCopyPaste = false;
        create.versionCode = 1;
        create.isPrimaryTask = true;
        ContentSuggestionsServiceWrapper contentSuggestionsServiceWrapper = this.serviceWrapper;
        ContentSuggestionsServiceClient$$Lambda$0 contentSuggestionsServiceClient$$Lambda$0 = new ContentSuggestionsServiceClient$$Lambda$0(this, nextInt, obtainContextImageBundle, str3, str4, currentTimeMillis, create, z, bundleCallback2);
        contentSuggestionsServiceWrapper.connectAndRunAsync(contentSuggestionsServiceClient$$Lambda$0);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: lambda$provideScreenshotActions$0$ContentSuggestionsServiceClient */
    public final /* synthetic */ void mo20174x7d6d7325(int i, Bundle bundle, String str, String str2, long j, InteractionContextParcelables$InteractionContext interactionContextParcelables$InteractionContext, boolean z, BundleCallback bundleCallback) {
        int i2 = i;
        Bundle bundle2 = bundle;
        this.serviceWrapper.processContextImage(i, null, bundle);
        final String str3 = str2;
        final int i3 = i;
        final long j2 = j;
        final InteractionContextParcelables$InteractionContext interactionContextParcelables$InteractionContext2 = interactionContextParcelables$InteractionContext;
        Bundle createSelectionsRequest = BundleUtils.createSelectionsRequest(str, str3, i3, j2, interactionContextParcelables$InteractionContext2, new Bundle(), ParserParcelables$ParsedViewHierarchy.create());
        createSelectionsRequest.putBoolean("IsManagedProfile", z);
        ContentSuggestionsServiceWrapper contentSuggestionsServiceWrapper = this.serviceWrapper;
        final String str4 = str;
        final BundleCallback bundleCallback2 = bundleCallback;
        C18441 r0 = new BundleCallback() {
            public void onResult(Bundle bundle) {
                try {
                    Bundle createClassificationsRequest = BundleUtils.createClassificationsRequest(str4, str3, i3, j2, new Bundle(), BundleUtils.extractContentsParcelable(bundle));
                    createClassificationsRequest.putParcelable("InteractionContext", interactionContextParcelables$InteractionContext2);
                    ContentSuggestionsServiceClient.this.serviceWrapper.classifyContentSelections(createClassificationsRequest, new BundleCallback() {
                        public void onResult(Bundle bundle) {
                            try {
                                EntitiesData extractEntitiesParcelable = BundleUtils.extractEntitiesParcelable(bundle);
                                SuggestParcelables$Entities create = extractEntitiesParcelable.entities() == null ? SuggestParcelables$Entities.create() : extractEntitiesParcelable.entities();
                                ArrayList arrayList = new ArrayList();
                                for (SuggestParcelables$Entity access$000 : create.entities) {
                                    Action access$0002 = ContentSuggestionsServiceClient.generateNotificationAction(access$000, extractEntitiesParcelable);
                                    if (access$0002 != null) {
                                        arrayList.add(access$0002);
                                    }
                                }
                                bundleCallback2.onResult(BundleUtils.createScreenshotActionsResponse(arrayList));
                            } catch (Throwable th) {
                                LogUtils.m118e("Failed to handle classification result while generating smart actions for screenshot notification", th);
                                bundleCallback2.onResult(Bundle.EMPTY);
                            }
                        }
                    });
                } catch (Throwable th) {
                    LogUtils.m118e("Failed to handle selections response while generating smart actions for screenshot notification", th);
                    bundleCallback2.onResult(Bundle.EMPTY);
                }
            }
        };
        contentSuggestionsServiceWrapper.suggestContentSelections(i, createSelectionsRequest, r0);
    }

    public void notifyOp(String str, FeedbackParcelables$ScreenshotOp feedbackParcelables$ScreenshotOp, FeedbackParcelables$ScreenshotOpStatus feedbackParcelables$ScreenshotOpStatus, long j) {
        ContentSuggestionsServiceWrapper contentSuggestionsServiceWrapper = this.serviceWrapper;
        ContentSuggestionsServiceClient$$Lambda$1 contentSuggestionsServiceClient$$Lambda$1 = new ContentSuggestionsServiceClient$$Lambda$1(this, str, feedbackParcelables$ScreenshotOp, feedbackParcelables$ScreenshotOpStatus, j);
        contentSuggestionsServiceWrapper.connectAndRunAsync(contentSuggestionsServiceClient$$Lambda$1);
    }

    /* access modifiers changed from: 0000 */
    public final /* synthetic */ void lambda$notifyOp$1$ContentSuggestionsServiceClient(String str, FeedbackParcelables$ScreenshotOp feedbackParcelables$ScreenshotOp, FeedbackParcelables$ScreenshotOpStatus feedbackParcelables$ScreenshotOpStatus, long j) {
        try {
            FeedbackDataBuilder newBuilder = FeedbackDataBuilder.newBuilder(str);
            newBuilder.addScreenshotOpFeedback(str, feedbackParcelables$ScreenshotOp, feedbackParcelables$ScreenshotOpStatus, j);
            this.serviceWrapper.notifyInteraction(str, FeedbackBundle.create(newBuilder.build()).createBundle());
        } catch (Throwable th) {
            LogUtils.m117d(String.format("Error calling notifyInteration %s", new Object[]{th}));
        }
    }

    public void notifyAction(String str, String str2, boolean z) {
        this.serviceWrapper.connectAndRunAsync(new ContentSuggestionsServiceClient$$Lambda$2(this, str, str2, z));
    }

    /* access modifiers changed from: 0000 */
    public final /* synthetic */ void lambda$notifyAction$2$ContentSuggestionsServiceClient(String str, String str2, boolean z) {
        try {
            FeedbackDataBuilder newBuilder = FeedbackDataBuilder.newBuilder(str);
            newBuilder.addScreenshotActionFeedback(str, str2, z);
            this.serviceWrapper.notifyInteraction(str, FeedbackBundle.create(newBuilder.build()).createBundle());
        } catch (Throwable th) {
            LogUtils.m117d(String.format("Error calling notifyInteration %s", new Object[]{th}));
        }
    }

    /* access modifiers changed from: private */
    @Nullable
    public static Action generateNotificationAction(SuggestParcelables$Entity suggestParcelables$Entity, EntitiesData entitiesData) {
        List<SuggestParcelables$ActionGroup> list = suggestParcelables$Entity.actions;
        if (list != null && !list.isEmpty()) {
            SuggestParcelables$Action suggestParcelables$Action = ((SuggestParcelables$ActionGroup) suggestParcelables$Entity.actions.get(0)).mainAction;
            if (suggestParcelables$Action != null) {
                String str = suggestParcelables$Action.f96id;
                if (str != null) {
                    Bitmap bitmap = entitiesData.getBitmap(str);
                    Utils.checkNotNull(entitiesData);
                    PendingIntent pendingIntent = entitiesData.getPendingIntent(suggestParcelables$Action.f96id);
                    if (pendingIntent == null || bitmap == null) {
                        LogUtils.m117d("Malformed EntitiesData: Expected icon bitmap and intent");
                        return null;
                    }
                    String firstNonEmptyString = getFirstNonEmptyString(suggestParcelables$Action.displayName, suggestParcelables$Action.fullDisplayName, suggestParcelables$Entity.searchQueryHint);
                    if (firstNonEmptyString == null) {
                        LogUtils.m117d("Title expected.");
                        return null;
                    }
                    return createNotificationActionFromRemoteAction(new RemoteAction(Icon.createWithBitmap(bitmap), firstNonEmptyString, firstNonEmptyString, pendingIntent), TextUtils.isEmpty(suggestParcelables$Entity.searchQueryHint) ? "Smart Action" : suggestParcelables$Entity.searchQueryHint, 1.0f);
                }
            }
            LogUtils.m117d("Malformed mainAction: Expected id");
        }
        return null;
    }

    @Nullable
    private static String getFirstNonEmptyString(String... strArr) {
        for (String str : strArr) {
            if (!TextUtils.isEmpty(str)) {
                return str;
            }
        }
        return null;
    }

    private static Action createNotificationActionFromRemoteAction(RemoteAction remoteAction, String str, float f) {
        Icon icon = remoteAction.shouldShowIcon() ? remoteAction.getIcon() : null;
        Bundle bundle = new Bundle();
        bundle.putString("action_type", str);
        bundle.putFloat("action_score", f);
        return new Builder(icon, remoteAction.getTitle(), remoteAction.getActionIntent()).setContextual(true).addExtras(bundle).build();
    }
}
