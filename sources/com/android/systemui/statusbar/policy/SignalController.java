package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.util.Log;
import com.android.systemui.statusbar.policy.NetworkController.SignalCallback;
import com.android.systemui.statusbar.policy.SignalController.IconGroup;
import com.android.systemui.statusbar.policy.SignalController.State;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.BitSet;

public abstract class SignalController<T extends State, I extends IconGroup> {
    protected static final boolean CHATTY = NetworkControllerImpl.CHATTY;
    protected static final boolean DEBUG = NetworkControllerImpl.DEBUG;
    private final CallbackHandler mCallbackHandler;
    protected final Context mContext;
    protected final T mCurrentState = cleanState();
    private final State[] mHistory = new State[64];
    private int mHistoryIndex;
    protected final T mLastState = cleanState();
    protected final NetworkControllerImpl mNetworkController;
    protected final String mTag;
    protected final int mTransportType;

    static class IconGroup {
        final int[] mContentDesc;
        final int mDiscContentDesc;
        final String mName;
        final int mQsDiscState;
        final int[][] mQsIcons;
        final int mQsNullState;
        final int mSbDiscState;
        final int[][] mSbIcons;
        final int mSbNullState;

        public IconGroup(String str, int[][] iArr, int[][] iArr2, int[] iArr3, int i, int i2, int i3, int i4, int i5) {
            this.mName = str;
            this.mSbIcons = iArr;
            this.mQsIcons = iArr2;
            this.mContentDesc = iArr3;
            this.mSbNullState = i;
            this.mQsNullState = i2;
            this.mSbDiscState = i3;
            this.mQsDiscState = i4;
            this.mDiscContentDesc = i5;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("IconGroup(");
            sb.append(this.mName);
            sb.append(")");
            return sb.toString();
        }
    }

    static class State {
        private static SimpleDateFormat sSDF = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        boolean activityIn;
        boolean activityOut;
        boolean connected;
        boolean enabled;
        IconGroup iconGroup;
        int inetCondition;
        int level;
        int rssi;
        long time;

        State() {
        }

        public void copyFrom(State state) {
            this.connected = state.connected;
            this.enabled = state.enabled;
            this.level = state.level;
            this.iconGroup = state.iconGroup;
            this.inetCondition = state.inetCondition;
            this.activityIn = state.activityIn;
            this.activityOut = state.activityOut;
            this.rssi = state.rssi;
            this.time = state.time;
        }

        public String toString() {
            if (this.time != 0) {
                StringBuilder sb = new StringBuilder();
                toString(sb);
                return sb.toString();
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Empty ");
            sb2.append(getClass().getSimpleName());
            return sb2.toString();
        }

        /* access modifiers changed from: protected */
        public void toString(StringBuilder sb) {
            sb.append("connected=");
            sb.append(this.connected);
            sb.append(',');
            sb.append("enabled=");
            sb.append(this.enabled);
            sb.append(',');
            sb.append("level=");
            sb.append(this.level);
            sb.append(',');
            sb.append("inetCondition=");
            sb.append(this.inetCondition);
            sb.append(',');
            sb.append("iconGroup=");
            sb.append(this.iconGroup);
            sb.append(',');
            sb.append("activityIn=");
            sb.append(this.activityIn);
            sb.append(',');
            sb.append("activityOut=");
            sb.append(this.activityOut);
            sb.append(',');
            sb.append("rssi=");
            sb.append(this.rssi);
            sb.append(',');
            sb.append("lastModified=");
            sb.append(sSDF.format(Long.valueOf(this.time)));
        }

        public boolean equals(Object obj) {
            boolean z = false;
            if (!obj.getClass().equals(getClass())) {
                return false;
            }
            State state = (State) obj;
            if (state.connected == this.connected && state.enabled == this.enabled && state.level == this.level && state.inetCondition == this.inetCondition && state.iconGroup == this.iconGroup && state.activityIn == this.activityIn && state.activityOut == this.activityOut && state.rssi == this.rssi) {
                z = true;
            }
            return z;
        }
    }

    /* access modifiers changed from: protected */
    public abstract T cleanState();

    public abstract void notifyListeners(SignalCallback signalCallback);

