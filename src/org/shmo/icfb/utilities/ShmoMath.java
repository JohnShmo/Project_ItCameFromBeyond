package org.shmo.icfb.utilities;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;

public class ShmoMath {

    /**
     * Calculate a rotated version of the given vector.
     * @param v The vector to rotate.
     * @param deltaDegrees The number of degrees to rotate by.
     * @return The resulting rotated vector.
     */
    public static @NotNull Vector2f rotateVector(@NotNull Vector2f v, float deltaDegrees) {
        deltaDegrees *= 0.0174533f; // to radians

        return new Vector2f(
                v.x * (float) Math.cos(deltaDegrees) - v.y * (float)Math.sin(deltaDegrees),
                v.x * (float)Math.sin(deltaDegrees) + v.y * (float)Math.cos(deltaDegrees)
        );
    }

    /**
     * Calculate a reflected version of the given projectile normal vector off of a given surface normal.
     * @param projectileNormal The direction normal of the projectile.
     * @param surfaceNormal The surface normal for the projectile to bounce from.
     * @return The resulting reflected normal vector.
     */
    public static @NotNull Vector2f reflectNormal(@NotNull Vector2f projectileNormal, @NotNull Vector2f surfaceNormal) {
        Vector2f normalizedProjectileNormal = new Vector2f(projectileNormal);
        normalizedProjectileNormal.normalise();

        Vector2f normalizedSurfaceNormal = new Vector2f(surfaceNormal);
        normalizedSurfaceNormal.normalise();

        float dotProduct = Vector2f.dot(normalizedProjectileNormal, normalizedSurfaceNormal);

        // Compute the reflection vector
        Vector2f reflection = new Vector2f(normalizedSurfaceNormal);
        reflection.scale(2 * dotProduct);
        Vector2f bounceNormal = Vector2f.sub(reflection, normalizedProjectileNormal, null);

        bounceNormal.normalise();

        return bounceNormal;
    }

    /**
     * Linearly interpolate within a range of two values (a and b) given the current position in the range (t).
     * @param a The starting value of the range.
     * @param b The ending value of the range.
     * @param t The current position in the range (0.0f - 1.0f).
     * @return The resulting interpolated value.
     */
    public static float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    /**
     * Linearly interpolate within a range of two values (a and b) given the current position in the range (t).
     * @param a The starting value of the range.
     * @param b The ending value of the range.
     * @param t The current position in the range (0.0f - 1.0f).
     * @return The resulting interpolated value.
     */
    public static @NotNull Vector2f lerp(@NotNull Vector2f a, @NotNull Vector2f b, float t) {
        return new Vector2f(lerp(a.x, b.x, t), lerp(a.y, b.y, t));
    }

    /**
     * Linearly interpolate within a range of two values (a and b) given the current position in the range (t).
     * @param a The starting value of the range.
     * @param b The ending value of the range.
     * @param t The current position in the range (0.0f - 1.0f).
     * @return The resulting interpolated value.
     */
    public static @NotNull Vector3f lerp(@NotNull Vector3f a, @NotNull Vector3f b, float t) {
        return new Vector3f(lerp(a.x, b.x, t), lerp(a.y, b.y, t), lerp(a.z, b.z, t));
    }

    /**
     * Linearly interpolate within a range of two values (a and b) given the current position in the range (t).
     * @param a The starting value of the range.
     * @param b The ending value of the range.
     * @param t The current position in the range (0.0f - 1.0f).
     * @return The resulting interpolated value.
     */
    public static @NotNull Color lerp(@NotNull Color a, @NotNull Color b, float t) {
        return new Color(
                (int)lerp((float)a.getRed(), (float)b.getRed(), t),
                (int)lerp((float)a.getGreen(), (float)b.getGreen(), t),
                (int)lerp((float)a.getBlue(), (float)b.getBlue(), t),
                (int)lerp((float)a.getAlpha(), (float)b.getAlpha(), t)
        );
    }

    public static float easeInSine(float t) {
        return 1f - (float)Math.cos((t * Math.PI) / 2);
    }

    public static float eastOutSine(float t) {
        return (float)Math.sin((t * Math.PI) / 2);
    }

    public static float easeInOutSine(float t) {
        return (float)(-(Math.cos(Math.PI * t) - 1) / 2);
    }

    public static float easeInQuad(float t) {
        return t * t;
    }

    public static float easeOutQuad(float t) {
        return 1f - (1f - t) * (1f - t);
    }

    public static float easeInOutQuad(float t) {
        return (float)(t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2);
    }

    public static float easeInCubic(float t) {
        return t * t * t;
    }

    public static float easeOutCubic(float t) {
        return 1f - ((1f - t) * (1f - t) * (1f - t));
    }

    public static float easeInOutCubic(float t) {
        return (float)(t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2);
    }

    public static float easeInQuart(float t) {
        return t * t * t * t;
    }

    public static float easeOutQuart(float t) {
        return 1f - ((1f - t) * (1f - t) * (1f - t) * (1f - t));
    }

    public static float easeInOutQuart(float t) {
        return (float)(t < 0.5 ? 8 * t * t * t * t : 1 - Math.pow(-2 * t + 2, 4) / 2);
    }

    public static float easeInExpo(float t) {
        return (float)(t == 0 ? 0 : Math.pow(2, 10 * t - 10));
    }

    public static float easeOutExpo(float t) {
        return (float)(t == 1 ? 1 : 1 - Math.pow(2, -10 * t));
    }

    public static float easeInOutExpo(float t) {
        return (float)(
                t == 0
                ? 0
                : t == 1
                ? 1
                : t < 0.5 ? Math.pow(2, 20 * t - 10) / 2
                : (2 - Math.pow(2, -20 * t + 10)) / 2
        );
    }

    public static float easeInCirc(float t) {
        return (float)(1 - Math.sqrt(1 - Math.pow(t, 2)));
    }

    public static float easeOutCirc(float t) {
        return (float)(Math.sqrt(1 - Math.pow(t - 1, 2)));
    }

    public static float easeInOutCirc(float t) {
        return (float)(
                t < 0.5
                ? (1 - Math.sqrt(1 - Math.pow(2 * t, 2))) / 2
                : (Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1) / 2
        );
    }
}
