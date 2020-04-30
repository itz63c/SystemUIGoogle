package com.android.systemui.volume;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.ZenRule;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.volume.Util;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2012R$integer;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import com.android.systemui.Prefs;
import com.android.systemui.statusbar.policy.ZenModeController;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class ZenModePanel extends FrameLayout {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("ZenModePanel", 3);
    /* access modifiers changed from: private */
    public static final int DEFAULT_BUCKET_INDEX;
    private static final int MAX_BUCKET_MINUTES;
    /* access modifiers changed from: private */
    public static final int[] MINUTE_BUCKETS;
    private static final int MIN_BUCKET_MINUTES;
    public static final Intent ZEN_PRIORITY_SETTINGS = new Intent("android.settings.ZEN_MODE_PRIORITY_SETTINGS");
    public static final Intent ZEN_SETTINGS = new Intent("android.settings.ZEN_MODE_SETTINGS");
    private boolean mAttached;
    private int mAttachedZen;
    private View mAutoRule;
    private TextView mAutoTitle;
    private int mBucketIndex;
    private Callback mCallback;
    private final ConfigurableTexts mConfigurableTexts;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public ZenModeController mController;
    private ViewGroup mEdit;
    private View mEmpty;
    private ImageView mEmptyIcon;
    private TextView mEmptyText;
    private Condition mExitCondition;
    /* access modifiers changed from: private */
    public boolean mExpanded;
    private final Uri mForeverId;
    /* access modifiers changed from: private */
    public final C1787H mHandler = new C1787H();
    private boolean mHidden;
    protected final LayoutInflater mInflater;
    private final com.android.systemui.volume.Interaction.Callback mInteractionCallback;
    private final ZenPrefs mPrefs;
    /* access modifiers changed from: private */
    public Condition mSessionExitCondition;
    /* access modifiers changed from: private */
    public int mSessionZen;
    private int mState;
    /* access modifiers changed from: private */
    public String mTag;
    private final TransitionHelper mTransitionHelper = new TransitionHelper();
    private boolean mVoiceCapable;
    private TextView mZenAlarmWarning;
    protected SegmentedButtons mZenButtons;
    protected final com.android.systemui.volume.SegmentedButtons.Callback mZenButtonsCallback;
    private final com.android.systemui.statusbar.policy.ZenModeController.Callback mZenCallback;
    protected LinearLayout mZenConditions;
    private View mZenIntroduction;
    private View mZenIntroductionConfirm;
    private TextView mZenIntroductionCustomize;
    private TextView mZenIntroductionMessage;
    protected int mZenModeButtonLayoutId;
    protected int mZenModeConditionLayoutId;
    private RadioGroup mZenRadioGroup;
    private LinearLayout mZenRadioGroupContent;

    public interface Callback {
        void onExpanded(boolean z);

        void onInteraction();

        void onPrioritySettings();
    }

    @VisibleForTesting
    static class ConditionTag {
        Condition condition;
        TextView line1;
        TextView line2;
        View lines;

        /* renamed from: rb */
        RadioButton f87rb;

        ConditionTag() {
        }
    }

    /* renamed from: com.android.systemui.volume.ZenModePanel$H */
    private final class C1787H extends Handler {
        private C1787H() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 2) {
                ZenModePanel.this.handleUpdateManualRule((ZenRule) message.obj);
            } else if (i == 3) {
                ZenModePanel.this.updateWidgets();
            }
        }
    }

    private final class TransitionHelper implements TransitionListener, Runnable {
        private boolean mPendingUpdateWidgets;
        private boolean mTransitioning;
        private final ArraySet<View> mTransitioningViews;

        private TransitionHelper() {
            this.mTransitioningViews = new ArraySet<>();
        }

        public void clear() {
            this.mTransitioningViews.clear();
            this.mPendingUpdateWidgets = false;
        }

        public void pendingUpdateWidgets() {
            this.mPendingUpdateWidgets = true;
        }

        public boolean isTransitioning() {
            return !this.mTransitioningViews.isEmpty();
        }

        public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            this.mTransitioningViews.add(view);
            updateTransitioning();
        }

        public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            this.mTransitioningViews.remove(view);
            updateTransitioning();
        }

        public void run() {
            if (ZenModePanel.DEBUG) {
                String access$800 = ZenModePanel.this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("TransitionHelper run mPendingUpdateWidgets=");
                sb.append(this.mPendingUpdateWidgets);
                Log.d(access$800, sb.toString());
            }
            if (this.mPendingUpdateWidgets) {
                ZenModePanel.this.updateWidgets();
            }
            this.mPendingUpdateWidgets = false;
        }

        private void updateTransitioning() {
            boolean isTransitioning = isTransitioning();
            if (this.mTransitioning != isTransitioning) {
                this.mTransitioning = isTransitioning;
                if (ZenModePanel.DEBUG) {
                    String access$800 = ZenModePanel.this.mTag;
                    StringBuilder sb = new StringBuilder();
                    sb.append("TransitionHelper mTransitioning=");
                    sb.append(this.mTransitioning);
                    Log.d(access$800, sb.toString());
                }
                if (!this.mTransitioning) {
                    if (this.mPendingUpdateWidgets) {
                        ZenModePanel.this.mHandler.post(this);
                    } else {
                        this.mPendingUpdateWidgets = false;
                    }
                }
            }
        }
    }

    private final class ZenPrefs implements OnSharedPreferenceChangeListener {
        /* access modifiers changed from: private */
        public boolean mConfirmedAlarmIntroduction;
        /* access modifiers changed from: private */
        public boolean mConfirmedPriorityIntroduction;
        /* access modifiers changed from: private */
        public boolean mConfirmedSilenceIntroduction;
        private int mMinuteIndex;
        private final int mNoneDangerousThreshold;
        private int mNoneSelected;

        private ZenPrefs() {
            this.mNoneDangerousThreshold = ZenModePanel.this.mContext.getResources().getInteger(C2012R$integer.zen_mode_alarm_warning_threshold);
            Prefs.registerListener(ZenModePanel.this.mContext, this);
            updateMinuteIndex();
            updateNoneSelected();
            updateConfirmedPriorityIntroduction();
            updateConfirmedSilenceIntroduction();
            updateConfirmedAlarmIntroduction();
        }

        public void trackNoneSelected() {
            this.mNoneSelected = clampNoneSelected(this.mNoneSelected + 1);
            if (ZenModePanel.DEBUG) {
                String access$800 = ZenModePanel.this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("Setting none selected: ");
                sb.append(this.mNoneSelected);
                sb.append(" threshold=");
                sb.append(this.mNoneDangerousThreshold);
                Log.d(access$800, sb.toString());
            }
            Prefs.putInt(ZenModePanel.this.mContext, "DndNoneSelected", this.mNoneSelected);
        }

        public void setMinuteIndex(int i) {
            int clampIndex = clampIndex(i);
            if (clampIndex != this.mMinuteIndex) {
                this.mMinuteIndex = clampIndex(clampIndex);
                if (ZenModePanel.DEBUG) {
                    String access$800 = ZenModePanel.this.mTag;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Setting favorite minute index: ");
                    sb.append(this.mMinuteIndex);
                    Log.d(access$800, sb.toString());
                }
                Prefs.putInt(ZenModePanel.this.mContext, "DndCountdownMinuteIndex", this.mMinuteIndex);
            }
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            updateMinuteIndex();
            updateNoneSelected();
            updateConfirmedPriorityIntroduction();
            updateConfirmedSilenceIntroduction();
            updateConfirmedAlarmIntroduction();
        }

        private void updateMinuteIndex() {
            this.mMinuteIndex = clampIndex(Prefs.getInt(ZenModePanel.this.mContext, "DndCountdownMinuteIndex", ZenModePanel.DEFAULT_BUCKET_INDEX));
            if (ZenModePanel.DEBUG) {
                String access$800 = ZenModePanel.this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("Favorite minute index: ");
                sb.append(this.mMinuteIndex);
                Log.d(access$800, sb.toString());
            }
        }

        private int clampIndex(int i) {
            return MathUtils.constrain(i, -1, ZenModePanel.MINUTE_BUCKETS.length - 1);
        }

        private void updateNoneSelected() {
            this.mNoneSelected = clampNoneSelected(Prefs.getInt(ZenModePanel.this.mContext, "DndNoneSelected", 0));
            if (ZenModePanel.DEBUG) {
                String access$800 = ZenModePanel.this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("None selected: ");
                sb.append(this.mNoneSelected);
                Log.d(access$800, sb.toString());
            }
        }

        private int clampNoneSelected(int i) {
            return MathUtils.constrain(i, 0, Integer.MAX_VALUE);
        }

        private void updateConfirmedPriorityIntroduction() {
            boolean z = Prefs.getBoolean(ZenModePanel.this.mContext, "DndConfirmedPriorityIntroduction", false);
            if (z != this.mConfirmedPriorityIntroduction) {
                this.mConfirmedPriorityIntroduction = z;
                if (ZenModePanel.DEBUG) {
                    String access$800 = ZenModePanel.this.mTag;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Confirmed priority introduction: ");
                    sb.append(this.mConfirmedPriorityIntroduction);
                    Log.d(access$800, sb.toString());
                }
            }
        }

        private void updateConfirmedSilenceIntroduction() {
            boolean z = Prefs.getBoolean(ZenModePanel.this.mContext, "DndConfirmedSilenceIntroduction", false);
            if (z != this.mConfirmedSilenceIntroduction) {
                this.mConfirmedSilenceIntroduction = z;
                if (ZenModePanel.DEBUG) {
                    String access$800 = ZenModePanel.this.mTag;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Confirmed silence introduction: ");
                    sb.append(this.mConfirmedSilenceIntroduction);
                    Log.d(access$800, sb.toString());
                }
            }
        }

        private void updateConfirmedAlarmIntroduction() {
            boolean z = Prefs.getBoolean(ZenModePanel.this.mContext, "DndConfirmedAlarmIntroduction", false);
            if (z != this.mConfirmedAlarmIntroduction) {
                this.mConfirmedAlarmIntroduction = z;
                if (ZenModePanel.DEBUG) {
                    String access$800 = ZenModePanel.this.mTag;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Confirmed alarm introduction: ");
                    sb.append(this.mConfirmedAlarmIntroduction);
                    Log.d(access$800, sb.toString());
                }
            }
        }
    }

    private static String prefKeyForConfirmation(int i) {
        if (i == 1) {
            return "DndConfirmedPriorityIntroduction";
        }
        if (i == 2) {
            return "DndConfirmedSilenceIntroduction";
        }
        if (i != 3) {
            return null;
        }
        return "DndConfirmedAlarmIntroduction";
    }

    static {
        int[] iArr = ZenModeConfig.MINUTE_BUCKETS;
        MINUTE_BUCKETS = iArr;
        MIN_BUCKET_MINUTES = iArr[0];
        MAX_BUCKET_MINUTES = iArr[iArr.length - 1];
        DEFAULT_BUCKET_INDEX = Arrays.binarySearch(iArr, 60);
    }

    public ZenModePanel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        StringBuilder sb = new StringBuilder();
        sb.append("ZenModePanel/");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        this.mTag = sb.toString();
        this.mBucketIndex = -1;
        this.mState = 0;
        this.mZenCallback = new com.android.systemui.statusbar.policy.ZenModeController.Callback() {
            public void onManualRuleChanged(ZenRule zenRule) {
                ZenModePanel.this.mHandler.obtainMessage(2, zenRule).sendToTarget();
            }
        };
        this.mZenButtonsCallback = new com.android.systemui.volume.SegmentedButtons.Callback() {
            public void onSelected(Object obj, boolean z) {
                if (obj != null && ZenModePanel.this.mZenButtons.isShown() && ZenModePanel.this.isAttachedToWindow()) {
                    final int intValue = ((Integer) obj).intValue();
                    if (z) {
                        MetricsLogger.action(ZenModePanel.this.mContext, 165, intValue);
                    }
                    if (ZenModePanel.DEBUG) {
                        String access$800 = ZenModePanel.this.mTag;
                        StringBuilder sb = new StringBuilder();
                        sb.append("mZenButtonsCallback selected=");
                        sb.append(intValue);
                        Log.d(access$800, sb.toString());
                    }
                    ZenModePanel zenModePanel = ZenModePanel.this;
                    final Uri access$2000 = zenModePanel.getRealConditionId(zenModePanel.mSessionExitCondition);
                    AsyncTask.execute(new Runnable() {
                        public void run() {
                            ZenModePanel.this.mController.setZen(intValue, access$2000, "ZenModePanel.selectZen");
                            if (intValue != 0) {
                                Prefs.putInt(ZenModePanel.this.mContext, "DndFavoriteZen", intValue);
                            }
                        }
                    });
                }
            }

            public void onInteraction() {
                ZenModePanel.this.fireInteraction();
            }
        };
        this.mInteractionCallback = new com.android.systemui.volume.Interaction.Callback() {
            public void onInteraction() {
                ZenModePanel.this.fireInteraction();
            }
        };
        this.mContext = context;
        this.mPrefs = new ZenPrefs();
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mForeverId = Condition.newId(this.mContext).appendPath("forever").build();
        this.mConfigurableTexts = new ConfigurableTexts(this.mContext);
        this.mVoiceCapable = Util.isVoiceCapable(this.mContext);
        this.mZenModeConditionLayoutId = C2013R$layout.zen_mode_condition;
        this.mZenModeButtonLayoutId = C2013R$layout.zen_mode_button;
        if (DEBUG) {
            Log.d(this.mTag, "new ZenModePanel");
        }
    }

    /* access modifiers changed from: protected */
    public void createZenButtons() {
        SegmentedButtons segmentedButtons = (SegmentedButtons) findViewById(C2011R$id.zen_buttons);
        this.mZenButtons = segmentedButtons;
        segmentedButtons.addButton(C2017R$string.interruption_level_none_twoline, C2017R$string.interruption_level_none_with_warning, Integer.valueOf(2));
        this.mZenButtons.addButton(C2017R$string.interruption_level_alarms_twoline, C2017R$string.interruption_level_alarms, Integer.valueOf(3));
        this.mZenButtons.addButton(C2017R$string.interruption_level_priority_twoline, C2017R$string.interruption_level_priority, Integer.valueOf(1));
        this.mZenButtons.setCallback(this.mZenButtonsCallback);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        createZenButtons();
        this.mZenIntroduction = findViewById(C2011R$id.zen_introduction);
        this.mZenIntroductionMessage = (TextView) findViewById(C2011R$id.zen_introduction_message);
        View findViewById = findViewById(C2011R$id.zen_introduction_confirm);
        this.mZenIntroductionConfirm = findViewById;
        findViewById.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                ZenModePanel.this.lambda$onFinishInflate$0$ZenModePanel(view);
            }
        });
        TextView textView = (TextView) findViewById(C2011R$id.zen_introduction_customize);
        this.mZenIntroductionCustomize = textView;
        textView.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                ZenModePanel.this.lambda$onFinishInflate$1$ZenModePanel(view);
            }
        });
        this.mConfigurableTexts.add(this.mZenIntroductionCustomize, C2017R$string.zen_priority_customize_button);
        this.mZenConditions = (LinearLayout) findViewById(C2011R$id.zen_conditions);
        this.mZenAlarmWarning = (TextView) findViewById(C2011R$id.zen_alarm_warning);
        this.mZenRadioGroup = (RadioGroup) findViewById(C2011R$id.zen_radio_buttons);
        this.mZenRadioGroupContent = (LinearLayout) findViewById(C2011R$id.zen_radio_buttons_content);
        this.mEdit = (ViewGroup) findViewById(C2011R$id.edit_container);
        View findViewById2 = findViewById(16908292);
        this.mEmpty = findViewById2;
        findViewById2.setVisibility(4);
        this.mEmptyText = (TextView) this.mEmpty.findViewById(16908310);
        this.mEmptyIcon = (ImageView) this.mEmpty.findViewById(16908294);
        View findViewById3 = findViewById(C2011R$id.auto_rule);
        this.mAutoRule = findViewById3;
        this.mAutoTitle = (TextView) findViewById3.findViewById(16908310);
        this.mAutoRule.setVisibility(4);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$0 */
    public /* synthetic */ void lambda$onFinishInflate$0$ZenModePanel(View view) {
        confirmZenIntroduction();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$1 */
    public /* synthetic */ void lambda$onFinishInflate$1$ZenModePanel(View view) {
        confirmZenIntroduction();
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onPrioritySettings();
        }
    }

    public void setEmptyState(int i, int i2) {
        this.mEmptyIcon.post(new Runnable(i, i2) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ZenModePanel.this.lambda$setEmptyState$2$ZenModePanel(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setEmptyState$2 */
    public /* synthetic */ void lambda$setEmptyState$2$ZenModePanel(int i, int i2) {
        this.mEmptyIcon.setImageResource(i);
        this.mEmptyText.setText(i2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setAutoText$3 */
    public /* synthetic */ void lambda$setAutoText$3$ZenModePanel(CharSequence charSequence) {
        this.mAutoTitle.setText(charSequence);
    }

    public void setAutoText(CharSequence charSequence) {
        this.mAutoTitle.post(new Runnable(charSequence) {
            public final /* synthetic */ CharSequence f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ZenModePanel.this.lambda$setAutoText$3$ZenModePanel(this.f$1);
            }
        });
    }

    public void setState(int i) {
        int i2 = this.mState;
        if (i2 != i) {
            transitionFrom(getView(i2), getView(i));
            this.mState = i;
        }
    }

    private void transitionFrom(View view, View view2) {
        view.post(new Runnable(view2, view) {
            public final /* synthetic */ View f$0;
            public final /* synthetic */ View f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                ZenModePanel.lambda$transitionFrom$5(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$transitionFrom$5(View view, View view2) {
        view.setAlpha(0.0f);
        view.setVisibility(0);
        view.bringToFront();
        view.animate().cancel();
        view.animate().alpha(1.0f).setDuration(300).withEndAction(new Runnable(view2) {
            public final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.setVisibility(4);
            }
        }).start();
    }

    private View getView(int i) {
        if (i == 1) {
            return this.mAutoRule;
        }
        if (i != 2) {
            return this.mEdit;
        }
        return this.mEmpty;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mConfigurableTexts.update();
        SegmentedButtons segmentedButtons = this.mZenButtons;
        if (segmentedButtons != null) {
            segmentedButtons.update();
        }
    }

    private void confirmZenIntroduction() {
        String prefKeyForConfirmation = prefKeyForConfirmation(getSelectedZen(0));
        if (prefKeyForConfirmation != null) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("confirmZenIntroduction ");
                sb.append(prefKeyForConfirmation);
                Log.d("ZenModePanel", sb.toString());
            }
            Prefs.putBoolean(this.mContext, prefKeyForConfirmation, true);
            this.mHandler.sendEmptyMessage(3);
        }
    }

    private void onAttach() {
        setExpanded(true);
        this.mAttachedZen = this.mController.getZen();
        ZenRule manualRule = this.mController.getManualRule();
        this.mExitCondition = manualRule != null ? manualRule.condition : null;
        if (DEBUG) {
            String str = this.mTag;
            StringBuilder sb = new StringBuilder();
            sb.append("onAttach ");
            sb.append(this.mAttachedZen);
            sb.append(" ");
            sb.append(manualRule);
            Log.d(str, sb.toString());
        }
        handleUpdateManualRule(manualRule);
        this.mZenButtons.setSelectedValue(Integer.valueOf(this.mAttachedZen), false);
        this.mSessionZen = this.mAttachedZen;
        this.mTransitionHelper.clear();
        this.mController.addCallback(this.mZenCallback);
        setSessionExitCondition(copy(this.mExitCondition));
        updateWidgets();
        setAttached(true);
    }

    private void onDetach() {
        if (DEBUG) {
            Log.d(this.mTag, "onDetach");
        }
        setExpanded(false);
        checkForAttachedZenChange();
        setAttached(false);
        this.mAttachedZen = -1;
        this.mSessionZen = -1;
        this.mController.removeCallback(this.mZenCallback);
        setSessionExitCondition(null);
        this.mTransitionHelper.clear();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setAttached(boolean z) {
        this.mAttached = z;
    }

    public void onVisibilityAggregated(boolean z) {
        super.onVisibilityAggregated(z);
        if (z != this.mAttached) {
            if (z) {
                onAttach();
            } else {
                onDetach();
            }
        }
    }

    private void setSessionExitCondition(Condition condition) {
        if (!Objects.equals(condition, this.mSessionExitCondition)) {
            if (DEBUG) {
                String str = this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("mSessionExitCondition=");
                sb.append(getConditionId(condition));
                Log.d(str, sb.toString());
            }
            this.mSessionExitCondition = condition;
        }
    }

    private void checkForAttachedZenChange() {
        int selectedZen = getSelectedZen(-1);
        if (DEBUG) {
            String str = this.mTag;
            StringBuilder sb = new StringBuilder();
            sb.append("selectedZen=");
            sb.append(selectedZen);
            Log.d(str, sb.toString());
        }
        if (selectedZen != this.mAttachedZen) {
            if (DEBUG) {
                String str2 = this.mTag;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("attachedZen: ");
                sb2.append(this.mAttachedZen);
                sb2.append(" -> ");
                sb2.append(selectedZen);
                Log.d(str2, sb2.toString());
            }
            if (selectedZen == 2) {
                this.mPrefs.trackNoneSelected();
            }
        }
    }

    private void setExpanded(boolean z) {
        if (z != this.mExpanded) {
            if (DEBUG) {
                String str = this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("setExpanded ");
                sb.append(z);
                Log.d(str, sb.toString());
            }
            this.mExpanded = z;
            updateWidgets();
            fireExpanded();
        }
    }

    /* access modifiers changed from: protected */
    public void addZenConditions(int i) {
        for (int i2 = 0; i2 < i; i2++) {
            View inflate = this.mInflater.inflate(this.mZenModeButtonLayoutId, this.mEdit, false);
            inflate.setId(i2);
            this.mZenRadioGroup.addView(inflate);
            View inflate2 = this.mInflater.inflate(this.mZenModeConditionLayoutId, this.mEdit, false);
            inflate2.setId(i2 + i);
            this.mZenRadioGroupContent.addView(inflate2);
        }
    }

    public void init(ZenModeController zenModeController) {
        this.mController = zenModeController;
        addZenConditions(3);
        this.mSessionZen = getSelectedZen(-1);
        handleUpdateManualRule(this.mController.getManualRule());
        if (DEBUG) {
            String str = this.mTag;
            StringBuilder sb = new StringBuilder();
            sb.append("init mExitCondition=");
            sb.append(this.mExitCondition);
            Log.d(str, sb.toString());
        }
        hideAllConditions();
    }

    private void setExitCondition(Condition condition) {
        if (!Objects.equals(this.mExitCondition, condition)) {
            this.mExitCondition = condition;
            if (DEBUG) {
                String str = this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("mExitCondition=");
                sb.append(getConditionId(this.mExitCondition));
                Log.d(str, sb.toString());
            }
            updateWidgets();
        }
    }

    private static Uri getConditionId(Condition condition) {
        if (condition != null) {
            return condition.id;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public Uri getRealConditionId(Condition condition) {
        if (isForever(condition)) {
            return null;
        }
        return getConditionId(condition);
    }

    private static Condition copy(Condition condition) {
        if (condition == null) {
            return null;
        }
        return condition.copy();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void handleUpdateManualRule(ZenRule zenRule) {
        Condition condition;
        handleUpdateZen(zenRule != null ? zenRule.zenMode : 0);
        if (zenRule == null) {
            condition = null;
        } else {
            Condition condition2 = zenRule.condition;
            if (condition2 != null) {
                condition = condition2;
            } else {
                condition = createCondition(zenRule.conditionId);
            }
        }
        handleUpdateConditions(condition);
        setExitCondition(condition);
    }

    private Condition createCondition(Uri uri) {
        if (ZenModeConfig.isValidCountdownToAlarmConditionId(uri)) {
            return ZenModeConfig.toNextAlarmCondition(this.mContext, ZenModeConfig.tryParseCountdownConditionId(uri), ActivityManager.getCurrentUser());
        } else if (!ZenModeConfig.isValidCountdownConditionId(uri)) {
            return forever();
        } else {
            long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(uri);
            return ZenModeConfig.toTimeCondition(this.mContext, tryParseCountdownConditionId, (int) (((tryParseCountdownConditionId - System.currentTimeMillis()) + 30000) / 60000), ActivityManager.getCurrentUser(), false);
        }
    }

    private void handleUpdateZen(int i) {
        int i2 = this.mSessionZen;
        if (!(i2 == -1 || i2 == i)) {
            this.mSessionZen = i;
        }
        this.mZenButtons.setSelectedValue(Integer.valueOf(i), false);
        updateWidgets();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public int getSelectedZen(int i) {
        Object selectedValue = this.mZenButtons.getSelectedValue();
        return selectedValue != null ? ((Integer) selectedValue).intValue() : i;
    }

    /* access modifiers changed from: private */
    public void updateWidgets() {
        int i;
        if (this.mTransitionHelper.isTransitioning()) {
            this.mTransitionHelper.pendingUpdateWidgets();
            return;
        }
        int i2 = 0;
        int selectedZen = getSelectedZen(0);
        boolean z = true;
        boolean z2 = selectedZen == 1;
        boolean z3 = selectedZen == 2;
        boolean z4 = selectedZen == 3;
        if ((!z2 || this.mPrefs.mConfirmedPriorityIntroduction) && ((!z3 || this.mPrefs.mConfirmedSilenceIntroduction) && (!z4 || this.mPrefs.mConfirmedAlarmIntroduction))) {
            z = false;
        }
        this.mZenButtons.setVisibility(this.mHidden ? 8 : 0);
        this.mZenIntroduction.setVisibility(z ? 0 : 8);
        if (z) {
            if (z2) {
                i = C2017R$string.zen_priority_introduction;
            } else if (z4) {
                i = C2017R$string.zen_alarms_introduction;
            } else if (this.mVoiceCapable) {
                i = C2017R$string.zen_silence_introduction_voice;
            } else {
                i = C2017R$string.zen_silence_introduction;
            }
            this.mConfigurableTexts.add(this.mZenIntroductionMessage, i);
            this.mConfigurableTexts.update();
            this.mZenIntroductionCustomize.setVisibility(z2 ? 0 : 8);
        }
        String computeAlarmWarningText = computeAlarmWarningText(z3);
        TextView textView = this.mZenAlarmWarning;
        if (computeAlarmWarningText == null) {
            i2 = 8;
        }
        textView.setVisibility(i2);
        this.mZenAlarmWarning.setText(computeAlarmWarningText);
    }

    private String computeAlarmWarningText(boolean z) {
        int i;
        if (!z) {
            return null;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long nextAlarm = this.mController.getNextAlarm();
        if (nextAlarm < currentTimeMillis) {
            return null;
        }
        Condition condition = this.mSessionExitCondition;
        if (condition == null || isForever(condition)) {
            i = C2017R$string.zen_alarm_warning_indef;
        } else {
            long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(this.mSessionExitCondition.id);
            i = (tryParseCountdownConditionId <= currentTimeMillis || nextAlarm >= tryParseCountdownConditionId) ? 0 : C2017R$string.zen_alarm_warning;
        }
        if (i == 0) {
            return null;
        }
        boolean z2 = nextAlarm - currentTimeMillis < 86400000;
        boolean is24HourFormat = DateFormat.is24HourFormat(this.mContext, ActivityManager.getCurrentUser());
        String str = z2 ? is24HourFormat ? "Hm" : "hma" : is24HourFormat ? "EEEHm" : "EEEhma";
        return getResources().getString(i, new Object[]{getResources().getString(z2 ? C2017R$string.alarm_template : C2017R$string.alarm_template_far, new Object[]{DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), str), nextAlarm)})});
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void handleUpdateConditions(Condition condition) {
        if (!this.mTransitionHelper.isTransitioning()) {
            int i = 0;
            bind(forever(), this.mZenRadioGroupContent.getChildAt(0), 0);
            if (condition == null) {
                bindGenericCountdown();
                bindNextAlarm(getTimeUntilNextAlarmCondition());
            } else if (isForever(condition)) {
                getConditionTagAt(0).f87rb.setChecked(true);
                bindGenericCountdown();
                bindNextAlarm(getTimeUntilNextAlarmCondition());
            } else if (isAlarm(condition)) {
                bindGenericCountdown();
                bindNextAlarm(condition);
                getConditionTagAt(2).f87rb.setChecked(true);
            } else if (isCountdown(condition)) {
                bindNextAlarm(getTimeUntilNextAlarmCondition());
                bind(condition, this.mZenRadioGroupContent.getChildAt(1), 1);
                getConditionTagAt(1).f87rb.setChecked(true);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Invalid manual condition: ");
                sb.append(condition);
                Slog.wtf("ZenModePanel", sb.toString());
            }
            LinearLayout linearLayout = this.mZenConditions;
            if (this.mSessionZen == 0) {
                i = 8;
            }
            linearLayout.setVisibility(i);
        }
    }

    private void bindGenericCountdown() {
        int i = DEFAULT_BUCKET_INDEX;
        this.mBucketIndex = i;
        Condition timeCondition = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[i], ActivityManager.getCurrentUser());
        if (!this.mAttached || getConditionTagAt(1).condition == null) {
            bind(timeCondition, this.mZenRadioGroupContent.getChildAt(1), 1);
        }
    }

    private void bindNextAlarm(Condition condition) {
        View childAt = this.mZenRadioGroupContent.getChildAt(2);
        ConditionTag conditionTag = (ConditionTag) childAt.getTag();
        if (condition != null && (!this.mAttached || conditionTag == null || conditionTag.condition == null)) {
            bind(condition, childAt, 2);
        }
        ConditionTag conditionTag2 = (ConditionTag) childAt.getTag();
        int i = 0;
        boolean z = (conditionTag2 == null || conditionTag2.condition == null) ? false : true;
        this.mZenRadioGroup.getChildAt(2).setVisibility(z ? 0 : 4);
        if (!z) {
            i = 4;
        }
        childAt.setVisibility(i);
    }

    private Condition forever() {
        Condition condition = new Condition(this.mForeverId, foreverSummary(this.mContext), "", "", 0, 1, 0);
        return condition;
    }

    private static String foreverSummary(Context context) {
        return context.getString(17041413);
    }

    private Condition getTimeUntilNextAlarmCondition() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        setToMidnight(gregorianCalendar);
        gregorianCalendar.add(5, 6);
        long nextAlarm = this.mController.getNextAlarm();
        if (nextAlarm > 0) {
            GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
            gregorianCalendar2.setTimeInMillis(nextAlarm);
            setToMidnight(gregorianCalendar2);
            if (gregorianCalendar.compareTo(gregorianCalendar2) >= 0) {
                return ZenModeConfig.toNextAlarmCondition(this.mContext, nextAlarm, ActivityManager.getCurrentUser());
            }
        }
        return null;
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public ConditionTag getConditionTagAt(int i) {
        return (ConditionTag) this.mZenRadioGroupContent.getChildAt(i).getTag();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public int getVisibleConditions() {
        int i = 0;
        for (int i2 = 0; i2 < this.mZenRadioGroupContent.getChildCount(); i2++) {
            i += this.mZenRadioGroupContent.getChildAt(i2).getVisibility() == 0 ? 1 : 0;
        }
        return i;
    }

    private void hideAllConditions() {
        int childCount = this.mZenRadioGroupContent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.mZenRadioGroupContent.getChildAt(i).setVisibility(8);
        }
    }

    private static boolean isAlarm(Condition condition) {
        return condition != null && ZenModeConfig.isValidCountdownToAlarmConditionId(condition.id);
    }

    private static boolean isCountdown(Condition condition) {
        return condition != null && ZenModeConfig.isValidCountdownConditionId(condition.id);
    }

    private boolean isForever(Condition condition) {
        return condition != null && this.mForeverId.equals(condition.id);
    }

    private void bind(Condition condition, View view, int i) {
        String str;
        Condition condition2 = condition;
        final View view2 = view;
        final int i2 = i;
        if (condition2 != null) {
            boolean z = true;
            boolean z2 = condition2.state == 1;
            final ConditionTag conditionTag = view.getTag() != null ? (ConditionTag) view.getTag() : new ConditionTag();
            view2.setTag(conditionTag);
            boolean z3 = conditionTag.f87rb == null;
            if (conditionTag.f87rb == null) {
                conditionTag.f87rb = (RadioButton) this.mZenRadioGroup.getChildAt(i2);
            }
            conditionTag.condition = condition2;
            final Uri conditionId = getConditionId(condition);
            if (DEBUG) {
                String str2 = this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("bind i=");
                sb.append(this.mZenRadioGroupContent.indexOfChild(view2));
                sb.append(" first=");
                sb.append(z3);
                sb.append(" condition=");
                sb.append(conditionId);
                Log.d(str2, sb.toString());
            }
            conditionTag.f87rb.setEnabled(z2);
            conditionTag.f87rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    if (ZenModePanel.this.mExpanded && z) {
                        conditionTag.f87rb.setChecked(true);
                        if (ZenModePanel.DEBUG) {
                            String access$800 = ZenModePanel.this.mTag;
                            StringBuilder sb = new StringBuilder();
                            sb.append("onCheckedChanged ");
                            sb.append(conditionId);
                            Log.d(access$800, sb.toString());
                        }
                        MetricsLogger.action(ZenModePanel.this.mContext, 164);
                        ZenModePanel.this.select(conditionTag.condition);
                        ZenModePanel.this.announceConditionSelection(conditionTag);
                    }
                }
            });
            if (conditionTag.lines == null) {
                conditionTag.lines = view2.findViewById(16908290);
            }
            if (conditionTag.line1 == null) {
                TextView textView = (TextView) view2.findViewById(16908308);
                conditionTag.line1 = textView;
                this.mConfigurableTexts.add(textView);
            }
            if (conditionTag.line2 == null) {
                TextView textView2 = (TextView) view2.findViewById(16908309);
                conditionTag.line2 = textView2;
                this.mConfigurableTexts.add(textView2);
            }
            if (!TextUtils.isEmpty(condition2.line1)) {
                str = condition2.line1;
            } else {
                str = condition2.summary;
            }
            String str3 = condition2.line2;
            conditionTag.line1.setText(str);
            if (TextUtils.isEmpty(str3)) {
                conditionTag.line2.setVisibility(8);
            } else {
                conditionTag.line2.setVisibility(0);
                conditionTag.line2.setText(str3);
            }
            conditionTag.lines.setEnabled(z2);
            conditionTag.lines.setAlpha(z2 ? 1.0f : 0.4f);
            ImageView imageView = (ImageView) view2.findViewById(16908313);
            imageView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ZenModePanel.this.onClickTimeButton(view2, conditionTag, false, i2);
                }
            });
            ImageView imageView2 = (ImageView) view2.findViewById(16908314);
            imageView2.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    ZenModePanel.this.onClickTimeButton(view2, conditionTag, true, i2);
                }
            });
            conditionTag.lines.setOnClickListener(new OnClickListener(this) {
                public void onClick(View view) {
                    conditionTag.f87rb.setChecked(true);
                }
            });
            long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(conditionId);
            if (i2 == 2 || tryParseCountdownConditionId <= 0) {
                imageView.setVisibility(8);
                imageView2.setVisibility(8);
            } else {
                imageView.setVisibility(0);
                imageView2.setVisibility(0);
                int i3 = this.mBucketIndex;
                if (i3 > -1) {
                    imageView.setEnabled(i3 > 0);
                    if (this.mBucketIndex >= MINUTE_BUCKETS.length - 1) {
                        z = false;
                    }
                    imageView2.setEnabled(z);
                } else {
                    imageView.setEnabled(tryParseCountdownConditionId - System.currentTimeMillis() > ((long) (MIN_BUCKET_MINUTES * 60000)));
                    imageView2.setEnabled(!Objects.equals(condition2.summary, ZenModeConfig.toTimeCondition(this.mContext, MAX_BUCKET_MINUTES, ActivityManager.getCurrentUser()).summary));
                }
                imageView.setAlpha(imageView.isEnabled() ? 1.0f : 0.5f);
                imageView2.setAlpha(imageView2.isEnabled() ? 1.0f : 0.5f);
            }
            if (z3) {
                Interaction.register(conditionTag.f87rb, this.mInteractionCallback);
                Interaction.register(conditionTag.lines, this.mInteractionCallback);
                Interaction.register(imageView, this.mInteractionCallback);
                Interaction.register(imageView2, this.mInteractionCallback);
            }
            view2.setVisibility(0);
            return;
        }
        throw new IllegalArgumentException("condition must not be null");
    }

    /* access modifiers changed from: private */
    public void announceConditionSelection(ConditionTag conditionTag) {
        String str;
        int selectedZen = getSelectedZen(0);
        if (selectedZen == 1) {
            str = this.mContext.getString(C2017R$string.interruption_level_priority);
        } else if (selectedZen == 2) {
            str = this.mContext.getString(C2017R$string.interruption_level_none);
        } else if (selectedZen == 3) {
            str = this.mContext.getString(C2017R$string.interruption_level_alarms);
        } else {
            return;
        }
        announceForAccessibility(this.mContext.getString(C2017R$string.zen_mode_and_condition, new Object[]{str, conditionTag.line1.getText()}));
    }

    /* access modifiers changed from: private */
    public void onClickTimeButton(View view, ConditionTag conditionTag, boolean z, int i) {
        Condition condition;
        int i2;
        int i3;
        long j;
        ConditionTag conditionTag2 = conditionTag;
        boolean z2 = z;
        MetricsLogger.action(this.mContext, 163, z2);
        int length = MINUTE_BUCKETS.length;
        int i4 = this.mBucketIndex;
        int i5 = 0;
        int i6 = -1;
        if (i4 == -1) {
            long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(getConditionId(conditionTag2.condition));
            long currentTimeMillis = System.currentTimeMillis();
            while (true) {
                if (i5 >= length) {
                    condition = null;
                    break;
                }
                i2 = z2 ? i5 : (length - 1) - i5;
                i3 = MINUTE_BUCKETS[i2];
                j = currentTimeMillis + ((long) (60000 * i3));
                if ((!z2 || j <= tryParseCountdownConditionId) && (z2 || j >= tryParseCountdownConditionId)) {
                    i5++;
                }
            }
            this.mBucketIndex = i2;
            condition = ZenModeConfig.toTimeCondition(this.mContext, j, i3, ActivityManager.getCurrentUser(), false);
            if (condition == null) {
                int i7 = DEFAULT_BUCKET_INDEX;
                this.mBucketIndex = i7;
                condition = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[i7], ActivityManager.getCurrentUser());
            }
        } else {
            int i8 = length - 1;
            if (z2) {
                i6 = 1;
            }
            int max = Math.max(0, Math.min(i8, i4 + i6));
            this.mBucketIndex = max;
            condition = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[max], ActivityManager.getCurrentUser());
        }
        bind(condition, view, i);
        conditionTag2.f87rb.setChecked(true);
        select(condition);
        announceConditionSelection(conditionTag2);
    }

    /* access modifiers changed from: private */
    public void select(Condition condition) {
        if (DEBUG) {
            String str = this.mTag;
            StringBuilder sb = new StringBuilder();
            sb.append("select ");
            sb.append(condition);
            Log.d(str, sb.toString());
        }
        int i = this.mSessionZen;
        if (i == -1 || i == 0) {
            if (DEBUG) {
                Log.d(this.mTag, "Ignoring condition selection outside of manual zen");
            }
            return;
        }
        final Uri realConditionId = getRealConditionId(condition);
        if (this.mController != null) {
            AsyncTask.execute(new Runnable() {
                public void run() {
                    ZenModePanel.this.mController.setZen(ZenModePanel.this.mSessionZen, realConditionId, "ZenModePanel.selectCondition");
                }
            });
        }
        setExitCondition(condition);
        if (realConditionId == null) {
            this.mPrefs.setMinuteIndex(-1);
        } else if (isAlarm(condition) || isCountdown(condition)) {
            int i2 = this.mBucketIndex;
            if (i2 != -1) {
                this.mPrefs.setMinuteIndex(i2);
            }
        }
        setSessionExitCondition(copy(condition));
    }

    /* access modifiers changed from: private */
    public void fireInteraction() {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onInteraction();
        }
    }

    private void fireExpanded() {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onExpanded(this.mExpanded);
        }
    }
}
