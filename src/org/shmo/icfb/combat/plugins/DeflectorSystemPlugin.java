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

    private static int computeHowManyEntitiesToSpawn(ShipAPI ship) {
        // We are going to hijack aspects of the shield spec.
        // (I hope this stuff is intact despite the shield being disabled)

        final ShipHullSpecAPI spec = ship.getHullSpec();
        final float shieldArc = spec.getShieldSpec().getArc();
        if (shieldArc > 180)
            return 4;
        return 2;
    }

    private static DeflectorEntity createDeflectorEntity(ShipAPI ship, int index) {
        final float degreesPerEntity = 360f / 4f;
        final float initialOffset = -(degreesPerEntity / 2f);
        final float facingAngle = initialOffset + (degreesPerEntity * index);
        return new DeflectorEntity(ship, facingAngle);
    }

    private void createDeflectorEntitiesIfNeeded(ShipAPI ship) {
        List<DeflectorEntity> deflectorEntityList = _deflectorEntities.get(ship);
        if (deflectorEntityList == null) {
            deflectorEntityList = new ArrayList<>();
            int entityCount = computeHowManyEntitiesToSpawn(ship);
            for (int i = 0; i < entityCount; i++) {
                DeflectorEntity entity = createDeflectorEntity(ship, i);
                deflectorEntityList.add(entity);
                getEngine().addEntity(entity);
            }
            _deflectorEntities.put(ship, deflectorEntityList);
        }
    }

    private List<DeflectorEntity> getOrCreateDeflectorEntities(ShipAPI ship) {
        createDeflectorEntitiesIfNeeded(ship);
        return _deflectorEntities.get(ship);
    }

    private void setEngine(CombatEngineAPI engine) {
        _engine = engine;
    }

    public boolean shipHasDeflector(ShipAPI ship) {
        return DeflectorSystem.ID.equals(ship.getHullSpec().getShipDefenseId());
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
