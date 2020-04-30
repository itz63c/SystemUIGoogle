package com.android.systemui.classifier;

import android.view.MotionEvent;
import java.util.HashMap;

public class AccelerationClassifier extends StrokeClassifier {
    private final HashMap<Stroke, Data> mStrokeMap = new HashMap<>();

    private static class Data {
        float maxSpeedRatio = 0.0f;
        Point previousPoint;
        float previousSpeed = 0.0f;

        public Data(Point point) {
            this.previousPoint = point;
        }

        public void addPoint(Point point) {
            float f = (float) ((point.timeOffsetNano - this.previousPoint.timeOffsetNano) + 1);
            float dist = this.previousPoint.dist(point) / f;
            if (f > 2.0E7f || f < 5000000.0f) {
                this.previousSpeed = 0.0f;
                this.previousPoint = point;
                return;
            }
            float f2 = this.previousSpeed;
            if (f2 != 0.0f) {
                this.maxSpeedRatio = Math.max(this.maxSpeedRatio, dist / f2);
            }
            this.previousSpeed = dist;
            this.previousPoint = point;
        }
    }

    public String getTag() {
        return "ACC";
    }

    public AccelerationClassifier(ClassifierData classifierData) {
        this.mClassifierData = classifierData;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.mStrokeMap.clear();
        }
        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
            Stroke stroke = this.mClassifierData.getStroke(motionEvent.getPointerId(i));
            Point point = (Point) stroke.getPoints().get(stroke.getPoints().size() - 1);
            if (this.mStrokeMap.get(stroke) == null) {
                this.mStrokeMap.put(stroke, new Data(point));
            } else {
                ((Data) this.mStrokeMap.get(stroke)).addPoint(point);
            }
        }
    }

    public float getFalseTouchEvaluation(int i, Stroke stroke) {
        return SpeedRatioEvaluator.evaluate(((Data) this.mStrokeMap.get(stroke)).maxSpeedRatio) * 2.0f;
    }
}
