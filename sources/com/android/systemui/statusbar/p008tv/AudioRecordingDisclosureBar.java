package com.android.systemui.statusbar.p008tv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OnOpActiveChangedListener;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import com.android.systemui.C2005R$array;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/* renamed from: com.android.systemui.statusbar.tv.AudioRecordingDisclosureBar */
class AudioRecordingDisclosureBar {
    private final Set<String> mActiveAudioRecordingPackages = new ArraySet();
    private View mBgRight;
    /* access modifiers changed from: private */
    public final Context mContext;
    private View mIcon;
    private View mIconContainerBg;
    private View mIconTextsContainer;
    /* access modifiers changed from: private */
    public View mIndicatorView;
    private final Queue<String> mPendingNotificationPackages = new LinkedList();
    private final Set<String> mSessionNotifiedPackages = new ArraySet();
    private int mState = 0;
    private TextView mTextView;
    private View mTextsContainers;

    /* renamed from: com.android.systemui.statusbar.tv.AudioRecordingDisclosureBar$OnActiveRecordingListener */
    private class OnActiveRecordingListener implements OnOpActiveChangedListener {
        private final Set<String> mExemptApps;

        private OnActiveRecordingListener() {
            this.mExemptApps = new ArraySet(Arrays.asList(AudioRecordingDisclosureBar.this.mContext.getResources().getStringArray(C2005R$array.audio_recording_disclosure_exempt_apps)));
        }

        public void onOpActiveChanged(String str, int i, String str2, boolean z) {
            if (!this.mExemptApps.contains(str2)) {
                if (z) {
                    AudioRecordingDisclosureBar.this.onStartedRecording(str2);
                } else {
                    AudioRecordingDisclosureBar.this.onDoneRecording(str2);
                }
            }
        }
    }

    AudioRecordingDisclosureBar(Context context) {
        this.mContext = context;
    }

    /* access modifiers changed from: 0000 */
    public void start() {
        ((AppOpsManager) this.mContext.getSystemService("appops")).startWatchingActive(new String[]{"android:record_audio"}, this.mContext.getMainExecutor(), new OnActiveRecordingListener());
    }

    /* access modifiers changed from: private */
    public void onStartedRecording(String str) {
        if (this.mActiveAudioRecordingPackages.add(str) && this.mSessionNotifiedPackages.add(str)) {
            switch (this.mState) {
                case 0:
                    show(str);
                    break;
                case 1:
                case 2:
                case 3:
                case 5:
                case 6:
                    this.mPendingNotificationPackages.add(str);
                    break;
                case 4:
                    expand(str);
                    break;
            }
        }
    }

    /* access modifiers changed from: private */
    public void onDoneRecording(String str) {
        if (this.mActiveAudioRecordingPackages.remove(str) && this.mState == 4 && this.mActiveAudioRecordingPackages.isEmpty()) {
            this.mSessionNotifiedPackages.clear();
            hide();
        }
    }

