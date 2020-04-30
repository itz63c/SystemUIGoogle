package kotlin.jvm.internal;

import java.util.Collection;
import java.util.Set;
import kotlin.Function;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.markers.KMappedMarker;

public class TypeIntrinsics {
    private static <T extends Throwable> T sanitizeStackTrace(T t) {
        Intrinsics.sanitizeStackTrace(t, TypeIntrinsics.class.getName());
        return t;
    }

    public static void throwCce(Object obj, String str) {
        String name = obj == null ? "null" : obj.getClass().getName();
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" cannot be cast to ");
        sb.append(str);
        throwCce(sb.toString());
        throw null;
    }

    public static void throwCce(String str) {
        throwCce(new ClassCastException(str));
        throw null;
    }

    public static ClassCastException throwCce(ClassCastException classCastException) {
        sanitizeStackTrace(classCastException);
        throw classCastException;
    }

    public static Iterable asMutableIterable(Object obj) {
        if (!(obj instanceof KMappedMarker)) {
            return castToIterable(obj);
        }
        throwCce(obj, "kotlin.collections.MutableIterable");
        throw null;
    }

    public static Iterable castToIterable(Object obj) {
        try {
            return (Iterable) obj;
        } catch (ClassCastException e) {
            throwCce(e);
            throw null;
        }
    }

    public static Collection asMutableCollection(Object obj) {
        if (!(obj instanceof KMappedMarker)) {
            return castToCollection(obj);
        }
        throwCce(obj, "kotlin.collections.MutableCollection");
        throw null;
    }

    public static Collection castToCollection(Object obj) {
        try {
            return (Collection) obj;
        } catch (ClassCastException e) {
            throwCce(e);
            throw null;
        }
    }

    public static Set asMutableSet(Object obj) {
        if (!(obj instanceof KMappedMarker)) {
            return castToSet(obj);
        }
        throwCce(obj, "kotlin.collections.MutableSet");
        throw null;
    }

    public static Set castToSet(Object obj) {
        try {
            return (Set) obj;
        } catch (ClassCastException e) {
            throwCce(e);
            throw null;
        }
    }

    public static int getFunctionArity(Object obj) {
        if (obj instanceof FunctionBase) {
            return ((FunctionBase) obj).getArity();
        }
        if (obj instanceof Function0) {
            return 0;
        }
        if (obj instanceof Function1) {
            return 1;
        }
        if (obj instanceof Function2) {
            return 2;
        }
        return obj instanceof Function3 ? 3 : -1;
    }

    public static boolean isFunctionOfArity(Object obj, int i) {
        return (obj instanceof Function) && getFunctionArity(obj) == i;
    }

    public static Object beforeCheckcastToFunctionOfArity(Object obj, int i) {
        if (obj == null || isFunctionOfArity(obj, i)) {
            return obj;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("kotlin.jvm.functions.Function");
        sb.append(i);
        throwCce(obj, sb.toString());
        throw null;
    }
}
