package kotlin.jvm.internal;

/* compiled from: PackageReference.kt */
public final class PackageReference implements ClassBasedDeclarationContainer {
    private final Class<?> jClass;

    public PackageReference(Class<?> cls, String str) {
        Intrinsics.checkParameterIsNotNull(cls, "jClass");
        Intrinsics.checkParameterIsNotNull(str, "moduleName");
        this.jClass = cls;
    }

    public Class<?> getJClass() {
        return this.jClass;
    }

    public boolean equals(Object obj) {
        return (obj instanceof PackageReference) && Intrinsics.areEqual((Object) getJClass(), (Object) ((PackageReference) obj).getJClass());
    }

    public int hashCode() {
        return getJClass().hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getJClass().toString());
        sb.append(" (Kotlin reflection is not available)");
        return sb.toString();
    }
}
