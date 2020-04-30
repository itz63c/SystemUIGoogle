package com.google.common.base;

import java.io.Serializable;

class Suppliers$MemoizingSupplier<T> implements Supplier<T>, Serializable {
    private static final long serialVersionUID = 0;
    final Supplier<T> delegate;
    volatile transient boolean initialized;
    transient T value;

    public T get() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    T t = this.delegate.get();
                    this.value = t;
                    this.initialized = true;
                    return t;
                }
            }
        }
        return this.value;
    }

    public String toString() {
        Object obj;
        StringBuilder sb = new StringBuilder();
        sb.append("Suppliers.memoize(");
        if (this.initialized) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("<supplier that returned ");
            sb2.append(this.value);
            sb2.append(">");
            obj = sb2.toString();
        } else {
            obj = this.delegate;
        }
        sb.append(obj);
        sb.append(")");
        return sb.toString();
    }
}
