package com.android.framework.protobuf.nano;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

public final class MessageNanoPrinter {
    public static <T extends MessageNano> String print(T t) {
        String str = "Error printing proto: ";
        if (t == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        try {
            print(null, t, new StringBuffer(), stringBuffer);
            return stringBuffer.toString();
        } catch (IllegalAccessException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(e.getMessage());
            return sb.toString();
        } catch (InvocationTargetException e2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(e2.getMessage());
            return sb2.toString();
        }
    }

    private static void print(String str, Object obj, StringBuffer stringBuffer, StringBuffer stringBuffer2) throws IllegalAccessException, InvocationTargetException {
        Field[] fields;
        int i;
        if (obj != null) {
            String str2 = ">\n";
            String str3 = "  ";
            String str4 = " <\n";
            if (obj instanceof MessageNano) {
                int length = stringBuffer.length();
                if (str != null) {
                    stringBuffer2.append(stringBuffer);
                    stringBuffer2.append(deCamelCaseify(str));
                    stringBuffer2.append(str4);
                    stringBuffer.append(str3);
                }
                Class cls = obj.getClass();
                for (Field field : cls.getFields()) {
                    int modifiers = field.getModifiers();
                    String name = field.getName();
                    if (!"cachedSize".equals(name) && (modifiers & 1) == 1 && (modifiers & 8) != 8) {
                        String str5 = "_";
                        if (!name.startsWith(str5) && !name.endsWith(str5)) {
                            Class type = field.getType();
                            Object obj2 = field.get(obj);
                            if (!type.isArray()) {
                                print(name, obj2, stringBuffer, stringBuffer2);
                            } else if (type.getComponentType() == Byte.TYPE) {
                                print(name, obj2, stringBuffer, stringBuffer2);
                            } else {
                                if (obj2 == null) {
                                    i = 0;
                                } else {
                                    i = Array.getLength(obj2);
                                }
                                for (int i2 = 0; i2 < i; i2++) {
                                    print(name, Array.get(obj2, i2), stringBuffer, stringBuffer2);
                                }
                            }
                        }
                    }
                }
                for (Method name2 : cls.getMethods()) {
                    String name3 = name2.getName();
                    if (name3.startsWith("set")) {
                        String substring = name3.substring(3);
                        try {
                            StringBuilder sb = new StringBuilder();
                            sb.append("has");
                            sb.append(substring);
                            if (((Boolean) cls.getMethod(sb.toString(), new Class[0]).invoke(obj, new Object[0])).booleanValue()) {
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("get");
                                sb2.append(substring);
                                print(substring, cls.getMethod(sb2.toString(), new Class[0]).invoke(obj, new Object[0]), stringBuffer, stringBuffer2);
                            }
                        } catch (NoSuchMethodException unused) {
                        }
                    }
                }
                if (str != null) {
                    stringBuffer.setLength(length);
                    stringBuffer2.append(stringBuffer);
                    stringBuffer2.append(str2);
                }
            } else if (obj instanceof Map) {
                Map map = (Map) obj;
                String deCamelCaseify = deCamelCaseify(str);
                for (Entry entry : map.entrySet()) {
                    stringBuffer2.append(stringBuffer);
                    stringBuffer2.append(deCamelCaseify);
                    stringBuffer2.append(str4);
                    int length2 = stringBuffer.length();
                    stringBuffer.append(str3);
                    print("key", entry.getKey(), stringBuffer, stringBuffer2);
                    print("value", entry.getValue(), stringBuffer, stringBuffer2);
                    stringBuffer.setLength(length2);
                    stringBuffer2.append(stringBuffer);
                    stringBuffer2.append(str2);
                }
            } else {
                String deCamelCaseify2 = deCamelCaseify(str);
                stringBuffer2.append(stringBuffer);
                stringBuffer2.append(deCamelCaseify2);
                stringBuffer2.append(": ");
                if (obj instanceof String) {
                    String sanitizeString = sanitizeString((String) obj);
                    String str6 = "\"";
                    stringBuffer2.append(str6);
                    stringBuffer2.append(sanitizeString);
                    stringBuffer2.append(str6);
                } else if (obj instanceof byte[]) {
                    appendQuotedBytes((byte[]) obj, stringBuffer2);
                } else {
                    stringBuffer2.append(obj);
                }
                stringBuffer2.append("\n");
            }
        }
    }

    private static String deCamelCaseify(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (i == 0) {
                stringBuffer.append(Character.toLowerCase(charAt));
            } else if (Character.isUpperCase(charAt)) {
                stringBuffer.append('_');
                stringBuffer.append(Character.toLowerCase(charAt));
            } else {
                stringBuffer.append(charAt);
            }
        }
        return stringBuffer.toString();
    }

    private static String sanitizeString(String str) {
        if (!str.startsWith("http") && str.length() > 200) {
            StringBuilder sb = new StringBuilder();
            sb.append(str.substring(0, 200));
            sb.append("[...]");
            str = sb.toString();
        }
        return escapeString(str);
    }

    private static String escapeString(String str) {
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt < ' ' || charAt > '~' || charAt == '\"' || charAt == '\'') {
                sb.append(String.format("\\u%04x", new Object[]{Integer.valueOf(charAt)}));
            } else {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    private static void appendQuotedBytes(byte[] bArr, StringBuffer stringBuffer) {
        if (bArr == null) {
            stringBuffer.append("\"\"");
            return;
        }
        stringBuffer.append('\"');
        for (byte b : bArr) {
            byte b2 = b & 255;
            if (b2 == 92 || b2 == 34) {
                stringBuffer.append('\\');
                stringBuffer.append((char) b2);
            } else if (b2 < 32 || b2 >= Byte.MAX_VALUE) {
                stringBuffer.append(String.format("\\%03o", new Object[]{Integer.valueOf(b2)}));
            } else {
                stringBuffer.append((char) b2);
            }
        }
        stringBuffer.append('\"');
    }
}
