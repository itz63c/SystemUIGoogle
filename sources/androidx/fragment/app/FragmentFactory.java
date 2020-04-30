package androidx.fragment.app;

import androidx.collection.SimpleArrayMap;
import androidx.fragment.app.Fragment.InstantiationException;

public class FragmentFactory {
    private static final SimpleArrayMap<String, Class<?>> sClassMap = new SimpleArrayMap<>();

    public abstract Fragment instantiate(ClassLoader classLoader, String str);

    private static Class<?> loadClass(ClassLoader classLoader, String str) throws ClassNotFoundException {
        Class<?> cls = (Class) sClassMap.get(str);
        if (cls != null) {
            return cls;
        }
        Class<?> cls2 = Class.forName(str, false, classLoader);
        sClassMap.put(str, cls2);
        return cls2;
    }

    static boolean isFragmentClass(ClassLoader classLoader, String str) {
        try {
            return Fragment.class.isAssignableFrom(loadClass(classLoader, str));
        } catch (ClassNotFoundException unused) {
            return false;
        }
    }

    public static Class<? extends Fragment> loadFragmentClass(ClassLoader classLoader, String str) {
        String str2 = "Unable to instantiate fragment ";
        try {
            return loadClass(classLoader, str);
        } catch (ClassNotFoundException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            sb.append(str);
            sb.append(": make sure class name exists");
            throw new InstantiationException(sb.toString(), e);
        } catch (ClassCastException e2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str2);
            sb2.append(str);
            sb2.append(": make sure class is a valid subclass of Fragment");
            throw new InstantiationException(sb2.toString(), e2);
        }
    }
}
