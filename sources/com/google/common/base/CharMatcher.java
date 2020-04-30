package com.google.common.base;

public abstract class CharMatcher {

    static abstract class FastMatcher extends CharMatcher {
        FastMatcher() {
        }
    }

    /* renamed from: com.google.common.base.CharMatcher$Is */
    private static final class C1952Is extends FastMatcher {
        private final char match;

        C1952Is(char c) {
            this.match = c;
        }

        public boolean matches(char c) {
            return c == this.match;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("CharMatcher.is('");
            sb.append(CharMatcher.showCharacter(this.match));
            sb.append("')");
            return sb.toString();
        }
    }

    static abstract class NamedFastMatcher extends FastMatcher {
        private final String description;

        NamedFastMatcher(String str) {
            Preconditions.checkNotNull(str);
            this.description = str;
        }

        public final String toString() {
            return this.description;
        }
    }

    private static final class None extends NamedFastMatcher {
        static final None INSTANCE = new None();

        public boolean matches(char c) {
            return false;
        }

        private None() {
            super("CharMatcher.none()");
        }

        public int indexIn(CharSequence charSequence, int i) {
            Preconditions.checkPositionIndex(i, charSequence.length());
            return -1;
        }
    }

    static final class Whitespace extends NamedFastMatcher {
        static final int SHIFT = Integer.numberOfLeadingZeros(31);

        static {
            new Whitespace();
        }

        Whitespace() {
            super("CharMatcher.whitespace()");
        }

        public boolean matches(char c) {
            return " 　\r   　 \u000b　   　 \t     \f 　 　　 \n 　".charAt((48906 * c) >>> SHIFT) == c;
        }
    }

    public abstract boolean matches(char c);

    public static CharMatcher none() {
        return None.INSTANCE;
    }

    /* renamed from: is */
    public static CharMatcher m125is(char c) {
        return new C1952Is(c);
    }

    protected CharMatcher() {
    }

    public int indexIn(CharSequence charSequence, int i) {
        int length = charSequence.length();
        Preconditions.checkPositionIndex(i, length);
        while (i < length) {
            if (matches(charSequence.charAt(i))) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public static String showCharacter(char c) {
        char[] cArr = {'\\', 'u', 0, 0, 0, 0};
        for (int i = 0; i < 4; i++) {
            cArr[5 - i] = "0123456789ABCDEF".charAt(c & 15);
            c = (char) (c >> 4);
        }
        return String.copyValueOf(cArr);
    }
}
