package com.google.common.base;

import java.util.logging.Logger;

final class Platform {

    private static final class JdkPatternCompiler implements PatternCompiler {
        private JdkPatternCompiler() {
        }
    }

    static {
        Logger.getLogger(Platform.class.getName());
        loadPatternCompiler();
    }

    private Platform() {
    }

    static long systemNanoTime() {
        return System.nanoTime();
    }

    private static PatternCompiler loadPatternCompiler() {
        return new JdkPatternCompiler();
    }
}
