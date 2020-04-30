package com.android.systemui.classifier;

import android.content.Context;
import android.database.ContentObserver;
import android.hardware.SensorEvent;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Global;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import com.android.systemui.C2007R$bool;
import java.util.ArrayDeque;

public class HumanInteractionClassifier extends Classifier {
    private static HumanInteractionClassifier sInstance;
    private final ArrayDeque<MotionEvent> mBufferedEvents = new ArrayDeque<>();
    private final Context mContext;
    private int mCurrentType = 7;
    private final float mDpi;
    private boolean mEnableClassifier = false;
    private final GestureClassifier[] mGestureClassifiers;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final HistoryEvaluator mHistoryEvaluator;
    protected final ContentObserver mSettingsObserver = new ContentObserver(this.mHandler) {
        public void onChange(boolean z) {
            HumanInteractionClassifier.this.updateConfiguration();
        }
    };
    private final StrokeClassifier[] mStrokeClassifiers;

    public String getTag() {
        return "HIC";
    }

    private HumanInteractionClassifier(Context context) {
        this.mContext = context;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float f = (displayMetrics.xdpi + displayMetrics.ydpi) / 2.0f;
        this.mDpi = f;
        this.mClassifierData = new ClassifierData(f);
        this.mHistoryEvaluator = new HistoryEvaluator();
        this.mStrokeClassifiers = new StrokeClassifier[]{new AnglesClassifier(this.mClassifierData), new SpeedClassifier(this.mClassifierData), new DurationCountClassifier(this.mClassifierData), new EndPointRatioClassifier(this.mClassifierData), new EndPointLengthClassifier(this.mClassifierData), new AccelerationClassifier(this.mClassifierData), new SpeedAnglesClassifier(this.mClassifierData), new LengthCountClassifier(this.mClassifierData), new DirectionClassifier(this.mClassifierData)};
        this.mGestureClassifiers = new GestureClassifier[]{new PointerCountClassifier(this.mClassifierData), new ProximityClassifier(this.mClassifierData)};
        this.mContext.getContentResolver().registerContentObserver(Global.getUriFor("HIC_enable"), false, this.mSettingsObserver, -1);
        updateConfiguration();
    }

