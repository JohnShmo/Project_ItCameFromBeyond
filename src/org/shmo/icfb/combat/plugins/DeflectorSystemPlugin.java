package org.shmo.icfb.combat.plugins;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;
import org.shmo.icfb.combat.entities.DeflectorEntity;
import org.shmo.icfb.combat.systems.DeflectorSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeflectorSystemPlugin extends BaseEveryFrameCombatPlugin {

    private transient CombatEngineAPI _engine;
    private transient Map<ShipAPI, List<DeflectorEntity>> _deflectorEntities;

    private CombatEngineAPI getEngine() {
        return _engine;
    }

    private List<DeflectorEntity> getOrCreateDeflectorEntityList(ShipAPI ship) {
        if (!_deflectorEntities.containsKey(ship)) {
            _deflectorEntities.put(ship, new ArrayList<DeflectorEntity>());
        }
        return _deflectorEntities.get(ship);
    }

    private void setEngine(CombatEngineAPI engine) {
        _engine = engine;
    }

    public boolean shipHasDeflector(ShipAPI ship) {
        ShipSystemAPI defenseSystem = ship.getPhaseCloak();
        if (defenseSystem == null)
            return false;
        return defenseSystem.getId().equals(DeflectorSystem.ID);
    }

    public List<ShipAPI> getShipsWithDeflectors() {
        final List<ShipAPI> result = new ArrayList<>();
        final List<ShipAPI> ships = getEngine().getShips();
        for (ShipAPI ship : ships) {
            if (shipHasDeflector(ship))
                result.add(ship);
        }
        return result;
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (getEngine() == null || getEngine().isPaused())
            return;
        List<ShipAPI> ships = getShipsWithDeflectors();
    }

    @Override
    public void renderInUICoords(ViewportAPI viewport) {

    }

    @Override
    public void init(CombatEngineAPI engine) {
        setEngine(engine);
        _deflectorEntities = new HashMap<>();
    }

    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {
        if (getEngine() == null)
            return;
        List<ShipAPI> ships = getShipsWithDeflectors();
    }

    @Override
    public void processInputPreCoreControls(float amount, List<InputEventAPI> events) {

    }

}
