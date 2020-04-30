package kotlin.jvm.internal;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;

/* compiled from: ClassReference.kt */
public final class ClassReference implements KClass<Object>, ClassBasedDeclarationContainer {
    private final Class<?> jClass;

    public ClassReference(Class<?> cls) {
        Intrinsics.checkParameterIsNotNull(cls, "jClass");
        this.jClass = cls;
    }

    public Class<?> getJClass() {
        return this.jClass;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ClassReference) && Intrinsics.areEqual((Object) JvmClassMappingKt.getJavaObjectType(this), (Object) JvmClassMappingKt.getJavaObjectType((KClass) obj));
    }

    public int hashCode() {
        return JvmClassMappingKt.getJavaObjectType(this).hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getJClass().toString());
        sb.append(" (Kotlin reflection is not available)");
        return sb.toString();
    }
}