    private void show(String str) {
        View inflate = LayoutInflater.from(this.mContext).inflate(C2013R$layout.tv_audio_recording_indicator, null);
        this.mIndicatorView = inflate;
        View findViewById = inflate.findViewById(C2011R$id.icon_texts_container);
        this.mIconTextsContainer = findViewById;
        this.mIconContainerBg = findViewById.findViewById(C2011R$id.icon_container_bg);
        this.mIcon = this.mIconTextsContainer.findViewById(C2011R$id.icon_mic);
        View findViewById2 = this.mIconTextsContainer.findViewById(C2011R$id.texts_container);
        this.mTextsContainers = findViewById2;
        this.mTextView = (TextView) findViewById2.findViewById(C2011R$id.text);
        this.mBgRight = this.mIndicatorView.findViewById(C2011R$id.bg_right);
        String applicationLabel = getApplicationLabel(str);
        this.mTextView.setText(this.mContext.getString(C2017R$string.app_accessed_mic, new Object[]{applicationLabel}));
        this.mIndicatorView.setVisibility(4);
        this.mIndicatorView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                AudioRecordingDisclosureBar.this.mIndicatorView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = AudioRecordingDisclosureBar.this.mIndicatorView.getWidth();
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(600);
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(AudioRecordingDisclosureBar.this.mIndicatorView, View.TRANSLATION_X, new float[]{(float) width, 0.0f}), ObjectAnimator.ofFloat(AudioRecordingDisclosureBar.this.mIndicatorView, View.ALPHA, new float[]{0.0f, 1.0f})});
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator, boolean z) {
                        AudioRecordingDisclosureBar.this.mIndicatorView.setVisibility(0);
                    }

                    public void onAnimationEnd(Animator animator) {
                        AudioRecordingDisclosureBar.this.startPulsatingAnimation();
                        AudioRecordingDisclosureBar.this.onExpanded();
                    }
                });
                animatorSet.start();
            }
        });
        LayoutParams layoutParams = new LayoutParams(-2, -2, 2006, 8, -3);
        layoutParams.gravity = 53;
        layoutParams.setTitle("MicrophoneCaptureIndicator");
        layoutParams.packageName = this.mContext.getPackageName();
        ((WindowManager) this.mContext.getSystemService("window")).addView(this.mIndicatorView, layoutParams);
        this.mState = 1;
    }

    private void expand(String str) {
        String applicationLabel = getApplicationLabel(str);
        this.mTextView.setText(this.mContext.getString(C2017R$string.app_accessed_mic, new Object[]{applicationLabel}));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mIconTextsContainer, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.mIconContainerBg, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.mTextsContainers, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.mBgRight, View.ALPHA, new float[]{1.0f})});
        animatorSet.setDuration(600);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AudioRecordingDisclosureBar.this.onExpanded();
            }
        });
        animatorSet.start();
        this.mState = 5;
    }

    /* access modifiers changed from: private */
    public void minimize() {
        int width = this.mTextsContainers.getWidth();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mIconTextsContainer, View.TRANSLATION_X, new float[]{(float) width}), ObjectAnimator.ofFloat(this.mIconContainerBg, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.mTextsContainers, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.mBgRight, View.ALPHA, new float[]{0.0f})});
        animatorSet.setDuration(600);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AudioRecordingDisclosureBar.this.onMinimized();
            }
        });
        animatorSet.start();
        this.mState = 3;
    }

    private void hide() {
        int width = this.mIndicatorView.getWidth() - ((int) this.mIconTextsContainer.getTranslationX());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mIndicatorView, View.TRANSLATION_X, new float[]{(float) width}), ObjectAnimator.ofFloat(this.mIcon, View.ALPHA, new float[]{0.0f})});
        animatorSet.setDuration(600);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AudioRecordingDisclosureBar.this.onHidden();
            }
        });
        animatorSet.start();
        this.mState = 6;
    }

    /* access modifiers changed from: private */
    public void onExpanded() {
        this.mState = 2;
        this.mIndicatorView.postDelayed(new Runnable() {
            public final void run() {
                AudioRecordingDisclosureBar.this.minimize();
            }
        }, 3000);
    }

    /* access modifiers changed from: private */
    public void onMinimized() {
        this.mState = 4;
        if (!this.mPendingNotificationPackages.isEmpty()) {
            expand((String) this.mPendingNotificationPackages.poll());
        } else if (this.mActiveAudioRecordingPackages.isEmpty()) {
            this.mSessionNotifiedPackages.clear();
            hide();
        }
    }

    /* access modifiers changed from: private */
    public void onHidden() {
        ((WindowManager) this.mContext.getSystemService("window")).removeView(this.mIndicatorView);
        this.mIndicatorView = null;
        this.mIconTextsContainer = null;
        this.mIconContainerBg = null;
        this.mIcon = null;
        this.mTextsContainers = null;
        this.mTextView = null;
        this.mBgRight = null;
        this.mState = 0;
        if (!this.mPendingNotificationPackages.isEmpty()) {
            show((String) this.mPendingNotificationPackages.poll());
        }
    }

    /* access modifiers changed from: private */
    public void startPulsatingAnimation() {
        ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(this.mIconTextsContainer.findViewById(C2011R$id.pulsating_circle), new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(View.SCALE_X, new float[]{1.25f}), PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[]{1.25f})});
        ofPropertyValuesHolder.setDuration(1000);
        ofPropertyValuesHolder.setRepeatCount(-1);
        ofPropertyValuesHolder.setRepeatMode(2);
        ofPropertyValuesHolder.start();
    }

    private String getApplicationLabel(String str) {
        PackageManager packageManager = this.mContext.getPackageManager();
        try {
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, 0)).toString();
        } catch (NameNotFoundException unused) {
            return str;
        }
    }
}
