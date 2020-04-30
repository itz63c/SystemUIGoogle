package com.android.systemui.statusbar.notification;

import android.util.FloatProperty;
import android.util.Property;
import android.view.View;
import com.android.systemui.C2011R$id;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AnimatableProperty {

    /* renamed from: X */
    public static final AnimatableProperty f69X = from(View.X, C2011R$id.x_animator_tag, C2011R$id.x_animator_tag_start_value, C2011R$id.x_animator_tag_end_value);

    /* renamed from: Y */
    public static final AnimatableProperty f70Y = from(View.Y, C2011R$id.y_animator_tag, C2011R$id.y_animator_tag_start_value, C2011R$id.y_animator_tag_end_value);

    public abstract int getAnimationEndTag();

    public abstract int getAnimationStartTag();

    public abstract int getAnimatorTag();

    public abstract Property getProperty();

    public static <T extends View> AnimatableProperty from(String str, final BiConsumer<T, Float> biConsumer, final Function<T, Float> function, final int i, final int i2, final int i3) {
        final C11781 r0 = new FloatProperty<T>(str) {
            public Float get(T t) {
                return (Float) function.apply(t);
            }

            public void setValue(T t, float f) {
                biConsumer.accept(t, Float.valueOf(f));
            }
        };
        return new AnimatableProperty() {
            public int getAnimationStartTag() {
                return i2;
            }

            public int getAnimationEndTag() {
                return i3;
            }

            public int getAnimatorTag() {
                return i;
            }

            public Property getProperty() {
                return r0;
            }
        };
    }

    public static <T extends View> AnimatableProperty from(final Property<T, Float> property, final int i, final int i2, final int i3) {
        return new AnimatableProperty() {
            public int getAnimationStartTag() {
                return i2;
            }

            public int getAnimationEndTag() {
                return i3;
            }

            public int getAnimatorTag() {
                return i;
            }

            public Property getProperty() {
                return property;
            }
        };
    }
}
