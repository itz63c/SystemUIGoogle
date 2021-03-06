package com.android.systemui.classifier;

import android.os.Build;
import android.os.SystemProperties;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpeedAnglesClassifier extends StrokeClassifier {
    public static final boolean VERBOSE = SystemProperties.getBoolean("debug.falsing_log.spd_ang", Build.IS_DEBUGGABLE);
    private HashMap<Stroke, Data> mStrokeMap = new HashMap<>();

    private static class Data {
        private float mAcceleratingAngles = 0.0f;
        private float mAnglesCount = 0.0f;
        private float mCount = 1.0f;
        private float mDist = 0.0f;
        private List<Point> mLastThreePoints = new ArrayList();
        private float mPreviousAngle = 3.1415927f;
        private Point mPreviousPoint = null;
        private float mSum = 0.0f;
        private float mSumSquares = 0.0f;

        public void addPoint(Point point) {
            Point point2 = this.mPreviousPoint;
            if (point2 != null) {
                this.mDist += point2.dist(point);
            }
            this.mPreviousPoint = point;
            Point point3 = new Point(((float) point.timeOffsetNano) / 1.0E8f, this.mDist / 1.0f);
            if (!this.mLastThreePoints.isEmpty()) {
                List<Point> list = this.mLastThreePoints;
                if (((Point) list.get(list.size() - 1)).equals(point3)) {
                    return;
                }
            }
            this.mLastThreePoints.add(point3);
            if (this.mLastThreePoints.size() == 4) {
                this.mLastThreePoints.remove(0);
                float angle = ((Point) this.mLastThreePoints.get(1)).getAngle((Point) this.mLastThreePoints.get(0), (Point) this.mLastThreePoints.get(2));
                this.mAnglesCount += 1.0f;
                if (angle >= 2.8274336f) {
                    this.mAcceleratingAngles += 1.0f;
                }
                float f = angle - this.mPreviousAngle;
                this.mSum += f;
                this.mSumSquares += f * f;
                this.mCount = (float) (((double) this.mCount) + 1.0d);
                this.mPreviousAngle = angle;
            }
        }

        public float getAnglesVariance() {
            float f = this.mSumSquares;
            float f2 = this.mCount;
            float f3 = f / f2;
            float f4 = this.mSum;
            float f5 = f3 - ((f4 / f2) * (f4 / f2));
            if (SpeedAnglesClassifier.VERBOSE) {
                StringBuilder sb = new StringBuilder();
                sb.append("getAnglesVariance: sum^2=");
                sb.append(this.mSumSquares);
                sb.append(" count=");
                sb.append(this.mCount);
                sb.append(" result=");
                sb.append(f5);
                FalsingLog.m22i("SPD_ANG", sb.toString());
            }
            return f5;
        }

        public float getAnglesPercentage() {
            float f = this.mAnglesCount;
            if (f == 0.0f) {
                return 1.0f;
            }
            float f2 = this.mAcceleratingAngles / f;
            if (SpeedAnglesClassifier.VERBOSE) {
                StringBuilder sb = new StringBuilder();
                sb.append("getAnglesPercentage: angles=");
                sb.append(this.mAcceleratingAngles);
                sb.append(" count=");
                sb.append(this.mAnglesCount);
                sb.append(" result=");
                sb.append(f2);
                FalsingLog.m22i("SPD_ANG", sb.toString());
            }
            return f2;
        }
    }

    public String getTag() {
        return "SPD_ANG";
    }

    public SpeedAnglesClassifier(ClassifierData classifierData) {
        this.mClassifierData = classifierData;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mStrokeMap.clear();
        }
        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
            Stroke stroke = this.mClassifierData.getStroke(motionEvent.getPointerId(i));
            if (this.mStrokeMap.get(stroke) == null) {
                this.mStrokeMap.put(stroke, new Data());
            }
            if (!(actionMasked == 1 || actionMasked == 3 || (actionMasked == 6 && i == motionEvent.getActionIndex()))) {
                ((Data) this.mStrokeMap.get(stroke)).addPoint((Point) stroke.getPoints().get(stroke.getPoints().size() - 1));
            }
        }
    }

    public float getFalseTouchEvaluation(int i, Stroke stroke) {
        Data data = (Data) this.mStrokeMap.get(stroke);
        return SpeedVarianceEvaluator.evaluate(data.getAnglesVariance()) + SpeedAnglesPercentageEvaluator.evaluate(data.getAnglesPercentage());
    }
}
