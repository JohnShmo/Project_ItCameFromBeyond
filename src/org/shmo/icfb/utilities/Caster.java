package org.shmo.icfb.utilities;

@SuppressWarnings("unchecked")
public class Caster {
    public static <T> T rawCast(Object o) {
        T result;
        result = (T)o;
        return result;
    }

    public static <T> T tryCast(Object o) {
        T result;
        try {
            result = rawCast(o);
        } catch (ClassCastException unused) {
            result = null;
        }
        return result;
    }
}
