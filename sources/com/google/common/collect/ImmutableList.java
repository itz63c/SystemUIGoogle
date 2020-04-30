package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

public abstract class ImmutableList<E> extends ImmutableCollection<E> implements List<E>, RandomAccess {
    private static final UnmodifiableListIterator<Object> EMPTY_ITR = new Itr(RegularImmutableList.EMPTY, 0);

    public static final class Builder<E> extends ArrayBasedBuilder<E> {
        public Builder() {
            this(4);
        }

        Builder(int i) {
            super(i);
        }

        @CanIgnoreReturnValue
        public Builder<E> add(E e) {
            super.add(e);
            return this;
        }

        public ImmutableList<E> build() {
            this.forceCopy = true;
            return ImmutableList.asImmutableList(this.contents, this.size);
        }
    }

    static class Itr<E> extends AbstractIndexedListIterator<E> {
        private final ImmutableList<E> list;

        Itr(ImmutableList<E> immutableList, int i) {
            super(immutableList.size(), i);
            this.list = immutableList;
        }

        /* access modifiers changed from: protected */
        public E get(int i) {
            return this.list.get(i);
        }
    }

    private static class ReverseImmutableList<E> extends ImmutableList<E> {
        private final transient ImmutableList<E> forwardList;

        ReverseImmutableList(ImmutableList<E> immutableList) {
            this.forwardList = immutableList;
        }

        private int reverseIndex(int i) {
            return (size() - 1) - i;
        }

        private int reversePosition(int i) {
            return size() - i;
        }

        public ImmutableList<E> reverse() {
            return this.forwardList;
        }

        public boolean contains(Object obj) {
            return this.forwardList.contains(obj);
        }

        public int indexOf(Object obj) {
            int lastIndexOf = this.forwardList.lastIndexOf(obj);
            if (lastIndexOf >= 0) {
                return reverseIndex(lastIndexOf);
            }
            return -1;
        }

        public int lastIndexOf(Object obj) {
            int indexOf = this.forwardList.indexOf(obj);
            if (indexOf >= 0) {
                return reverseIndex(indexOf);
            }
            return -1;
        }

        public ImmutableList<E> subList(int i, int i2) {
            Preconditions.checkPositionIndexes(i, i2, size());
            return this.forwardList.subList(reversePosition(i2), reversePosition(i)).reverse();
        }

        public E get(int i) {
            Preconditions.checkElementIndex(i, size());
            return this.forwardList.get(reverseIndex(i));
        }

        public int size() {
            return this.forwardList.size();
        }

        /* access modifiers changed from: 0000 */
        public boolean isPartialView() {
            return this.forwardList.isPartialView();
        }
    }

    static class SerializedForm implements Serializable {
        private static final long serialVersionUID = 0;
        final Object[] elements;

        SerializedForm(Object[] objArr) {
            this.elements = objArr;
        }

        /* access modifiers changed from: 0000 */
        public Object readResolve() {
            return ImmutableList.copyOf(this.elements);
        }
    }

    class SubList extends ImmutableList<E> {
        final transient int length;
        final transient int offset;

        /* access modifiers changed from: 0000 */
        public boolean isPartialView() {
            return true;
        }

        SubList(int i, int i2) {
            this.offset = i;
            this.length = i2;
        }

        public int size() {
            return this.length;
        }

        /* access modifiers changed from: 0000 */
        public Object[] internalArray() {
            return ImmutableList.this.internalArray();
        }

        /* access modifiers changed from: 0000 */
        public int internalArrayStart() {
            return ImmutableList.this.internalArrayStart() + this.offset;
        }

        /* access modifiers changed from: 0000 */
        public int internalArrayEnd() {
            return ImmutableList.this.internalArrayStart() + this.offset + this.length;
        }

        public E get(int i) {
            Preconditions.checkElementIndex(i, this.length);
            return ImmutableList.this.get(i + this.offset);
        }

