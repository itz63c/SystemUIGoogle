package kotlin.jvm.internal;

import kotlin.reflect.KDeclarationContainer;

public class PropertyReference1Impl extends PropertyReference1 {
    private final String name;
    private final KDeclarationContainer owner;
    private final String signature;

    public PropertyReference1Impl(KDeclarationContainer kDeclarationContainer, String str, String str2) {
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

    /* JADX WARNING: type inference failed for: r2v1, types: [kotlin.reflect.KProperty1$Getter, kotlin.reflect.KCallable] */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r2v1, types: [kotlin.reflect.KProperty1$Getter, kotlin.reflect.KCallable]
      assigns: [kotlin.reflect.KProperty1$Getter]
      uses: [kotlin.reflect.KCallable]
      mth insns count: 5
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
    public java.lang.Object get(java.lang.Object r3) {
        /*
            r2 = this;
            kotlin.reflect.KProperty1$Getter r2 = r2.getGetter()
            r0 = 1
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r1 = 0
            r0[r1] = r3
            java.lang.Object r2 = r2.call(r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.jvm.internal.PropertyReference1Impl.get(java.lang.Object):java.lang.Object");
    }
}
