package com.android.systemui.log;

import android.content.ContentResolver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import androidx.appcompat.R$styleable;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: LogcatEchoTrackerDebug.kt */
public final class LogcatEchoTrackerDebug implements LogcatEchoTracker {
    public static final Factory Factory = new Factory(null);
    /* access modifiers changed from: private */
    public final Map<String, LogLevel> cachedBufferLevels;
    /* access modifiers changed from: private */
    public final Map<String, LogLevel> cachedTagLevels;
    private final ContentResolver contentResolver;

    /* compiled from: LogcatEchoTrackerDebug.kt */
    public static final class Factory {
        private Factory() {
        }

        public /* synthetic */ Factory(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final LogcatEchoTrackerDebug create(ContentResolver contentResolver, Looper looper) {
            Intrinsics.checkParameterIsNotNull(contentResolver, "contentResolver");
            Intrinsics.checkParameterIsNotNull(looper, "mainLooper");
            LogcatEchoTrackerDebug logcatEchoTrackerDebug = new LogcatEchoTrackerDebug(contentResolver, null);
            logcatEchoTrackerDebug.attach(looper);
            return logcatEchoTrackerDebug;
        }
    }

    public static final LogcatEchoTrackerDebug create(ContentResolver contentResolver2, Looper looper) {
        return Factory.create(contentResolver2, looper);
    }

    private LogcatEchoTrackerDebug(ContentResolver contentResolver2) {
        this.contentResolver = contentResolver2;
        this.cachedBufferLevels = new LinkedHashMap();
        this.cachedTagLevels = new LinkedHashMap();
    }

    public /* synthetic */ LogcatEchoTrackerDebug(ContentResolver contentResolver2, DefaultConstructorMarker defaultConstructorMarker) {
        this(contentResolver2);
    }

    /* access modifiers changed from: private */
    public final void attach(Looper looper) {
        this.contentResolver.registerContentObserver(Global.getUriFor("systemui/buffer"), true, new LogcatEchoTrackerDebug$attach$1(this, looper, new Handler(looper)));
        this.contentResolver.registerContentObserver(Global.getUriFor("systemui/tag"), true, new LogcatEchoTrackerDebug$attach$2(this, looper, new Handler(looper)));
    }

    public synchronized boolean isBufferLoggable(String str, LogLevel logLevel) {
        Intrinsics.checkParameterIsNotNull(str, "bufferName");
        Intrinsics.checkParameterIsNotNull(logLevel, "level");
        return logLevel.ordinal() >= getLogLevel(str, "systemui/buffer", this.cachedBufferLevels).ordinal();
    }

    public synchronized boolean isTagLoggable(String str, LogLevel logLevel) {
        Intrinsics.checkParameterIsNotNull(str, "tagName");
        Intrinsics.checkParameterIsNotNull(logLevel, "level");
        return logLevel.compareTo(getLogLevel(str, "systemui/tag", this.cachedTagLevels)) >= 0;
    }

    private final LogLevel getLogLevel(String str, String str2, Map<String, LogLevel> map) {
        LogLevel logLevel = (LogLevel) map.get(str);
        if (logLevel != null) {
            return logLevel;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append('/');
        sb.append(str);
        LogLevel readSetting = readSetting(sb.toString());
        map.put(str, readSetting);
        return readSetting;
    }

    private final LogLevel readSetting(String str) {
        try {
            return parseProp(Global.getString(this.contentResolver, str));
        } catch (SettingNotFoundException unused) {
            return LogcatEchoTrackerDebugKt.DEFAULT_LEVEL;
        }
    }

    private final LogLevel parseProp(String str) {
        String str2;
        if (str == null) {
            str2 = null;
        } else if (str != null) {
            str2 = str.toLowerCase();
            Intrinsics.checkExpressionValueIsNotNull(str2, "(this as java.lang.String).toLowerCase()");
        } else {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
        }
        if (str2 != null) {
            switch (str2.hashCode()) {
                case -1408208058:
                    if (str2.equals("assert")) {
                        return LogLevel.WTF;
                    }
                    break;
                case 100:
                    if (str2.equals("d")) {
                        return LogLevel.DEBUG;
                    }
                    break;
                case 101:
                    if (str2.equals("e")) {
                        return LogLevel.ERROR;
                    }
                    break;
                case 105:
                    if (str2.equals("i")) {
                        return LogLevel.INFO;
                    }
                    break;
                case R$styleable.AppCompatTheme_windowFixedHeightMajor /*118*/:
                    if (str2.equals("v")) {
                        return LogLevel.VERBOSE;
                    }
                    break;
                case R$styleable.AppCompatTheme_windowFixedHeightMinor /*119*/:
                    if (str2.equals("w")) {
                        return LogLevel.WARNING;
                    }
                    break;
                case 118057:
                    if (str2.equals("wtf")) {
                        return LogLevel.WTF;
                    }
                    break;
                case 3237038:
                    if (str2.equals("info")) {
                        return LogLevel.INFO;
                    }
                    break;
                case 3641990:
                    if (str2.equals("warn")) {
                        return LogLevel.WARNING;
                    }
                    break;
                case 95458899:
                    if (str2.equals("debug")) {
                        return LogLevel.DEBUG;
                    }
                    break;
                case 96784904:
                    if (str2.equals("error")) {
                        return LogLevel.ERROR;
                    }
                    break;
                case 351107458:
                    if (str2.equals("verbose")) {
                        return LogLevel.VERBOSE;
                    }
                    break;
                case 1124446108:
                    if (str2.equals("warning")) {
                        return LogLevel.WARNING;
                    }
                    break;
            }
        }
        return LogcatEchoTrackerDebugKt.DEFAULT_LEVEL;
    }
}
