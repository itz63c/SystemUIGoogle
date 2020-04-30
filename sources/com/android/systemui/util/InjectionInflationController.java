package com.android.systemui.util;

import android.content.Context;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory2;
import android.view.View;
import com.android.keyguard.KeyguardClockSwitch;
import com.android.keyguard.KeyguardMessageArea;
import com.android.keyguard.KeyguardSliceView;
import com.android.systemui.dagger.SystemUIRootComponent;
import com.android.systemui.p007qs.QSFooterImpl;
import com.android.systemui.p007qs.QSPanel;
import com.android.systemui.p007qs.QuickQSPanel;
import com.android.systemui.p007qs.QuickStatusBarHeader;
import com.android.systemui.p007qs.customize.QSCustomizer;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.phone.LockIcon;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InjectionInflationController {
    private final Factory2 mFactory = new InjectionFactory();
    /* access modifiers changed from: private */
    public final ArrayMap<String, Method> mInjectionMap = new ArrayMap<>();
    /* access modifiers changed from: private */
    public final ViewCreator mViewCreator;

    private class InjectionFactory implements Factory2 {
        private InjectionFactory() {
        }

        public View onCreateView(String str, Context context, AttributeSet attributeSet) {
            String str2 = "Could not inflate ";
            Method method = (Method) InjectionInflationController.this.mInjectionMap.get(str);
            if (method == null) {
                return null;
            }
            try {
                return (View) method.invoke(InjectionInflationController.this.mViewCreator.createInstanceCreator(new ViewAttributeProvider(context, attributeSet)), new Object[0]);
            } catch (IllegalAccessException e) {
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append(str);
                throw new InflateException(sb.toString(), e);
            } catch (InvocationTargetException e2) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str2);
                sb2.append(str);
                throw new InflateException(sb2.toString(), e2);
            }
        }

        public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
            return onCreateView(str, context, attributeSet);
        }
    }

    public class ViewAttributeProvider {
        private final AttributeSet mAttrs;
        private final Context mContext;

        private ViewAttributeProvider(InjectionInflationController injectionInflationController, Context context, AttributeSet attributeSet) {
            this.mContext = context;
            this.mAttrs = attributeSet;
        }

        public Context provideContext() {
            return this.mContext;
        }

        public AttributeSet provideAttributeSet() {
            return this.mAttrs;
        }
    }

    public interface ViewCreator {
        ViewInstanceCreator createInstanceCreator(ViewAttributeProvider viewAttributeProvider);
    }

    public interface ViewInstanceCreator {
        NotificationShelf creatNotificationShelf();

        KeyguardClockSwitch createKeyguardClockSwitch();

        KeyguardMessageArea createKeyguardMessageArea();

        KeyguardSliceView createKeyguardSliceView();

        LockIcon createLockIcon();

        NotificationStackScrollLayout createNotificationStackScrollLayout();

        QSCustomizer createQSCustomizer();

        QSPanel createQSPanel();

        QSFooterImpl createQsFooter();

        QuickStatusBarHeader createQsHeader();

        QuickQSPanel createQuickQSPanel();
    }

    public InjectionInflationController(SystemUIRootComponent systemUIRootComponent) {
        this.mViewCreator = systemUIRootComponent.createViewCreator();
        initInjectionMap();
    }

    public LayoutInflater injectable(LayoutInflater layoutInflater) {
        LayoutInflater cloneInContext = layoutInflater.cloneInContext(layoutInflater.getContext());
        cloneInContext.setPrivateFactory(this.mFactory);
        return cloneInContext;
    }

    private void initInjectionMap() {
        Method[] declaredMethods;
        for (Method method : ViewInstanceCreator.class.getDeclaredMethods()) {
            if (View.class.isAssignableFrom(method.getReturnType()) && (method.getModifiers() & 1) != 0) {
                this.mInjectionMap.put(method.getReturnType().getName(), method);
            }
        }
    }
}
