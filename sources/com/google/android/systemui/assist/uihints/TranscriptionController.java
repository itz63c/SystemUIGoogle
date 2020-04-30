package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.android.systemui.C2011R$id;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.CardInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ChipsInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ClearListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.GreetingInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.TranscriptionInfoListener;
import com.google.android.systemui.assist.uihints.input.TouchActionRegion;
import com.google.android.systemui.assist.uihints.input.TouchInsideRegion;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TranscriptionController implements CardInfoListener, TranscriptionInfoListener, GreetingInfoListener, ChipsInfoListener, ClearListener, ConfigurationListener, TouchActionRegion, TouchInsideRegion {
    private State mCurrentState;
    private final TouchInsideHandler mDefaultOnTap;
    private final FlingVelocityWrapper mFlingVelocity;
    private boolean mHasAccurateBackground;
    private ListenableFuture<Void> mHideFuture;
    private TranscriptionSpaceListener mListener;
    private PendingIntent mOnGreetingTap;
    private PendingIntent mOnTranscriptionTap;
    private final ViewGroup mParent;
    private Runnable mQueuedCompletion;
    private State mQueuedState;
    private boolean mQueuedStateAnimates;
    private Map<State, TranscriptionSpaceView> mViewMap = new HashMap();

    /* renamed from: com.google.android.systemui.assist.uihints.TranscriptionController$1 */
    static /* synthetic */ class C18791 {

        /* renamed from: $SwitchMap$com$google$android$systemui$assist$uihints$TranscriptionController$State */
        static final /* synthetic */ int[] f100x4ecc3d98;

        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        static {
            /*
                com.google.android.systemui.assist.uihints.TranscriptionController$State[] r0 = com.google.android.systemui.assist.uihints.TranscriptionController.State.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f100x4ecc3d98 = r0
                com.google.android.systemui.assist.uihints.TranscriptionController$State r1 = com.google.android.systemui.assist.uihints.TranscriptionController.State.TRANSCRIPTION     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f100x4ecc3d98     // Catch:{ NoSuchFieldError -> 0x001d }
                com.google.android.systemui.assist.uihints.TranscriptionController$State r1 = com.google.android.systemui.assist.uihints.TranscriptionController.State.GREETING     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f100x4ecc3d98     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.google.android.systemui.assist.uihints.TranscriptionController$State r1 = com.google.android.systemui.assist.uihints.TranscriptionController.State.CHIPS     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f100x4ecc3d98     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.google.android.systemui.assist.uihints.TranscriptionController$State r1 = com.google.android.systemui.assist.uihints.TranscriptionController.State.NONE     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.assist.uihints.TranscriptionController.C18791.<clinit>():void");
        }
    }

    public enum State {
        TRANSCRIPTION,
        GREETING,
        CHIPS,
        NONE
    }

    public interface TranscriptionSpaceListener {
        void onStateChanged(State state, State state2);
    }

    interface TranscriptionSpaceView {
        void getHitRect(Rect rect);

        ListenableFuture<Void> hide(boolean z);

        void onFontSizeChanged();

        void setCardVisible(boolean z) {
        }

        void setHasDarkBackground(boolean z);
    }

    TranscriptionController(ViewGroup viewGroup, TouchInsideHandler touchInsideHandler, FlingVelocityWrapper flingVelocityWrapper, ConfigurationController configurationController) {
        State state = State.NONE;
        this.mCurrentState = state;
        this.mHasAccurateBackground = false;
        this.mQueuedStateAnimates = false;
        this.mQueuedState = state;
        this.mParent = viewGroup;
        this.mDefaultOnTap = touchInsideHandler;
        this.mFlingVelocity = flingVelocityWrapper;
        setUpViews();
        configurationController.addCallback(this);
    }

    public void onCardInfo(boolean z, int i, boolean z2, boolean z3) {
        setCardVisible(z);
    }

    public void onTranscriptionInfo(String str, PendingIntent pendingIntent, int i) {
        setTranscription(str, pendingIntent);
        setTranscriptionColor(i);
    }

    public void onGreetingInfo(String str, PendingIntent pendingIntent) {
        if (!TextUtils.isEmpty(str)) {
            this.mOnGreetingTap = pendingIntent;
            Optional consumeVelocity = this.mFlingVelocity.consumeVelocity();
            if (this.mCurrentState != State.NONE || !consumeVelocity.isPresent()) {
                setQueuedState(State.GREETING, false, new Runnable(str) {
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        TranscriptionController.this.lambda$onGreetingInfo$1$TranscriptionController(this.f$1);
                    }
                });
            } else {
                setQueuedState(State.GREETING, false, new Runnable(str, consumeVelocity) {
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ Optional f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        TranscriptionController.this.lambda$onGreetingInfo$0$TranscriptionController(this.f$1, this.f$2);
                    }
                });
            }
            maybeSetState();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onGreetingInfo$0 */
    public /* synthetic */ void lambda$onGreetingInfo$0$TranscriptionController(String str, Optional optional) {
        ((GreetingView) this.mViewMap.get(State.GREETING)).setGreetingAnimated(str, ((Float) optional.get()).floatValue());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onGreetingInfo$1 */
    public /* synthetic */ void lambda$onGreetingInfo$1$TranscriptionController(String str) {
        ((GreetingView) this.mViewMap.get(State.GREETING)).setGreeting(str);
    }

    public void onChipsInfo(List<Bundle> list) {
        if (list == null || list.size() == 0) {
            Log.e("TranscriptionController", "Null or empty chip list received; clearing transcription space");
            onClear(false);
            return;
        }
        Optional consumeVelocity = this.mFlingVelocity.consumeVelocity();
        if (this.mCurrentState != State.NONE || !consumeVelocity.isPresent()) {
            State state = this.mCurrentState;
            if (state == State.GREETING || state == State.TRANSCRIPTION) {
                setQueuedState(State.CHIPS, false, new Runnable(list) {
                    public final /* synthetic */ List f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        TranscriptionController.this.lambda$onChipsInfo$3$TranscriptionController(this.f$1);
                    }
                });
            } else {
                setQueuedState(State.CHIPS, false, new Runnable(list) {
                    public final /* synthetic */ List f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        TranscriptionController.this.lambda$onChipsInfo$4$TranscriptionController(this.f$1);
                    }
                });
            }
        } else {
            setQueuedState(State.CHIPS, false, new Runnable(list, consumeVelocity) {
                public final /* synthetic */ List f$1;
                public final /* synthetic */ Optional f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    TranscriptionController.this.lambda$onChipsInfo$2$TranscriptionController(this.f$1, this.f$2);
                }
            });
        }
        maybeSetState();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onChipsInfo$2 */
    public /* synthetic */ void lambda$onChipsInfo$2$TranscriptionController(List list, Optional optional) {
        ((ChipsContainer) this.mViewMap.get(State.CHIPS)).setChipsAnimatedBounce(list, ((Float) optional.get()).floatValue());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onChipsInfo$3 */
    public /* synthetic */ void lambda$onChipsInfo$3$TranscriptionController(List list) {
        ((ChipsContainer) this.mViewMap.get(State.CHIPS)).setChipsAnimatedZoom(list);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onChipsInfo$4 */
    public /* synthetic */ void lambda$onChipsInfo$4$TranscriptionController(List list) {
        ((ChipsContainer) this.mViewMap.get(State.CHIPS)).setChips(list);
    }

    public void onClear(boolean z) {
        setQueuedState(State.NONE, z, null);
        maybeSetState();
    }

    public void setListener(TranscriptionSpaceListener transcriptionSpaceListener) {
        this.mListener = transcriptionSpaceListener;
        if (transcriptionSpaceListener != null) {
            transcriptionSpaceListener.onStateChanged(null, this.mCurrentState);
        }
    }

    public void onDensityOrFontScaleChanged() {
        for (TranscriptionSpaceView onFontSizeChanged : this.mViewMap.values()) {
            onFontSizeChanged.onFontSizeChanged();
        }
    }

    public void setHasDarkBackground(boolean z) {
        for (TranscriptionSpaceView hasDarkBackground : this.mViewMap.values()) {
            hasDarkBackground.setHasDarkBackground(z);
        }
    }

    public void setCardVisible(boolean z) {
        for (TranscriptionSpaceView cardVisible : this.mViewMap.values()) {
            cardVisible.setCardVisible(z);
        }
    }

    public void setTranscription(String str, PendingIntent pendingIntent) {
        this.mOnTranscriptionTap = pendingIntent;
        setQueuedState(State.TRANSCRIPTION, false, new Runnable(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TranscriptionController.this.lambda$setTranscription$5$TranscriptionController(this.f$1);
            }
        });
        maybeSetState();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setTranscription$5 */
    public /* synthetic */ void lambda$setTranscription$5$TranscriptionController(String str) {
        ((TranscriptionView) this.mViewMap.get(State.TRANSCRIPTION)).setTranscription(str);
    }

    public void setTranscriptionColor(int i) {
        ((TranscriptionView) this.mViewMap.get(State.TRANSCRIPTION)).setTranscriptionColor(i);
    }

    public void setHasAccurateBackground(boolean z) {
        if (this.mHasAccurateBackground != z) {
            this.mHasAccurateBackground = z;
            if (z) {
                maybeSetState();
            }
        }
    }

    public Optional<Region> getTouchInsideRegion() {
        return hasTapAction() ? Optional.empty() : getTouchRegion();
    }

    public Optional<Region> getTouchActionRegion() {
        return hasTapAction() ? getTouchRegion() : Optional.empty();
    }

    private boolean hasTapAction() {
        int i = C18791.f100x4ecc3d98[this.mCurrentState.ordinal()];
        boolean z = false;
        if (i == 1) {
            if (this.mOnTranscriptionTap != null) {
                z = true;
            }
            return z;
        } else if (i != 2) {
            return i == 3;
        } else {
            if (this.mOnGreetingTap != null) {
                z = true;
            }
            return z;
        }
    }

    private Optional<Region> getTouchRegion() {
        TranscriptionSpaceView transcriptionSpaceView = (TranscriptionSpaceView) this.mViewMap.get(this.mCurrentState);
        if (transcriptionSpaceView == null) {
            return Optional.empty();
        }
        Rect rect = new Rect();
        transcriptionSpaceView.getHitRect(rect);
        return Optional.of(new Region(rect));
    }

    private void setQueuedState(State state, boolean z, Runnable runnable) {
        this.mQueuedState = state;
        this.mQueuedStateAnimates = z;
        this.mQueuedCompletion = runnable;
    }

    private void maybeSetState() {
        State state = this.mCurrentState;
        State state2 = this.mQueuedState;
        if (state == state2) {
            Runnable runnable = this.mQueuedCompletion;
            if (runnable != null) {
                runnable.run();
            }
        } else if (this.mHasAccurateBackground || state2 == State.NONE) {
            ListenableFuture<Void> listenableFuture = this.mHideFuture;
            if (listenableFuture == null || listenableFuture.isDone()) {
                updateListener(this.mCurrentState, this.mQueuedState);
                if (State.NONE.equals(this.mCurrentState)) {
                    this.mCurrentState = this.mQueuedState;
                    Runnable runnable2 = this.mQueuedCompletion;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                } else {
                    ListenableFuture<Void> hide = ((TranscriptionSpaceView) this.mViewMap.get(this.mCurrentState)).hide(this.mQueuedStateAnimates);
                    this.mHideFuture = hide;
                    Futures.transform(hide, new Function() {
                        public final Object apply(Object obj) {
                            return TranscriptionController.this.lambda$maybeSetState$6$TranscriptionController((Void) obj);
                        }
                    }, MoreExecutors.directExecutor());
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$maybeSetState$6 */
    public /* synthetic */ Object lambda$maybeSetState$6$TranscriptionController(Void voidR) {
        this.mCurrentState = this.mQueuedState;
        Runnable runnable = this.mQueuedCompletion;
        if (runnable != null) {
            runnable.run();
        }
        return null;
    }

    private void updateListener(State state, State state2) {
        TranscriptionSpaceListener transcriptionSpaceListener = this.mListener;
        if (transcriptionSpaceListener != null) {
            transcriptionSpaceListener.onStateChanged(state, state2);
        }
    }

    private void setUpViews() {
        this.mViewMap = new HashMap();
        TranscriptionView transcriptionView = (TranscriptionView) this.mParent.findViewById(C2011R$id.transcription);
        transcriptionView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                TranscriptionController.this.lambda$setUpViews$7$TranscriptionController(view);
            }
        });
        transcriptionView.setOnTouchListener(this.mDefaultOnTap);
        this.mViewMap.put(State.TRANSCRIPTION, transcriptionView);
        GreetingView greetingView = (GreetingView) this.mParent.findViewById(C2011R$id.greeting);
        greetingView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                TranscriptionController.this.lambda$setUpViews$8$TranscriptionController(view);
            }
        });
        greetingView.setOnTouchListener(this.mDefaultOnTap);
        this.mViewMap.put(State.GREETING, greetingView);
        this.mViewMap.put(State.CHIPS, (ChipsContainer) this.mParent.findViewById(C2011R$id.chips));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setUpViews$7 */
    public /* synthetic */ void lambda$setUpViews$7$TranscriptionController(View view) {
        PendingIntent pendingIntent = this.mOnTranscriptionTap;
        if (pendingIntent == null) {
            this.mDefaultOnTap.onTouchInside();
            return;
        }
        try {
            pendingIntent.send();
        } catch (CanceledException unused) {
            Log.e("TranscriptionController", "Transcription tap PendingIntent cancelled");
            this.mDefaultOnTap.onTouchInside();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setUpViews$8 */
    public /* synthetic */ void lambda$setUpViews$8$TranscriptionController(View view) {
        PendingIntent pendingIntent = this.mOnGreetingTap;
        if (pendingIntent == null) {
            this.mDefaultOnTap.onTouchInside();
            return;
        }
        try {
            pendingIntent.send();
        } catch (CanceledException unused) {
            Log.e("TranscriptionController", "Greeting tap PendingIntent cancelled");
            this.mDefaultOnTap.onTouchInside();
        }
    }
}