    public SignalController(String str, Context context, int i, CallbackHandler callbackHandler, NetworkControllerImpl networkControllerImpl) {
        StringBuilder sb = new StringBuilder();
        sb.append("NetworkController.");
        sb.append(str);
        this.mTag = sb.toString();
        this.mNetworkController = networkControllerImpl;
        this.mTransportType = i;
        this.mContext = context;
        this.mCallbackHandler = callbackHandler;
        for (int i2 = 0; i2 < 64; i2++) {
            this.mHistory[i2] = cleanState();
        }
    }

    public T getState() {
        return this.mCurrentState;
    }

    public void updateConnectivity(BitSet bitSet, BitSet bitSet2) {
        this.mCurrentState.inetCondition = bitSet2.get(this.mTransportType) ? 1 : 0;
        notifyListenersIfNecessary();
    }

    public void resetLastState() {
        this.mCurrentState.copyFrom(this.mLastState);
    }

    public boolean isDirty() {
        if (this.mLastState.equals(this.mCurrentState)) {
            return false;
        }
        if (DEBUG) {
            String str = this.mTag;
            StringBuilder sb = new StringBuilder();
            sb.append("Change in state from: ");
            sb.append(this.mLastState);
            sb.append("\n\tto: ");
            sb.append(this.mCurrentState);
            Log.d(str, sb.toString());
        }
        return true;
    }

    public void saveLastState() {
        recordLastState();
        this.mCurrentState.time = System.currentTimeMillis();
        this.mLastState.copyFrom(this.mCurrentState);
    }

    public int getQsCurrentIconId() {
        T t = this.mCurrentState;
        if (t.connected) {
            int[][] iArr = getIcons().mQsIcons;
            T t2 = this.mCurrentState;
            return iArr[t2.inetCondition][t2.level];
        } else if (t.enabled) {
            return getIcons().mQsDiscState;
        } else {
            return getIcons().mQsNullState;
        }
    }

    public int getCurrentIconId() {
        T t = this.mCurrentState;
        if (t.connected) {
            int[][] iArr = getIcons().mSbIcons;
            T t2 = this.mCurrentState;
            return iArr[t2.inetCondition][t2.level];
        } else if (t.enabled) {
            return getIcons().mSbDiscState;
        } else {
            return getIcons().mSbNullState;
        }
    }

    public int getContentDescription() {
        if (this.mCurrentState.connected) {
            return getIcons().mContentDesc[this.mCurrentState.level];
        }
        return getIcons().mDiscContentDesc;
    }

    public void notifyListenersIfNecessary() {
        if (isDirty()) {
            saveLastState();
            notifyListeners();
        }
    }

    /* access modifiers changed from: 0000 */
    public CharSequence getTextIfExists(int i) {
        return i != 0 ? this.mContext.getText(i) : "";
    }

    /* access modifiers changed from: protected */
    public I getIcons() {
        return this.mCurrentState.iconGroup;
    }

    /* access modifiers changed from: protected */
    public void recordLastState() {
        State[] stateArr = this.mHistory;
        int i = this.mHistoryIndex;
        this.mHistoryIndex = i + 1;
        stateArr[i & 63].copyFrom(this.mLastState);
    }

    public void dump(PrintWriter printWriter) {
        StringBuilder sb = new StringBuilder();
        sb.append("  - ");
        sb.append(this.mTag);
        sb.append(" -----");
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  Current State: ");
        sb2.append(this.mCurrentState);
        printWriter.println(sb2.toString());
        int i = 0;
        for (int i2 = 0; i2 < 64; i2++) {
            if (this.mHistory[i2].time != 0) {
                i++;
            }
        }
        int i3 = this.mHistoryIndex + 64;
        while (true) {
            i3--;
            if (i3 >= (this.mHistoryIndex + 64) - i) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("  Previous State(");
                sb3.append((this.mHistoryIndex + 64) - i3);
                sb3.append("): ");
                sb3.append(this.mHistory[i3 & 63]);
                printWriter.println(sb3.toString());
            } else {
                return;
            }
        }
    }

    public final void notifyListeners() {
        notifyListeners(this.mCallbackHandler);
    }
}