    public static HumanInteractionClassifier getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new HumanInteractionClassifier(context);
        }
        return sInstance;
    }

    /* access modifiers changed from: private */
    public void updateConfiguration() {
        this.mEnableClassifier = Global.getInt(this.mContext.getContentResolver(), "HIC_enable", this.mContext.getResources().getBoolean(C2007R$bool.config_lockscreenAntiFalsingClassifierEnabled) ? 1 : 0) != 0;
    }

    public void setType(int i) {
        this.mCurrentType = i;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        if (this.mEnableClassifier) {
            int i = this.mCurrentType;
            if (i == 2 || i == 9) {
                this.mBufferedEvents.add(MotionEvent.obtain(motionEvent));
                Point point = new Point(motionEvent.getX() / this.mDpi, motionEvent.getY() / this.mDpi);
                while (point.dist(new Point(((MotionEvent) this.mBufferedEvents.getFirst()).getX() / this.mDpi, ((MotionEvent) this.mBufferedEvents.getFirst()).getY() / this.mDpi)) > 0.1f) {
                    addTouchEvent((MotionEvent) this.mBufferedEvents.getFirst());
                    this.mBufferedEvents.remove();
                }
                if (motionEvent.getActionMasked() == 1) {
                    ((MotionEvent) this.mBufferedEvents.getFirst()).setAction(1);
                    addTouchEvent((MotionEvent) this.mBufferedEvents.getFirst());
                    this.mBufferedEvents.clear();
                }
            } else {
                addTouchEvent(motionEvent);
            }
        }
    }

    private void addTouchEvent(MotionEvent motionEvent) {
        String str;
        String str2;
        String str3;
        StringBuilder sb;
        float f;
        GestureClassifier[] gestureClassifierArr;
        int i;
        MotionEvent motionEvent2 = motionEvent;
        if (this.mClassifierData.update(motionEvent2)) {
            for (StrokeClassifier onTouchEvent : this.mStrokeClassifiers) {
                onTouchEvent.onTouchEvent(motionEvent2);
            }
            for (GestureClassifier onTouchEvent2 : this.mGestureClassifiers) {
                onTouchEvent2.onTouchEvent(motionEvent2);
            }
            int size = this.mClassifierData.getEndingStrokes().size();
            int i2 = 0;
            while (true) {
                str = "=";
                str2 = " ";
                str3 = " addTouchEvent";
                sb = null;
                f = 0.0f;
                if (i2 >= size) {
                    break;
                }
                Stroke stroke = (Stroke) this.mClassifierData.getEndingStrokes().get(i2);
                if (FalsingLog.ENABLED) {
                    sb = new StringBuilder("stroke");
                }
                StrokeClassifier[] strokeClassifierArr = this.mStrokeClassifiers;
                int length = strokeClassifierArr.length;
                int i3 = 0;
                while (i3 < length) {
                    StrokeClassifier strokeClassifier = strokeClassifierArr[i3];
                    float falseTouchEvaluation = strokeClassifier.getFalseTouchEvaluation(this.mCurrentType, stroke);
                    if (FalsingLog.ENABLED) {
                        String tag = strokeClassifier.getTag();
                        i = size;
                        StringBuilder append = sb.append(str2);
                        if (falseTouchEvaluation < 1.0f) {
                            tag = tag.toLowerCase();
                        }
                        append.append(tag);
                        append.append(str);
                        append.append(falseTouchEvaluation);
                    } else {
                        i = size;
                    }
                    f += falseTouchEvaluation;
                    i3++;
                    size = i;
                }
                int i4 = size;
                if (FalsingLog.ENABLED) {
                    FalsingLog.m22i(str3, sb.toString());
                }
                this.mHistoryEvaluator.addStroke(f);
                i2++;
                size = i4;
            }
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 1 || actionMasked == 3) {
                if (FalsingLog.ENABLED) {
                    sb = new StringBuilder("gesture");
                }
                for (GestureClassifier gestureClassifier : this.mGestureClassifiers) {
                    float falseTouchEvaluation2 = gestureClassifier.getFalseTouchEvaluation(this.mCurrentType);
                    if (FalsingLog.ENABLED) {
                        String tag2 = gestureClassifier.getTag();
                        StringBuilder append2 = sb.append(str2);
                        if (falseTouchEvaluation2 < 1.0f) {
                            tag2 = tag2.toLowerCase();
                        }
                        append2.append(tag2);
                        append2.append(str);
                        append2.append(falseTouchEvaluation2);
                    }
                    f += falseTouchEvaluation2;
                }
                if (FalsingLog.ENABLED) {
                    FalsingLog.m22i(str3, sb.toString());
                }
                this.mHistoryEvaluator.addGesture(f);
                setType(7);
            }
            this.mClassifierData.cleanUp(motionEvent2);
        }
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        for (StrokeClassifier onSensorChanged : this.mStrokeClassifiers) {
            onSensorChanged.onSensorChanged(sensorEvent);
        }
        for (GestureClassifier onSensorChanged2 : this.mGestureClassifiers) {
            onSensorChanged2.onSensorChanged(sensorEvent);
        }
    }

    public boolean isFalseTouch() {
        boolean z = false;
        if (this.mEnableClassifier) {
            float evaluation = this.mHistoryEvaluator.getEvaluation();
            if (evaluation >= 5.0f) {
                z = true;
            }
            if (FalsingLog.ENABLED) {
                StringBuilder sb = new StringBuilder();
                sb.append("eval=");
                sb.append(evaluation);
                sb.append(" result=");
                sb.append(z ? 1 : 0);
                FalsingLog.m22i("isFalseTouch", sb.toString());
            }
        }
        return z;
    }

    public boolean isEnabled() {
        return this.mEnableClassifier;
    }
}
