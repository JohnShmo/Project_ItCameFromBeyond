package org.shmo.icfb.combat.systems.ai;

import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.combat.systems.QuantumInversionSystem;

import java.util.List;

public class QuantumInversionAI implements ShipSystemAIScript {
    private ShipAPI _ship;
    private ShipSystemAPI _system;

    private static final float MIN_FRACTION_TO_USE = 0.5f;
    private static final float TIME_WEIGHT = 0.025f;
    private static final float HIGH_FLUX_WEIGHT = 0.2f;
    private static final float LOW_HIT_POINTS_WEIGHT = 0.2f;
    private static final float FLUX_DIFF_WEIGHT = 0.66666f;
    private static final float HIT_POINTS_DIFF_WEIGHT = 1.25f;
    private static final float VULNERABLE_WEIGHT = 0.2f;
    private static final float WAS_OVERLOADED_WEIGHT = 0.25f;

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        _ship = ship;
        _system = system;
        _timeSinceLastUse = 0;
    }

    private float computeHighFluxFraction() {
        float currFlux = _ship.getCurrFlux();
        float maxFlux = _ship.getMaxFlux();
        float currFluxFraction = currFlux / maxFlux;
        float hardFluxFraction = _ship.getFluxTracker().getHardFlux() / maxFlux;
        return currFluxFraction + hardFluxFraction;
    }

    private float computeLowHitPointsFraction() {
        float currHP = _ship.getHitpoints();
        float maxHP = _ship.getMaxHitpoints();
        return 1.0f - (currHP / maxHP);
    }

    private float computeFluxDiffFraction() {
        float curr = _ship.getCurrFlux();
        float max = _ship.getMaxFlux();
        float currFraction = curr / max;
        float lastFraction = QuantumInversionSystem.getFinalFlux(_ship) / max;
        return -(lastFraction - currFraction); // bigger = worse
    }

    private float computeHitPointsDiffFraction() {
        float curr = _ship.getHitpoints();
        float max = _ship.getMaxHitpoints();
        float currFraction = curr / max;
        float lastFraction = QuantumInversionSystem.getFinalHitPoints(_ship) / max;
        return lastFraction - currFraction;
    }

    private boolean isEnemyWeak(ShipAPI target) {
        if (target == null)
            return false;
        if (target.isFighter())
            return false;

        return target.getFluxTracker().isOverloadedOrVenting() &&
                (target.getFluxTracker().getOverloadTimeRemaining() > 5f ||
                        target.getFluxTracker().getTimeToVent() > 5f);
    }

    private boolean isEnemyHealthy(ShipAPI target) {
        if (target == null)
            return false;
        if (target.isFighter())
            return false;

        return !target.getFluxTracker().isOverloadedOrVenting() && (target.getFluxLevel() < 0.1f);
    }

    private float computeVulnerableFraction() {
        final Vector2f currLocation = new Vector2f(_ship.getLocation());
        final Vector2f lastLocation = QuantumInversionSystem.getFinalLocation(_ship);
        final float range = _ship.getCollisionRadius() + 500f;
        float activateFraction = 0f;
        float doNotActivateFraction = 0f;

        final List<ShipAPI> shipsNearCurr =
                CombatUtils.getShipsWithinRange(currLocation, range);
        final List<ShipAPI> shipsNearLast =
                CombatUtils.getShipsWithinRange(lastLocation, range);

        for (ShipAPI enemy : shipsNearCurr) {
            if (enemy.getOwner() == _ship.getOwner())
                continue;
            if (isEnemyWeak(enemy))
                doNotActivateFraction += 0.1f;
            else if (isEnemyHealthy(enemy))
                activateFraction += 0.1f;
        }

        for (ShipAPI enemy : shipsNearLast) {
            if (enemy.getOwner() == _ship.getOwner())
                continue;
            if (isEnemyWeak(enemy))
                activateFraction += 0.1f;
            else if (isEnemyHealthy(enemy))
                doNotActivateFraction += 0.1f;
        }

        return activateFraction - doNotActivateFraction;
    }

    private float computeWasOverloadedFraction() {
        return -QuantumInversionSystem.getFinalOverloadTime(_ship); // big = worse
    }

    private float _timeSinceLastUse;
    private float computeTimeFraction(float amount, float currentFraction) {
        _timeSinceLastUse += amount * currentFraction;
        return _timeSinceLastUse * currentFraction;
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (_system.isActive() || _system.isOutOfAmmo() || _system.isCoolingDown())
            return;

        float fraction = 0f;
        fraction += computeHighFluxFraction()               * HIGH_FLUX_WEIGHT;
        fraction += computeLowHitPointsFraction()           * LOW_HIT_POINTS_WEIGHT;
        fraction += computeFluxDiffFraction()               * FLUX_DIFF_WEIGHT;
        fraction += computeHitPointsDiffFraction()          * HIT_POINTS_DIFF_WEIGHT;
        fraction += computeVulnerableFraction()             * VULNERABLE_WEIGHT;
        fraction += computeWasOverloadedFraction()          * WAS_OVERLOADED_WEIGHT;
        fraction += computeTimeFraction(amount, fraction)   * TIME_WEIGHT;

        if (fraction >= MIN_FRACTION_TO_USE) {
            _ship.useSystem();
            _timeSinceLastUse = 0;
        }
    }
}
