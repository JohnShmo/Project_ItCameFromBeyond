package org.shmo.icfb.combat.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import org.shmo.icfb.combat.systems.QuantumInversionSystem;

import java.util.ArrayList;
import java.util.List;

public class QuantumInversionPlugin extends BaseEveryFrameCombatPlugin {

    private static final float KEYFRAME_INTERVAL = 0.1f;
    private float _timePassed = 0f;

    private List<ShipAPI> getShipsWithQuantumInversion(CombatEngineAPI engine) {
        final List<ShipAPI> result = new ArrayList<>();
        final List<ShipAPI> allShips = engine.getShips();

        for (ShipAPI ship : allShips) {
            if (ship.getSystem().getId().equals(QuantumInversionSystem.ID))
                result.add(ship);
        }

        return result;
    }

    @Override
    public void init(CombatEngineAPI engine) {
        _timePassed = 0f;
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null)
            return;
        if (engine.isPaused())
            return;
        _timePassed += amount;

        final List<ShipAPI> ships = getShipsWithQuantumInversion(engine);
        for (ShipAPI ship : ships) {
            if (QuantumInversionSystem.isPlaying(ship)) {
                QuantumInversionSystem.play(ship, amount);
            } else {
                if (!QuantumInversionSystem.isActive(ship)) {
                    if (_timePassed >= KEYFRAME_INTERVAL)
                        QuantumInversionSystem.record(ship, _timePassed);
                }
            }
        }

        if (_timePassed >= KEYFRAME_INTERVAL)
            _timePassed = 0f;
    }
}
