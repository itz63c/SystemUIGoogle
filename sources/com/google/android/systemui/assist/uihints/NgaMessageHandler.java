package com.google.android.systemui.assist.uihints;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NgaMessageHandler {
    private static final boolean VERBOSE = (Build.TYPE.toLowerCase(Locale.ROOT).contains("debug") || Build.TYPE.toLowerCase(Locale.ROOT).equals("eng"));
    private final AssistantPresenceHandler mAssistantPresenceHandler;
    private final Set<AudioInfoListener> mAudioInfoListeners;
    private final Set<CardInfoListener> mCardInfoListeners;
    private final Set<ChipsInfoListener> mChipsInfoListeners;
    private final Set<ClearListener> mClearListeners;
    private final Set<ConfigInfoListener> mConfigInfoListeners;
    private final Set<EdgeLightsInfoListener> mEdgeLightsInfoListeners;
    private final Set<GoBackListener> mGoBackListeners;
    private final Set<GreetingInfoListener> mGreetingInfoListeners;
    private final Handler mHandler;
    private final Set<KeepAliveListener> mKeepAliveListeners;
    private final Set<KeyboardInfoListener> mKeyboardInfoListeners;
    private final NgaUiController mNgaUiController;
    private final Set<StartActivityInfoListener> mStartActivityInfoListeners;
    private final Set<TakeScreenshotListener> mTakeScreenshotListeners;
    private final Set<TranscriptionInfoListener> mTranscriptionInfoListeners;
    private final Set<WarmingListener> mWarmingListeners;
    private final Set<ZerostateInfoListener> mZerostateInfoListeners;

    public interface AudioInfoListener {
        void onAudioInfo(float f, float f2);
    }

    public interface CardInfoListener {
        void onCardInfo(boolean z, int i, boolean z2, boolean z3);
    }

    public interface ChipsInfoListener {
        void onChipsInfo(List<Bundle> list);
    }

    public interface ClearListener {
        void onClear(boolean z);
    }

    public static class ConfigInfo {
        public final PendingIntent configurationCallback;
        public final boolean ngaIsAssistant;
        public PendingIntent onColorChanged;
        public final PendingIntent onKeyboardShowingChange;
        public final PendingIntent onTaskChange;
        public final PendingIntent onTouchInside;
        public final PendingIntent onTouchOutside;
        public final boolean sysUiIsNgaUi;

        ConfigInfo(Bundle bundle) {
            boolean z = bundle.getBoolean("is_available");
            boolean z2 = bundle.getBoolean("nga_is_assistant", z);
            this.ngaIsAssistant = z2;
            this.sysUiIsNgaUi = z && z2;
            this.onColorChanged = (PendingIntent) bundle.getParcelable("color_changed");
            this.onTouchOutside = (PendingIntent) bundle.getParcelable("touch_outside");
            this.onTouchInside = (PendingIntent) bundle.getParcelable("touch_inside");
            this.onTaskChange = (PendingIntent) bundle.getParcelable("task_stack_changed");
            this.onKeyboardShowingChange = (PendingIntent) bundle.getParcelable("keyboard_showing_changed");
            this.configurationCallback = (PendingIntent) bundle.getParcelable("configuration");
        }
    }

    public interface ConfigInfoListener {
        void onConfigInfo(ConfigInfo configInfo);
    }

    public interface EdgeLightsInfoListener {
        void onEdgeLightsInfo(String str, boolean z);
    }

    public interface GoBackListener {
        void onGoBack();
    }

    public interface GreetingInfoListener {
        void onGreetingInfo(String str, PendingIntent pendingIntent);
    }

    public interface KeepAliveListener {
        void onKeepAlive(String str);
    }

    public interface KeyboardInfoListener {
        void onHideKeyboard();

        void onShowKeyboard(PendingIntent pendingIntent);
    }

    public interface StartActivityInfoListener {
        void onStartActivityInfo(Intent intent, boolean z);
    }

    public interface TakeScreenshotListener {
        void onTakeScreenshot(PendingIntent pendingIntent);
    }

    public interface TranscriptionInfoListener {
        void onTranscriptionInfo(String str, PendingIntent pendingIntent, int i);
    }

    public interface WarmingListener {
        void onWarmingRequest(WarmingRequest warmingRequest);
    }

    public static class WarmingRequest {
        private final PendingIntent onWarm;
        private final float threshold;

        public WarmingRequest(PendingIntent pendingIntent, float f) {
            this.onWarm = pendingIntent;
            this.threshold = MathUtils.clamp(f, 0.0f, 1.0f);
        }

        public float getThreshold() {
            return this.threshold;
        }

        public void notify(Context context, boolean z) {
            PendingIntent pendingIntent = this.onWarm;
            if (pendingIntent != null) {
                try {
                    pendingIntent.send(context, 0, new Intent().putExtra("primed", z));
                } catch (CanceledException e) {
                    Log.e("NgaMessageHandler", "Unable to warm assistant, PendingIntent cancelled", e);
                }
            }
        }
    }

    public interface ZerostateInfoListener {
        void onHideZerostate();

        void onShowZerostate(PendingIntent pendingIntent);
    }

    NgaMessageHandler(NgaUiController ngaUiController, AssistantPresenceHandler assistantPresenceHandler, Set<KeepAliveListener> set, Set<AudioInfoListener> set2, Set<CardInfoListener> set3, Set<ConfigInfoListener> set4, Set<EdgeLightsInfoListener> set5, Set<TranscriptionInfoListener> set6, Set<GreetingInfoListener> set7, Set<ChipsInfoListener> set8, Set<ClearListener> set9, Set<StartActivityInfoListener> set10, Set<KeyboardInfoListener> set11, Set<ZerostateInfoListener> set12, Set<GoBackListener> set13, Set<TakeScreenshotListener> set14, Set<WarmingListener> set15, Handler handler) {
        this.mNgaUiController = ngaUiController;
        this.mAssistantPresenceHandler = assistantPresenceHandler;
        this.mKeepAliveListeners = set;
        this.mAudioInfoListeners = set2;
        this.mCardInfoListeners = set3;
        this.mConfigInfoListeners = set4;
        this.mEdgeLightsInfoListeners = set5;
        this.mTranscriptionInfoListeners = set6;
        this.mGreetingInfoListeners = set7;
        this.mChipsInfoListeners = set8;
        this.mClearListeners = set9;
        this.mStartActivityInfoListeners = set10;
        this.mKeyboardInfoListeners = set11;
        this.mZerostateInfoListeners = set12;
        this.mGoBackListeners = set13;
        this.mTakeScreenshotListeners = set14;
        this.mWarmingListeners = set15;
        this.mHandler = handler;
    }

    /* renamed from: processBundle */
    public void lambda$processBundle$0(Bundle bundle, Runnable runnable) {
        if (Looper.myLooper() != this.mHandler.getLooper()) {
            this.mHandler.post(new Runnable(bundle, runnable) {
                public final /* synthetic */ Bundle f$1;
                public final /* synthetic */ Runnable f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NgaMessageHandler.this.lambda$processBundle$0$NgaMessageHandler(this.f$1, this.f$2);
                }
            });
            return;
        }
        logBundle(bundle);
        String string = bundle.getString("action", "");
        String str = "NgaMessageHandler";
        if (string.isEmpty()) {
            Log.w(str, "No action specified, ignoring");
            return;
        }
        boolean isNgaAssistant = this.mAssistantPresenceHandler.isNgaAssistant();
        boolean isSysUiNgaUi = this.mAssistantPresenceHandler.isSysUiNgaUi();
        boolean processAlwaysAvailableActions = processAlwaysAvailableActions(string, bundle);
        if (!processAlwaysAvailableActions && isNgaAssistant) {
            processAlwaysAvailableActions = processNgaActions(string, bundle);
            if (!processAlwaysAvailableActions && isSysUiNgaUi) {
                processAlwaysAvailableActions = processSysUiNgaUiActions(string, bundle);
            }
        }
        if (!processAlwaysAvailableActions) {
            Log.w(str, String.format("Invalid action \"%s\" for state:\n  NGA is Assistant = %b\n  SysUI is NGA UI = %b", new Object[]{string, Boolean.valueOf(isNgaAssistant), Boolean.valueOf(isSysUiNgaUi)}));
        }
        runnable.run();
    }

    private boolean processAlwaysAvailableActions(String str, Bundle bundle) {
        if (!"config".equals(str)) {
            return false;
        }
        ConfigInfo configInfo = new ConfigInfo(bundle);
        for (ConfigInfoListener onConfigInfo : this.mConfigInfoListeners) {
            onConfigInfo.onConfigInfo(configInfo);
        }
        this.mNgaUiController.onUiMessageReceived();
        return true;
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean processNgaActions(java.lang.String r7, android.os.Bundle r8) {
        /*
            r6 = this;
            int r0 = r7.hashCode()
            r1 = 4
            r2 = 3
            r3 = 2
            r4 = 0
            r5 = 1
            switch(r0) {
                case 3046160: goto L_0x0035;
                case 192184798: goto L_0x002b;
                case 371207756: goto L_0x0021;
                case 777739294: goto L_0x0017;
                case 1124416317: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x003f
        L_0x000d:
            java.lang.String r0 = "warming"
            boolean r7 = r7.equals(r0)
            if (r7 == 0) goto L_0x003f
            r7 = r1
            goto L_0x0040
        L_0x0017:
            java.lang.String r0 = "take_screenshot"
            boolean r7 = r7.equals(r0)
            if (r7 == 0) goto L_0x003f
            r7 = r2
            goto L_0x0040
        L_0x0021:
            java.lang.String r0 = "start_activity"
            boolean r7 = r7.equals(r0)
            if (r7 == 0) goto L_0x003f
            r7 = r5
            goto L_0x0040
        L_0x002b:
            java.lang.String r0 = "go_back"
            boolean r7 = r7.equals(r0)
            if (r7 == 0) goto L_0x003f
            r7 = r3
            goto L_0x0040
        L_0x0035:
            java.lang.String r0 = "card"
            boolean r7 = r7.equals(r0)
            if (r7 == 0) goto L_0x003f
            r7 = r4
            goto L_0x0040
        L_0x003f:
            r7 = -1
        L_0x0040:
            if (r7 == 0) goto L_0x00cd
            java.lang.String r0 = "intent"
            if (r7 == r5) goto L_0x00ab
            if (r7 == r3) goto L_0x0095
            if (r7 == r2) goto L_0x0077
            if (r7 == r1) goto L_0x004d
            return r4
        L_0x004d:
            android.os.Parcelable r7 = r8.getParcelable(r0)
            android.app.PendingIntent r7 = (android.app.PendingIntent) r7
            r0 = 1036831949(0x3dcccccd, float:0.1)
            java.lang.String r1 = "threshold"
            float r8 = r8.getFloat(r1, r0)
            com.google.android.systemui.assist.uihints.NgaMessageHandler$WarmingRequest r0 = new com.google.android.systemui.assist.uihints.NgaMessageHandler$WarmingRequest
            r0.<init>(r7, r8)
            java.util.Set<com.google.android.systemui.assist.uihints.NgaMessageHandler$WarmingListener> r6 = r6.mWarmingListeners
            java.util.Iterator r6 = r6.iterator()
        L_0x0067:
            boolean r7 = r6.hasNext()
            if (r7 == 0) goto L_0x00fb
            java.lang.Object r7 = r6.next()
            com.google.android.systemui.assist.uihints.NgaMessageHandler$WarmingListener r7 = (com.google.android.systemui.assist.uihints.NgaMessageHandler.WarmingListener) r7
            r7.onWarmingRequest(r0)
            goto L_0x0067
        L_0x0077:
            java.lang.String r7 = "on_finish"
            android.os.Parcelable r7 = r8.getParcelable(r7)
            android.app.PendingIntent r7 = (android.app.PendingIntent) r7
            java.util.Set<com.google.android.systemui.assist.uihints.NgaMessageHandler$TakeScreenshotListener> r6 = r6.mTakeScreenshotListeners
            java.util.Iterator r6 = r6.iterator()
        L_0x0085:
            boolean r8 = r6.hasNext()
            if (r8 == 0) goto L_0x00fb
            java.lang.Object r8 = r6.next()
            com.google.android.systemui.assist.uihints.NgaMessageHandler$TakeScreenshotListener r8 = (com.google.android.systemui.assist.uihints.NgaMessageHandler.TakeScreenshotListener) r8
            r8.onTakeScreenshot(r7)
            goto L_0x0085
        L_0x0095:
            java.util.Set<com.google.android.systemui.assist.uihints.NgaMessageHandler$GoBackListener> r6 = r6.mGoBackListeners
            java.util.Iterator r6 = r6.iterator()
        L_0x009b:
            boolean r7 = r6.hasNext()
            if (r7 == 0) goto L_0x00fb
            java.lang.Object r7 = r6.next()
            com.google.android.systemui.assist.uihints.NgaMessageHandler$GoBackListener r7 = (com.google.android.systemui.assist.uihints.NgaMessageHandler.GoBackListener) r7
            r7.onGoBack()
            goto L_0x009b
        L_0x00ab:
            android.os.Parcelable r7 = r8.getParcelable(r0)
            android.content.Intent r7 = (android.content.Intent) r7
            java.lang.String r0 = "dismiss_shade"
            boolean r8 = r8.getBoolean(r0)
            java.util.Set<com.google.android.systemui.assist.uihints.NgaMessageHandler$StartActivityInfoListener> r6 = r6.mStartActivityInfoListeners
            java.util.Iterator r6 = r6.iterator()
        L_0x00bd:
            boolean r0 = r6.hasNext()
            if (r0 == 0) goto L_0x00fb
            java.lang.Object r0 = r6.next()
            com.google.android.systemui.assist.uihints.NgaMessageHandler$StartActivityInfoListener r0 = (com.google.android.systemui.assist.uihints.NgaMessageHandler.StartActivityInfoListener) r0
            r0.onStartActivityInfo(r7, r8)
            goto L_0x00bd
        L_0x00cd:
            java.lang.String r7 = "is_visible"
            boolean r7 = r8.getBoolean(r7)
            java.lang.String r0 = "sysui_color"
            int r0 = r8.getInt(r0, r4)
            java.lang.String r1 = "animate_transition"
            boolean r1 = r8.getBoolean(r1, r5)
            java.lang.String r2 = "card_forces_scrim"
            boolean r8 = r8.getBoolean(r2)
            java.util.Set<com.google.android.systemui.assist.uihints.NgaMessageHandler$CardInfoListener> r6 = r6.mCardInfoListeners
            java.util.Iterator r6 = r6.iterator()
        L_0x00eb:
            boolean r2 = r6.hasNext()
            if (r2 == 0) goto L_0x00fb
            java.lang.Object r2 = r6.next()
            com.google.android.systemui.assist.uihints.NgaMessageHandler$CardInfoListener r2 = (com.google.android.systemui.assist.uihints.NgaMessageHandler.CardInfoListener) r2
            r2.onCardInfo(r7, r0, r1, r8)
            goto L_0x00eb
        L_0x00fb:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.assist.uihints.NgaMessageHandler.processNgaActions(java.lang.String, android.os.Bundle):boolean");
    }

    private boolean processSysUiNgaUiActions(String str, Bundle bundle) {
        for (KeepAliveListener onKeepAlive : this.mKeepAliveListeners) {
            onKeepAlive.onKeepAlive(str);
        }
        char c = 65535;
        String str2 = "chips";
        switch (str.hashCode()) {
            case -2051025175:
                if (str.equals("show_keyboard")) {
                    c = 7;
                    break;
                }
                break;
            case -2040419289:
                if (str.equals("show_zerostate")) {
                    c = 9;
                    break;
                }
                break;
            case -1160605116:
                if (str.equals("hide_keyboard")) {
                    c = 8;
                    break;
                }
                break;
            case -241763182:
                if (str.equals("transcription")) {
                    c = 3;
                    break;
                }
                break;
            case -207201236:
                if (str.equals("hide_zerostate")) {
                    c = 10;
                    break;
                }
                break;
            case 94631335:
                if (str.equals(str2)) {
                    c = 5;
                    break;
                }
                break;
            case 94746189:
                if (str.equals("clear")) {
                    c = 6;
                    break;
                }
                break;
            case 205422649:
                if (str.equals("greeting")) {
                    c = 4;
                    break;
                }
                break;
            case 771587807:
                if (str.equals("edge_lights")) {
                    c = 2;
                    break;
                }
                break;
            case 1549039479:
                if (str.equals("audio_info")) {
                    c = 1;
                    break;
                }
                break;
            case 1642639251:
                if (str.equals("keep_alive")) {
                    c = 0;
                    break;
                }
                break;
        }
        String str3 = "text";
        String str4 = "tap_action";
        switch (c) {
            case 0:
                break;
            case 1:
                float f = bundle.getFloat("volume");
                float f2 = bundle.getFloat("speech_confidence");
                for (AudioInfoListener onAudioInfo : this.mAudioInfoListeners) {
                    onAudioInfo.onAudioInfo(f, f2);
                }
                break;
            case 2:
                String string = bundle.getString("state", "");
                boolean z = bundle.getBoolean("listening");
                for (EdgeLightsInfoListener onEdgeLightsInfo : this.mEdgeLightsInfoListeners) {
                    onEdgeLightsInfo.onEdgeLightsInfo(string, z);
                }
                break;
            case 3:
                String string2 = bundle.getString(str3);
                PendingIntent pendingIntent = (PendingIntent) bundle.getParcelable(str4);
                int i = bundle.getInt("text_color");
                for (TranscriptionInfoListener onTranscriptionInfo : this.mTranscriptionInfoListeners) {
                    onTranscriptionInfo.onTranscriptionInfo(string2, pendingIntent, i);
                }
                break;
            case 4:
                String string3 = bundle.getString(str3);
                PendingIntent pendingIntent2 = (PendingIntent) bundle.getParcelable(str4);
                for (GreetingInfoListener onGreetingInfo : this.mGreetingInfoListeners) {
                    onGreetingInfo.onGreetingInfo(string3, pendingIntent2);
                }
                break;
            case 5:
                ArrayList parcelableArrayList = bundle.getParcelableArrayList(str2);
                for (ChipsInfoListener onChipsInfo : this.mChipsInfoListeners) {
                    onChipsInfo.onChipsInfo(parcelableArrayList);
                }
                break;
            case 6:
                boolean z2 = bundle.getBoolean("show_animation", true);
                for (ClearListener onClear : this.mClearListeners) {
                    onClear.onClear(z2);
                }
                break;
            case 7:
                PendingIntent pendingIntent3 = (PendingIntent) bundle.getParcelable(str4);
                for (KeyboardInfoListener onShowKeyboard : this.mKeyboardInfoListeners) {
                    onShowKeyboard.onShowKeyboard(pendingIntent3);
                }
                break;
            case 8:
                for (KeyboardInfoListener onHideKeyboard : this.mKeyboardInfoListeners) {
                    onHideKeyboard.onHideKeyboard();
                }
                break;
            case 9:
                PendingIntent pendingIntent4 = (PendingIntent) bundle.getParcelable(str4);
                for (ZerostateInfoListener onShowZerostate : this.mZerostateInfoListeners) {
                    onShowZerostate.onShowZerostate(pendingIntent4);
                }
                break;
            case 10:
                for (ZerostateInfoListener onHideZerostate : this.mZerostateInfoListeners) {
                    onHideZerostate.onHideZerostate();
                }
                break;
            default:
                return false;
        }
        this.mNgaUiController.onUiMessageReceived();
        return true;
    }

    private void logBundle(Bundle bundle) {
        if (VERBOSE) {
            if (!"audio_info".equals(bundle.get("action"))) {
                StringBuilder sb = new StringBuilder();
                sb.append("Contents of NGA Bundle:");
                for (String str : bundle.keySet()) {
                    sb.append("\n   ");
                    sb.append(str);
                    String str2 = ": ";
                    sb.append(str2);
                    if ("text".equals(str)) {
                        sb.append("(");
                        sb.append(bundle.getString(str).length());
                        sb.append(" characters)");
                    } else {
                        String str3 = "chips";
                        if (str3.equals(str)) {
                            ArrayList<Bundle> parcelableArrayList = bundle.getParcelableArrayList(str3);
                            if (parcelableArrayList != null) {
                                for (Bundle bundle2 : parcelableArrayList) {
                                    sb.append("\n      Chip:");
                                    for (String str4 : bundle2.keySet()) {
                                        sb.append("\n         ");
                                        sb.append(str4);
                                        sb.append(str2);
                                        sb.append(bundle2.get(str4));
                                    }
                                }
                            }
                        } else {
                            sb.append(bundle.get(str));
                        }
                    }
                }
                Log.v("NgaMessageHandler", sb.toString());
            }
        }
    }
}
