package kotlin.coroutines.jvm.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/* compiled from: DebugMetadata.kt */
public @interface DebugMetadata {
    /* renamed from: c */
    String mo22270c() default "";

    /* renamed from: f */
    String mo22271f() default "";

    /* renamed from: l */
    int[] mo22272l() default {};

    /* renamed from: m */
    String mo22273m() default "";

    /* renamed from: v */
    int mo22274v() default 1;
}
