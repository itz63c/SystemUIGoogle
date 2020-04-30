package com.android.systemui.statusbar.notification.row;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.metrics.LogMaker;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.service.notification.SnoozeCriterion;
import android.service.notification.StatusBarNotification;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.KeyValueListParser;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.C2005R$array;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2012R$integer;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2015R$plurals;
import com.android.systemui.C2017R$string;
import com.android.systemui.Interpolators;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper.SnoozeOption;
import com.android.systemui.statusbar.notification.row.NotificationGuts.GutsContent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationSnooze extends LinearLayout implements GutsContent, OnClickListener {
    private static final LogMaker OPTIONS_CLOSE_LOG = new LogMaker(1142).setType(2);
    private static final LogMaker OPTIONS_OPEN_LOG = new LogMaker(1142).setType(1);
    private static final LogMaker UNDO_LOG = new LogMaker(1141).setType(4);
    private static final int[] sAccessibilityActions = {C2011R$id.action_snooze_shorter, C2011R$id.action_snooze_short, C2011R$id.action_snooze_long, C2011R$id.action_snooze_longer};
    private int mCollapsedHeight;
    private SnoozeOption mDefaultOption;
    private View mDivider;
    private AnimatorSet mExpandAnimation;
    private ImageView mExpandButton;
    private boolean mExpanded;
    private NotificationGuts mGutsContainer;
    private MetricsLogger mMetricsLogger = new MetricsLogger();
    private KeyValueListParser mParser = new KeyValueListParser(',');
    private StatusBarNotification mSbn;
    private SnoozeOption mSelectedOption;
    private TextView mSelectedOptionText;
    private NotificationSwipeActionHelper mSnoozeListener;
    /* access modifiers changed from: private */
    public ViewGroup mSnoozeOptionContainer;
    private List<SnoozeOption> mSnoozeOptions;
    private boolean mSnoozing;
    private TextView mUndoButton;

    public class NotificationSnoozeOption implements SnoozeOption {
        private AccessibilityAction mAction;
        private CharSequence mConfirmation;
        private SnoozeCriterion mCriterion;
        private CharSequence mDescription;
        private int mMinutesToSnoozeFor;

        public NotificationSnoozeOption(NotificationSnooze notificationSnooze, SnoozeCriterion snoozeCriterion, int i, CharSequence charSequence, CharSequence charSequence2, AccessibilityAction accessibilityAction) {
            this.mCriterion = snoozeCriterion;
            this.mMinutesToSnoozeFor = i;
            this.mDescription = charSequence;
            this.mConfirmation = charSequence2;
            this.mAction = accessibilityAction;
        }

        public SnoozeCriterion getSnoozeCriterion() {
            return this.mCriterion;
        }

        public CharSequence getDescription() {
            return this.mDescription;
        }

        public CharSequence getConfirmation() {
            return this.mConfirmation;
        }

        public int getMinutesToSnoozeFor() {
            return this.mMinutesToSnoozeFor;
        }

        public AccessibilityAction getAccessibilityAction() {
            return this.mAction;
        }
    }

    public boolean isLeavebehind() {
        return true;
    }

    public boolean shouldBeSaved() {
        return true;
    }

    public NotificationSnooze(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public SnoozeOption getDefaultOption() {
        return this.mDefaultOption;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setKeyValueListParser(KeyValueListParser keyValueListParser) {
        this.mParser = keyValueListParser;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mCollapsedHeight = getResources().getDimensionPixelSize(C2009R$dimen.snooze_snackbar_min_height);
        findViewById(C2011R$id.notification_snooze).setOnClickListener(this);
        this.mSelectedOptionText = (TextView) findViewById(C2011R$id.snooze_option_default);
        TextView textView = (TextView) findViewById(C2011R$id.undo);
        this.mUndoButton = textView;
        textView.setOnClickListener(this);
        this.mExpandButton = (ImageView) findViewById(C2011R$id.expand_button);
        View findViewById = findViewById(C2011R$id.divider);
        this.mDivider = findViewById;
        findViewById.setAlpha(0.0f);
        ViewGroup viewGroup = (ViewGroup) findViewById(C2011R$id.snooze_options);
        this.mSnoozeOptionContainer = viewGroup;
        viewGroup.setVisibility(4);
        this.mSnoozeOptionContainer.setAlpha(0.0f);
        this.mSnoozeOptions = getDefaultSnoozeOptions();
        createOptionViews();
        setSelected(this.mDefaultOption, false);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        logOptionSelection(1137, this.mDefaultOption);
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        NotificationGuts notificationGuts = this.mGutsContainer;
        if (notificationGuts != null && notificationGuts.isExposed() && accessibilityEvent.getEventType() == 32) {
            accessibilityEvent.getText().add(this.mSelectedOptionText.getText());
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.addAction(new AccessibilityAction(C2011R$id.action_snooze_undo, getResources().getString(C2017R$string.snooze_undo)));
        int size = this.mSnoozeOptions.size();
        for (int i = 0; i < size; i++) {
            AccessibilityAction accessibilityAction = ((SnoozeOption) this.mSnoozeOptions.get(i)).getAccessibilityAction();
            if (accessibilityAction != null) {
                accessibilityNodeInfo.addAction(accessibilityAction);
            }
        }
    }

    public boolean performAccessibilityActionInternal(int i, Bundle bundle) {
        if (super.performAccessibilityActionInternal(i, bundle)) {
            return true;
        }
        if (i == C2011R$id.action_snooze_undo) {
            undoSnooze(this.mUndoButton);
            return true;
        }
        int i2 = 0;
        while (i2 < this.mSnoozeOptions.size()) {
            SnoozeOption snoozeOption = (SnoozeOption) this.mSnoozeOptions.get(i2);
            if (snoozeOption.getAccessibilityAction() == null || snoozeOption.getAccessibilityAction().getId() != i) {
                i2++;
            } else {
                setSelected(snoozeOption, true);
                return true;
            }
        }
        return false;
    }

    public void setSnoozeOptions(List<SnoozeCriterion> list) {
        if (list != null) {
            this.mSnoozeOptions.clear();
            this.mSnoozeOptions = getDefaultSnoozeOptions();
            int min = Math.min(1, list.size());
            for (int i = 0; i < min; i++) {
                SnoozeCriterion snoozeCriterion = (SnoozeCriterion) list.get(i);
                AccessibilityAction accessibilityAction = new AccessibilityAction(C2011R$id.action_snooze_assistant_suggestion_1, snoozeCriterion.getExplanation());
                List<SnoozeOption> list2 = this.mSnoozeOptions;
                NotificationSnoozeOption notificationSnoozeOption = new NotificationSnoozeOption(this, snoozeCriterion, 0, snoozeCriterion.getExplanation(), snoozeCriterion.getConfirmation(), accessibilityAction);
                list2.add(notificationSnoozeOption);
            }
            createOptionViews();
        }
    }

    public boolean isExpanded() {
        return this.mExpanded;
    }

    public void setSnoozeListener(NotificationSwipeActionHelper notificationSwipeActionHelper) {
        this.mSnoozeListener = notificationSwipeActionHelper;
    }

    public void setStatusBarNotification(StatusBarNotification statusBarNotification) {
        this.mSbn = statusBarNotification;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public ArrayList<SnoozeOption> getDefaultSnoozeOptions() {
        Resources resources = getContext().getResources();
        ArrayList<SnoozeOption> arrayList = new ArrayList<>();
        try {
            this.mParser.setString(Global.getString(getContext().getContentResolver(), "notification_snooze_options"));
        } catch (IllegalArgumentException unused) {
            Log.e("NotificationSnooze", "Bad snooze constants");
        }
        int i = this.mParser.getInt("default", resources.getInteger(C2012R$integer.config_notification_snooze_time_default));
        int[] intArray = this.mParser.getIntArray("options_array", resources.getIntArray(C2005R$array.config_notification_snooze_times));
        for (int i2 = 0; i2 < intArray.length; i2++) {
            int[] iArr = sAccessibilityActions;
            if (i2 >= iArr.length) {
                break;
            }
            int i3 = intArray[i2];
            SnoozeOption createOption = createOption(i3, iArr[i2]);
            if (i2 == 0 || i3 == i) {
                this.mDefaultOption = createOption;
            }
            arrayList.add(createOption);
        }
        return arrayList;
    }

    private SnoozeOption createOption(int i, int i2) {
        int i3;
        Resources resources = getResources();
        boolean z = i >= 60;
        if (z) {
            i3 = C2015R$plurals.snoozeHourOptions;
        } else {
            i3 = C2015R$plurals.snoozeMinuteOptions;
        }
        int i4 = z ? i / 60 : i;
        String quantityString = resources.getQuantityString(i3, i4, new Object[]{Integer.valueOf(i4)});
        String format = String.format(resources.getString(C2017R$string.snoozed_for_time), new Object[]{quantityString});
        AccessibilityAction accessibilityAction = new AccessibilityAction(i2, quantityString);
        int indexOf = format.indexOf(quantityString);
        if (indexOf == -1) {
            NotificationSnoozeOption notificationSnoozeOption = new NotificationSnoozeOption(this, null, i, quantityString, format, accessibilityAction);
            return notificationSnoozeOption;
        }
        SpannableString spannableString = new SpannableString(format);
        spannableString.setSpan(new StyleSpan(1), indexOf, quantityString.length() + indexOf, 0);
        NotificationSnoozeOption notificationSnoozeOption2 = new NotificationSnoozeOption(this, null, i, quantityString, spannableString, accessibilityAction);
        return notificationSnoozeOption2;
    }

    private void createOptionViews() {
        this.mSnoozeOptionContainer.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
        for (int i = 0; i < this.mSnoozeOptions.size(); i++) {
            SnoozeOption snoozeOption = (SnoozeOption) this.mSnoozeOptions.get(i);
            TextView textView = (TextView) layoutInflater.inflate(C2013R$layout.notification_snooze_option, this.mSnoozeOptionContainer, false);
            this.mSnoozeOptionContainer.addView(textView);
            textView.setText(snoozeOption.getDescription());
            textView.setTag(snoozeOption);
            textView.setOnClickListener(this);
        }
    }

    private void hideSelectedOption() {
        int childCount = this.mSnoozeOptionContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mSnoozeOptionContainer.getChildAt(i);
            childAt.setVisibility(childAt.getTag() == this.mSelectedOption ? 8 : 0);
        }
    }

    private void showSnoozeOptions(boolean z) {
        this.mExpandButton.setImageResource(z ? 17302360 : 17302419);
        if (this.mExpanded != z) {
            this.mExpanded = z;
            animateSnoozeOptions(z);
            NotificationGuts notificationGuts = this.mGutsContainer;
            if (notificationGuts != null) {
                notificationGuts.onHeightChanged();
            }
        }
    }

    private void animateSnoozeOptions(final boolean z) {
        Property property = View.ALPHA;
        AnimatorSet animatorSet = this.mExpandAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        View view = this.mDivider;
        float[] fArr = new float[2];
        fArr[0] = view.getAlpha();
        float f = 1.0f;
        fArr[1] = z ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, property, fArr);
        ViewGroup viewGroup = this.mSnoozeOptionContainer;
        float[] fArr2 = new float[2];
        fArr2[0] = viewGroup.getAlpha();
        if (!z) {
            f = 0.0f;
        }
        fArr2[1] = f;
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(viewGroup, property, fArr2);
        this.mSnoozeOptionContainer.setVisibility(0);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mExpandAnimation = animatorSet2;
        animatorSet2.playTogether(new Animator[]{ofFloat, ofFloat2});
        this.mExpandAnimation.setDuration(150);
        this.mExpandAnimation.setInterpolator(z ? Interpolators.ALPHA_IN : Interpolators.ALPHA_OUT);
        this.mExpandAnimation.addListener(new AnimatorListenerAdapter() {
            boolean cancelled = false;

            public void onAnimationCancel(Animator animator) {
                this.cancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (!z && !this.cancelled) {
                    NotificationSnooze.this.mSnoozeOptionContainer.setVisibility(4);
                    NotificationSnooze.this.mSnoozeOptionContainer.setAlpha(0.0f);
                }
            }
        });
        this.mExpandAnimation.start();
    }

    private void setSelected(SnoozeOption snoozeOption, boolean z) {
        this.mSelectedOption = snoozeOption;
        this.mSelectedOptionText.setText(snoozeOption.getConfirmation());
        showSnoozeOptions(false);
        hideSelectedOption();
        sendAccessibilityEvent(32);
        if (z) {
            logOptionSelection(1138, snoozeOption);
        }
    }

    private void logOptionSelection(int i, SnoozeOption snoozeOption) {
        this.mMetricsLogger.write(new LogMaker(i).setType(4).addTaggedData(1140, Integer.valueOf(this.mSnoozeOptions.indexOf(snoozeOption))).addTaggedData(1139, Long.valueOf(TimeUnit.MINUTES.toMillis((long) snoozeOption.getMinutesToSnoozeFor()))));
    }

    public void onClick(View view) {
        NotificationGuts notificationGuts = this.mGutsContainer;
        if (notificationGuts != null) {
            notificationGuts.resetFalsingCheck();
        }
        int id = view.getId();
        SnoozeOption snoozeOption = (SnoozeOption) view.getTag();
        if (snoozeOption != null) {
            setSelected(snoozeOption, true);
        } else if (id == C2011R$id.notification_snooze) {
            showSnoozeOptions(!this.mExpanded);
            this.mMetricsLogger.write(!this.mExpanded ? OPTIONS_OPEN_LOG : OPTIONS_CLOSE_LOG);
        } else {
            undoSnooze(view);
            this.mMetricsLogger.write(UNDO_LOG);
        }
    }

    private void undoSnooze(View view) {
        this.mSelectedOption = null;
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        this.mGutsContainer.getLocationOnScreen(iArr);
        view.getLocationOnScreen(iArr2);
        int width = view.getWidth() / 2;
        int i = (iArr2[0] - iArr[0]) + width;
        int height = (iArr2[1] - iArr[1]) + (view.getHeight() / 2);
        showSnoozeOptions(false);
        this.mGutsContainer.closeControls(i, height, false, false);
    }

    public int getActualHeight() {
        return this.mExpanded ? getHeight() : this.mCollapsedHeight;
    }

    public boolean willBeRemoved() {
        return this.mSnoozing;
    }

    public View getContentView() {
        setSelected(this.mDefaultOption, false);
        return this;
    }

    public void setGutsParent(NotificationGuts notificationGuts) {
        this.mGutsContainer = notificationGuts;
    }

    public boolean handleCloseControls(boolean z, boolean z2) {
        if (!this.mExpanded || z2) {
            NotificationSwipeActionHelper notificationSwipeActionHelper = this.mSnoozeListener;
            if (notificationSwipeActionHelper != null) {
                SnoozeOption snoozeOption = this.mSelectedOption;
                if (snoozeOption != null) {
                    this.mSnoozing = true;
                    notificationSwipeActionHelper.snooze(this.mSbn, snoozeOption);
                    return true;
                }
            }
            setSelected((SnoozeOption) this.mSnoozeOptions.get(0), false);
            return false;
        }
        showSnoozeOptions(false);
        return true;
    }
}