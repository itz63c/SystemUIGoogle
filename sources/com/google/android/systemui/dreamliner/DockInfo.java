package com.google.android.systemui.dreamliner;

import android.os.Bundle;

public class DockInfo {
    private int accessoryType = -1;
    private String manufacturer;
    private String model;
    private String serialNumber;

    public DockInfo(String str, String str2, String str3, int i) {
        String str4 = "";
        this.manufacturer = str4;
        this.model = str4;
        this.serialNumber = str4;
        this.manufacturer = str;
        this.model = str2;
        this.serialNumber = str3;
        this.accessoryType = i;
    }

    /* access modifiers changed from: 0000 */
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("manufacturer", this.manufacturer);
        bundle.putString("model", this.model);
        bundle.putString("serialNumber", this.serialNumber);
        bundle.putInt("accessoryType", this.accessoryType);
        return bundle;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.manufacturer);
        String str = ", ";
        sb.append(str);
        sb.append(this.model);
        sb.append(str);
        sb.append(this.serialNumber);
        sb.append(str);
        sb.append(this.accessoryType);
        return sb.toString();
    }
}
