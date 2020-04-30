package com.android.systemui.log;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: LogMessageImpl.kt */
public final class LogMessageImpl implements LogMessage {
    public static final Factory Factory = new Factory(null);
    private boolean bool1;
    private boolean bool2;
    private boolean bool3;
    private boolean bool4;
    private double double1;
    private int int1;
    private int int2;
    private LogLevel level;
    private long long1;
    private long long2;
    private Function1<? super LogMessage, String> printer;
    private String str1;
    private String str2;
    private String str3;
    private String tag;
    private long timestamp;

    /* compiled from: LogMessageImpl.kt */
    public static final class Factory {
        private Factory() {
        }

        public /* synthetic */ Factory(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final LogMessageImpl create() {
            LogMessageImpl logMessageImpl = new LogMessageImpl(LogLevel.DEBUG, "UnknownTag", 0, LogMessageImplKt.DEFAULT_RENDERER, null, null, null, 0, 0, 0, 0, 0.0d, false, false, false, false);
            return logMessageImpl;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00c8, code lost:
        if (getBool4() == r5.getBool4()) goto L_0x00cd;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r5) {
        /*
            r4 = this;
            if (r4 == r5) goto L_0x00cd
            boolean r0 = r5 instanceof com.android.systemui.log.LogMessageImpl
            if (r0 == 0) goto L_0x00cb
            com.android.systemui.log.LogMessageImpl r5 = (com.android.systemui.log.LogMessageImpl) r5
            com.android.systemui.log.LogLevel r0 = r4.getLevel()
            com.android.systemui.log.LogLevel r1 = r5.getLevel()
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x00cb
            java.lang.String r0 = r4.getTag()
            java.lang.String r1 = r5.getTag()
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x00cb
            long r0 = r4.getTimestamp()
            long r2 = r5.getTimestamp()
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x00cb
            kotlin.jvm.functions.Function1 r0 = r4.getPrinter()
            kotlin.jvm.functions.Function1 r1 = r5.getPrinter()
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x00cb
            java.lang.String r0 = r4.getStr1()
            java.lang.String r1 = r5.getStr1()
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x00cb
            java.lang.String r0 = r4.getStr2()
            java.lang.String r1 = r5.getStr2()
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x00cb
            java.lang.String r0 = r4.getStr3()
            java.lang.String r1 = r5.getStr3()
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x00cb
            int r0 = r4.getInt1()
            int r1 = r5.getInt1()
            if (r0 != r1) goto L_0x00cb
            int r0 = r4.getInt2()
            int r1 = r5.getInt2()
            if (r0 != r1) goto L_0x00cb
            long r0 = r4.getLong1()
            long r2 = r5.getLong1()
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x00cb
            long r0 = r4.getLong2()
            long r2 = r5.getLong2()
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x00cb
            double r0 = r4.getDouble1()
            double r2 = r5.getDouble1()
            int r0 = java.lang.Double.compare(r0, r2)
            if (r0 != 0) goto L_0x00cb
            boolean r0 = r4.getBool1()
            boolean r1 = r5.getBool1()
            if (r0 != r1) goto L_0x00cb
            boolean r0 = r4.getBool2()
            boolean r1 = r5.getBool2()
            if (r0 != r1) goto L_0x00cb
            boolean r0 = r4.getBool3()
            boolean r1 = r5.getBool3()
            if (r0 != r1) goto L_0x00cb
            boolean r4 = r4.getBool4()
            boolean r5 = r5.getBool4()
            if (r4 != r5) goto L_0x00cb
            goto L_0x00cd
        L_0x00cb:
            r4 = 0
            return r4
        L_0x00cd:
            r4 = 1
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.log.LogMessageImpl.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        LogLevel level2 = getLevel();
        int i = 0;
        int hashCode = (level2 != null ? level2.hashCode() : 0) * 31;
        String tag2 = getTag();
        int hashCode2 = (((hashCode + (tag2 != null ? tag2.hashCode() : 0)) * 31) + Long.hashCode(getTimestamp())) * 31;
        Function1 printer2 = getPrinter();
        int hashCode3 = (hashCode2 + (printer2 != null ? printer2.hashCode() : 0)) * 31;
        String str12 = getStr1();
        int hashCode4 = (hashCode3 + (str12 != null ? str12.hashCode() : 0)) * 31;
        String str22 = getStr2();
        int hashCode5 = (hashCode4 + (str22 != null ? str22.hashCode() : 0)) * 31;
        String str32 = getStr3();
        if (str32 != null) {
            i = str32.hashCode();
        }
        int hashCode6 = (((((((((((hashCode5 + i) * 31) + Integer.hashCode(getInt1())) * 31) + Integer.hashCode(getInt2())) * 31) + Long.hashCode(getLong1())) * 31) + Long.hashCode(getLong2())) * 31) + Double.hashCode(getDouble1())) * 31;
        int bool12 = getBool1();
        int i2 = 1;
        if (bool12 != 0) {
            bool12 = 1;
        }
        int i3 = (hashCode6 + bool12) * 31;
        int bool22 = getBool2();
        if (bool22 != 0) {
            bool22 = 1;
        }
        int i4 = (i3 + bool22) * 31;
        int bool32 = getBool3();
        if (bool32 != 0) {
            bool32 = 1;
        }
        int i5 = (i4 + bool32) * 31;
        boolean bool42 = getBool4();
        if (!bool42) {
            i2 = bool42;
        }
        return i5 + i2;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LogMessageImpl(level=");
        sb.append(getLevel());
        sb.append(", tag=");
        sb.append(getTag());
        sb.append(", timestamp=");
        sb.append(getTimestamp());
        sb.append(", printer=");
        sb.append(getPrinter());
        sb.append(", str1=");
        sb.append(getStr1());
        sb.append(", str2=");
        sb.append(getStr2());
        sb.append(", str3=");
        sb.append(getStr3());
        sb.append(", int1=");
        sb.append(getInt1());
        sb.append(", int2=");
        sb.append(getInt2());
        sb.append(", long1=");
        sb.append(getLong1());
        sb.append(", long2=");
        sb.append(getLong2());
        sb.append(", double1=");
        sb.append(getDouble1());
        sb.append(", bool1=");
        sb.append(getBool1());
        sb.append(", bool2=");
        sb.append(getBool2());
        sb.append(", bool3=");
        sb.append(getBool3());
        sb.append(", bool4=");
        sb.append(getBool4());
        sb.append(")");
        return sb.toString();
    }

    public LogMessageImpl(LogLevel logLevel, String str, long j, Function1<? super LogMessage, String> function1, String str4, String str5, String str6, int i, int i2, long j2, long j3, double d, boolean z, boolean z2, boolean z3, boolean z4) {
        LogLevel logLevel2 = logLevel;
        String str7 = str;
        Function1<? super LogMessage, String> function12 = function1;
        Intrinsics.checkParameterIsNotNull(logLevel, "level");
        Intrinsics.checkParameterIsNotNull(str, "tag");
        Intrinsics.checkParameterIsNotNull(function1, "printer");
        this.level = logLevel2;
        this.tag = str7;
        this.timestamp = j;
        this.printer = function12;
        this.str1 = str4;
        this.str2 = str5;
        this.str3 = str6;
        this.int1 = i;
        this.int2 = i2;
        this.long1 = j2;
        this.long2 = j3;
        this.double1 = d;
        this.bool1 = z;
        this.bool2 = z2;
        this.bool3 = z3;
        this.bool4 = z4;
    }

    public LogLevel getLevel() {
        return this.level;
    }

    public void setLevel(LogLevel logLevel) {
        Intrinsics.checkParameterIsNotNull(logLevel, "<set-?>");
        this.level = logLevel;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String str) {
        Intrinsics.checkParameterIsNotNull(str, "<set-?>");
        this.tag = str;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long j) {
        this.timestamp = j;
    }

    public Function1<LogMessage, String> getPrinter() {
        return this.printer;
    }

    public void setPrinter(Function1<? super LogMessage, String> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "<set-?>");
        this.printer = function1;
    }

    public String getStr1() {
        return this.str1;
    }

    public void setStr1(String str) {
        this.str1 = str;
    }

    public String getStr2() {
        return this.str2;
    }

    public void setStr2(String str) {
        this.str2 = str;
    }

    public String getStr3() {
        return this.str3;
    }

    public void setStr3(String str) {
        this.str3 = str;
    }

    public int getInt1() {
        return this.int1;
    }

    public void setInt1(int i) {
        this.int1 = i;
    }

    public int getInt2() {
        return this.int2;
    }

    public void setInt2(int i) {
        this.int2 = i;
    }

    public long getLong1() {
        return this.long1;
    }

    public void setLong1(long j) {
        this.long1 = j;
    }

    public long getLong2() {
        return this.long2;
    }

    public void setLong2(long j) {
        this.long2 = j;
    }

    public double getDouble1() {
        return this.double1;
    }

    public void setDouble1(double d) {
        this.double1 = d;
    }

    public boolean getBool1() {
        return this.bool1;
    }

    public void setBool1(boolean z) {
        this.bool1 = z;
    }

    public boolean getBool2() {
        return this.bool2;
    }

    public void setBool2(boolean z) {
        this.bool2 = z;
    }

    public boolean getBool3() {
        return this.bool3;
    }

    public void setBool3(boolean z) {
        this.bool3 = z;
    }

    public boolean getBool4() {
        return this.bool4;
    }

    public void setBool4(boolean z) {
        this.bool4 = z;
    }

    public final void reset(String str, LogLevel logLevel, long j, Function1<? super LogMessage, String> function1) {
        Intrinsics.checkParameterIsNotNull(str, "tag");
        Intrinsics.checkParameterIsNotNull(logLevel, "level");
        Intrinsics.checkParameterIsNotNull(function1, "renderer");
        setLevel(logLevel);
        setTag(str);
        setTimestamp(j);
        setPrinter(function1);
        setStr1(null);
        setStr2(null);
        setStr3(null);
        setInt1(0);
        setInt2(0);
        setLong1(0);
        setLong2(0);
        setDouble1(0.0d);
        setBool1(false);
        setBool2(false);
        setBool3(false);
        setBool4(false);
    }
}
