package com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui;

import android.content.Context;
import android.os.Handler;
import com.google.android.apps.miphone.aiai.matchmaker.overview.p011ui.SuggestController.Factory;
import java.util.concurrent.Executor;

/* renamed from: com.google.android.apps.miphone.aiai.matchmaker.overview.ui.SuggestController$$Lambda$3 */
final /* synthetic */ class SuggestController$$Lambda$3 implements Factory {
    static final Factory $instance = new SuggestController$$Lambda$3();

    private SuggestController$$Lambda$3() {
    }

    public ContentSuggestionsServiceWrapper create(Context context, Executor executor, Handler handler) {
        return SuggestController.lambda$static$0$SuggestController(context, executor, handler);
    }
}
