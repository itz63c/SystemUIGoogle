package kotlin.coroutines.jvm.internal;

import java.lang.reflect.Field;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DebugMetadata.kt */
public final class DebugMetadataKt {
    public static final StackTraceElement getStackTraceElement(BaseContinuationImpl baseContinuationImpl) {
        int i;
        String str;
        Intrinsics.checkParameterIsNotNull(baseContinuationImpl, "$this$getStackTraceElementImpl");
        DebugMetadata debugMetadataAnnotation = getDebugMetadataAnnotation(baseContinuationImpl);
        if (debugMetadataAnnotation == null) {
            return null;
        }
        checkDebugMetadataVersion(1, debugMetadataAnnotation.mo22274v());
        int label = getLabel(baseContinuationImpl);
        if (label < 0) {
            i = -1;
        } else {
            i = debugMetadataAnnotation.mo22272l()[label];
        }
        String moduleName = ModuleNameRetriever.INSTANCE.getModuleName(baseContinuationImpl);
        if (moduleName == null) {
            str = debugMetadataAnnotation.mo22270c();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(moduleName);
            sb.append('/');
            sb.append(debugMetadataAnnotation.mo22270c());
            str = sb.toString();
        }
        return new StackTraceElement(str, debugMetadataAnnotation.mo22273m(), debugMetadataAnnotation.mo22271f(), i);
    }

    private static final DebugMetadata getDebugMetadataAnnotation(BaseContinuationImpl baseContinuationImpl) {
        return (DebugMetadata) baseContinuationImpl.getClass().getAnnotation(DebugMetadata.class);
    }

    private static final int getLabel(BaseContinuationImpl baseContinuationImpl) {
        try {
            Field declaredField = baseContinuationImpl.getClass().getDeclaredField("label");
            Intrinsics.checkExpressionValueIsNotNull(declaredField, "field");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(baseContinuationImpl);
            if (!(obj instanceof Integer)) {
                obj = null;
            }
            Integer num = (Integer) obj;
            return (num != null ? num.intValue() : 0) - 1;
        } catch (Exception unused) {
            return -1;
        }
    }

    private static final void checkDebugMetadataVersion(int i, int i2) {
        if (i2 > i) {
            StringBuilder sb = new StringBuilder();
            sb.append("Debug metadata version mismatch. Expected: ");
            sb.append(i);
            sb.append(", got ");
            sb.append(i2);
            sb.append(". Please update the Kotlin standard library.");
            throw new IllegalStateException(sb.toString().toString());
        }
    }
}
