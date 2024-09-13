package org.shmo.icfb.combat.systems.ai;

import com.fs.starfarer.api.combat.*;
import org.lwjgl.util.vector.Vector2f;

public class QuantumInversionAI implements ShipSystemAIScript {
    private ShipAPI _ship;
    private ShipSystemAPI _system;

    private float _timeSinceLastUse;
    private float _prevHitPoints;
    private float _prevFlux;

    private static final float MIN_FRACTION_TO_USE = 0.5f;
    private static final float TIME_WEIGHT = 0.025f;
    private static final float TOOK_BIG_DAMAGE_WEIGHT = 1.0f;
    private static final float TOOK_BIG_FLUX_WEIGHT = 0.5f;
    private static final float HIGH_FLUX_WEIGHT = 0.2f;
    private static final float HIGH_HARD_FLUX_WEIGHT = 0.2f;

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        _ship = ship;
        _system = system;
        _timeSinceLastUse = 0;
        _prevHitPoints = _ship.getHitpoints();
        _prevFlux = _ship.getCurrFlux();
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (_system.isActive() || _system.isOutOfAmmo() || _system.isCoolingDown())
            return;

        _timeSinceLastUse += amount;

        float currHitPoints = _ship.getHitpoints();
        float maxHitPoints = _ship.getMaxHitpoints();
        float prevHitPoints = _prevHitPoints;
        float currHPFraction = currHitPoints / maxHitPoints;
        float prevHPFraction = prevHitPoints / maxHitPoints;
        float damageTakenFraction = prevHPFraction - currHPFraction;
        _prevHitPoints = currHitPoints;

        float currFlux = _ship.getCurrFlux();
        float maxFlux = _ship.getMaxFlux();
        float prevFlux = _prevFlux;
        float currFluxFraction = currFlux / maxFlux;
        float hardFluxFraction = _ship.getFluxTracker().getHardFlux() / maxFlux;
        float prevFluxFraction = prevFlux / maxFlux;
        float fluxTakenFraction = prevFluxFraction - currFluxFraction;
        _prevFlux = currFlux;

        float fraction = 0f;
        fraction += damageTakenFraction * TOOK_BIG_DAMAGE_WEIGHT;
        fraction += fluxTakenFraction * TOOK_BIG_FLUX_WEIGHT;
        fraction += currFluxFraction * HIGH_FLUX_WEIGHT;
        fraction += hardFluxFraction * HIGH_HARD_FLUX_WEIGHT;
        fraction += (fraction * _timeSinceLastUse) * TIME_WEIGHT;

        if (fraction >= MIN_FRACTION_TO_USE) {
            _ship.useSystem();
            _timeSinceLastUse = 0;
        }
    }
}
