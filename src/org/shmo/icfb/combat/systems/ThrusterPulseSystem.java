package org.shmo.icfb.combat.systems;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.utilities.ShmoCombatUtils;
import org.shmo.icfb.utilities.ShmoMath;
import org.shmo.icfb.utilities.ShmoRenderUtils;

import java.awt.*;
import java.util.List;

public class ThrusterPulseSystem extends BaseShipSystemScript {
    public static final String ID = "icfb_thruster_pulse";
    private static final String IMPULSE_KEY = "$ICFB_THRUSTER_PULSE_0";

    private static Vector2f getStartVelocity(ShipAPI ship) {
        Vector2f startVelocity = ShmoCombatUtils.computeShipAccelerationVector(ship);
        if (startVelocity.lengthSquared() == 0) {
            startVelocity = new Vector2f(ship.getVelocity());
        }
        if (startVelocity.lengthSquared() == 0) {
            return startVelocity;
        }
        startVelocity.normalise();
        startVelocity.scale(ship.getMaxSpeed());
        return startVelocity;
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = (ShipAPI)stats.getEntity();
        if (ship == null)
            return;
        applyInit(state, ship);
        applySpeedBoost(stats, effectLevel, ship);
        applyEngineEffect(effectLevel, ship);
        applyTrailEffect(state, ship);
    }

    private void applyTrailEffect(State state, ShipAPI ship) {
        if (state != State.ACTIVE)
            return;
        ShmoRenderUtils.drawShipTrailEffect(ship, new Color(255,255,255,50), true);
    }

    private void applyInit(State state, ShipAPI ship) {
        if (state == State.IN) {
            if (ship.getCustomData().get(IMPULSE_KEY) == null) {
                ship.setCustomData(IMPULSE_KEY, new Object());
                Vector2f startVel = getStartVelocity(ship);
                ship.getVelocity().set(startVel.x, startVel.y);
            }
        } else if (ship.getCustomData().get(IMPULSE_KEY) != null) {
            ship.setCustomData(IMPULSE_KEY, null);
        }
    }

    private void applySpeedBoost(MutableShipStatsAPI stats, float effectLevel, ShipAPI ship) {
        float speed = stats.getMaxSpeed().base;
        speed += ItCameFromBeyond.Global.getSettings().shipSystem.thrusterPulseImpulseMagnitude *
                ShmoMath.easeInCubic(effectLevel);
        Vector2f velocity = ship.getVelocity();
        velocity.normalise();
        velocity.scale(speed);
    }

    private void applyEngineEffect(float effectLevel, ShipAPI ship) {
        List<ShipEngineControllerAPI.ShipEngineAPI> engines = ship.getEngineController().getShipEngines();
        for (ShipEngineControllerAPI.ShipEngineAPI engine : engines) {
            if (!engine.isSystemActivated())
                continue;
            ship.getEngineController().setFlameLevel(engine.getEngineSlot(), effectLevel);
        }
        ship.getEngineController().forceShowAccelerating();
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {

    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        return getStartVelocity(ship).lengthSquared() != 0;
    }
}
