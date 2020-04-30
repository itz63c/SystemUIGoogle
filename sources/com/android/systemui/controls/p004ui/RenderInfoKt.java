package com.android.systemui.controls.p004ui;

import com.android.systemui.C2008R$color;
import com.android.systemui.C2010R$drawable;
import java.util.Map;
import kotlin.Pair;
import kotlin.TuplesKt;

/* renamed from: com.android.systemui.controls.ui.RenderInfoKt */
/* compiled from: RenderInfo.kt */
public final class RenderInfoKt {
    /* access modifiers changed from: private */
    public static final Map<Integer, Pair<Integer, Integer>> deviceColorMap;
    /* access modifiers changed from: private */
    public static final Map<Integer, IconState> deviceIconMap;

    static {
        Integer valueOf = Integer.valueOf(49002);
        Integer valueOf2 = Integer.valueOf(49003);
        Integer valueOf3 = Integer.valueOf(13);
        deviceColorMap = MapsKt__MapWithDefaultKt.withDefault(MapsKt__MapsKt.mapOf(TuplesKt.m136to(valueOf, new Pair(Integer.valueOf(C2008R$color.thermo_heat_foreground), Integer.valueOf(C2008R$color.control_enabled_thermo_heat_background))), TuplesKt.m136to(valueOf2, new Pair(Integer.valueOf(C2008R$color.thermo_cool_foreground), Integer.valueOf(C2008R$color.control_enabled_thermo_cool_background))), TuplesKt.m136to(valueOf3, new Pair(Integer.valueOf(C2008R$color.light_foreground), Integer.valueOf(C2008R$color.control_enabled_light_background)))), RenderInfoKt$deviceColorMap$1.INSTANCE);
        Integer valueOf4 = Integer.valueOf(49000);
        int i = C2010R$drawable.ic_device_thermostat_gm2_24px;
        Integer valueOf5 = Integer.valueOf(49001);
        int i2 = C2010R$drawable.ic_device_thermostat_gm2_24px;
        int i3 = C2010R$drawable.ic_device_thermostat_gm2_24px;
        int i4 = C2010R$drawable.ic_device_thermostat_gm2_24px;
        Integer valueOf6 = Integer.valueOf(49004);
        int i5 = C2010R$drawable.ic_device_thermostat_gm2_24px;
        Integer valueOf7 = Integer.valueOf(49005);
        int i6 = C2010R$drawable.ic_device_thermostat_gm2_24px;
        Integer valueOf8 = Integer.valueOf(49);
        int i7 = C2010R$drawable.ic_device_thermostat_gm2_24px;
        Integer valueOf9 = Integer.valueOf(50);
        int i8 = C2010R$drawable.ic_videocam_gm2_24px;
        Integer valueOf10 = Integer.valueOf(21);
        int i9 = C2010R$drawable.ic_switches_gm2_24px;
        Integer valueOf11 = Integer.valueOf(32);
        int i10 = C2010R$drawable.ic_vacuum_gm2_24px;
        Integer valueOf12 = Integer.valueOf(26);
        int i11 = C2010R$drawable.ic_vacuum_gm2_24px;
        deviceIconMap = MapsKt__MapWithDefaultKt.withDefault(MapsKt__MapsKt.mapOf(TuplesKt.m136to(valueOf4, new IconState(i, i)), TuplesKt.m136to(valueOf5, new IconState(i2, i2)), TuplesKt.m136to(valueOf, new IconState(i3, i3)), TuplesKt.m136to(valueOf2, new IconState(i4, i4)), TuplesKt.m136to(valueOf6, new IconState(i5, i5)), TuplesKt.m136to(valueOf7, new IconState(i6, i6)), TuplesKt.m136to(valueOf8, new IconState(i7, i7)), TuplesKt.m136to(valueOf3, new IconState(C2010R$drawable.ic_light_off_gm2_24px, C2010R$drawable.ic_lightbulb_outline_gm2_24px)), TuplesKt.m136to(valueOf9, new IconState(i8, i8)), TuplesKt.m136to(Integer.valueOf(45), new IconState(C2010R$drawable.ic_lock_open_gm2_24px, C2010R$drawable.ic_lock_gm2_24px)), TuplesKt.m136to(valueOf10, new IconState(i9, i9)), TuplesKt.m136to(Integer.valueOf(15), new IconState(C2010R$drawable.ic_power_off_gm2_24px, C2010R$drawable.ic_power_gm2_24px)), TuplesKt.m136to(valueOf11, new IconState(i10, i10)), TuplesKt.m136to(valueOf12, new IconState(i11, i11)), TuplesKt.m136to(Integer.valueOf(52), new IconState(-1, -1))), RenderInfoKt$deviceIconMap$1.INSTANCE);
    }
}
