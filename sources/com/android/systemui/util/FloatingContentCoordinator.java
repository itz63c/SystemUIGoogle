package com.android.systemui.util;

import android.graphics.Rect;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Lazy;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference0Impl;
import kotlin.jvm.internal.Ref$ObjectRef;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;

/* compiled from: FloatingContentCoordinator.kt */
public final class FloatingContentCoordinator {
    public static final Companion Companion = new Companion(null);
    private final Map<FloatingContent, Rect> allContentBounds = new HashMap();
    private boolean currentlyResolvingConflicts;

    /* compiled from: FloatingContentCoordinator.kt */
    public static final class Companion {
        static final /* synthetic */ KProperty[] $$delegatedProperties;

        static {
            Class<Companion> cls = Companion.class;
            PropertyReference0Impl propertyReference0Impl = new PropertyReference0Impl(Reflection.getOrCreateKotlinClass(cls), "newContentBoundsAbove", "<v#0>");
            Reflection.property0(propertyReference0Impl);
            PropertyReference0Impl propertyReference0Impl2 = new PropertyReference0Impl(Reflection.getOrCreateKotlinClass(cls), "newContentBoundsBelow", "<v#1>");
            Reflection.property0(propertyReference0Impl2);
            PropertyReference0Impl propertyReference0Impl3 = new PropertyReference0Impl(Reflection.getOrCreateKotlinClass(cls), "positionAboveInBounds", "<v#2>");
            Reflection.property0(propertyReference0Impl3);
            PropertyReference0Impl propertyReference0Impl4 = new PropertyReference0Impl(Reflection.getOrCreateKotlinClass(cls), "positionBelowInBounds", "<v#3>");
            Reflection.property0(propertyReference0Impl4);
            $$delegatedProperties = new KProperty[]{propertyReference0Impl, propertyReference0Impl2, propertyReference0Impl3, propertyReference0Impl4};
        }

        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final Rect findAreaForContentVertically(Rect rect, Rect rect2, Collection<Rect> collection, Rect rect3) {
            Intrinsics.checkParameterIsNotNull(rect, "contentRect");
            Intrinsics.checkParameterIsNotNull(rect2, "newlyOverlappingRect");
            Intrinsics.checkParameterIsNotNull(collection, "exclusionRects");
            Intrinsics.checkParameterIsNotNull(rect3, "allowedBounds");
            boolean z = true;
            boolean z2 = rect2.centerY() < rect.centerY();
            Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
            Ref$ObjectRef ref$ObjectRef2 = new Ref$ObjectRef();
            ArrayList arrayList = new ArrayList();
            for (Object next : collection) {
                if (FloatingContentCoordinator.Companion.rectsIntersectVertically((Rect) next, rect)) {
                    arrayList.add(next);
                }
            }
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            for (Object next2 : arrayList) {
                if (((Rect) next2).top < rect.top) {
                    arrayList2.add(next2);
                } else {
                    arrayList3.add(next2);
                }
            }
            Pair pair = new Pair(arrayList2, arrayList3);
            ref$ObjectRef.element = (List) pair.component1();
            ref$ObjectRef2.element = (List) pair.component2();
            Lazy lazy = LazyKt__LazyJVMKt.lazy(new C1731xa8ad3931(rect, ref$ObjectRef, rect2));
            KProperty kProperty = $$delegatedProperties[0];
            Lazy lazy2 = LazyKt__LazyJVMKt.lazy(new C1732xe284ccc5(rect, ref$ObjectRef2, rect2));
            KProperty kProperty2 = $$delegatedProperties[1];
            Lazy lazy3 = LazyKt__LazyJVMKt.lazy(new C1733x994e5850(rect3, lazy, kProperty));
            KProperty kProperty3 = $$delegatedProperties[2];
            Lazy lazy4 = LazyKt__LazyJVMKt.lazy(new C1734xf7317e4(rect3, lazy2, kProperty2));
            KProperty kProperty4 = $$delegatedProperties[3];
            if ((!z2 || !((Boolean) lazy4.getValue()).booleanValue()) && (z2 || ((Boolean) lazy3.getValue()).booleanValue())) {
                z = false;
            }
            return (Rect) (z ? lazy2.getValue() : lazy.getValue());
        }

