package dagger.internal;

import dagger.Lazy;
import javax.inject.Provider;

public final class DoubleCheck<T> implements Provider<T>, Lazy<T> {
    private static final Object UNINITIALIZED = new Object();
    private volatile Object instance = UNINITIALIZED;
    private volatile Provider<T> provider;

    private DoubleCheck(Provider<T> provider2) {
        this.provider = provider2;
    }

    public T get() {
        Object obj = UNINITIALIZED;
        Object obj2 = this.instance;
        if (obj2 == obj) {
            synchronized (this) {
                obj2 = this.instance;
                if (obj2 == obj) {
                    Object obj3 = this.provider.get();
                    reentrantCheck(this.instance, obj3);
                    this.instance = obj3;
                    this.provider = null;
                    obj2 = obj3;
                }
            }
        }
        return obj2;
    }

    public static Object reentrantCheck(Object obj, Object obj2) {
        if (!(obj != UNINITIALIZED) || obj == obj2) {
            return obj2;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Scoped provider was invoked recursively returning different results: ");
        sb.append(obj);
        sb.append(" & ");
        sb.append(obj2);
        sb.append(". This is likely due to a circular dependency.");
        throw new IllegalStateException(sb.toString());
    }

    public static <P extends Provider<T>, T> Provider<T> provider(P p) {
        Preconditions.checkNotNull(p);
        if (p instanceof DoubleCheck) {
            return p;
        }
        return new DoubleCheck(p);
    }

    public static <P extends Provider<T>, T> Lazy<T> lazy(P p) {
        if (p instanceof Lazy) {
            return (Lazy) p;
        }
        Preconditions.checkNotNull(p);
        return new DoubleCheck((Provider) p);
    }
}
