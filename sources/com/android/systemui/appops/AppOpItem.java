package com.android.systemui.appops;

public class AppOpItem {
    private int mCode;
    private String mPackageName;
    private String mState;
    private int mUid;

    public AppOpItem(int i, int i2, String str, long j) {
        this.mCode = i;
        this.mUid = i2;
        this.mPackageName = str;
        StringBuilder sb = new StringBuilder();
        sb.append("AppOpItem(");
        sb.append("Op code=");
        sb.append(i);
        String str2 = ", ";
        sb.append(str2);
        sb.append("UID=");
        sb.append(i2);
        sb.append(str2);
        sb.append("Package name=");
        sb.append(str);
        sb.append(")");
        this.mState = sb.toString();
    }

    public int getCode() {
        return this.mCode;
    }

    public int getUid() {
        return this.mUid;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String toString() {
        return this.mState;
    }
}
