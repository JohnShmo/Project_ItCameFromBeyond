package org.shmo.icfb.utilities;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.vector.Vector2f;

public class ShmoCombatUtils {

    /**
     * Calculate the world-aligned vector of a ship's current acceleration.
     * Useful for getting the current intended direction of a ship.
     *
     * @param ship The ship to get the normalised direction vector of acceleration for.
     *
     * @return The normalised direction vector of acceleration. Vector2f(0, 0) if no current acceleration.
     */
    public static @NotNull Vector2f computeShipAccelerationVector(@NotNull ShipAPI ship) {
        ShipEngineControllerAPI engineController = ship.getEngineController();
        float acceleratingForwards = engineController.isAccelerating() ? 1f : 0f;
        float acceleratingBackwards = engineController.isAcceleratingBackwards() ? 1f : 0f;
        float acceleratingLeft = engineController.isStrafingLeft() ? 1f : 0f;
        float acceleratingRight = engineController.isStrafingRight() ? 1f : 0f;

        Vector2f acceleration = new Vector2f(
                acceleratingForwards - acceleratingBackwards,
                acceleratingLeft - acceleratingRight
        );
        if (acceleration.lengthSquared() == 0)
            return acceleration;

        acceleration.normalise();
        return ShmoMath.rotateVector(acceleration, ship.getFacing());
    }
}
