package org.shmo.icfb.combat.systems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class DeflectorSystem extends BaseShipSystemScript {
    public static final String ID = "icfb_deflector";
    public static final String EFFECT_LEVEL_KEY = "$icfb_DeflectorSystem_effectLevel";
    public static final String STATE_KEY = "$icfb_DeflectorSystem_state";

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = (ShipAPI)stats.getEntity();
        ship.setCustomData(EFFECT_LEVEL_KEY, effectLevel);
        ship.setCustomData(STATE_KEY, state);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        return null;
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (ShipAPI)stats.getEntity();
        ship.setCustomData(EFFECT_LEVEL_KEY, 0);
    }

    @Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
        return null;
    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        return true;
    }

    @Override
    public float getActiveOverride(ShipAPI ship) {
        return -1;
    }

    @Override
    public float getInOverride(ShipAPI ship) {
        return -1;
    }

    @Override
    public float getOutOverride(ShipAPI ship) {
        return -1;
    }

    @Override
    public float getRegenOverride(ShipAPI ship) {
        return -1;
    }

    @Override
    public int getUsesOverride(ShipAPI ship) {
        return -1;
    }

    @Override
    public String getDisplayNameOverride(State state, float effectLevel) {
        return null;
    }
}
