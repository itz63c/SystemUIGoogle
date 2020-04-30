package org.tensorflow.lite;

public enum DataType {
    FLOAT32(1),
    INT32(2),
    UINT8(3),
    INT64(4),
    STRING(5);
    
    private static final DataType[] values = null;
    private final int value;

    /* renamed from: org.tensorflow.lite.DataType$1 */
    static /* synthetic */ class C20001 {
        static final /* synthetic */ int[] $SwitchMap$org$tensorflow$lite$DataType = null;

        /* JADX WARNING: Can't wrap try/catch for region: R(12:0|1|2|3|4|5|6|7|8|9|10|12) */
        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                org.tensorflow.lite.DataType[] r0 = org.tensorflow.lite.DataType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$org$tensorflow$lite$DataType = r0
                org.tensorflow.lite.DataType r1 = org.tensorflow.lite.DataType.FLOAT32     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$org$tensorflow$lite$DataType     // Catch:{ NoSuchFieldError -> 0x001d }
                org.tensorflow.lite.DataType r1 = org.tensorflow.lite.DataType.INT32     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$org$tensorflow$lite$DataType     // Catch:{ NoSuchFieldError -> 0x0028 }
                org.tensorflow.lite.DataType r1 = org.tensorflow.lite.DataType.UINT8     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$org$tensorflow$lite$DataType     // Catch:{ NoSuchFieldError -> 0x0033 }
                org.tensorflow.lite.DataType r1 = org.tensorflow.lite.DataType.INT64     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = $SwitchMap$org$tensorflow$lite$DataType     // Catch:{ NoSuchFieldError -> 0x003e }
                org.tensorflow.lite.DataType r1 = org.tensorflow.lite.DataType.STRING     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.tensorflow.lite.DataType.C20001.<clinit>():void");
        }
    }

    static {
        values = values();
    }

    private DataType(int i) {
        this.value = i;
    }

    public int byteSize() {
        int i = C20001.$SwitchMap$org$tensorflow$lite$DataType[ordinal()];
        if (i == 1 || i == 2) {
            return 4;
        }
        if (i == 3) {
            return 1;
        }
        if (i == 4) {
            return 8;
        }
        if (i == 5) {
            return -1;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("DataType error: DataType ");
        sb.append(this);
        sb.append(" is not supported yet");
        throw new IllegalArgumentException(sb.toString());
    }

    static DataType fromC(int i) {
        DataType[] dataTypeArr;
        for (DataType dataType : values) {
            if (dataType.value == i) {
                return dataType;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("DataType error: DataType ");
        sb.append(i);
        sb.append(" is not recognized in Java (version ");
        sb.append(TensorFlowLite.runtimeVersion());
        sb.append(")");
        throw new IllegalArgumentException(sb.toString());
    }
}
