package kotlin.coroutines.jvm.internal;

import java.lang.reflect.Method;

/* compiled from: DebugMetadata.kt */
final class ModuleNameRetriever {
    public static final ModuleNameRetriever INSTANCE = new ModuleNameRetriever();
    public static Cache cache;
    private static final Cache notOnJava9 = new Cache(null, null, null);

    /* compiled from: DebugMetadata.kt */
    private static final class Cache {
        public final Method getDescriptorMethod;
        public final Method getModuleMethod;
        public final Method nameMethod;

        public Cache(Method method, Method method2, Method method3) {
            this.getModuleMethod = method;
            this.getDescriptorMethod = method2;
            this.nameMethod = method3;
        }
    }

    private ModuleNameRetriever() {
    }

    /* JADX WARNING: type inference failed for: r4v5 */
    /* JADX WARNING: type inference failed for: r4v6 */
    /* JADX WARNING: type inference failed for: r4v8 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.String getModuleName(kotlin.coroutines.jvm.internal.BaseContinuationImpl r5) {
        /*
            r4 = this;
            java.lang.String r0 = "continuation"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r5, r0)
            kotlin.coroutines.jvm.internal.ModuleNameRetriever$Cache r0 = cache
            if (r0 == 0) goto L_0x000a
            goto L_0x000e
        L_0x000a:
            kotlin.coroutines.jvm.internal.ModuleNameRetriever$Cache r0 = r4.buildCache(r5)
        L_0x000e:
            kotlin.coroutines.jvm.internal.ModuleNameRetriever$Cache r4 = notOnJava9
            r1 = 0
            if (r0 != r4) goto L_0x0014
            return r1
        L_0x0014:
            java.lang.reflect.Method r4 = r0.getModuleMethod
            if (r4 == 0) goto L_0x0045
            java.lang.Class r5 = r5.getClass()
            r2 = 0
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.Object r4 = r4.invoke(r5, r3)
            if (r4 == 0) goto L_0x0045
            java.lang.reflect.Method r5 = r0.getDescriptorMethod
            if (r5 == 0) goto L_0x0045
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.Object r4 = r5.invoke(r4, r3)
            if (r4 == 0) goto L_0x0045
            java.lang.reflect.Method r5 = r0.nameMethod
            if (r5 == 0) goto L_0x003c
            java.lang.Object[] r0 = new java.lang.Object[r2]
            java.lang.Object r4 = r5.invoke(r4, r0)
            goto L_0x003d
        L_0x003c:
            r4 = r1
        L_0x003d:
            boolean r5 = r4 instanceof java.lang.String
            if (r5 != 0) goto L_0x0042
            goto L_0x0043
        L_0x0042:
            r1 = r4
        L_0x0043:
            java.lang.String r1 = (java.lang.String) r1
        L_0x0045:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.coroutines.jvm.internal.ModuleNameRetriever.getModuleName(kotlin.coroutines.jvm.internal.BaseContinuationImpl):java.lang.String");
    }

    private final Cache buildCache(BaseContinuationImpl baseContinuationImpl) {
        try {
            Cache cache2 = new Cache(Class.class.getDeclaredMethod("getModule", new Class[0]), baseContinuationImpl.getClass().getClassLoader().loadClass("java.lang.Module").getDeclaredMethod("getDescriptor", new Class[0]), baseContinuationImpl.getClass().getClassLoader().loadClass("java.lang.module.ModuleDescriptor").getDeclaredMethod("name", new Class[0]));
            cache = cache2;
            return cache2;
        } catch (Exception unused) {
            Cache cache3 = notOnJava9;
            cache = cache3;
            return cache3;
        }
    }
}
