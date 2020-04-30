package androidx.slice;

public class SliceSpecs {
    public static final SliceSpec BASIC = new SliceSpec("androidx.slice.BASIC", 1);
    public static final SliceSpec LIST;
    public static final SliceSpec LIST_V2;

    static {
        String str = "androidx.slice.LIST";
        LIST = new SliceSpec(str, 1);
        LIST_V2 = new SliceSpec(str, 2);
    }
}
