package org.shmo.icfb.combat.systems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class DeflectorSystem extends BaseShipSystemScript {
    public static final String ID = "icfb_deflector";

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        return null;
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {

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