        private final boolean rectsIntersectVertically(Rect rect, Rect rect2) {
            int i = rect.left;
            if (i < rect2.left || i > rect2.right) {
                int i2 = rect.right;
                if (i2 > rect2.right || i2 < rect2.left) {
                    return false;
                }
            }
            return true;
        }

        public final Rect findAreaForContentAboveOrBelow(Rect rect, Collection<Rect> collection, boolean z) {
            Intrinsics.checkParameterIsNotNull(rect, "contentRect");
            Intrinsics.checkParameterIsNotNull(collection, "exclusionRects");
            List<Rect> sortedWith = CollectionsKt___CollectionsKt.sortedWith(collection, new C1730x8b489ee0(z));
            Rect rect2 = new Rect(rect);
            for (Rect rect3 : sortedWith) {
                if (!Rect.intersects(rect2, rect3)) {
                    break;
                }
                rect2.offsetTo(rect2.left, rect3.top + (z ? -rect.height() : rect3.height()));
            }
            return rect2;
        }
    }

    /* compiled from: FloatingContentCoordinator.kt */
    public interface FloatingContent {
        Rect getAllowedFloatingBoundsRegion();

        Rect getFloatingBoundsOnScreen();

        void moveToBounds(Rect rect);

        Rect calculateNewBoundsOnOverlap(Rect rect, List<Rect> list) {
            Intrinsics.checkParameterIsNotNull(rect, "overlappingContentBounds");
            Intrinsics.checkParameterIsNotNull(list, "otherContentBounds");
            return FloatingContentCoordinator.Companion.findAreaForContentVertically(getFloatingBoundsOnScreen(), rect, list, getAllowedFloatingBoundsRegion());
        }
    }

    public final void onContentAdded(FloatingContent floatingContent) {
        Intrinsics.checkParameterIsNotNull(floatingContent, "newContent");
        updateContentBounds();
        this.allContentBounds.put(floatingContent, floatingContent.getFloatingBoundsOnScreen());
        maybeMoveConflictingContent(floatingContent);
    }

    public final void onContentMoved(FloatingContent floatingContent) {
        Intrinsics.checkParameterIsNotNull(floatingContent, "content");
        if (!this.currentlyResolvingConflicts) {
            if (!this.allContentBounds.containsKey(floatingContent)) {
                Log.wtf("FloatingCoordinator", "Received onContentMoved call before onContentAdded! This should never happen.");
                return;
            }
            updateContentBounds();
            maybeMoveConflictingContent(floatingContent);
        }
    }

    public final void onContentRemoved(FloatingContent floatingContent) {
        Intrinsics.checkParameterIsNotNull(floatingContent, "removedContent");
        this.allContentBounds.remove(floatingContent);
    }

    private final void maybeMoveConflictingContent(FloatingContent floatingContent) {
        this.currentlyResolvingConflicts = true;
        Object obj = this.allContentBounds.get(floatingContent);
        if (obj != null) {
            Rect rect = (Rect) obj;
            Map<FloatingContent, Rect> map = this.allContentBounds;
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            Iterator it = map.entrySet().iterator();
            while (true) {
                boolean z = false;
                if (!it.hasNext()) {
                    break;
                }
                Entry entry = (Entry) it.next();
                Rect rect2 = (Rect) entry.getValue();
                if ((!Intrinsics.areEqual((Object) (FloatingContent) entry.getKey(), (Object) floatingContent)) && Rect.intersects(rect, rect2)) {
                    z = true;
                }
                if (z) {
                    linkedHashMap.put(entry.getKey(), entry.getValue());
                }
            }
            for (Entry entry2 : linkedHashMap.entrySet()) {
                FloatingContent floatingContent2 = (FloatingContent) entry2.getKey();
                floatingContent2.moveToBounds(floatingContent2.calculateNewBoundsOnOverlap(rect, CollectionsKt___CollectionsKt.minus(CollectionsKt___CollectionsKt.minus(this.allContentBounds.values(), (Rect) entry2.getValue()), rect)));
                this.allContentBounds.put(floatingContent2, floatingContent2.getFloatingBoundsOnScreen());
            }
            this.currentlyResolvingConflicts = false;
            return;
        }
        Intrinsics.throwNpe();
        throw null;
    }

    private final void updateContentBounds() {
        for (FloatingContent floatingContent : this.allContentBounds.keySet()) {
            this.allContentBounds.put(floatingContent, floatingContent.getFloatingBoundsOnScreen());
        }
    }
}
