package com.android.settingslib.datetime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import libcore.timezone.CountryTimeZones;
import libcore.timezone.CountryTimeZones.TimeZoneMapping;
import libcore.timezone.TimeZoneFinder;

public final class ZoneGetter$ZoneGetterData {
    public List<String> lookupTimeZoneIdsByCountry(String str) {
        CountryTimeZones lookupCountryTimeZones = TimeZoneFinder.getInstance().lookupCountryTimeZones(str);
        if (lookupCountryTimeZones == null) {
            return null;
        }
        return extractTimeZoneIds(lookupCountryTimeZones.getTimeZoneMappings());
    }

    private static List<String> extractTimeZoneIds(List<TimeZoneMapping> list) {
        ArrayList arrayList = new ArrayList(list.size());
        for (TimeZoneMapping timeZoneId : list) {
            arrayList.add(timeZoneId.getTimeZoneId());
        }
        return Collections.unmodifiableList(arrayList);
    }
}
