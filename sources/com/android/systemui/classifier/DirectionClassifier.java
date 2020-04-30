package com.android.systemui.classifier;

public class DirectionClassifier extends StrokeClassifier {
    public String getTag() {
        return "DIR";
    }

    public DirectionClassifier(ClassifierData classifierData) {
    }

    public float getFalseTouchEvaluation(int i, Stroke stroke) {
        Point point = (Point) stroke.getPoints().get(0);
        Point point2 = (Point) stroke.getPoints().get(stroke.getPoints().size() - 1);
        return DirectionEvaluator.evaluate(point2.f35x - point.f35x, point2.f36y - point.f36y, i);
    }
}
