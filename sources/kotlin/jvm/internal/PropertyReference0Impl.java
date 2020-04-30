package kotlin.jvm.internal;

import kotlin.reflect.KDeclarationContainer;

public class PropertyReference0Impl extends PropertyReference0 {
    private final String name;
    private final KDeclarationContainer owner;
    private final String signature;

    public PropertyReference0Impl(KDeclarationContainer kDeclarationContainer, String str, String str2) {
        this.owner = kDeclarationContainer;
        this.name = str;
        this.signature = str2;
    }

    public KDeclarationContainer getOwner() {
        return this.owner;
    }

    public String getName() {
        return this.name;
    }

    public String getSignature() {
        return this.signature;
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [kotlin.reflect.KProperty0$Getter, kotlin.reflect.KCallable] */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r1v1, types: [kotlin.reflect.KProperty0$Getter, kotlin.reflect.KCallable]
      assigns: [kotlin.reflect.KProperty0$Getter]
      uses: [kotlin.reflect.KCallable]
      mth insns count: 4
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object get() {
        /*
            r1 = this;
            kotlin.reflect.KProperty0$Getter r1 = r1.getGetter()
            r0 = 0
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.lang.Object r1 = r1.call(r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.jvm.internal.PropertyReference0Impl.get():java.lang.Object");
    }
}
