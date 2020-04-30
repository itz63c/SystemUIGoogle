package com.google.android.systemui.columbus.sensors;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import org.tensorflow.lite.Interpreter;

public class TfClassifier {
    Interpreter interpreter = null;

    public TfClassifier(AssetManager assetManager, String str) {
        String str2 = "Columbus";
        try {
            AssetFileDescriptor openFd = assetManager.openFd(str);
            this.interpreter = new Interpreter(new FileInputStream(openFd.getFileDescriptor()).getChannel().map(MapMode.READ_ONLY, openFd.getStartOffset(), openFd.getDeclaredLength()));
            StringBuilder sb = new StringBuilder();
            sb.append("tflite file loaded: ");
            sb.append(str);
            Log.d(str2, sb.toString());
        } catch (Exception e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("load tflite file error: ");
            sb2.append(str);
            Log.d(str2, sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append("tflite file:");
            sb3.append(e.toString());
            Log.e(str2, sb3.toString());
        }
    }

    public ArrayList<ArrayList<Float>> predict(ArrayList<Float> arrayList, int i) {
        if (this.interpreter == null) {
            return new ArrayList<>();
        }
        float[] fArr = new float[arrayList.size()];
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            fArr[i2] = ((Float) arrayList.get(i2)).floatValue();
        }
        Object[] objArr = {fArr};
        HashMap hashMap = new HashMap();
        int[] iArr = new int[2];
        iArr[1] = i;
        iArr[0] = 1;
        hashMap.put(Integer.valueOf(0), (float[][]) Array.newInstance(float.class, iArr));
        this.interpreter.runForMultipleInputsOutputs(objArr, hashMap);
        float[][] fArr2 = (float[][]) hashMap.get(Integer.valueOf(0));
        ArrayList<ArrayList<Float>> arrayList2 = new ArrayList<>();
        ArrayList arrayList3 = new ArrayList();
        for (int i3 = 0; i3 < i; i3++) {
            arrayList3.add(Float.valueOf(fArr2[0][i3]));
        }
        arrayList2.add(arrayList3);
        return arrayList2;
    }
}
