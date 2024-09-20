package org.shmo.icfb.combat.systems.ai;

import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.combat.systems.QuantumInversionSystem;

import java.util.List;

public class QuantumInversionAI implements ShipSystemAIScript {
    private static final float MIN_FRACTION_TO_USE = 0.6f;
    private static final float TIME_WEIGHT = 0.05f;
    private static final float HIGH_FLUX_WEIGHT = 0.2f;
    private static final float LOW_HIT_POINTS_WEIGHT = 0.2f;
    private static final float FLUX_DIFF_WEIGHT = 0.6f;
    private static final float HIT_POINTS_DIFF_WEIGHT = 1f;
    private static final float VULNERABLE_WEIGHT = 0.2f;
    private static final float WAS_OVERLOADED_WEIGHT = 0.25f;
    private static final float TOOK_BIG_DAMAGE_WEIGHT = 1f;
    private static final float AMMO_DIFF_WEIGHT = 1.5f;
    private static final float WEAK_TARGET_WEIGHT = 0.75f;
    private static final float RETREAT_WEIGHT = 1.0f;

    private ShipAPI _ship;
    private ShipSystemAPI _system;
    private float _timeSinceLastUse;
    private float _prevHitPoints;

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        _ship = ship;
        _system = system;
        _timeSinceLastUse = 0;
        _prevHitPoints = ship.getHitpoints();
    }

    private float computeHighFluxFraction() {
        final float currFlux = _ship.getCurrFlux();
        final float maxFlux = _ship.getMaxFlux();
        final float currFluxFraction = currFlux / maxFlux;
        final float hardFluxFraction = _ship.getFluxTracker().getHardFlux() / maxFlux;
        return currFluxFraction + hardFluxFraction;
    }

    private float computeLowHitPointsFraction() {
        final float currHP = _ship.getHitpoints();
        final float maxHP = _ship.getMaxHitpoints();
        return 1.0f - (currHP / maxHP);
    }

    private float computeFluxDiffFraction() {
        final float curr = _ship.getCurrFlux();
        final float max = _ship.getMaxFlux();
        final float currFraction = curr / max;
        final float lastFraction = QuantumInversionSystem.getFinalFlux(_ship) / max;
        return -(lastFraction - currFraction); // bigger = worse
    }

    private float computeHitPointsDiffFraction() {
        final float curr = _ship.getHitpoints();
        final float max = _ship.getMaxHitpoints();
        final float currFraction = curr / max;
        final float lastFraction = QuantumInversionSystem.getFinalHitPoints(_ship) / max;
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

        final List<ShipAPI> shipsNearCurr = CombatUtils.getShipsWithinRange(currLocation, range);
        final List<ShipAPI> shipsNearLast = CombatUtils.getShipsWithinRange(lastLocation, range);

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

    private float computeNearbyEnemyWhileRetreatingFraction() {
        final Vector2f currLocation = new Vector2f(_ship.getLocation());
        final Vector2f lastLocation = QuantumInversionSystem.getFinalLocation(_ship);
        final float range = _ship.getCollisionRadius() + 500f;
        float activateFraction = 0f;
        float doNotActivateFraction = 0f;

        final List<ShipAPI> shipsNearCurr = CombatUtils.getShipsWithinRange(currLocation, range);
        final List<ShipAPI> shipsNearLast = CombatUtils.getShipsWithinRange(lastLocation, range);

        for (ShipAPI enemy : shipsNearCurr) {
            if (enemy.getOwner() == _ship.getOwner())
                continue;
            activateFraction += 0.1f;
        }

        for (ShipAPI enemy : shipsNearLast) {
            if (enemy.getOwner() == _ship.getOwner())
                continue;
            doNotActivateFraction += 0.1f;
        }

        return activateFraction - doNotActivateFraction;
    }

    private float computeWasOverloadedFraction() {
        return -QuantumInversionSystem.getFinalOverloadTime(_ship); // big = worse
    }

    private float computeTookBigDamageFraction() {
        final float currHP = _ship.getHitpoints();
        final float prevHP = _prevHitPoints;
        final float maxHP = _ship.getMaxHitpoints();
        final float currHPLevel = currHP / maxHP;
        final float prevHPLevel = prevHP / maxHP;
        _prevHitPoints = currHP;
        return prevHPLevel - currHPLevel;
    }

    private float computeAmmoDiffFraction() {
        List<QuantumInversionSystem.Frame.WeaponData> weapons = QuantumInversionSystem.getFinalWeaponDataList(_ship);
        int numberOfAmmoWeapons = 0;
        float result = 0f;
        for (QuantumInversionSystem.Frame.WeaponData data : weapons) {
            WeaponAPI weapon = data.weapon;
            if (!weapon.getSpec().usesAmmo())
                continue;
            numberOfAmmoWeapons++;
            final boolean isStrike = weapon.hasAIHint(WeaponAPI.AIHints.STRIKE) ||
                    weapon.hasAIHint(WeaponAPI.AIHints.CONSERVE_FOR_ANTI_ARMOR);
            final int maxAmmo = weapon.getMaxAmmo();
            final int currAmmo = weapon.getAmmo();
            final int lastAmmo = data.ammo;
            final float currAmmoLevel = (float)currAmmo / maxAmmo;
            final float lastAmmoLevel = (float)lastAmmo / maxAmmo;
            float diff = lastAmmoLevel - currAmmoLevel;
            if (isStrike)
                diff *= 10f; // Strike weapons are super valuable!
            result += diff;
        }
        if (numberOfAmmoWeapons > 0)
            result /= numberOfAmmoWeapons;
        return result;
    }

    private float computeWeakTargetFraction(ShipAPI target) {
        if (target == null)
            return 0f;
        if (MathUtils.getDistance(_ship, target) > 1500f)
            return 0f;
        if (isEnemyWeak(target))
            return -1f;
        final float myFluxLevel = _ship.getFluxLevel();
        final float theirFluxLevel = target.getFluxLevel();
        return Math.min(myFluxLevel - theirFluxLevel, 0f);
    }

    private float computeTimeFraction(float amount, float currentFraction) {
        _timeSinceLastUse += amount * currentFraction;
        return _timeSinceLastUse * currentFraction;
    }

    private boolean isRetreating() {
        return _ship.isRetreating();
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (_system.isActive() || _system.isOutOfAmmo() || _system.isCoolingDown())
            return;

        float fraction = 0f;
        if (!isRetreating()) {
            fraction += computeHighFluxFraction() * HIGH_FLUX_WEIGHT;
            fraction += computeLowHitPointsFraction() * LOW_HIT_POINTS_WEIGHT;
            fraction += computeFluxDiffFraction() * FLUX_DIFF_WEIGHT;
            fraction += computeHitPointsDiffFraction() * HIT_POINTS_DIFF_WEIGHT;
            fraction += computeVulnerableFraction() * VULNERABLE_WEIGHT;
            fraction += computeWasOverloadedFraction() * WAS_OVERLOADED_WEIGHT;
            fraction += computeTookBigDamageFraction() * TOOK_BIG_DAMAGE_WEIGHT;
            fraction += computeAmmoDiffFraction() * AMMO_DIFF_WEIGHT;
            fraction += computeWeakTargetFraction(target) * WEAK_TARGET_WEIGHT;
        } else {
            fraction += computeNearbyEnemyWhileRetreatingFraction() * RETREAT_WEIGHT;
            fraction += computeTookBigDamageFraction() * TOOK_BIG_DAMAGE_WEIGHT;
            fraction += computeLowHitPointsFraction() * LOW_HIT_POINTS_WEIGHT;
        }

        fraction += computeTimeFraction(amount, fraction) * TIME_WEIGHT;
        if (fraction >= MIN_FRACTION_TO_USE) {
            _ship.useSystem();
            _timeSinceLastUse = 0;
        }
    }
}