        public ImmutableList<E> subList(int i, int i2) {
            Preconditions.checkPositionIndexes(i, i2, this.length);
            ImmutableList immutableList = ImmutableList.this;
            int i3 = this.offset;
            return immutableList.subList(i + i3, i2 + i3);
        }
    }

    /* renamed from: of */
    public static <E> ImmutableList<E> m130of() {
        return RegularImmutableList.EMPTY;
    }

    /* renamed from: of */
    public static <E> ImmutableList<E> m131of(E e) {
        return construct(e);
    }

    public static <E> ImmutableList<E> copyOf(E[] eArr) {
        if (eArr.length == 0) {
            return m130of();
        }
        return construct((Object[]) eArr.clone());
    }

    private static <E> ImmutableList<E> construct(Object... objArr) {
        ObjectArrays.checkElementsNotNull(objArr);
        return asImmutableList(objArr);
    }

    static <E> ImmutableList<E> asImmutableList(Object[] objArr) {
        return asImmutableList(objArr, objArr.length);
    }

    static <E> ImmutableList<E> asImmutableList(Object[] objArr, int i) {
        if (i == 0) {
            return m130of();
        }
        return new RegularImmutableList(objArr, i);
    }

    ImmutableList() {
    }

    public UnmodifiableIterator<E> iterator() {
        return listIterator();
    }

    public UnmodifiableListIterator<E> listIterator() {
        return listIterator(0);
    }

    public UnmodifiableListIterator<E> listIterator(int i) {
        Preconditions.checkPositionIndex(i, size());
        if (isEmpty()) {
            return EMPTY_ITR;
        }
        return new Itr(this, i);
    }

    public int indexOf(Object obj) {
        if (obj == null) {
            return -1;
        }
        return Lists.indexOfImpl(this, obj);
    }

    public int lastIndexOf(Object obj) {
        if (obj == null) {
            return -1;
        }
        return Lists.lastIndexOfImpl(this, obj);
    }

    public boolean contains(Object obj) {
        return indexOf(obj) >= 0;
    }

    public ImmutableList<E> subList(int i, int i2) {
        Preconditions.checkPositionIndexes(i, i2, size());
        int i3 = i2 - i;
        if (i3 == size()) {
            return this;
        }
        if (i3 == 0) {
            return m130of();
        }
        return subListUnchecked(i, i2);
    }

    /* access modifiers changed from: 0000 */
    public ImmutableList<E> subListUnchecked(int i, int i2) {
        return new SubList(i, i2 - i);
    }

    @CanIgnoreReturnValue
    @Deprecated
    public final boolean addAll(int i, Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    @CanIgnoreReturnValue
    @Deprecated
    public final E set(int i, E e) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public final void add(int i, E e) {
        throw new UnsupportedOperationException();
    }

    @CanIgnoreReturnValue
    @Deprecated
    public final E remove(int i) {
        throw new UnsupportedOperationException();
    }

    /* access modifiers changed from: 0000 */
    public int copyIntoArray(Object[] objArr, int i) {
        int size = size();
        for (int i2 = 0; i2 < size; i2++) {
            objArr[i + i2] = get(i2);
        }
        return i + size;
    }

    public ImmutableList<E> reverse() {
        return size() <= 1 ? this : new ReverseImmutableList(this);
    }

    public boolean equals(Object obj) {
        return Lists.equalsImpl(this, obj);
    }

    public int hashCode() {
        int size = size();
        int i = 1;
        for (int i2 = 0; i2 < size; i2++) {
            i = ~(~((i * 31) + get(i2).hashCode()));
        }
        return i;
    }

    private void readObject(ObjectInputStream objectInputStream) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }

    /* access modifiers changed from: 0000 */
    public Object writeReplace() {
        return new SerializedForm(toArray());
    }

    public static <E> Builder<E> builder() {
        return new Builder<>();
    }
}
