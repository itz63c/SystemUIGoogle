package com.google.common.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Throwables {
    static final String SHARED_SECRETS_CLASSNAME = "sun.misc.SharedSecrets";
    private static final Object jla;

    public static void throwIfUnchecked(Throwable th) {
        Preconditions.checkNotNull(th);
        if (th instanceof RuntimeException) {
            throw ((RuntimeException) th);
        } else if (th instanceof Error) {
            throw ((Error) th);
        }
    }

    static {
        Object jla2 = getJLA();
        jla = jla2;
        if (jla2 != null) {
            getGetMethod();
        }
        if (jla != null) {
            getSizeMethod();
        }
    }

    private static Object getJLA() {
        try {
            return Class.forName(SHARED_SECRETS_CLASSNAME, false, null).getMethod("getJavaLangAccess", new Class[0]).invoke(null, new Object[0]);
        } catch (ThreadDeath e) {
            throw e;
        } catch (Throwable unused) {
            return null;
        }
    }

    private static Method getGetMethod() {
        return getJlaMethod("getStackTraceElement", Throwable.class, Integer.TYPE);
    }

    private static Method getSizeMethod() {
        try {
            Method jlaMethod = getJlaMethod("getStackTraceDepth", Throwable.class);
            if (jlaMethod == null) {
                return null;
            }
            jlaMethod.invoke(getJLA(), new Object[]{new Throwable()});
            return jlaMethod;
        } catch (IllegalAccessException | UnsupportedOperationException | InvocationTargetException unused) {
            return null;
        }
    }

    private static Method getJlaMethod(String str, Class<?>... clsArr) throws ThreadDeath {
        try {
            return Class.forName("sun.misc.JavaLangAccess", false, null).getMethod(str, clsArr);
        } catch (ThreadDeath e) {
            throw e;
        } catch (Throwable unused) {
            return null;
        }
    }
}
