package com.android.systemui.controls.management;

import android.service.controls.Control;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.android.systemui.controls.ControlStatus;
import com.android.systemui.controls.controller.ControlInfo.Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;

/* compiled from: AllModel.kt */
public final class AllModel implements ControlsModel {
    private final List<ControlStatus> controls;
    private final List<ElementWrapper> elements;
    private final CharSequence emptyZoneString;
    private final List<String> favoriteIds;

    /* compiled from: AllModel.kt */
    private static final class OrderedMap<K, V> implements Map<K, V>, Object {
        private final Map<K, V> map;
        private final List<K> orderedKeys = new ArrayList();

        public boolean containsKey(Object obj) {
            return this.map.containsKey(obj);
        }

        public boolean containsValue(Object obj) {
            return this.map.containsValue(obj);
        }

        public V get(Object obj) {
            return this.map.get(obj);
        }

        public Set<Entry<K, V>> getEntries() {
            return this.map.entrySet();
        }

        public Set<K> getKeys() {
            return this.map.keySet();
        }

        public int getSize() {
            return this.map.size();
        }

        public Collection<V> getValues() {
            return this.map.values();
        }

        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        public void putAll(Map<? extends K, ? extends V> map2) {
            Intrinsics.checkParameterIsNotNull(map2, "from");
            this.map.putAll(map2);
        }

        public OrderedMap(Map<K, V> map2) {
            Intrinsics.checkParameterIsNotNull(map2, "map");
            this.map = map2;
        }

        public final /* bridge */ Set<Entry<K, V>> entrySet() {
            return getEntries();
        }

        public final /* bridge */ Set<K> keySet() {
            return getKeys();
        }

        public final /* bridge */ int size() {
            return getSize();
        }

        public final /* bridge */ Collection<V> values() {
            return getValues();
        }

        public final List<K> getOrderedKeys() {
            return this.orderedKeys;
        }

        public V put(K k, V v) {
            if (!this.map.containsKey(k)) {
                this.orderedKeys.add(k);
            }
            return this.map.put(k, v);
        }

        public void clear() {
            this.orderedKeys.clear();
            this.map.clear();
        }

        public V remove(Object obj) {
            V remove = this.map.remove(obj);
            if (remove != null) {
                this.orderedKeys.remove(obj);
            }
            return remove;
        }
    }

    public AllModel(List<ControlStatus> list, List<String> list2, CharSequence charSequence) {
        Intrinsics.checkParameterIsNotNull(list, "controls");
        Intrinsics.checkParameterIsNotNull(list2, "initialFavoriteIds");
        Intrinsics.checkParameterIsNotNull(charSequence, "emptyZoneString");
        this.controls = list;
        this.emptyZoneString = charSequence;
        HashSet hashSet = new HashSet();
        for (ControlStatus control : list) {
            hashSet.add(control.getControl().getControlId());
        }
        ArrayList arrayList = new ArrayList();
        for (Object next : list2) {
            if (hashSet.contains((String) next)) {
                arrayList.add(next);
            }
        }
        this.favoriteIds = CollectionsKt___CollectionsKt.toMutableList((Collection) arrayList);
        this.elements = createWrappers(this.controls);
    }

    public List<Builder> getFavorites() {
        Builder builder;
        Object obj;
        List<String> list = this.favoriteIds;
        ArrayList arrayList = new ArrayList();
        for (String str : list) {
            Iterator it = this.controls.iterator();
            while (true) {
                builder = null;
                if (!it.hasNext()) {
                    obj = null;
                    break;
                }
                obj = it.next();
                if (Intrinsics.areEqual((Object) ((ControlStatus) obj).getControl().getControlId(), (Object) str)) {
                    break;
                }
            }
            ControlStatus controlStatus = (ControlStatus) obj;
            Control control = controlStatus != null ? controlStatus.getControl() : null;
            if (control != null) {
                builder = new Builder();
                String controlId = control.getControlId();
                Intrinsics.checkExpressionValueIsNotNull(controlId, "it.controlId");
                builder.setControlId(controlId);
                CharSequence title = control.getTitle();
                Intrinsics.checkExpressionValueIsNotNull(title, "it.title");
                builder.setControlTitle(title);
                CharSequence subtitle = control.getSubtitle();
                Intrinsics.checkExpressionValueIsNotNull(subtitle, "it.subtitle");
                builder.setControlSubtitle(subtitle);
                builder.setDeviceType(control.getDeviceType());
            }
            if (builder != null) {
                arrayList.add(builder);
            }
        }
        return arrayList;
    }

    public List<ElementWrapper> getElements() {
        return this.elements;
    }

    public void changeFavoriteStatus(String str, boolean z) {
        Intrinsics.checkParameterIsNotNull(str, "controlId");
        if (z) {
            this.favoriteIds.add(str);
        } else {
            this.favoriteIds.remove(str);
        }
    }

    private final List<ElementWrapper> createWrappers(List<ControlStatus> list) {
        OrderedMap orderedMap = new OrderedMap(new ArrayMap());
        for (Object next : list) {
            Object zone = ((ControlStatus) next).getControl().getZone();
            if (zone == null) {
                zone = "";
            }
            Object obj = orderedMap.get(zone);
            if (obj == null) {
                obj = new ArrayList();
                orderedMap.put(zone, obj);
            }
            ((List) obj).add(next);
        }
        ArrayList arrayList = new ArrayList();
        Sequence sequence = null;
        for (CharSequence charSequence : orderedMap.getOrderedKeys()) {
            Object value = MapsKt__MapsKt.getValue(orderedMap, charSequence);
            Intrinsics.checkExpressionValueIsNotNull(value, "map.getValue(zoneName)");
            Sequence map = SequencesKt___SequencesKt.map(CollectionsKt___CollectionsKt.asSequence((Iterable) value), AllModel$createWrappers$values$1.INSTANCE);
            if (TextUtils.isEmpty(charSequence)) {
                sequence = map;
            } else {
                Intrinsics.checkExpressionValueIsNotNull(charSequence, "zoneName");
                arrayList.add(new ZoneNameWrapper(charSequence));
                CollectionsKt__MutableCollectionsKt.addAll((Collection) arrayList, map);
            }
        }
        if (sequence != null) {
            if (orderedMap.size() != 1) {
                arrayList.add(new ZoneNameWrapper(this.emptyZoneString));
            }
            CollectionsKt__MutableCollectionsKt.addAll((Collection) arrayList, sequence);
        }
        return arrayList;
    }
}
