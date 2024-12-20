package org.shmo.icfb.utilities;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.util.Arrays;

public class ShmoMath {

    public static float[][] copyMatrix(float[][] matrix) {
        if (matrix == null)
            return null;
        final float[][] result = new float[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return result;
    }

    public static float[][] lerpMatrix(float[][] matrix1, float[][] matrix2, float t) {
        if (matrix1 == null)
            return null;
        if (matrix2 == null)
            return null;
        if (matrix1.length != matrix2.length)
            return null;
        if (matrix1[0].length != matrix2[0].length)
            return null;

        if (t <= 0f)
            return copyMatrix(matrix1);
        if (t >= 1f)
            return copyMatrix(matrix2);

        final float[][] result = copyMatrix(matrix1);
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = lerp(matrix1[i][j], matrix2[i][j], t);
            }
        }
        return result;
    }

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
    public static int lerp(int a, int b, float t) {
        if (t <= 0f)
            return a;
        if (t >= 1f)
            return b;

        return (int)((float)a + t * ((float)b - (float)a));
    }

    /**
     * Linearly interpolate within a range of two values (a and b) given the current position in the range (t).
     * @param a The starting value of the range.
     * @param b The ending value of the range.
     * @param t The current position in the range (0.0f - 1.0f).
     * @return The resulting interpolated value.
     */
    public static long lerp(long a, long b, float t) {
        if (t <= 0f)
            return a;
        if (t >= 1f)
            return b;

        return (long)((double)a + (double)t * ((double)b - (double)a));
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

    public static float easeOutSine(float t) {
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

    public static float easeOutBounce(float t) {
        final float n1 = 7.5625f;
        final float d1 = 2.75f;

        if (t < 1 / d1) {
            return n1 * t * t;
        } else if (t < 2 / d1) {
            return n1 * (t -= 1.5 / d1) * t + 0.75f;
        } else if (t < 2.5 / d1) {
            return n1 * (t -= 2.25 / d1) * t + 0.9375f;
        } else {
            return n1 * (t -= 2.625 / d1) * t + 0.984375f;
        }
    }

    public static float easeInOutBounce(float t) {
        return (float)(t < 0.5
                ? (1 - easeOutBounce(1 - 2 * t)) / 2
                : (1 + easeOutBounce(2 * t - 1)) / 2);
    }

    public static float angleDifference(float angle1, float angle2) {
        // Normalize angles to range [0, 360)
        angle1 = (angle1 % 360 + 360) % 360;
        angle2 = (angle2 % 360 + 360) % 360;

        // Calculate the raw difference
        float difference = Math.abs(angle1 - angle2);

        // Ensure the smallest circular difference is returned
        return Math.min(difference, 360 - difference);
    }

    public static float signedAngleDifference(float angle1, float angle2) {
        // Normalize angles to range [0, 360)
        angle1 = ((angle1 % 360) + 360) % 360;
        angle2 = ((angle2 % 360) + 360) % 360;

        // Calculate the signed difference
        float difference = angle1 - angle2;

        // Adjust the difference to be within the range [-180, 180]
        if (difference > 180) {
            difference -= 360;
        } else if (difference < -180) {
            difference += 360;
        }

        return difference;
    }

    public static float dampen(float value, float max, float strength) {
        // Apply damping using a non-linear curve where each output is greater than the one before
        float ratio = value / max;

        // Use a modified curve that retains increasing outputs

        return value * (1 - (float)Math.pow(ratio, strength));
    }

}
