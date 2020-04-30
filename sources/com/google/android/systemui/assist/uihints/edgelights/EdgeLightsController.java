package com.google.android.systemui.assist.uihints.edgelights;

import android.content.Context;
import android.view.ViewGroup;
import com.android.systemui.C2011R$id;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.AudioInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.EdgeLightsInfoListener;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsView.Mode;
import com.google.android.systemui.assist.uihints.edgelights.mode.FullListening;
import com.google.android.systemui.assist.uihints.edgelights.mode.Gone;

public final class EdgeLightsController implements AudioInfoListener, EdgeLightsInfoListener {
    private final Context mContext;
    private final EdgeLightsView mEdgeLightsView;
    private ModeChangeThrottler mThrottler;

    public interface ModeChangeThrottler {
        void runWhenReady(String str, Runnable runnable);
    }

    public EdgeLightsController(Context context, ViewGroup viewGroup) {
        this.mContext = context;
        this.mEdgeLightsView = (EdgeLightsView) viewGroup.findViewById(C2011R$id.edge_lights);
    }

    public void onAudioInfo(float f, float f2) {
        this.mEdgeLightsView.onAudioLevelUpdate(f2, f);
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onEdgeLightsInfo(java.lang.String r8, boolean r9) {
        /*
            r7 = this;
            int r0 = r8.hashCode()
            r1 = 0
            r2 = 4
            r3 = 3
            r4 = 2
            r5 = 1
            switch(r0) {
                case -1911007510: goto L_0x0035;
                case 2193567: goto L_0x002b;
                case 429932431: goto L_0x0021;
                case 1387022046: goto L_0x0017;
                case 1971150571: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x003f
        L_0x000d:
            java.lang.String r0 = "FULL_LISTENING"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x003f
            r0 = r1
            goto L_0x0040
        L_0x0017:
            java.lang.String r0 = "FULFILL_PERIMETER"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x003f
            r0 = r3
            goto L_0x0040
        L_0x0021:
            java.lang.String r0 = "HALF_LISTENING"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x003f
            r0 = r5
            goto L_0x0040
        L_0x002b:
            java.lang.String r0 = "GONE"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x003f
            r0 = r2
            goto L_0x0040
        L_0x0035:
            java.lang.String r0 = "FULFILL_BOTTOM"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x003f
            r0 = r4
            goto L_0x0040
        L_0x003f:
            r0 = -1
        L_0x0040:
            java.lang.String r6 = "EdgeLightsController"
            if (r0 == 0) goto L_0x0072
            if (r0 == r5) goto L_0x0065
            if (r0 == r4) goto L_0x005c
            if (r0 == r3) goto L_0x0054
            if (r0 == r2) goto L_0x004e
            r9 = 0
            goto L_0x0079
        L_0x004e:
            com.google.android.systemui.assist.uihints.edgelights.mode.Gone r9 = new com.google.android.systemui.assist.uihints.edgelights.mode.Gone
            r9.<init>()
            goto L_0x0079
        L_0x0054:
            com.google.android.systemui.assist.uihints.edgelights.mode.FulfillPerimeter r9 = new com.google.android.systemui.assist.uihints.edgelights.mode.FulfillPerimeter
            android.content.Context r0 = r7.mContext
            r9.<init>(r0)
            goto L_0x0079
        L_0x005c:
            com.google.android.systemui.assist.uihints.edgelights.mode.FulfillBottom r0 = new com.google.android.systemui.assist.uihints.edgelights.mode.FulfillBottom
            android.content.Context r1 = r7.mContext
            r0.<init>(r1, r9)
            r9 = r0
            goto L_0x0079
        L_0x0065:
            java.lang.String r9 = "Rendering full instead of half listening for now."
            android.util.Log.i(r6, r9)
            com.google.android.systemui.assist.uihints.edgelights.mode.FullListening r9 = new com.google.android.systemui.assist.uihints.edgelights.mode.FullListening
            android.content.Context r0 = r7.mContext
            r9.<init>(r0, r5)
            goto L_0x0079
        L_0x0072:
            com.google.android.systemui.assist.uihints.edgelights.mode.FullListening r9 = new com.google.android.systemui.assist.uihints.edgelights.mode.FullListening
            android.content.Context r0 = r7.mContext
            r9.<init>(r0, r1)
        L_0x0079:
            if (r9 != 0) goto L_0x0090
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r9 = "Invalid edge lights mode: "
            r7.append(r9)
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            android.util.Log.e(r6, r7)
            return
        L_0x0090:
            com.google.android.systemui.assist.uihints.edgelights.-$$Lambda$EdgeLightsController$BD70M70wHYhKAr30BKmAvcn_cLg r0 = new com.google.android.systemui.assist.uihints.edgelights.-$$Lambda$EdgeLightsController$BD70M70wHYhKAr30BKmAvcn_cLg
            r0.<init>(r9)
            com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController$ModeChangeThrottler r7 = r7.mThrottler
            if (r7 != 0) goto L_0x009d
            r0.run()
            goto L_0x00a0
        L_0x009d:
            r7.runWhenReady(r8, r0)
        L_0x00a0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController.onEdgeLightsInfo(java.lang.String, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onEdgeLightsInfo$0 */
    public /* synthetic */ void lambda$onEdgeLightsInfo$0$EdgeLightsController(Mode mode) {
        getMode().onNewModeRequest(this.mEdgeLightsView, mode);
        mode.logState();
    }

    public void setModeChangeThrottler(ModeChangeThrottler modeChangeThrottler) {
        this.mThrottler = modeChangeThrottler;
    }

    public void setGone() {
        getMode().onNewModeRequest(this.mEdgeLightsView, new Gone());
    }

    public void setFullListening() {
        getMode().onNewModeRequest(this.mEdgeLightsView, new FullListening(this.mContext, false));
    }

    public void addListener(EdgeLightsListener edgeLightsListener) {
        this.mEdgeLightsView.addListener(edgeLightsListener);
    }

    public Mode getMode() {
        return this.mEdgeLightsView.getMode();
    }
}
